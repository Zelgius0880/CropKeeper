package com.zelgius.cropkeeper.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import com.zelgius.database.model.Seed

object EditSeedRoute : Routes {
    override val route: String = "edit/{uid}"

    @ExperimentalAnimationApi
    override fun popEnterTransition(scope: AnimatedContentScope<NavBackStackEntry>): EnterTransition =
        enterTransition(scope)

    @ExperimentalAnimationApi
    override fun enterTransition(scope: AnimatedContentScope<NavBackStackEntry>): EnterTransition =
        with(scope) {
            when (initialState.destination.route) {
                HomeRoute.route ->
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Up,
                        animationSpec = tween(Routes.DEFAULT_DELAY)
                    )
                OverviewRoute.route -> slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(Routes.DEFAULT_DELAY)
                )
                else -> fadeIn(tween(Routes.DEFAULT_DELAY))
            }
        }

    @ExperimentalAnimationApi
    override fun popExitTransition(scope: AnimatedContentScope<NavBackStackEntry>): ExitTransition =
        exitTransition(scope)

    @ExperimentalAnimationApi
    override fun exitTransition(scope: AnimatedContentScope<NavBackStackEntry>): ExitTransition =
        with(scope) {
            when (targetState.destination.route) {
                OverviewRoute.route -> /*slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Down,
                    animationSpec = tween(50000)
                )*/
                fadeOut(tween(Routes.DEFAULT_DELAY))
                else -> fadeOut(tween(Routes.DEFAULT_DELAY))
            }
        }


    fun createRoute(seed: Seed) = route.replace("{uid}", seed.seedUid)
}