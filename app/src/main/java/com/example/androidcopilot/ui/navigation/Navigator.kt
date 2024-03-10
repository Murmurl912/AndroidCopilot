package com.example.androidcopilot.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onSubscription

object Navigator {

    private val commands = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = Int.MAX_VALUE)
    private val navControllerState = MutableStateFlow<NavController?>(null)

    fun navigate(route: String, optionBuilder: (NavOptionsBuilder.() -> Unit)? = null) {
        commands.tryEmit(
            NavigationCommand.NavigateToRoute(
                route, optionBuilder?.let {
                    navOptions(it)
                }
            )
        )
    }

    suspend fun processNavCommands(navController: NavController) {
        commands
            .onSubscription {
                navControllerState.value = navController
            }
            .onCompletion {
                navControllerState.value = null
            }
            .collect {
                when (it) {
                    is NavigationCommand.NavigateUp -> {
                        navController.navigateUp()
                    }
                    is NavigationCommand.NavigateUpWithResult<*> -> {
                        val backStackEntry = it.route?.let {
                            navController.getBackStackEntry(it)
                        } ?: navController.previousBackStackEntry
                        backStackEntry?.savedStateHandle?.set(
                            it.key,
                            it.result
                        )
                        it.route?.let {
                            navController.popBackStack(
                                it, false
                            )
                        }?: {
                            navController.navigateUp()
                        }
                    }
                    is NavigationCommand.NavigateToRoute -> {
                        navController.navigate(it.route, it.options)
                    }
                    is NavigationCommand.PopUpToRoute -> {
                        navController.popBackStack(
                            it.route,
                            it.inclusive
                        )
                    }
                }
            }
    }
}

sealed class NavigationCommand {

    object NavigateUp: NavigationCommand()

    data class NavigateToRoute(
        val route: String,
        val options: NavOptions?,
    ): NavigationCommand()

    data class NavigateUpWithResult<T>(
        val key: String,
        val result: T,
        val route: String?
    ): NavigationCommand()

    data class PopUpToRoute(val route: String, val inclusive: Boolean) : NavigationCommand()
}

