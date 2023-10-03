package com.example.androidcopilot.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.androidcopilot.navigation.AppScreens.MessageScreen.ArgConversationId

sealed class AppScreens(
    val route: String,
    val args: List<NamedNavArgument> = emptyList()
) {

    val name = route.appendArgs(args)

    object MessageScreen: AppScreens("messages", args = listOf(
        navArgument("conversationId") {
            type = NavType.LongType
        }
    )) {

        fun createRoute(conversationId: Long): String {
            return name.replace("{${args.first().name}}", "$conversationId")
        }

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
        "${it.name}=${it.name}"
    }
    return "$this$mandatoryArgs$optionalArgs"
}