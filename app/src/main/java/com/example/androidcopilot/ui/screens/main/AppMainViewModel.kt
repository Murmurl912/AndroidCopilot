package com.example.androidcopilot.ui.screens.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.storage.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationsState(
    val conversation: Conversation? = null,
    val conversations: List<Conversation> = emptyList(),
)

sealed interface AppState {
    object AppIdle : AppState

    data class AppInitialized(val conversationId: Long) : AppState

    object AppInitializing : AppState
}


@HiltViewModel
class AppMainViewModel @Inject constructor(private val chatRepo: ChatRepository) : ViewModel() {

    private val _conversationState = MutableStateFlow(ConversationsState())
    private val currentConversation = MutableStateFlow<Long?>(null)
    val conversationState = _conversationState.asStateFlow()

    private val _appState = MutableStateFlow<AppState>(AppState.AppIdle)
    val appState = _appState.asStateFlow()

    val _drawerCommand = MutableSharedFlow<DrawerCommand>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val drawerCommand = _drawerCommand.asSharedFlow()

    init {
        init()
    }

    private fun init() {
        if (!_appState.compareAndSet(AppState.AppIdle, AppState.AppInitializing)) {
            return
        }
        viewModelScope.launch {
            val conversationId = initCurrentConversation()
            watchConversations()
            _appState.compareAndSet(
                AppState.AppInitializing, AppState.AppInitialized(
                    conversationId = conversationId
                )
            )
        }
    }

    private suspend fun initCurrentConversation(): Long {
        return currentConversation.updateAndGet {
            if (it == null) {
                val conversation = chatRepo.findEmptyConversationOrNewConversation(Conversation(
                    model = "gpt-3.5-turbo"
                ))
                conversation.id
            } else {
                it
            }
        }!!
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchConversations() {
        chatRepo.conversationListFlow()
            .onEach {
                _conversationState.update { state ->
                    state.copy(conversations = it)
                }
            }
            .launchIn(viewModelScope)
        currentConversation.flatMapConcat { id ->
            if (id == null) {
                flowOf(null)
            } else {
                chatRepo.conversation(id)
            }
        }.onEach {
            _conversationState.update { state ->
                state.copy(
                    conversation = it
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onNewConversation() {

    }

    fun onDeleteConversation(conversation: Conversation) {

    }

    fun onSwitchConversation(conversation: Conversation) {

    }

    fun onOpenSetting() {

    }

    fun onOpenAbout() {

    }

    /**
     * the method must be called inside a composition
     */
    fun onOpenDrawer() {
        _drawerCommand.tryEmit(DrawerCommand.Open)
    }

    fun onCloseDrawer() {
        _drawerCommand.tryEmit(DrawerCommand.Close)
    }
}

sealed interface DrawerCommand {

    object Open : DrawerCommand

    object Close : DrawerCommand

}