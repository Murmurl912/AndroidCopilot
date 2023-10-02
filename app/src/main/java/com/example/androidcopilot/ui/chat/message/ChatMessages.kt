package com.example.androidcopilot.ui.chat.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.ui.chat.conversation.ChatConversationViewModel
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.ui.chat.conversation.ConversationListDrawerSheet
import com.example.androidcopilot.ui.chat.input.MixedMessageInput
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    conversationId: Long = 0,
    conversationViewModel: ChatConversationViewModel,
    messageViewModel: ChatMessageViewModel,
) {
    val conversation by messageViewModel.conversation.collectAsState()
    val messages by messageViewModel.messages.collectAsState()
    val inputState by messageViewModel.inputState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(drawerContent = {
        ConversationListDrawerSheet(conversationViewModel = conversationViewModel)
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
                MessageList(modifier = Modifier.weight(1F), messages = messages)
                MixedMessageInput(
                    modifier = Modifier,
                    state = inputState,
                    onModeChange = messageViewModel::switchInput,
                    onInputChange = messageViewModel::input,
                    onSendMessage = messageViewModel::send,
                    onPause = messageViewModel::pause,
                    onRetry = messageViewModel::retry
                )
            }
        }
    }
}


@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messages: List<Message> = emptyList(),
) {

    LazyColumn(modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(messages.size, {messages[it].id}, {
            messages[it]::class.simpleName
        }) {
            val message = messages[it]
            when (message.type) {
                Message.MessageType.TypeHuman -> {
                    HumanMessageItem(
                        Modifier.fillMaxWidth(),
                        message = message
                    )

                }
                Message.MessageType.TypeAssistant -> {
                    AssistantMessageItem(
                        Modifier.fillMaxWidth(),
                        message = message
                    )
                }
                Message.MessageType.TypeFunctionCallRequest -> {
                    FunctionCallRequestMessageItem(
                        Modifier.fillMaxWidth(),
                        message = message
                    )
                }
                Message.MessageType.TypeFunctionCallResponse -> {
                    FunctionCallResponseMessageItem(
                        Modifier.fillMaxWidth(),
                        message = message
                    )
                }

                Message.MessageType.TypeSystem -> {
                    SystemMessageItem(
                        Modifier.fillMaxWidth(),
                        message = message
                    )
                }
            }
        }
    }
}

@Composable
fun AssistantMessageItem(
    modifier: Modifier = Modifier,
    message: Message
) {
    Row(modifier) {
        Card(modifier =
        Modifier
            .weight(0.9F, false)
            .wrapContentSize()
        , colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )) {
            Box(Modifier.padding(12.dp)) {
                Text(message.content)
            }
        }
        Spacer(modifier = Modifier.weight(0.1F))
    }
}


@Composable
fun HumanMessageItem(
    modifier: Modifier = Modifier,
    message: Message
) {
    Row(modifier, horizontalArrangement = Arrangement.End) {
        Card(modifier = Modifier
            .weight(0.9F, false)
            .wrapContentSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        ) {
            Box(Modifier.padding(12.dp)) {
                Text(message.content)
            }
        }
    }
}

@Composable
fun SystemMessageItem(
    modifier: Modifier = Modifier,
    message: Message
) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        Text(message.content, Modifier.padding(12.dp))
    }
}

@Composable
fun FunctionCallRequestMessageItem(
    modifier: Modifier = Modifier,
    message: Message
) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        Text(message.functionName + message.functionArgs, Modifier.padding(12.dp))
    }
}


@Composable
fun FunctionCallResponseMessageItem(
    modifier: Modifier = Modifier,
    message: Message
) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        Text(message.content, Modifier.padding(12.dp))
    }
}


@Preview
@Composable
fun MessagesPreview() {
    MessageList(
        messages = listOf(
            Message(
                id = 1,
                conversation = 0,
                type = Message.MessageType.TypeHuman,
                status = Message.MessageStatus.StatusSuccess,
                content = "你不要瞎说"
            ),
            Message(
                id = 2,
                conversation = 0,
                type = Message.MessageType.TypeAssistant,
                status = Message.MessageStatus.StatusSuccess,
                content = "我才不会骗人啦"
            ),
            Message(
                id = 3,
                conversation = 0,
                type = Message.MessageType.TypeSystem,
                status = Message.MessageStatus.StatusSuccess,
                content = "不要相信人工智能"
            ),
            Message(
                id = 4,
                conversation = 0,
                type = Message.MessageType.TypeFunctionCallRequest,
                status = Message.MessageStatus.StatusSuccess,
                content = "",
                functionName = "Current User Location",
                functionArgs = "AI wants your location"
            ),
            Message(
                id = 4,
                conversation = 0,
                type = Message.MessageType.TypeFunctionCallResponse,
                status = Message.MessageStatus.StatusSuccess,
                content = "Location: 北京",
                functionName = "Current User Location",
            )

        )
    )
}