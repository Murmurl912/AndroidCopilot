package com.example.androidcopilot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.api.chat.ChatClient
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.navigation.AppScreens
import com.example.androidcopilot.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppNavigationDrawerViewModel  @Inject constructor (
    private val client: ChatClient
): ViewModel() {

    internal val drawerOpenStateFlow = MutableStateFlow(false)
    val currentConversation = MutableStateFlow<Conversation?>(null)
    val conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val drawerOpenState: StateFlow<Boolean> = drawerOpenStateFlow
    internal val drawerCommands = MutableStateFlow(DrawerCommand(false))

    init {
        viewModelScope.launch {
            client.conversations().collect {
                conversations.value = it

            }
        }
    }

    fun onNewConversation() {
        Navigator.navigate(
            AppScreens.HomeScreen.name
        )
    }

    fun onDeleteConversation(
        conversation: Conversation,
        onCompleted: (Boolean, Throwable?) -> Unit = {_, _ -> }
    ) {
        viewModelScope.launch {
            client.delete(conversation)
            onCompleted(true, null)
            // wait animation
            delay(200)
            Navigator.navigate(
                AppScreens.HomeScreen.name
            )
        }.invokeOnCompletion {
            onCompleted(false, it)
        }
    }

    fun onSwitchConversation(conversation: Conversation) {
        currentConversation.value = conversation
        Navigator.navigate(
            AppScreens.MessageScreen.createRoute(conversation.id)
        )
        toggleDrawer()
    }

    fun openSetting() {
        Navigator.navigate(
            AppScreens.SettingScreen.name
        )
        toggleDrawer()
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

    internal class DrawerCommand(val open: Boolean) {
        var consumed = false
        fun consume() {
            consumed = true
        }
    }
}