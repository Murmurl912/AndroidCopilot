package com.example.androidcopilot.ui.screens.main

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.ui.theme.LocalWindowSizeClass


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainDrawer(
    viewModel: AppMainViewModel,
    body: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val windowSizeClass = LocalWindowSizeClass.current
    val conversationsState by viewModel.conversationState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.drawerCommand.collect {
            when (it) {
                is DrawerCommand.Close -> {
                    drawerState.close()
                }

                is DrawerCommand.Open -> {
                    drawerState.open()
                }
            }
        }
    }

    when (windowSizeClass?.widthSizeClass) {
        WindowWidthSizeClass.Expanded,
        WindowWidthSizeClass.Medium -> {
            DismissibleNavigationDrawer(drawerContent = {
                DrawerContent(
                    viewModel = viewModel,
                    conversations = conversationsState.conversations,
                    currentConversation = conversationsState.conversation
                )
            }, drawerState = drawerState) {
                body()
            }
        }

        WindowWidthSizeClass.Compact, null -> {
            ModalNavigationDrawer(drawerContent = {
                DrawerContent(
                    viewModel = viewModel,
                    conversations = conversationsState.conversations,
                    currentConversation = conversationsState.conversation
                )
            }, drawerState = drawerState) {
                body()
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DrawerContent(
    viewModel: AppMainViewModel,
    conversations: List<Conversation>,
    currentConversation: Conversation?
) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Conversations",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        NewConversationItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            onClick = viewModel::onNewConversation
        )
        Spacer(modifier = Modifier.height(12.dp))
        ConversationList(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 12.dp),
            currentConversation = currentConversation,
            conversations = conversations,
            onClickConversation = viewModel::onSwitchConversation,
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(0.6F))
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = 12.dp),
            label = {
                Text(text = "Setting")
            }, selected = false,
            onClick = viewModel::onOpenSetting,
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
            onClick = viewModel::onOpenAbout,
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
    currentConversation: Conversation? = null,
    conversations: List<Conversation> = emptyList(),
    onClickConversation: (Conversation) -> Unit = {},
) {

    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(conversations.size) {
            val item = conversations[it]
            ConversationItem(
                Modifier,
                item,
                item == currentConversation
            ) {
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
            val title = conversation.title.ifEmpty {
                "Untitled Conversation"
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
