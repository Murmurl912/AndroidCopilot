package com.example.androidcopilot.chat.openai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OpenaiChatClient(
    private val openai: OpenAI,
    private val chatRepository: ChatRepository,
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
        send = chatRepository.newMessage(send)
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
                reply
            }
            .onCompletion {
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
                } else if (it != null) {
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
                } else {
                    send = send.copy(
                        status = Message.MessageStatus.StatusSuccess,
                        updateAt = System.currentTimeMillis(),
                        child = reply.id
                    )
                    reply = reply.copy(
                        status = Message.MessageStatus.StatusSuccess,
                        updateAt = System.currentTimeMillis()
                    )
                }
                withContext(NonCancellable) {
                    chatRepository.updateMessage(send)
                    chatRepository.updateMessage(reply)
                    chatRepository.trimMemoryOffset(conversation.id)
                }
            }
            .catch {

            }
    }


    override fun messages(conversation: Conversation): Flow<List<Message>> {
        return chatRepository.messageListFlow(conversation)
    }

    override suspend fun conversation(): Conversation {
        return chatRepository.newConversation(
            Conversation(
                model = "gpt-3.5-turbo-16k"
            )
        )
    }

    override fun conversations(): Flow<List<Conversation>> {
        return chatRepository.conversationListFlow()
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
