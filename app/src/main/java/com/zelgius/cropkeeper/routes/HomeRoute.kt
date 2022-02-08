package com.zelgius.cropkeeper.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.zelgius.cropkeeper.routes.Routes.Companion.DEFAULT_DELAY

object HomeRoute : Routes {
    override val route: String = "home"
    @ExperimentalAnimationApi
    override fun popEnterTransition(scope: AnimatedContentScope<NavBackStackEntry>): EnterTransition =
        enterTransition(scope)

    @ExperimentalAnimationApi
    override fun enterTransition(scope: AnimatedContentScope<NavBackStackEntry>): EnterTransition =
        with(scope) {
            when (initialState.destination.route) {
                OverviewRoute.route, AddSeedRoute.route -> fadeIn(tween(DEFAULT_DELAY))
                else -> slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(DEFAULT_DELAY)
                )
            }
        }

    @ExperimentalAnimationApi
    override fun popExitTransition(scope: AnimatedContentScope<NavBackStackEntry>): ExitTransition =
        exitTransition(scope)

    @ExperimentalAnimationApi
    override fun exitTransition(scope: AnimatedContentScope<NavBackStackEntry>): ExitTransition =
        fadeOut(tween(DEFAULT_DELAY))
}