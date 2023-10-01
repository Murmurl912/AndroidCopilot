package com.example.androidcopilot.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidcopilot.chat.model.ChatMessage
import com.example.androidcopilot.chat.model.ChatMessageViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConversationScreen(
    viewModel: ChatMessageViewModel,
) {
    val conversation by viewModel.conversation.collectAsState()
    val conversationList by viewModel.conversationList.collectAsState()
    val messages by viewModel.messageList.collectAsState()
    val inputState by viewModel.inputState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(drawerContent = {
        ConversationList(
            conversation = conversation,
            conversations = conversationList
        )
    }, drawerState = drawerState) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(conversation.title)
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
                ChatMessageList(modifier = Modifier.weight(1F), messages = messages)
                MixedMessageInput(
                    modifier = Modifier,
                    state = inputState,
                    onModeChange = viewModel::switchInput,
                    onInputChange = viewModel::input,
                    onSendMessage = viewModel::send,
                    onPause = viewModel::pause,
                    onRetry = viewModel::retry
                )
            }
        }
    }
}

@Preview
@Composable
internal fun ChatConversationPreview() {
    ChatConversationScreen(
        viewModel = ChatMessageViewModel().apply {
        messageList.value = listOf(
            ChatMessage.AssistantMessage(
                0,
                0,
                0,
                "你不要骗我",
                0,
                0,
                ChatMessage.Status.StatusSuccess
            ),
            ChatMessage.HumanMessage(
                1,
                0,
                0,
                "你不要吓我",
                0,
                0,
                ChatMessage.Status.StatusSuccess
            ),
            ChatMessage.SystemMessage(
                2,
                0,
                0,
                "推出了群聊",
                0,
                0,
            ),
            ChatMessage.FunctionCallRequestMessage(
                3,
                0,
                0,
                "Android Copilot Request GPS Location Access",
                "",
                "",
                0,
                0,
                ChatMessage.Status.StatusSuccess
            ),
            ChatMessage.FunctionCallResponseMessage(
                4,
                0,
                0,
                "You denied Location Access",
                "",
                "",
                "",
                0,
                0,
                ChatMessage.Status.StatusSuccess
            )

        )
    })
}