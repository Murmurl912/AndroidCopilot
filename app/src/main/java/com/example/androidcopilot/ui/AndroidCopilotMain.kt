package com.example.androidcopilot.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidcopilot.ui.chat.conversation.ChatConversationViewModel
import com.example.androidcopilot.navigation.AppScreens
import com.example.androidcopilot.navigation.Navigator
import com.example.androidcopilot.ui.chat.message.MessageScreen
import com.example.androidcopilot.ui.theme.AndroidCopilotTheme

@Composable
fun AndroidCopilotMain() {

    AndroidCopilotTheme {

        val controller = rememberNavController()
        LaunchedEffect(Unit) {
            Navigator.processNavCommands(controller)
        }
        val conversationViewModel: ChatConversationViewModel = viewModel()
        NavHost(
            navController = controller,
            startDestination = AppScreens.MessageScreen.createRoute(0)
        ) {
            composable(
                route = AppScreens.MessageScreen.name,
                arguments = AppScreens.MessageScreen.args
            ) {
                val conversationId = it.arguments?.getLong(AppScreens.MessageScreen.ArgConversationId)
                    ?: 0
                MessageScreen(
                    conversationId,
                    conversationViewModel = conversationViewModel,
                    messageViewModel = viewModel()
                )
            }
        }
    }

}