package com.zelgius.cropkeeper

import androidx.navigation.NavController
import com.zelgius.cropkeeper.routes.AddSeedRoute
import com.zelgius.cropkeeper.routes.EditSeedRoute
import com.zelgius.cropkeeper.routes.OverviewRoute
import com.zelgius.cropkeeper.routes.Routes
import com.zelgius.database.model.Seed

class Navigator(private val navController: NavController? = null) {
    fun navigateToSeedOverview(seed: Seed) {
        navController?.navigate(OverviewRoute.createRoute(seed))
    }

    fun navigateToEditSeed(seed: Seed) {
        navController?.navigate(EditSeedRoute.createRoute(seed))
    }

    fun navigateToAddSeed() {
        navController?.navigate(AddSeedRoute.route)
    }

    fun popBackStack() {
        navController?.popBackStack()
    }
}


