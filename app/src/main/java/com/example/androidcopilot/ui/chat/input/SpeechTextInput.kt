package com.example.androidcopilot.ui.chat.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


enum class SpeechState {
   StateStopped,
   StateStarted,
}
class SpeechInputState {

    private val speechState = mutableStateOf(SpeechState.StateStopped)
    val state: SpeechState by speechState


    fun isRecognizing(): Boolean {
        return state == SpeechState.StateStarted
    }

    fun start() {
        speechState.value = SpeechState.StateStarted
    }

    fun stop() {
        speechState.value = SpeechState.StateStopped
    }
}


@Preview
@Composable
fun SpeechInput(
    speechInputState: SpeechInputState = remember {
        SpeechInputState()
    }
) {


    Column(Modifier) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1F))
            Text("Try say something")
            Spacer(modifier = Modifier.weight(1F))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Add, "")
            }
            Spacer(modifier = Modifier.width(20.dp))
            if (speechInputState.isRecognizing()) {
                Box(
                    Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .clip(CircleShape)
                ) {
                    IconButton(onClick = {
                        speechInputState.stop()
                    }) {
                        Icon(Icons.Default.Stop, contentDescription = "")
                    }
                    CircularProgressIndicator(Modifier.size(40.dp))
                }
            } else {
                IconButton(onClick = {
                    speechInputState.start()
                }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Mic, "")
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Keyboard, "")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

}