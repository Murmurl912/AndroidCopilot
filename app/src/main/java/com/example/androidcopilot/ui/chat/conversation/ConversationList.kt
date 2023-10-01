package com.example.androidcopilot.ui.chat.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.chat.model.ChatConversation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListDrawerSheet(
    modifier: Modifier = Modifier,
    conversationViewModel: ChatConversationViewModel
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
fun ConversationList(
    modifier: Modifier = Modifier,
    conversation: ChatConversation? = null,
    conversations: List<ChatConversation> = emptyList(),
    onClickConversation: (ChatConversation) -> Unit = {},
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
            ConversationItem(Modifier,
                item,
                item == conversation) {
                onClickConversation(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationItem(
    modifier: Modifier = Modifier,
    conversation: ChatConversation,
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
fun NewConversationItem(
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
        ChatConversation(
            0,
            "How to make money",
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
        ),
        ChatConversation(
            1,
            "How to make money",
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
        ),
        ChatConversation(
            2,
            "How to be rich",
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
        ),
    )

    ModalDrawerSheet {
        ConversationList(
            conversations = conversations,
            conversation = conversations[0]
        )
    }

}