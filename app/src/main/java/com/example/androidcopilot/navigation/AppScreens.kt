package com.example.androidcopilot.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.navigation.AppScreens.MessageScreen.ArgConversationId
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

sealed class AppScreens(
    val route: String,
    val args: List<NamedNavArgument> = emptyList()
) {

    val name = route.appendArgs(args)

    object MessageScreen: AppScreens("messages", args = listOf(
        navArgument("conversationId") {
            type = NavType.LongType
        },
        navArgument("send") {
            type = NavType.StringType
            defaultValue = ""
        },
        navArgument("attachments") {
            type = NavType.LongArrayType
            defaultValue = LongArray(0)
        }
    )) {

        /**
         * Message is encoded with base64
         */
        @OptIn(ExperimentalEncodingApi::class)
        fun createRoute(conversationId: Long,
                        send: String? = null,
                        attachments: List<Long> = emptyList()): String {
            return name.replace("{${args[0].name}}", "$conversationId")
                .let {
                    if (send != null) {
                        val encoded = Base64.encode(send.toByteArray())
                        it.replace("{${args[1].name}}", "$encoded")
                    } else {
                        it
                    }
                }
                .let {
                    if (send != null) {
                        it.replace("{${args[2].name}}", "${attachments.joinToString(",")}")
                    } else {
                        it
                    }
                }
        }


        const val ArgSendMessage = "send"
        const val ArgSendAttachment = "attachments"
        const val ArgConversationId = "conversationId"
    }

    object HomeScreen: AppScreens("home")

    object SettingScreen: AppScreens("setting")

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