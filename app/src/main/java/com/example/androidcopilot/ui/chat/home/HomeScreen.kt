package com.example.androidcopilot.ui.chat.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.androidcopilot.ui.chat.conversation.ChatConversationViewModel
import com.example.androidcopilot.ui.chat.conversation.ConversationListDrawerSheet
import com.example.androidcopilot.ui.chat.input.MessageInput
import com.example.androidcopilot.ui.chat.message.MessageList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    conversationViewModel: ChatConversationViewModel
) {
    val inputState by homeViewModel.inputState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(drawerContent = {
        ConversationListDrawerSheet(conversationViewModel = conversationViewModel)
    }, drawerState = drawerState) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text("Android Copilot")
                }, navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.ListAlt, "")
                    }
                }, actions = {
                    IconButton(onClick = {

                    }) {
                        Icon(Icons.Default.MoreVert, "")
                    }
                })
            }
        ) {
            Column(
                Modifier
                    .padding(it)
                    .systemBarsPadding()) {
                MessageList(modifier = Modifier.weight(1F), messages = emptyList())
                MessageInput(
                    modifier = Modifier,
                    state = inputState,
                    onModeChange = homeViewModel::mode,
                    onInputChange = homeViewModel::input,
                    onSendMessage = homeViewModel::send,
                    onPause = homeViewModel::pause,
                    onRetry = homeViewModel::retry
                )
            }
        }
    }
}