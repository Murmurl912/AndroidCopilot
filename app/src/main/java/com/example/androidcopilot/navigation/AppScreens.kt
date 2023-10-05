package com.example.androidcopilot.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgument
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.navigation.AppScreens.MessageScreen.ArgConversationId
import com.example.androidcopilot.ui.chat.input.InputValue
import com.example.androidcopilot.ui.chat.input.TextSpeechInputState
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

sealed interface AppScreens {

    val name: String

    val args: List<NamedNavArgument>

    object MessageScreen: AppScreens {


        const val ArgSendMessage = "message"
        const val ArgSendAttachment = "attachments"
        const val ArgConversationId = "conversationId"
        const val ArgInputMethod = "inputMethod"


        override val args: List<NamedNavArgument> = listOf(
            navArgument(ArgSendMessage) {
                type = NavType.StringType
            },
            navArgument(ArgSendAttachment) {
                type = NavType.LongArrayType
                defaultValue = LongArray(0)
            },
            navArgument(ArgConversationId) {
                type = NavType.LongType
            },
            navArgument(ArgInputMethod) {
                type = NavType.StringType
                defaultValue = TextSpeechInputState.InputMethod.Keyboard.name
            }
        )

        override val name: String = "messages".appendArgs(args)

        /**
         * Message is encoded with base64
         */
        @OptIn(ExperimentalEncodingApi::class)
        fun createRoute(conversationId: Long,
                        send: String? = "",
                        attachments: List<Long> = emptyList(),
                        inputMethod: TextSpeechInputState.InputMethod? = null
        ): String {
            return name.replace("{$ArgConversationId}", "$conversationId")
                .let {
                    if (!send.isNullOrEmpty()) {
                        val encodedMessage = Base64.encode(send.toByteArray())
                        it.replace("{$ArgSendMessage}", encodedMessage)
                    } else {
                        it.replace("{$ArgSendMessage}", "")
                    }
                }
                .let {
                    if (attachments.isNotEmpty()) {
                        it.replace("{$ArgSendAttachment}", attachments.joinToString(","))
                    } else {
                        it.replace("{$ArgSendAttachment}", "")
                    }
                }
                .let {
                    if (inputMethod != null) {
                        it.replace("{$ArgInputMethod}", inputMethod.name)
                    } else {
                        it.replace("{$ArgInputMethod}", "")
                    }
                }
        }
    }

    object HomeScreen: AppScreens {
        override val name: String = "home"
        override val args: List<NamedNavArgument> = emptyList()
    }

    object SettingScreen: AppScreens {
        override val name: String = "setting"
        override val args: List<NamedNavArgument> = emptyList()
    }

}

private fun String.appendArgs(args: List<NamedNavArgument>): String {

    val mandatoryArgs = args.filter { it.argument.defaultValue == null }
        .takeIf(List<NamedNavArgument>::isNotEmpty)?.joinToString(separator = "/", prefix = "/") {
            "{${it.name}}"
        }
    val optionalArgs = args.filter {
        it.argument.defaultValue != null
    }.takeIf(List<NamedNavArgument>::isNotEmpty)?.joinToString(separator = "&", prefix = "?") {
        "${it.name}={${it.name}}"
    }
    return "$this$mandatoryArgs$optionalArgs"
}