package com.example.androidcopilot.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidcopilot.navigation.AppScreens
import com.example.androidcopilot.navigation.Navigator
import com.example.androidcopilot.ui.home.HomeScreen
import com.example.androidcopilot.ui.chat.message.MessageScreen
import com.example.androidcopilot.ui.chat.message.MessageViewModel
import com.example.androidcopilot.ui.setting.AppSettingScreen
import com.example.compose.AppTheme

@Composable
fun AndroidCopilotMain() {

    AppTheme {

        val controller = rememberNavController()
        LaunchedEffect(Unit) {
            Navigator.processNavCommands(controller)
        }
        val appNavigationDrawerViewModel: AppNavigationDrawerViewModel = hiltViewModel()
        AppNavigationDrawer(appNavigationDrawerViewModel = appNavigationDrawerViewModel) {
            NavHost(
                navController = controller,
                startDestination = AppScreens.HomeScreen.name
            ) {

                composable(route = AppScreens.HomeScreen.name) {
                    HomeScreen(
                        homeViewModel = hiltViewModel(),
                        conversationViewModel = appNavigationDrawerViewModel
                    )
                }

                composable(
                    route = AppScreens.MessageScreen.name,
                    arguments = AppScreens.MessageScreen.args
                ) {
                    val conversation = it.arguments?.getLong(AppScreens.MessageScreen.ArgConversationId)
                        ?: 0
                    val message = it.arguments?.getString(AppScreens.MessageScreen.ArgSendMessage)
                    val attachments = it.arguments?.getLongArray(AppScreens.MessageScreen.ArgSendAttachment)
                    val viewModel: MessageViewModel = hiltViewModel()
                    LaunchedEffect(Unit) {
                        if (conversation != 0L) {
                            viewModel.conversation(conversation)
                        }
                        if (!message.isNullOrEmpty()) {
                            viewModel.sendWithAttachmentId(message, attachments?.toList()?: emptyList<Long>())
                        }
                    }
                    MessageScreen(
                        appNavigationDrawerViewModel = appNavigationDrawerViewModel,
                        messageViewModel = hiltViewModel()
                    )
                }

                composable(route = AppScreens.SettingScreen.name) {
                    AppSettingScreen()
                }
            }
        }

    }

}