package com.zelgius.database

enum class SeedGroup(val order: Int) {
    Actual(1), Planned(2), Ended(0)
}