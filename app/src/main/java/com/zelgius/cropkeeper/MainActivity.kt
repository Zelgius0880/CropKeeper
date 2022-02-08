package com.zelgius.cropkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.zelgius.cropkeeper.routes.AddSeedRoute
import com.zelgius.cropkeeper.routes.EditSeedRoute
import com.zelgius.cropkeeper.routes.HomeRoute
import com.zelgius.cropkeeper.routes.OverviewRoute
import com.zelgius.cropkeeper.routes.Routes
import com.zelgius.cropkeeper.ui.seed.add.AddSeed
import com.zelgius.cropkeeper.ui.seed.add.EditSeed
import com.zelgius.cropkeeper.ui.seed.list.SeedList
import com.zelgius.cropkeeper.ui.seed.list.SeedListViewModel
import com.zelgius.cropkeeper.ui.seed.overview.SeedOverview
import com.zelgius.cropkeeper.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberAnimatedNavController()

                val navigator = Navigator(navController)

                Surface {
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = HomeRoute.route,
                    ) {
                        routeComposable(HomeRoute) {
                            Home(navigator = navigator)
                        }

                        routeComposable(route = OverviewRoute) { backStackEntry ->
                            backStackEntry.arguments?.getString("uid")?.let { uid ->
                                SeedOverview(uid, navigator = navigator)
                            }
                        }

                        routeComposable(route = EditSeedRoute) { backStackEntry ->
                            backStackEntry.arguments?.getString("uid")?.let { uid ->
                                EditSeed(uid = uid, navigator = navigator)
                            }
                        }
                        routeComposable(route = AddSeedRoute) {
                            AddSeed(navigator = navigator)
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.routeComposable(
    route: Routes, content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) =
    composable(
        route = route.route,
        popEnterTransition = { route.popEnterTransition(this) },
        enterTransition = { route.enterTransition(this) },
        exitTransition = { route.exitTransition(this) },
        popExitTransition = { route.popExitTransition(this) },
        content = content
    )

@ExperimentalAnimationApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navigator: Navigator, viewModel: SeedListViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        SmallTopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.my_seeds),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navigator.navigateToAddSeed() }) {
            Icon(
                Icons.TwoTone.Add,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val mapSeeds by viewModel.seedsMap.observeAsState(mapOf())
            SeedList(map = mapSeeds, onSeedClicked = {
                navigator.navigateToSeedOverview(it.seed)
            }, onPhaseClicked = { item, phase ->

            }, onClose = {

            })
        }

    }
}

