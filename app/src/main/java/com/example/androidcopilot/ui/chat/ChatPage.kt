package com.example.androidcopilot.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidcopilot.chat.model.ChatMessage
import com.example.androidcopilot.chat.model.ChatMessageViewModel


@Composable
fun ChatConversationScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatMessageViewModel
) {
    val messages by viewModel.messageList.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val inputHint by viewModel.inputHint.collectAsState()
    val inputMode by viewModel.inputMode.collectAsState()
    val sendState by viewModel.inputSendState.collectAsState()

    Column(modifier) {
        Row {
            Spacer(modifier = Modifier.weight(1F))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.MoreVert, "")
            }
        }
        Spacer(modifier = Modifier.weight(1F))
        ChatMessageList(messages = messages)

        MixedMessageInput(
            Modifier,
            inputText,
            inputHint,
            inputMode,
            sendState,
            onChangeInputMode = viewModel::switchInput,
            onInputTextChange = viewModel::input,
            onSend = viewModel::send,
            onPause = viewModel::pause,
            onRetry = viewModel::retry
        )
    }
}

@Preview
@Composable
internal fun ChatConversationPreview() {
    ChatConversationScreen(viewModel = ChatMessageViewModel().apply {
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