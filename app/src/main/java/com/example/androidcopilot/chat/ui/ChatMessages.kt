package com.example.androidcopilot.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.chat.model.ChatMessage


@Composable
fun ChatMessageList(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage> = emptyList(),
) {

    LazyColumn(modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(messages.size, {messages[it].id()}, {
            messages[it]::class.simpleName
        }) {
            when (val message = messages[it]) {
                is ChatMessage.HumanMessage -> {
                    HumanMessageItem(
                        Modifier.fillMaxWidth(),
                        message = message
                    )

                }
                is ChatMessage.AssistantMessage -> {
                    AssistantMessageItem(
                        Modifier.fillMaxWidth(),
                        message = message
                    )

                }
                is ChatMessage.SystemMessage -> {
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
    message: ChatMessage.AssistantMessage
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
                Text(message.message)
            }
        }
        Spacer(modifier = Modifier.weight(0.1F))
    }
}


@Composable
fun HumanMessageItem(
    modifier: Modifier = Modifier,
    message: ChatMessage.HumanMessage
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
                Text(message.message)
            }
        }
    }
}

@Composable
fun SystemMessageItem(
    modifier: Modifier = Modifier,
    message: ChatMessage.SystemMessage
) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        Text(message.message, Modifier.padding(12.dp))
    }
}


@Preview
@Composable
fun MessagesPreview() {
    ChatMessageList(
        messages = listOf(
            ChatMessage.AssistantMessage(0, "你不要吓我"),
            ChatMessage.HumanMessage(1, "你不要吓我"),
            ChatMessage.SystemMessage(2, "你不要吓我")
        )
    )
}
