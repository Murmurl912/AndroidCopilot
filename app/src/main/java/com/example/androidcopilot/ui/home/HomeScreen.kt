package com.example.androidcopilot.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
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
import com.example.androidcopilot.ui.AppNavigationDrawerViewModel
import com.example.androidcopilot.ui.chat.input.TextSpeechInput
import com.example.androidcopilot.ui.chat.input.rememberTextSpeechInputState
import com.example.androidcopilot.ui.chat.message.MessageList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    conversationViewModel: AppNavigationDrawerViewModel
) {
    val isSendingMessage by homeViewModel.isSendingMessage.collectAsState()

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
        Column(Modifier.padding(it)
            .consumeWindowInsets(it)
        ) {
            MessageList(modifier = Modifier
                .weight(1F)
                .fillMaxWidth(), messages = emptyList())
            val textSpeechInputState = rememberTextSpeechInputState(
                isSending = isSendingMessage,
                onSend = { input ->
                    homeViewModel.send(input, emptyList())
                }
            )
            TextSpeechInput(
                modifier = Modifier.fillMaxWidth(),
                inputState = textSpeechInputState,
            )
        }
    }
}