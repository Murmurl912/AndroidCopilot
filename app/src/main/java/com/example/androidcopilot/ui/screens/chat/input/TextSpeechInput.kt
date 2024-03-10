package com.example.androidcopilot.ui.screens.chat.input

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import com.example.androidcopilot.speech.ISpeechRecognizer
import com.example.androidcopilot.speech.RecognizerState
import com.example.androidcopilot.speech.android.AndroidSpeechRecognizer
import lerpF
import toPxf

import kotlin.random.Random


class TextSpeechInputState internal constructor(
    private val speechRecognizer: ISpeechRecognizer
) {

    var inputMethod by mutableStateOf(InputMethod.Keyboard)
        internal set

    var isVoiceActivated: Boolean by mutableStateOf(false)
        internal set

    var isSendingMessage by mutableStateOf(false)
        internal set

    var inputValue by mutableStateOf(TextFieldValue())
        internal set

    var sendActionHandler: (InputValue) -> Boolean = { false }
    var stopSendActionHandler: () -> Unit = {}

    internal var isMicPermissionGranted by mutableStateOf(false)
    internal var micPermissionLauncher: ActivityResultLauncher<String>? = null
    internal var requestMicPermissionResultCallback: (Boolean) -> Unit =
        { isMicPermissionGranted = it }

    @OptIn(ExperimentalComposeUiApi::class)
    internal var localSoftwareKeyboardController: SoftwareKeyboardController? = null

    @SuppressLint("ComposableNaming")
    @Composable
    internal fun rememberMicPermission() {
        val context = LocalContext.current
        val currentState = LocalLifecycleOwner.current.lifecycle.currentState
        LaunchedEffect(currentState) {
            if (currentState == Lifecycle.State.RESUMED) {
                isMicPermissionGranted =
                    context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            }
        }
        micPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) {
            requestMicPermissionResultCallback(it)
        }
    }

    @SuppressLint("ComposableNaming")
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    internal fun rememberSoftkeyboard() {
        localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
    }

    internal fun onRequestMicPermission() {
        requestMicPermissionResultCallback = {
            isMicPermissionGranted = it
            if (it) {
                onStartSpeech()
            }
        }
        micPermissionLauncher?.launch(Manifest.permission.RECORD_AUDIO)
    }

    fun onInputTextChange(value: TextFieldValue) {
        inputValue = value
    }

    fun onChangeInputMethod(method: InputMethod) {
        inputMethod = method
        if (inputMethod == InputMethod.Speech) {
            onStartSpeech()
        } else {
            speechRecognizer.stop()
        }
    }

    @SuppressLint("MissingPermission")
    fun onStartSpeech() {
        speechRecognizer.start()
    }

    fun onSpeechStarted() {
        isVoiceActivated = true
    }

    fun onSpeechEnded() {
        isVoiceActivated = false
        val speech = speechRecognizer.speech.value
        if (speech.isNotEmpty()) {
            onStartSend()
        }
    }

    fun onStopSpeech() {
        speechRecognizer.stop()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onStartSend() {
        localSoftwareKeyboardController?.hide()
        val sent = sendActionHandler(
            when (inputMethod) {
                InputMethod.Speech -> {
                    InputValue.SpeechInputValue(speechRecognizer.speech.value)
                }

                InputMethod.Keyboard -> {
                    InputValue.TextInputValue(inputValue.text)
                }
            }
        )
        if (sent) {
            inputValue = TextFieldValue()
        }
    }

    fun onStopSend() {
        stopSendActionHandler()
    }

    enum class InputMethod {
        Speech,
        Keyboard,
    }


}

sealed interface InputValue {

    data class TextInputValue(val text: String) : InputValue

    data class SpeechInputValue(val text: String, val audioFile: String = "") : InputValue

}

fun InputValue.asText(): String {
    return when (this) {
        is InputValue.TextInputValue -> {
            text
        }

        is InputValue.SpeechInputValue -> {
            text
        }
    }
}

@Composable
fun rememberTextSpeechInputState(
    onSend: (InputValue) -> Boolean = { false },
    onStop: () -> Unit = {},
    isSending: Boolean = false,
    textInput: TextFieldValue = TextFieldValue()
): TextSpeechInputState {
    val context = LocalContext.current
    val speechRecognizer = remember {
        AndroidSpeechRecognizer(context)
    }
    val state = remember {
        TextSpeechInputState(
            speechRecognizer
        )
    }
    state.rememberMicPermission()
    state.rememberSoftkeyboard()
    LaunchedEffect(isSending) {
        state.isSendingMessage = isSending
    }
    LaunchedEffect(textInput) {
        state.inputValue = textInput
    }
    val speechState by speechRecognizer.state.collectAsState()
    LaunchedEffect(speechState) {
        when (speechState) {
            RecognizerState.Started -> {
                state.onSpeechStarted()
            }

            RecognizerState.Stopped -> {
                state.onSpeechEnded()
            }

            else -> {

            }
        }
    }
    state.stopSendActionHandler = onStop
    state.sendActionHandler = onSend
    return state
}


