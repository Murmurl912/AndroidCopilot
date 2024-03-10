package com.example.androidcopilot.ui.chat.message

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.ui.chat.input.TextSpeechInput
import com.example.androidcopilot.ui.chat.input.rememberTextSpeechInputState
import com.example.androidcopilot.ui.main.AppMainViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MessageScreen(
    mainViewModel: AppMainViewModel,
    messageViewModel: MessageViewModel,
) {

    val conversation by messageViewModel.conversation.collectAsState()
    val messages by messageViewModel.messages.collectAsState()
    var showMenu by remember {
        mutableStateOf(false)
    }
    var showDelete by remember {
        mutableStateOf(false)
    }
    val isSendingMessage by messageViewModel.isSendingMessage.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = {
                val title = conversation.title.ifEmpty {
                    "Untitled Conversation"
                }
                Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }, navigationIcon = {
                IconButton(onClick = {
                    mainViewModel.onOpenDrawer()
                }
                ) {
                    Icon(Icons.Default.ListAlt, "")
                }
            }, actions = {
                IconButton(onClick = {
                    showMenu = true
                }) {
                    Icon(Icons.Default.MoreVert, "")
                }
                MessageMenu(expanded = showMenu, onDismissRequest = {
                    showMenu = false
                }, onNewChat = {
                    showMenu = false
                    messageViewModel.onNewConversation()
                }, onDelete = {
                    showMenu = false
                    showDelete = true
                }, onRename = {
                    showMenu = false
                })
            })
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .consumeWindowInsets(it)) {
            MessageList(modifier = Modifier
                .weight(1F)
                .fillMaxWidth(),
                messages = messages,
                messageViewModel.scrollCommand
            )
            val textSpeechInputState = rememberTextSpeechInputState(
                isSending = isSendingMessage,
                onSend = { input ->
                    messageViewModel.send(input, emptyList())
                }
            )
            TextSpeechInput(
                modifier = Modifier.fillMaxWidth(),
                inputState = textSpeechInputState,
            )
        }
        MessageDeleteDialog(show = showDelete, onDismissRequest = {
            showDelete = false
        }, onDelete = {
            messageViewModel.onDeleteConversation(conversation)
            showDelete = false
        })
    }
}


@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messages: List<Message> = emptyList(),
    scrollCommand: Flow<MessageViewModel.ScrollCommand> = remember {
        emptyFlow()
    }
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(lazyListState) {
        lazyListState.interactionSource
            .interactions
            .filterIsInstance<DragInteraction>()
            .collect {
                when (it) {
                    is DragInteraction.Start -> {

                    }
                }
            }
    }

    LazyColumn(modifier,
        state = lazyListState,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item("start-item") {
            Spacer(modifier = Modifier
                .height(1.dp)
                .fillMaxWidth())
        }
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
        item("end-item") {
            Spacer(modifier = Modifier
                .height(1.dp)
                .fillMaxWidth())
        }
    }

    LaunchedEffect(scrollCommand) {
        scrollCommand.collect {
            when (it) {
                MessageViewModel.ScrollCommand.Noop -> {
                    // noop
                }
                is MessageViewModel.ScrollCommand.ScrollTo -> {
                    val index = (it.index - 1).coerceAtLeast(0)
                    lazyListState.scrollToItem(index.coerceAtLeast(0), it.offset)
                    lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
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


@Composable
fun MessageMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onNewChat: () -> Unit = {},
    onHistory: () -> Unit = {},
    onSetting: () -> Unit = {},
    onShare: () -> Unit = {},
    onRename: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(12.dp, 12.dp),
        modifier = Modifier.width(150.dp)
        ) {
        DropdownMenuItem(text = {
            Text(text = "New chat")
        }, onClick = onNewChat, leadingIcon = {
            Icon(Icons.Default.Add, contentDescription = "")
        })
        DropdownMenuItem(text = {
            Text(text = "History")
        }, onClick = onHistory, leadingIcon = {
            Icon(Icons.Default.History, contentDescription = "")
        })
        DropdownMenuItem(text = {
            Text(text = "Settings")
        }, onClick = onSetting, leadingIcon = {
            Icon(Icons.Default.Settings, contentDescription = "")
        })

        Divider(
            Modifier
                .width(IntrinsicSize.Max)
                .height(2.dp))

        DropdownMenuItem(text = {
            Text(text = "Share chat")
        }, onClick = onShare, leadingIcon = {
            Icon(Icons.Default.IosShare, contentDescription = "")
        })
        DropdownMenuItem(text = {
            Text(text = "Rename")
        }, onClick = onRename, leadingIcon = {
            Icon(Icons.Default.Edit, contentDescription = "")
        })
        DropdownMenuItem(text = {
            Text(text = "Delete")
        }, onClick = onDelete, leadingIcon = {
            Icon(Icons.Default.DeleteOutline, contentDescription = "")
        }, colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.error,
            leadingIconColor = MaterialTheme.colorScheme.error
        ))
    }
}

@Composable
fun MessageDeleteDialog(
    show: Boolean = false,
    isDeleting: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    if (show) {
        AlertDialog(
            onDismissRequest = {
               if (!isDeleting) {
                   onDismissRequest()
               }
            },
            confirmButton = {
                TextButton(
                    onClick = onDelete, enabled = !isDeleting, colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest, enabled = !isDeleting) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(text = "Delete Conversation")
            },
            text = {
                Text(text = "Are your sure to delete this conversation?")

            }
        )
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