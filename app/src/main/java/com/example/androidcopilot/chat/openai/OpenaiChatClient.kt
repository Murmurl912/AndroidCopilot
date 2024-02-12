package com.example.androidcopilot.chat.openai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.androidcopilot.app.AppLogger
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.api.chat.ChatClient
import com.example.androidcopilot.chat.model.isCompleted
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class OpenaiChatClient(
    private val openai: OpenAI,
    private val chatRepository: ChatRepository,
    private val scope: CoroutineScope,
    private val logger: AppLogger,
): ChatClient {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun send(
        message: Message
    ): Flow<Message> {
        return flow {
            emit(sendMessage(message))
        }.flatMapConcat {
            it
        }
    }

    private suspend fun sendMessage(message: Message): Flow<Message> {
        val conversation = chatRepository.findConversationById(message.conversation)
            ?: throw IllegalStateException("Conversation: ${message.id} Not Found")

        var send = message.copy(
            parent = conversation.latestMessageId,
        )
        scope.launch {
            send = chatRepository.newMessage(send)
        }.join()

        val memoryMessages = chatRepository.contextMessages(conversation.id)
        val memory = memoryMessages.filter {
            it.status == Message.MessageStatus.StatusSuccess
        } + send
        val memoryTokens = memory.sumOf {
            it.token
        }

        val request = ChatCompletionRequest(
            ModelId(conversation.model),
            messages = memory.mapNotNull(Message::toChatMessage)
        )

        var reply = Message(
            parent = send.id,
            conversation = conversation.id,
            content = "",
            type = Message.MessageType.TypeAssistant,
            status = Message.MessageStatus.StatusPending
        )
        return openai.chatCompletions(request)
            .map {
                val delta = it.choices[0].delta
                val messageStatus = when (it.choices[0].finishReason) {
                    FinishReason.Stop -> {
                        Message.MessageStatus.StatusSuccess
                    }
                    FinishReason.Length -> {
                        Message.MessageStatus.StatusSuccess
                    }
                    FinishReason.FunctionCall -> {
                        Message.MessageStatus.StatusSuccess
                    }
                    else -> {
                        Message.MessageStatus.StatusReceiving
                    }
                }
                val type = if (delta.functionCall != null) {
                    Message.MessageType.TypeFunctionCallRequest
                } else {
                    Message.MessageType.TypeAssistant
                }
                reply = reply.copy(
                    content = reply.content + (delta.content?:""),
                    functionName = delta.functionCall?.name,
                    functionArgs = delta.functionCall?.arguments,
                    updateAt = System.currentTimeMillis(),
                    token = it.usage?.completionTokens?:0,
                    status = messageStatus,
                    type = type
                )
                scope.launch {
                    if (reply.id == 0L) {
                        reply = chatRepository.newMessage(reply)
                        send = send.copy(
                            child = reply.id,
                            token = it.usage?.totalTokens?.let { total ->
                                total - memoryTokens - reply.token
                            } ?: 0,
                            status = messageStatus
                        )
                        chatRepository.updateMessageStatusChildAndToken(
                            send.id,
                            messageStatus,
                            it.usage?.totalTokens?.let { total ->
                                total - memoryTokens - reply.token
                            } ?: 0,
                            reply.id
                        )
                    } else {
                        chatRepository.updateMessage(reply)
                    }
                }.join()
                reply
            }
            .catch {
                logger.error(TAG, it) {
                    "error send message"
                }
                // error
                send = send.copy(
                    status = Message.MessageStatus.StatusError,
                    updateAt = System.currentTimeMillis(),
                    child = reply.id
                )
                reply = reply.copy(
                    status = Message.MessageStatus.StatusError,
                    updateAt = System.currentTimeMillis()
                )
                emit(reply)
                scope.launch {
                    chatRepository.updateMessage(send)
                    chatRepository.updateMessage(reply)
                    chatRepository.refreshContextMessageOffset(conversation.id)
                }.join()
            }
            .onCompletion {
                if (it is CancellationException) {
                    // canceled
                    logger.error(TAG, it) {
                        "request is canceled"
                    }
                    send = send.copy(
                        status = Message.MessageStatus.StatusStopped,
                        updateAt = System.currentTimeMillis(),
                        child = reply.id
                    )
                    reply = reply.copy(
                        status = Message.MessageStatus.StatusStopped,
                        updateAt = System.currentTimeMillis(),
                    )
                } else {
                    if (!send.isCompleted()) {
                        send = send.copy(
                            status = Message.MessageStatus.StatusSuccess,
                            updateAt = System.currentTimeMillis(),
                            child = reply.id
                        )
                    }
                    if (!reply.isCompleted()) {
                        reply = reply.copy(
                            status = Message.MessageStatus.StatusSuccess,
                            updateAt = System.currentTimeMillis(),
                        )
                    }
                    scope.launch {
                        val hasTitle = conversation.title.isNotEmpty()
                                && !chatRepository.findConversationById(conversation.id)
                            ?.title.isNullOrEmpty()
                        if (!hasTitle) {
                            summarizeConversationTitle(
                                send.content,
                                reply.content,
                                conversation
                            )
                        }
                        chatRepository.updateConversationType(conversation.id, Conversation.ConversationType.TypePersistent)
                    }
                }
                scope.launch {
                    chatRepository.updateMessage(send)
                    chatRepository.updateMessage(reply)
                    chatRepository.refreshContextMessageOffset(conversation.id)
                }.join()
            }
    }

    private suspend fun summarizeConversationTitle(
        request: String,
        response: String,
        conversation: Conversation
    ) {
        val titleBuilder = StringBuilder()
        openai.chatCompletions(
            ChatCompletionRequest(
                ModelId(conversation.model),
                messages = listOf(
                    ChatMessage(
                        ChatRole.System,
                        """
                         你是一个总结会话主题的AI。
                         你的任务是总结用户和其他AI助手的会话主题。
                         你所给出的回答应该简明扼要，以便作为该用户与AI助手的会话标题。
                         主题在十个字左右较为合适，优先考虑较短的主题。
                         回答的语言应该使用用户消息所使用的语言。
                         回答仅包含主题，不可添加其他无关文字。
                         如果你无法确定主题，请回答：用户请求帮助。
                         用户与AI助手的对话格式如:
                            用户：用户的消息
                            AI：AI的回复
                         现在给出如下会话，总结消息主题：
                         用户：$request
                         AI: $response
                        """.trimIndent()
                    )
                )
            )
        ).catch {
            logger.error(TAG, it) {
                "error summary message"
            }
        }.collect {
            titleBuilder.append(it.choices[0].delta.content?:"")
            chatRepository.updateConversationTitle(conversation.id, titleBuilder.toString())
            chatRepository.updateConversationType(conversation.id, Conversation.ConversationType.TypePersistent)
        }
    }


    override fun messages(conversationId: Long): Flow<List<Message>> {
        return chatRepository.messageListFlow(conversationId)
    }

    override suspend fun conversation(): Conversation {
        return chatRepository.newConversation(
            Conversation(
                model = "gpt-3.5-turbo-16k"
            )
        )
    }

    override suspend fun conversation(id: Long): Flow<Conversation> {
        return chatRepository.conversation(id)
    }

    override fun conversations(): Flow<List<Conversation>> {
        return chatRepository.conversationListFlow()
    }

    override suspend fun delete(conversation: Conversation) {
        chatRepository.deleteConversation(conversation)
    }

    companion object {
        const val TAG = "OpenaiChatClient"
    }
}

private fun Message.toChatMessage(): ChatMessage? {
    return when (type) {
        Message.MessageType.TypeAssistant -> {
            ChatMessage(
                ChatRole.Assistant,
                content,
                null,
                null
            )
        }
        Message.MessageType.TypeHuman -> {
            ChatMessage(
                ChatRole.User,
                content,
                null,
                null
            )
        }
        Message.MessageType.TypeSystem -> {
            ChatMessage(
                ChatRole.System,
                content,
                null,
                null
            )
        }
        Message.MessageType.TypeFunctionCallRequest -> {
            ChatMessage(
                ChatRole.Assistant,
                functionCall = FunctionCall(
                    functionName,
                    functionArgs
                )
            )
        }

        Message.MessageType.TypeFunctionCallResponse -> {
            ChatMessage(
                ChatRole.Function,
                content,
                functionName,
                null
            )
        }

        else -> {
            null
        }
    }
}
