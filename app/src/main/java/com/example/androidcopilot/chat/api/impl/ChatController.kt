package com.example.androidcopilot.chat.api.impl

import com.example.androidcopilot.app.log.AppLogger
import com.example.androidcopilot.chat.api.ChatApiProvider
import com.example.androidcopilot.chat.api.ChatControllerApi
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

const val TAG = "ChatController"

class ChatController(
    private val chatRepository: ChatRepository,
    private val conversationId: String,
    private val logger: AppLogger,
    private val chatApiProvider: ChatApiProvider,
    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob(),
) : CoroutineScope, ChatControllerApi {

    private val conversationState: MutableStateFlow<Conversation> = MutableStateFlow(
        Conversation(id = conversationId)
    )
    private val messageListState: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())

    override val conversation = conversationState.asStateFlow()
    override val messages = messageListState.asStateFlow()

    private val sendLock = Mutex()
    private val jobs = ConcurrentHashMap<String, Job>()
    private val scope: CoroutineScope
        get() = this

    init {
        scope.launch {
            init()
        }
    }

    private suspend fun init() = sendLock.withLock {
        val conversation = chatRepository.findConversationById(conversationId)
        if (conversation != null) {
            conversationState.value = conversation
        } else {
            // conversation not found
            logger.error(TAG) {
                "Conversation not found"
            }
        }
        val messages = chatRepository.findConversationMessages(conversationId)
        messageListState.value = messages
        logger.debug(TAG) {
            "init: conversation=$conversation, messages=$messages"
        }
    }

    /**
     * send a user message
     * @return the message sent
     */
    override suspend fun send(message: Message, attachments: List<Attachment>): Unit = sendLock.withLock {
        withContext(NonCancellable) {
            val requestMessageId = UUID.randomUUID().toString()
            val responseMessageId = UUID.randomUUID().toString()
            val request =
                chatRepository.newMessage(
                    message,
                    attachments
                )
            val response = chatRepository.newMessage(
                Message(
                    id = responseMessageId,
                    conversation = conversationState.value.id,
                    parent = requestMessageId,
                    content = "",
                    status = Message.MessageStatus.StatusSuccess,
                    type = Message.MessageType.TypeText,
                    role = Message.MessageRole.RoleUser,
                ),
                emptyList()
            )
            val history =
                messageListState.getAndUpdate {
                    it + request + response
                }
            val conversation = conversationState.updateAndGet {
                it.copy(
                    lastMessageId = responseMessageId,
                    updateAt = System.currentTimeMillis(),
                )
            }
            chatRepository.updateConversation(conversationState.value)
            generate(request, response, conversation, history)
        }
    }

    override suspend fun stop(message: Message) {
        val job = jobs.remove(message.id)
        if (job != null) {
            job.join()
            job.cancel()
        }
    }

    override suspend fun retry(message: Message) = sendLock.withLock {
        val lastMessage = messageListState.value.lastOrNull()
        if (lastMessage == null) {
            logger.error(TAG) {
                "No last message"
            }
            return@withLock
        }
        if (lastMessage.id != message.id) {
            // last message is not the message to retry
            logger.error(TAG) {
                "Last message is not the message to retry"
            }
            return@withLock
        }
        if (lastMessage.role != Message.MessageRole.RoleAssistant) {
            // last message is not a response
            logger.error(TAG) {
                "Last message is not a assistant message"
            }
            return@withLock
        }

        // find parent message
        val parent = messageListState.value.find { it.id == message.parent }
        if (parent == null) {
            logger.error(TAG) {
                "Parent message not found"
            }
            return@withLock
        }
        // delete last response
        val history = messageListState.updateAndGet {
            it - lastMessage
        }

    }

    private suspend fun generate(
        request: Message,
        response: Message,
        conversation: Conversation,
        history: List<Message>
    ): Message {
        var reply = response
        val api = chatApiProvider.create(conversation)
        if (api.isFailure) {
            logger.error(TAG, api.exceptionOrNull()) {
                "ChatApi not found"
            }
            reply = response.copy(
                status = Message.MessageStatus.StatusError,
                error = "ChatApi Not Found"
            )
            messageListState.update {
                it.map { m ->
                    if (m.id == reply.id) {
                        reply
                    } else {
                        m
                    }
                }
            }
            return chatRepository.updateMessage(reply)
        }
        val job = api.getOrThrow()
            .generate(request, response, conversation, history)
            .onEach { message ->
                reply = message
                messageListState.update {
                    it.map { m ->
                        if (m.id == reply.id) {
                            reply
                        } else {
                            m
                        }
                    }
                }
                scope.launch {
                    chatRepository.updateMessage(message)
                }.join()
            }
            .catch { error ->
                reply = reply.copy(
                    status = Message.MessageStatus.StatusError,
                    error = error.message ?: error.stackTraceToString()
                )
                messageListState.update {
                    it.map { m ->
                        if (m.id == reply.id) {
                            reply
                        } else {
                            m
                        }
                    }
                }
                scope.launch {
                    chatRepository.updateMessage(reply)
                }
            }
            .launchIn(scope)
        jobs[reply.id] = job
        job.invokeOnCompletion { error ->
            if (error is CancellationException) {
                reply = reply.copy(
                    status = Message.MessageStatus.StatusStopped,
                    error = "Canceled"
                )

            } else if (error != null) {
                reply = reply.copy(
                    status = Message.MessageStatus.StatusStopped,
                    error = error.message ?: error.stackTraceToString()
                )
            }
            messageListState.update {
                it.map { m ->
                    if (m.id == reply.id) {
                        reply
                    } else {
                        m
                    }
                }
            }
            scope.launch {
                chatRepository.updateMessage(reply)
            }
        }
        job.join()
        return reply
    }

}

