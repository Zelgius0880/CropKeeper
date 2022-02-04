package com.zelgius.cropkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zelgius.cropkeeper.ui.seed.add.AddSeed
import com.zelgius.cropkeeper.ui.seed.add.EditSeed
import com.zelgius.cropkeeper.ui.seed.list.SeedList
import com.zelgius.cropkeeper.ui.seed.list.SeedListViewModel
import com.zelgius.cropkeeper.ui.seed.overview.SeedOverview
import com.zelgius.cropkeeper.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberNavController()

                val navigator = Navigator(navController)

                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Home.route,
                    ) {
                        composable(Routes.Home.route) {
                            Home(navigator = navigator)
                        }

                        composable("${Routes.SeedOverview.route}/{uid}") { backStackEntry ->
                            backStackEntry.arguments?.getString("uid")?.let { uid ->
                                SeedOverview(uid, navigator = navigator)
                            }
                        }

                        composable(route = "${Routes.AddOrEditSeed.route}/{uid}") { backStackEntry ->
                            backStackEntry.arguments?.getString("uid")?.let { uid ->
                                EditSeed(uid = uid, navigator = navigator)
                            }
                        }
                        composable(Routes.AddOrEditSeed.route) {
                            AddSeed(navigator = navigator)
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CircularRevealEffect(
    visible: Boolean,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    offset: Offset = Offset(0.5f, 0.5f),
    content: @Composable () -> Unit
) {

    Surface(
        Modifier
            .circularReveal(visible, offset)
            .fillMaxSize(),
        color = color
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun EnterTransitionPreview() {
    AppTheme(true) {
        CircularRevealEffect(visible = true) {
            Box(modifier = Modifier.background(Color.Magenta))
        }
    }
}


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

