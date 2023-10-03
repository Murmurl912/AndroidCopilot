package com.example.androidcopilot.ui.chat.input

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardAlt
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.ui.keyboard.KeyboardHeights
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.linc.audiowaveform.AudioWaveform
import kotlinx.coroutines.android.awaitFrame


enum class SendState {
    StateIdle,
    StateSending,
    StateError,
    StatePause,
}

enum class InputMode {
    TextInput,
    VoiceInput,
    PickAttachment,
}

data class MessageInputState(
    val input: String,
    val mode: InputMode,
    val sendState: SendState,
    val attachments: List<Attachment>,
)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    state: MessageInputState,
    label: @Composable () -> Unit = {
        Text("Ask me anything", color = MaterialTheme.colorScheme.onSurface.copy(0.3F))
    },
    onModeChange: (InputMode) -> Unit = {},
    onInputChange: (String) -> Unit = {},
    onSendMessage: (String, List<Attachment>) -> Unit = { _, _ -> },
    onPause: () -> Unit = {},
    onRetry: () -> Unit = {},
    onResume: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier) {
        Row(Modifier.padding(vertical = 12.dp)) {
            Spacer(Modifier.width(12.dp))

            Row(
                Modifier
                    .weight(1F)
                    .background(
                        Color.Black.copy(alpha = 0.1F),
                        RoundedCornerShape(20.dp)
                    )
                    .defaultMinSize(minHeight = 40.dp)
                    .align(Alignment.CenterVertically),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (state.mode == InputMode.PickAttachment) {
                    IconButton(
                        onClick = {
                            onModeChange(InputMode.TextInput)
                            keyboardController?.show()
                        },
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = ""
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            onModeChange(InputMode.PickAttachment)
                        },
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = ""
                        )
                    }
                }

                val source = remember {
                    MutableInteractionSource()
                }

                BasicTextField(
                    value = state.input,
                    onValueChange = onInputChange,
                    modifier = Modifier
                        .weight(1F)
                        .defaultMinSize(40.dp),
                    maxLines = 3,
                    interactionSource = source
                ) {
                    if (state.input.isEmpty()) {
                        label()
                    }
                }
                IconButton(
                    onClick = {
                        onModeChange(InputMode.VoiceInput)
                    },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardVoice,
                        contentDescription = ""
                    )
                }
                val isPressed by source.collectIsPressedAsState()
                LaunchedEffect(isPressed) {
                    if (isPressed) {
                        onModeChange(InputMode.TextInput)
                        keyboardController?.show()
                    }
                }
            }

            Spacer(Modifier.width(12.dp))
            when (state.sendState) {
                SendState.StateIdle -> {
                    IconButton(
                        onClick = {
                            onSendMessage(state.input, state.attachments)
                                  },
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .clip(CircleShape),
                        enabled = state.input.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                SendState.StateSending -> {
                    Box(
                        Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .clip(CircleShape)
                    ) {
                        IconButton(onClick = onPause) {
                            Icon(Icons.Default.Stop, contentDescription = "")
                        }
                        CircularProgressIndicator(Modifier.size(40.dp))
                    }
                }
                SendState.StatePause -> {
                    if (state.input.isEmpty()) {
                        IconButton(
                            onClick = onResume,
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                                .clip(CircleShape),
                            enabled = state.input.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                      onSendMessage(state.input, state.attachments)
                                      },
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                .clip(CircleShape),
                            enabled = state.input.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                SendState.StateError -> {
                    if (state.input.isEmpty()) {
                        IconButton(onClick = onRetry,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                                .clip(CircleShape),
                            ) {
                            Icon(Icons.Default.Refresh, contentDescription = "")
                        }
                    } else {
                        IconButton(
                            onClick = {
                                      onSendMessage(state.input, state.attachments)
                                      },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                )
                                .clip(CircleShape),
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "")
                        }
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
        }

        var inputPanelHeight by remember {
            mutableStateOf(0.dp)
        }
        val density = LocalDensity.current
        val imeInsets = WindowInsets.ime
        val systemInsets = WindowInsets.systemBars
        val imeHeight = with(density) {
            imeInsets.getBottom(density).toDp()
        }
        val systemHeight = with(density) {
            systemInsets.getBottom(density).toDp()
        }
        val (imeState, imeMaxHeight) = KeyboardHeights.rememberKeyboardHeight()
        println("ime state: $imeState $imeMaxHeight $imeHeight")
        val expectedPanelHeight = remember(state.mode, imeHeight, systemHeight, imeState, imeMaxHeight) {
            when (state.mode) {
                InputMode.TextInput -> {
                    when (imeState) {
                        KeyboardHeights.ImeState.ImeShown -> {
                            (imeHeight - systemHeight).coerceAtLeast(0.dp)
                        }
                        KeyboardHeights.ImeState.ImeHidden -> {
                            if (inputPanelHeight > imeHeight - systemHeight) {
                                inputPanelHeight
                            } else {
                                (imeHeight - systemHeight).coerceAtLeast(0.dp)
                            }
                        }
                        KeyboardHeights.ImeState.ImeShowing -> {
                            if (inputPanelHeight > imeHeight - systemHeight) {
                                inputPanelHeight
                            } else {
                                (imeHeight - systemHeight).coerceAtLeast(0.dp)
                            }
                        }
                        KeyboardHeights.ImeState.ImeHiding -> {
                            (imeHeight - systemHeight).coerceAtLeast(0.dp)
                        }
                    }
                }
                InputMode.VoiceInput, InputMode.PickAttachment -> {
                    // ime will hide or show
                    imeMaxHeight
                }
            }
        }
        Box(modifier = Modifier
            .onSizeChanged {
                with(density) {
                    inputPanelHeight = it.height.toDp()
                }
            }
            .height(expectedPanelHeight)
        ) {
            when (state.mode) {
                InputMode.VoiceInput -> {
                    LaunchedEffect(Unit) {
                        awaitFrame()
                        keyboardController?.hide()
                    }
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {

                        val audioRecordPermission =
                            rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
                        if (audioRecordPermission.status.isGranted) {
                            var waveformProgress by remember { mutableStateOf(0F) }

                            AudioWaveform(
                                progress = waveformProgress,
                                amplitudes = listOf(
                                    1, 2, 3, 4, 2, 3, 4, 3, 2, 3, 2, 1
                                ),
                                spikePadding = 2.dp,
                                onProgressChange = { waveformProgress = it },
                                modifier = Modifier.height(20.dp)
                            )
                        } else  {
                            Text(text = "To use voice input, you need allow app to access your microphone.")
                            Button(onClick = {
                                audioRecordPermission.launchPermissionRequest()
                            }) {
                                Text(text = "Grant Microphone Access")
                            }
                        }

                        Spacer(modifier = Modifier.weight(1F))
                        IconButton(onClick = {
                            onModeChange(InputMode.TextInput)
                            keyboardController?.show()
                        }) {
                            Icon(Icons.Default.KeyboardAlt, contentDescription = "")
                        }
                    }
                }
                InputMode.PickAttachment -> {
                    LaunchedEffect(Unit) {
                        keyboardController?.hide()
                    }
                    Box(Modifier.fillMaxSize()) {
                        Text("Select Input")
                    }
                }
                InputMode.TextInput -> {
                }
            }
        }
    }
}


@Preview
@Composable
fun MixedMessageInputPreivew() {
   Column {
       val state  by remember {
           mutableStateOf(
               MessageInputState(
               "", InputMode.TextInput, SendState.StateIdle, emptyList())
           )
       }
       MessageInput(
           state = state
       )
   }
}