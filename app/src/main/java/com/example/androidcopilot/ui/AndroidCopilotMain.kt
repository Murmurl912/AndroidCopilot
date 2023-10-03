package com.example.androidcopilot.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidcopilot.ui.chat.conversation.ConversationListDrawerViewModel
import com.example.androidcopilot.navigation.AppScreens
import com.example.androidcopilot.navigation.Navigator
import com.example.androidcopilot.ui.chat.home.HomeScreen
import com.example.androidcopilot.ui.chat.message.MessageScreen
import com.example.androidcopilot.ui.theme.AndroidCopilotTheme

@Composable
fun AndroidCopilotMain() {

    AndroidCopilotTheme {

        val controller = rememberNavController()
        LaunchedEffect(Unit) {
            Navigator.processNavCommands(controller)
        }
        val conversationViewModel: ConversationListDrawerViewModel = hiltViewModel()
        NavHost(
            navController = controller,
            startDestination = AppScreens.HomeScreen.name
        ) {

            composable(route = AppScreens.HomeScreen.name) {
                HomeScreen(
                    homeViewModel = hiltViewModel(),
                    conversationViewModel = conversationViewModel
                )
            }

            composable(
                route = AppScreens.MessageScreen.name,
                arguments = AppScreens.MessageScreen.args
            ) {
                val conversationId = it.arguments?.getLong(AppScreens.MessageScreen.ArgConversationId)
                    ?: 0
                MessageScreen(
                    conversationId,
                    conversationViewModel = conversationViewModel,
                    messageViewModel = hiltViewModel()
                )
            }
        }
    }

}