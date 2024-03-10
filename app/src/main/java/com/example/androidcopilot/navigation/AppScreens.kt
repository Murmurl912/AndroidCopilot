package com.example.androidcopilot.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed interface AppScreens {

    val name: String

    val args: List<NamedNavArgument>

    object MainSplashScreen : AppScreens {
        override val name: String = "splash"
        override val args: List<NamedNavArgument> = emptyList()
    }

    object MessageScreen : AppScreens {


        const val ArgConversationId = "conversationId"


        override val args: List<NamedNavArgument> = listOf(
            navArgument(ArgConversationId) {
                type = NavType.LongType
            },
        )

        override val name: String = "messages".appendArgs(args)

        fun createRoute(conversationId: Long): String {
            return name.replace("{$ArgConversationId}", "$conversationId")
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
        } ?: ""
    val optionalArgs = args.filter {
        it.argument.defaultValue != null
    }.takeIf(List<NamedNavArgument>::isNotEmpty)?.joinToString(separator = "&", prefix = "?") {
        "${it.name}={${it.name}}"
    } ?: ""
    return "$this$mandatoryArgs$optionalArgs"
}