package com.zelgius.database.model

import androidx.room.*
import java.time.LocalDate
import java.util.*

@Entity(
    tableName = "seed",
    foreignKeys = [ForeignKey(
        entity = Vegetable::class,
        parentColumns = ["vegetable_uid"],
        childColumns = ["vegetable_uid"]
    ), ForeignKey(
        entity = Period::class,
        parentColumns = ["period_uid"],
        childColumns = ["actual_period_uid"]
    )],
    indices = [Index("vegetable_uid"), Index("actual_period_uid")]
)
data class Seed(
    @PrimaryKey
    @ColumnInfo(name = "seed_uid")
    val seedUid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,
    @ColumnInfo(name = "vegetable_uid")
    val vegetableUid: String,
    @ColumnInfo(name = "actual_period_uid")
    val actualPeriodUid: String
)

data class SeedWithVegetable(
    @Embedded val seed: Seed,

    @Relation(
        parentColumn = "vegetable_uid",
        entityColumn = "vegetable_uid"
    )
    val vegetable: Vegetable
)

data class SeedWithVegetableAndPeriod(
    @Embedded(prefix = "seed") val seed: Seed,

    @Embedded(prefix = "vegetable") val vegetable: Vegetable,

    @Embedded(prefix = "period")
    val actualPeriod: Period
)

data class FullSeed(
    val seed: Seed,
    val vegetable: Vegetable,
    val actualPeriod: PeriodWithPhase,
    val periods: List<PeriodWithPhase>,
    var isNew: Boolean = false
) {
    val isLast: Boolean
        get() {
            val index = periods.indexOfFirst { it.phase == actualPeriod.phase }

            return index == periods.size - 1
        }

    val nextPhaseStarted: Boolean
        get() {
            val now = LocalDate.now().month.value - 1f
            val index = periods.indexOfFirst { it.phase == actualPeriod.phase }
            val period = periods.getOrNull(index + 1)?.period

            return period != null && now in period.startingMonth..period.endingMonth - 1
        }
}

