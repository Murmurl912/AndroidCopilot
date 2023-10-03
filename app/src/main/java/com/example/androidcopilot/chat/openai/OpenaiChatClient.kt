package com.example.androidcopilot.chat.openai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.androidcopilot.chat.AppLogger
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.model.isCompleted
import com.example.androidcopilot.chat.repository.ChatRepository
import com.example.androidcopilot.ui.chat.input.SendState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        var conversation = chatRepository.findConversationById(message.conversation)
            ?: throw IllegalStateException("Conversation: ${message.id} Not Found")

        val newTitleConversation = conversation.title.isEmpty() && conversation.messageCount == 0

        val memoryMessages = chatRepository.messages(
            conversation,
            0,
            conversation.memoryMessageLimit
        )
        val memory = memoryMessages.filter {
            it.status == Message.MessageStatus.StatusSuccess
        }
        val memoryTokens = memory.sumOf {
            it.token
        }

        val request = ChatCompletionRequest(
            ModelId(conversation.model),
            messages = memory.mapNotNull(Message::toChatMessage)
        )
        var send = message.copy(
            conversation = conversation.id,
            parent = conversation.latestMessageId,
        )
        scope.launch {
            send = chatRepository.newMessage(send)
        }.join()
        if (newTitleConversation) {
            scope.launch {
                summarizeConversationTitle(message.content, conversation)
            }
        }
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
                    content = reply.content + delta.content,
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
                        chatRepository.updateMessage(send)
                        chatRepository.updateConversation(
                            conversation.copy(latestMessageId = reply.id).also {
                                conversation = it
                            }
                        )
                    } else {
                        chatRepository.updateMessage(reply)
                    }
                }.join()

                reply
            }
            .catch {
                if (it is CancellationException) {
                    // canceled
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
                }
                scope.launch {
                    chatRepository.updateMessage(send)
                    chatRepository.updateMessage(reply)
                    chatRepository.trimMemoryOffset(conversation.id)
                }.join()
            }
            .onCompletion {
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
                    chatRepository.updateMessage(send)
                    chatRepository.updateMessage(reply)
                    chatRepository.trimMemoryOffset(conversation.id)
                }.join()
            }
            .catch {
                logger.error("chat", it) {
                    "error send message"
                }
            }
    }

    private suspend fun summarizeConversationTitle(message: String, conversation: Conversation) {
        var currentConversation = conversation
        openai.chatCompletions(
            ChatCompletionRequest(
                ModelId(conversation.model),
                messages = listOf(
                    ChatMessage(
                        ChatRole.System,
                        """
                                You are Android Copilot, An AI assistant design to help user's questions.
                                Now your are given a task: 
                                Given user input, summarize conversation topics in few words. 
                                And follow user's language.
                                If you don't know how to summarize it, reply: Assist with user's request.
                                User input is: $message
                            """.trimIndent()
                    )
                )
            )
        ).catch {
            logger.error("chat", it) {
                "error summary message"
            }
        }.collect {
            val content = it.choices[0].delta.content
            currentConversation =
                currentConversation.copy(title = currentConversation.title + content)
            chatRepository.updateConversation(conversation)
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