@Preview
@Composable
fun TextSpeechInput(
    modifier: Modifier = Modifier,
    textInputLabel: @Composable () -> Unit = {
        Text("Ask me anything?", color = LocalContentColor.current.copy(0.3F))
    },
    inputState: TextSpeechInputState = rememberTextSpeechInputState(),
) {
    Row(
        Modifier
            .padding(vertical = 12.dp)
            .windowInsetsPadding(WindowInsets.ime)
    ) {
        Spacer(Modifier.width(12.dp))

        Row(
            Modifier
                .weight(1F)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(20.dp)
                )
                .defaultMinSize(minHeight = 40.dp)
                .align(Alignment.CenterVertically),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (inputState.inputMethod) {
                TextSpeechInputState.InputMethod.Speech -> {
                    FilledIconToggleButton(
                        checked = false,
                        onCheckedChange = {
                            inputState.onChangeInputMethod(TextSpeechInputState.InputMethod.Keyboard)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Keyboard,
                            contentDescription = "",
                        )
                    }
                    if (inputState.isMicPermissionGranted) {
                        AnimatedVolumeLevelBar(
                            modifier = Modifier
                                .weight(1F)
                                .height(40.dp)
                        )
                    } else {
                        Text(
                            "Speech input require microphone access",
                            maxLines = 1,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1F)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }

                TextSpeechInputState.InputMethod.Keyboard -> {
                    BasicTextField(
                        value = inputState.inputValue,
                        onValueChange = inputState::onInputTextChange,
                        modifier = Modifier
                            .weight(1F)
                            .defaultMinSize(40.dp)
                            .padding(vertical = 6.dp, horizontal = 12.dp),
                        textStyle = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 3,
                    ) {
                        if (inputState.inputValue.text.isEmpty()) {
                            textInputLabel()
                        }
                        it()
                    }
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        when (inputState.inputMethod) {
            TextSpeechInputState.InputMethod.Keyboard -> {
                if (inputState.isSendingMessage) {
                    Box(
                        Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledIconButton(
                            onClick = inputState::onStopSend,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = "",
                            )
                        }
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            strokeWidth = 5.dp
                        )
                    }
                } else if (inputState.inputValue.text.isNotEmpty()) {
                    FilledIconButton(
                        onClick = inputState::onStartSend,
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "",
                        )
                    }
                } else {
                    FilledIconButton(
                        onClick = {
                            inputState.onChangeInputMethod(TextSpeechInputState.InputMethod.Speech)
                        },
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "",
                        )
                    }
                }
            }

            TextSpeechInputState.InputMethod.Speech -> {
                if (inputState.isMicPermissionGranted) {
                    if (inputState.isVoiceActivated) {
                        Box(
                            Modifier.size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FilledIconButton(
                                onClick = {
                                    inputState.onStopSpeech()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Stop,
                                    contentDescription = "",
                                )
                            }
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                strokeWidth = 5.dp
                            )
                        }
                    } else {
                        FilledIconButton(
                            onClick = {
                                inputState.onStartSpeech()
                            },
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "",
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            inputState.onRequestMicPermission()
                        }, modifier = Modifier
                            .align(Alignment.Bottom)
                            .height(40.dp)
                            .defaultMinSize(minWidth = 40.dp)
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "",
                        )
                        Text(text = "Allow")
                    }
                }
            }
        }
        Spacer(Modifier.width(12.dp))
    }

}

@Preview
@Composable
fun AnimatedVolumeLevelBar(
    modifier: Modifier = Modifier,
    barWidth: Dp = 2.dp,
    gapWidth: Dp = 2.dp,
    barColor: Color = MaterialTheme.colorScheme.onPrimary,
    isAnimating: Boolean = true,
) {
    val infiniteAnimation = rememberInfiniteTransition()
    val animations = mutableListOf<State<Float>>()
    val random = remember { Random(100) }

    repeat(15) {
        val durationMillis = random.nextInt(500, 2000)
        animations += infiniteAnimation.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis),
                repeatMode = RepeatMode.Reverse,
            )
        )
    }

    val barWidthFloat by rememberUpdatedState(newValue = barWidth.toPxf())
    val gapWidthFloat by rememberUpdatedState(newValue = gapWidth.toPxf())

    val initialMultipliers = remember {
        mutableListOf<Float>().apply {
            repeat(100) { this += random.nextFloat() }
        }
    }

    val heightDivider by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 6f,
        animationSpec = tween(1000, easing = LinearEasing)
    )

    Canvas(modifier = modifier) {
        val canvasHeight = size.height
        val canvasWidth = size.width
        val canvasCenterY = canvasHeight / 2f

        val count =
            (canvasWidth / (barWidthFloat + gapWidthFloat)).toInt().coerceAtMost(200)
        val animatedVolumeWidth = count * (barWidthFloat + gapWidthFloat)
        var startOffset = (canvasWidth - animatedVolumeWidth) / 2

        val barMinHeight = 0f
        val barMaxHeight = canvasHeight / 2f / heightDivider

        repeat(count) { index ->
            val currentSize = animations[index % animations.size].value
            var barHeightPercent = initialMultipliers[index] + currentSize
            if (barHeightPercent > 1.0f) {
                val diff = barHeightPercent - 1.0f
                barHeightPercent = 1.0f - diff
            }
            val barHeight = lerpF(barMinHeight, barMaxHeight, barHeightPercent)

            drawLine(
                color = barColor,
                start = Offset(startOffset, canvasCenterY - barHeight / 2),
                end = Offset(startOffset, canvasCenterY + barHeight / 2),
                strokeWidth = barWidthFloat,
                cap = StrokeCap.Round,
            )
            startOffset += barWidthFloat + gapWidthFloat
        }
    }
}
