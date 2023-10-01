package com.example.androidcopilot.chat.openai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatCompletionRequestBuilder
import com.aallam.openai.api.chat.ChatDelta
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicReference

class OpenaiChatClient(
    private val openai: OpenAI,
    private val chatRepository: ChatRepository
): ChatClient {

    override suspend fun newConversation(): Result<ChatConversation> {
        return kotlin.runCatching {
            chatRepository.newConversation(
                ChatConversation()
            )
        }
    }

    override suspend fun updateConversation(conversation: ChatConversation): Result<ChatConversation> {
        return kotlin.runCatching {
            chatRepository.updateConversation(conversation)!!
        }
    }

    override suspend fun deleteConversation(conversation: ChatConversation): Result<ChatConversation> {
        return kotlin.runCatching {
            chatRepository.deleteConversation(conversation)!!
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun send(
        message: Message
    ): Flow<Message> {
        return flow {
            message.ensureMessageValid()
            val conversation = chatRepository.findConversationById(message.conversation)
            val messages = chatRepository.messages(
                conversation,
                conversation.memoryOffset,
                conversation.memoryLimit
            )
            emit(conversation to messages)
        }.flatMapConcat {(conversation, messages) ->
            val request = ChatCompletionRequest(
                ModelId(conversation.model),
                messages = messages.mapNotNull(Message::toChatMessage)
            )
            val messageRef = AtomicReference(
                Message(
                    conversation = conversation.id,
                    type = Message.MessageType.MESSAGE_TYPE_ASSISTANT,
                    content = "",
                    createAt = System.currentTimeMillis()
                )
            )
            val tokenUsage = AtomicReference<Usage?>()
            openai.chatCompletions(request)
                .map { chunk ->
                    val delta = chunk.choices[0].delta
                    var newMessage = messageRef.get()
                    newMessage = newMessage.copy(
                        content = message.content + delta.content,
                        functionName = delta.functionCall?.name,
                        functionArgs = delta.functionCall?.arguments,
                        updateAt = System.currentTimeMillis(),
                        token = chunk.usage?.completionTokens?:0
                    )
                    tokenUsage.set(chunk.usage)
                    messageRef.set(newMessage)
                    newMessage
                }
                .onCompletion {

                }
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

    override fun messages(): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun conversations(): Flow<List<ChatConversation>> {
        TODO("Not yet implemented")
    }

    override fun watchConversation(conversationId: Long): Flow<ChatConversation> {
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
