package com.example.androidcopilot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.api.chat.ChatClient
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.ui.theme.LocalWindowSizeClass
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AppNavigationDrawer(
    appNavigationDrawerViewModel: AppNavigationDrawerViewModel,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(drawerState.currentValue) {
        appNavigationDrawerViewModel.drawerOpenStateFlow.value = drawerState.currentValue == DrawerValue.Open
        if (drawerState.currentValue == DrawerValue.Open) {
            keyboardController?.hide()
        }
    }
    LaunchedEffect(Unit) {
        appNavigationDrawerViewModel.drawerCommands.collect { command ->
            if (command.consumed) {
                return@collect
            }
            if (command.open && drawerState.targetValue != DrawerValue.Open) {
                drawerState.open()
            } else if (drawerState.targetValue != DrawerValue.Closed){
                drawerState.close()
            }
            command.consume()
        }
    }
    val windowSizeClass = LocalWindowSizeClass.current
    when (windowSizeClass?.widthSizeClass) {
        WindowWidthSizeClass.Expanded,
        WindowWidthSizeClass.Medium -> {
                DismissibleNavigationDrawer(drawerContent = {
                    AppNavigationDrawerSheet(appNavigationDrawerViewModel = appNavigationDrawerViewModel)
                }, drawerState = drawerState) {
                    content()
                }
            }
        WindowWidthSizeClass.Compact, null -> {
            ModalNavigationDrawer(drawerContent = {
                AppNavigationDrawerSheet(appNavigationDrawerViewModel = appNavigationDrawerViewModel)
            }, drawerState = drawerState) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppNavigationDrawerSheet(
    modifier: Modifier = Modifier,
    appNavigationDrawerViewModel: AppNavigationDrawerViewModel
) {
    val conversation by appNavigationDrawerViewModel.currentConversation.collectAsState()
    val conversations by appNavigationDrawerViewModel.conversations.collectAsState()
    ModalDrawerSheet(modifier) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Conversations",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        NewConversationItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            onClick = appNavigationDrawerViewModel::onNewConversation
        )
        Spacer(modifier = Modifier.height(12.dp))
        ConversationList(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 12.dp),
            conversation = conversation,
            conversations = conversations,
            onClickConversation = appNavigationDrawerViewModel::onSwitchConversation,
        )
        Divider(modifier = Modifier.fillMaxWidth().padding(12.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(0.6F)))
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            label = {
               Text(text = "Setting")
            }, selected = false,
            onClick = appNavigationDrawerViewModel::openSetting,
            shape = RoundedCornerShape(6.dp),
            icon = {
                Icon(Icons.Rounded.Settings, contentDescription = "")
            }
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            label = {
                Text(text = "About")
            }, selected = false,
            onClick = appNavigationDrawerViewModel::openSetting,
            shape = RoundedCornerShape(6.dp),
            icon = {
                Icon(Icons.Rounded.Info, contentDescription = "")
            }
        )
    }
}

@Composable
internal fun ConversationList(
    modifier: Modifier = Modifier,
    conversation: Conversation? = null,
    conversations: List<Conversation> = emptyList(),
    onClickConversation: (Conversation) -> Unit = {},
) {

    LazyColumn(modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(conversations.size) {
            val item = conversations[it]
            ConversationItem(
                Modifier,
                item,
                item == conversation) {
                onClickConversation(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConversationItem(
    modifier: Modifier = Modifier,
    conversation: Conversation,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    NavigationDrawerItem(
        modifier = modifier,
        label = {
            val title = if (conversation.title.isEmpty()) {
                "Untitled Conversation"
            } else {
                conversation.title
            }
            Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }, selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        icon = {
            Icon(Icons.Rounded.Message, contentDescription = "")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewConversationItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    NavigationDrawerItem(
        modifier = modifier,
        label = {
            Text(text = "New Conversation")
        }, selected = false,
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        icon = {
            Icon(Icons.Default.Add, contentDescription = "")
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ConversationItemPreview() {
    val conversations = listOf(
        Conversation(
            0,
            "How to make money",
        ),
        Conversation(
            1,
            "How to make money",
        ),
        Conversation(
            2,
            "How to be rich",
        ),
    )

    AppNavigationDrawerSheet(appNavigationDrawerViewModel = AppNavigationDrawerViewModel(
        object : ChatClient {
            override suspend fun send(message: Message): Flow<Message> {
                TODO("Not yet implemented")
            }

            override fun messages(conversationId: Long): Flow<List<Message>> {
                TODO("Not yet implemented")
            }

            override suspend fun conversation(): Conversation {
                TODO("Not yet implemented")
            }

            override suspend fun conversation(id: Long): Flow<Conversation> {
                TODO("Not yet implemented")
            }

            override fun conversations(): Flow<List<Conversation>> {
                TODO("Not yet implemented")
            }

            override suspend fun delete(conversation: Conversation) {
                TODO("Not yet implemented")
            }
        }
    ))

}