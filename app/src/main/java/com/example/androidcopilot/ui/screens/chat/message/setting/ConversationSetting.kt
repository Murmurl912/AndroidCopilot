package com.example.androidcopilot.ui.screens.chat.message.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class ConversationSetting {
}

@Preview
@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
) {

    Row(modifier) {


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ConversationSettingDesign() {

    Column(
        Modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState())) {

        Spacer(modifier = Modifier.height(12.dp))
        Text("Conversation Info")
        Spacer(modifier = Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = "Untitled Conversation",
                    onValueChange = {},
                    label = {
                        Text("Name")
                    },
                    placeholder = {
                        Text("Conversation Name")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Message Count: ", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.weight(1F))
                    Text(text = "100")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Token Count: ", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.weight(1F))
                    Text(text = "100")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Memory Size: ", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.weight(1F))
                    Text(text = "100")
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Model")
        Spacer(modifier = Modifier.height(12.dp))
        Card {
            Column(Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = "GPT-3.5",
                    onValueChange = {},
                    label = {
                        Text("LLM Model")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = {
                        Text("Instruction")
                    },
                    placeholder = {
                        Text("Tell Android Copilot how to response")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Memory")
        Spacer(modifier = Modifier.height(12.dp))
        Card {
            Column(Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = "4k token",
                    onValueChange = {},
                    label = {
                        Text("Memory Size")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = "3k token",
                    onValueChange = {},
                    label = {
                        Text("Conversation Window Size")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = "1k token",
                    onValueChange = {},
                    label = {
                        Text("Conversation Summary Size")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Function Call")
        Spacer(modifier = Modifier.height(12.dp))
        Card {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(text = "Enable function call", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.weight(1F))
                    Switch(checked = false, onCheckedChange = {})
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = false, onCheckedChange = {})
                    Text(text = "Allow open app",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = false, onCheckedChange = {})
                    Text(text = "Allow change system setting",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row {
                    Checkbox(checked = false, onCheckedChange = {})
                    Text(text = "Allow access location",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row {
                    Checkbox(checked = false, onCheckedChange = {})
                    Text(text = "Allow access storage",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Speech And Input")
        Card {
            Column(Modifier.padding(12.dp)) {

            }
        }
    }
}