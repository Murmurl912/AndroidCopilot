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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

class OpenaiChatClient(
    private val openai: OpenAI,
    private val chatRepository: ChatRepository,
    private val scope: CoroutineScope
): ChatClient {

    override suspend fun newConversation(): Result<Conversation> {
        return kotlin.runCatching {
            chatRepository.newConversation(
                Conversation()
            )
        }
    }

    override suspend fun updateConversation(conversation: Conversation): Result<Conversation> {
        return kotlin.runCatching {
            chatRepository.updateConversation(conversation)!!
        }
    }

    override suspend fun deleteConversation(conversation: Conversation): Result<Conversation> {
        return kotlin.runCatching {
            chatRepository.deleteConversation(conversation)!!
        }
    }

    override suspend fun send(
        message: Message
    ): Flow<Message> {
        callbackFlow {


            val conversation = chatRepository.findConversationById(message.conversation)
            val sendMessage = chatRepository.newMessage(message.copy(id = 0,
                conversation = conversation.id,
                parent = conversation.latestMessageId,
                createAt = System.currentTimeMillis(),
                updateAt = System.currentTimeMillis(),
                status = Message.Status.StatusPending
            ))
            val messages = chatRepository.messages(
                conversation,
                conversation.memoryOffset,
                conversation.memoryLimit
            )
            send(sendMessage, conversation, messages)
            awaitClose {
                //
            }
        }
    }

    private suspend fun send(
        send: Message,
        conversation: Conversation,
        messages: List<Message>
    ): Flow<Message> {
        val request = ChatCompletionRequest(
            ModelId(conversation.model),
            messages = messages.mapNotNull(Message::toChatMessage)
        )
        var sendMessage = send
        var replyMessage = Message(
            parent = sendMessage.id,
            conversation = conversation.id,
            content = "",
            type = Message.MessageType.MESSAGE_TYPE_ASSISTANT,
            status = Message.Status.StatusPending
        )
        return openai.chatCompletions(request)
            .map {
                val delta = it.choices[0].delta
                val status = when (it.choices[0].finishReason) {
                    FinishReason.Stop -> {
                        Message.Status.StatusSuccess
                    }
                    FinishReason.Length -> {
                        Message.Status.StatusSuccess
                    }
                    FinishReason.FunctionCall -> {
                        Message.Status.StatusSuccess
                    }
                    else -> {
                        Message.Status.StatusReceiving
                    }
                }
                replyMessage = replyMessage.copy(
                    content = replyMessage.content + delta.content,
                    functionName = delta.functionCall?.name,
                    functionArgs = delta.functionCall?.arguments,
                    updateAt = System.currentTimeMillis(),
                    token = it.usage?.completionTokens?:0,
                    status = status
                )
                if (replyMessage.id == 0L) {
                    withContext(NonCancellable) {
                        replyMessage = chatRepository.newMessage(replyMessage)
                        sendMessage.copy(
                            child = replyMessage.id,
                            token = it.usage?.totalTokens?.let { count ->
                                conversation.totalTokens + replyMessage.token - count
                            } ?: 0,
                            status = status
                        )

                    }
                } else {
                    chatRepository.updateMessage(replyMessage)
                }
                replyMessage
            }
            .onCompletion {
                withContext(NonCancellable) {
                    if (it is CancellationException) {
                        // canceled
                        sendMessage = sendMessage.copy(
                            status = Message.Status.StatusStopped,
                            updateAt = System.currentTimeMillis()
                        )
                        replyMessage = replyMessage.copy(
                            status = Message.Status.StatusStopped,
                            updateAt = System.currentTimeMillis()
                        )
                    } else if (it != null) {
                        // error
                        sendMessage = sendMessage.copy(
                            status = Message.Status.StatusError,
                            updateAt = System.currentTimeMillis()
                        )
                        replyMessage = replyMessage.copy(
                            status = Message.Status.StatusError,
                            updateAt = System.currentTimeMillis()
                        )
                    } else {
                        sendMessage = sendMessage.copy(
                            status = Message.Status.StatusSuccess,
                            updateAt = System.currentTimeMillis()
                        )
                        replyMessage = replyMessage.copy(
                            status = Message.Status.StatusSuccess,
                            updateAt = System.currentTimeMillis()
                        )
                    }
                    chatRepository.updateMessage(sendMessage)
                    chatRepository.updateMessage(replyMessage)
                    if (it == null) {
                        conversation.copy(

                        )

                    }
                }
            }
            .catch {

            }
    }
    private fun Message.ensureMessageValid() {
        if (conversation == 0L) {
            throw IllegalArgumentException("Conversation Is Required")
        }
        if (type !in intArrayOf(
            Message.MessageType.MESSAGE_TYPE_HUMAN,
            Message.MessageType.MESSAGE_TYPE_FUNCTION_CALL_RESPONSE
        )) {
            throw IllegalArgumentException("Message Type: $type Cannot be send")
        }
    }

    override fun messages(conversation: Conversation): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun conversations(): Flow<List<Conversation>> {
        TODO("Not yet implemented")
    }

    override fun conversation(conversationId: Long): Flow<Conversation> {
        TODO("Not yet implemented")
    }

}

private fun Message.toChatMessage(): ChatMessage? {
    return when (type) {
        Message.MessageType.MESSAGE_TYPE_ASSISTANT -> {
            ChatMessage(
                ChatRole.Assistant,
                content,
                null,
                null
            )
        }
        Message.MessageType.MESSAGE_TYPE_HUMAN -> {
            ChatMessage(
                ChatRole.User,
                content,
                null,
                null
            )
        }
        Message.MessageType.MESSAGE_TYPE_SYSTEM -> {
            ChatMessage(
                ChatRole.System,
                content,
                null,
                null
            )
        }
        Message.MessageType.MESSAGE_TYPE_FUNCTION_CALL_REQUEST -> {
            ChatMessage(
                ChatRole.Assistant,
                functionCall = FunctionCall(
                    functionName,
                    functionArgs
                )
            )
        }

        Message.MessageType.MESSAGE_TYPE_FUNCTION_CALL_RESPONSE -> {
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
