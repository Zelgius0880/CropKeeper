package com.zelgius.cropkeeper

import androidx.navigation.NavController
import com.zelgius.database.model.Seed
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class Navigator(private val navController: NavController? = null) {
    fun navigateToSeedOverview(seed: Seed) {
        navController?.navigate("${Routes.SeedOverview.route}/${seed.seedUid}")
    }

    fun navigateToEditSeed(seed: Seed) {
        navController?.navigate("${Routes.AddOrEditSeed.route}/${seed.seedUid}")
    }

    fun navigateToAddSeed() {
        navController?.navigate(Routes.AddOrEditSeed.route)
    }

    fun popBackStack() {
        navController?.popBackStack()
    }
}



enum class Routes(val route: String){
    AddOrEditSeed("edit_or_add"), Home("home"), SeedOverview("seed"),
}