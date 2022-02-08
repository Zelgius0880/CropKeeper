package com.zelgius.cropkeeper.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavBackStackEntry

sealed interface Routes {
    companion object {
        const val DEFAULT_DELAY = 700
    }

    val route: String
    @ExperimentalAnimationApi
    fun popEnterTransition(scope: AnimatedContentScope<NavBackStackEntry>): EnterTransition?
    @ExperimentalAnimationApi
    fun enterTransition(scope: AnimatedContentScope<NavBackStackEntry>): EnterTransition?
    @ExperimentalAnimationApi
    fun popExitTransition(scope: AnimatedContentScope<NavBackStackEntry>): ExitTransition?
    @ExperimentalAnimationApi
    fun exitTransition(scope: AnimatedContentScope<NavBackStackEntry>): ExitTransition?
}