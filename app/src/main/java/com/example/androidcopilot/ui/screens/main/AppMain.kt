package com.example.androidcopilot.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidcopilot.ui.navigation.AppScreens
import com.example.androidcopilot.ui.navigation.Navigator
import com.example.androidcopilot.ui.screens.chat.message.ConversationViewModel
import com.example.androidcopilot.ui.screens.chat.message.MessageScreen
import com.example.androidcopilot.ui.screens.setting.AppSettingScreen
import com.example.compose.AppTheme


@Composable
fun AndroidCopilotMain() {
    AppTheme {
        val controller: NavHostController = rememberNavController()
        LaunchedEffect(Unit) {
            Navigator.processNavCommands(controller)
        }
        val viewModel: AppMainViewModel = hiltViewModel()
        MainDrawer(viewModel = viewModel) {
            AndroidCopilotNavigation(controller = controller, viewModel = viewModel)
        }
    }

}


@Composable
internal fun AndroidCopilotNavigation(
    controller: NavHostController,
    viewModel: AppMainViewModel,
) {

    NavHost(
        navController = controller,
        startDestination = AppScreens.MainSplashScreen.name
    ) {
        composable(route = AppScreens.MainSplashScreen.name, arguments = AppScreens.MainSplashScreen.args) {
            val state by viewModel.appState.collectAsState()
            when (state) {
                is AppState.AppInitialized -> {
                    LaunchedEffect(state) {
                        val conversationId = (state as AppState.AppInitialized).conversationId
                        Navigator.navigate(
                            AppScreens.ConversationScreen.createRoute(
                                conversationId
                            )
                        ) {
                            popUpTo(AppScreens.MainSplashScreen.name) {
                                inclusive = true
                            }
                        }
                    }
                }
                is AppState.AppInitializing, is AppState.AppIdle -> {
                    Surface {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
        composable(
            route = AppScreens.ConversationScreen.name,
            arguments = AppScreens.ConversationScreen.args
        ) {
            MessageScreen(
                mainViewModel = viewModel,
                conversationViewModel = hiltViewModel<ConversationViewModel>().apply {
                    val id = AppScreens.ConversationScreen.getConversationId(it.arguments)
                    if (id != null) {
                        switchConversation(id)
                    }
                }
            )
        }

        composable(route = AppScreens.SettingScreen.name) {
            AppSettingScreen()
        }
    }

}
