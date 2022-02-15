package com.zelgius.cropkeeper.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavBackStackEntry

object LicenceRoute : Routes {
    override val route: String = "licence"
    @ExperimentalAnimationApi
    override fun popEnterTransition(scope: AnimatedContentScope<NavBackStackEntry>): EnterTransition =
        EnterTransition.None

    @ExperimentalAnimationApi
    override fun enterTransition(scope: AnimatedContentScope<NavBackStackEntry>): EnterTransition =
        EnterTransition.None

    @ExperimentalAnimationApi
    override fun popExitTransition(scope: AnimatedContentScope<NavBackStackEntry>): ExitTransition =
        ExitTransition.None

    @ExperimentalAnimationApi
    override fun exitTransition(scope: AnimatedContentScope<NavBackStackEntry>): ExitTransition =
        ExitTransition.None
}