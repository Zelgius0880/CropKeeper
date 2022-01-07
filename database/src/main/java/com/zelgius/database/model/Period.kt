package com.zelgius.database.model

import androidx.room.*
import java.util.*

/**
 * Period range: [startingMonth, endingMonth[
 */
@Entity(
    tableName = "period",
    foreignKeys = [ForeignKey(
        entity = Phase::class,
        parentColumns = ["phase_uid"],
        childColumns = ["phase_uid"]
    ),
        ForeignKey(
            entity = Vegetable::class,
            parentColumns = ["vegetable_uid"],
            childColumns = ["vegetable_uid"]
        )],
    indices = [Index("phase_uid"), Index("vegetable_uid")]
)
data class Period(
    @ColumnInfo(name = "period_uid")
    @PrimaryKey val periodUid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "starting_month")
    val startingMonth: Float,
    @ColumnInfo(name = "ending_month")
    val endingMonth: Float,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    val order: Int,
    @ColumnInfo(name = "phase_uid")
    val phaseUid: String,
    @ColumnInfo(name = "vegetable_uid")
    val vegetableUid: String
)

data class PeriodWithPhase(
    @Embedded val phase: Phase,
    @Relation(
        parentColumn = "phase_uid",
        entityColumn = "phase_uid"
    )
    val period: Period
)



data class PeriodWithPhaseAndHistory(
    @Embedded val period: Period,
    @Relation(
        parentColumn = "phase_uid",
        entityColumn = "phase_uid"
    )
    val phase: Phase,

    @Relation(
        parentColumn = "period_uid",
        entityColumn = "period_uid"
    )
    val periodHistories: List<PeriodHistory>
)