package com.example.androidcopilot.ui.chat.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.ui.theme.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ConversationListDrawer(
    conversationViewModel: ConversationListDrawerViewModel,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(drawerState.currentValue) {
        conversationViewModel.drawerOpenStateFlow.value = drawerState.currentValue == DrawerValue.Open
        if (drawerState.currentValue == DrawerValue.Open) {
            keyboardController?.hide()
        }
    }
    LaunchedEffect(Unit) {
        conversationViewModel.drawerCommands.collect { command ->
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
                    ConversationListDrawerSheet(conversationViewModel = conversationViewModel)
                }, drawerState = drawerState) {
                    content()
                }
            }
        WindowWidthSizeClass.Compact, null -> {
            ModalNavigationDrawer(drawerContent = {
                ConversationListDrawerSheet(conversationViewModel = conversationViewModel)
            }, drawerState = drawerState) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConversationListDrawerSheet(
    modifier: Modifier = Modifier,
    conversationViewModel: ConversationListDrawerViewModel
) {
    val conversation by conversationViewModel.currentConversation.collectAsState()
    val conversations by conversationViewModel.conversations.collectAsState()
    ModalDrawerSheet(modifier) {
        ConversationList(
            conversation = conversation,
            conversations = conversations,
            onClickConversation = conversationViewModel::onSwitchConversation,
            onNewConversation = conversationViewModel::onNewConversation
        )
    }
}

@Composable
internal fun ConversationList(
    modifier: Modifier = Modifier,
    conversation: Conversation? = null,
    conversations: List<Conversation> = emptyList(),
    onClickConversation: (Conversation) -> Unit = {},
    onNewConversation: () -> Unit = {}
) {

    LazyColumn(modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            NewConversationItem(onClick = onNewConversation)
        }
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
            Text(text = conversation.title)
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

    ModalDrawerSheet {
        ConversationList(
            conversations = conversations,
            conversation = conversations[0]
        )
    }

}