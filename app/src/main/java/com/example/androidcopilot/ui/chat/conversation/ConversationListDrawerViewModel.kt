package com.example.androidcopilot.ui.chat.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.navigation.AppScreens
import com.example.androidcopilot.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListDrawerViewModel  @Inject constructor (
    private val client: ChatClient
): ViewModel() {

    internal val drawerOpenStateFlow = MutableStateFlow(false)
    val currentConversation = MutableStateFlow<Conversation?>(null)
    val conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val drawerOpenState: StateFlow<Boolean> = drawerOpenStateFlow
    internal val drawerCommands = MutableStateFlow(DrawerCommand(false))

    init {
        viewModelScope.launch {
            client.conversations()
                .stateIn(this)
                .collect {
                    conversations.value = it
                }
        }
    }

    fun onNewConversation() {
        Navigator.navigate(
            AppScreens.HomeScreen.name
        )
    }

    fun onSwitchConversation(conversation: Conversation) {
        currentConversation.value = conversation
        Navigator.navigate(
            AppScreens.MessageScreen.createRoute(conversation.id)
        )
    }

    fun openSetting() {
        Navigator.navigate(
            AppScreens.SettingScreen.name
        )
    }


    fun openDrawer() {
        drawerCommands.value = DrawerCommand(true)
    }

    fun closeDrawer() {
        drawerCommands.value = DrawerCommand(false)
    }

    fun toggleDrawer() {
        val open = drawerOpenStateFlow.updateAndGet {
            !it
        }
        if (open) {
            openDrawer()
        } else {
            closeDrawer()
        }
    }

    internal class DrawerCommand(val open: Boolean)
}