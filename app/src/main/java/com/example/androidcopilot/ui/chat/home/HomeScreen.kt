package com.example.androidcopilot.ui.chat.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.androidcopilot.ui.chat.conversation.ConversationListDrawer
import com.example.androidcopilot.ui.chat.conversation.ConversationListDrawerViewModel
import com.example.androidcopilot.ui.chat.input.MessageInput
import com.example.androidcopilot.ui.chat.message.MessageList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    conversationViewModel: ConversationListDrawerViewModel
) {
    val inputState by homeViewModel.inputState.collectAsState()

    ConversationListDrawer(conversationViewModel = conversationViewModel) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text("Android Copilot")
                }, navigationIcon = {
                    IconButton(onClick = conversationViewModel::toggleDrawer) {
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
                Modifier.padding(it)
            ) {
                MessageList(modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth(), messages = emptyList())
                MessageInput(
                    modifier = Modifier,
                    state = inputState,
                    onModeChange = homeViewModel::mode,
                    onInputChange = homeViewModel::input,
                    onSendMessage = homeViewModel::send,
                )
            }
        }
    }
}