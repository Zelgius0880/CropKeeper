package com.zelgius.database.model

import androidx.room.*
import java.util.*

@Entity(tableName = "vegetable")
data class Vegetable(
    @ColumnInfo(name = "vegetable_uid")
    @PrimaryKey val vegetableUid: String = UUID.randomUUID()
        .toString(),
    val name: String,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    @ColumnInfo(name = "drawable_resource")
    val drawableResource: String? = null,
    @ColumnInfo(name = "string_resource")
    val stringResource: String? = null
)


data class VegetableWithPeriod(
    @Embedded val vegetable: Vegetable,
    @Relation(
        parentColumn = "vegetable_uid",
        entityColumn = "vegetable_uid"
    )
    val periods: List<PeriodWithPhase>
)

data class FullVegetable(
    @Embedded val vegetable: Vegetable,

    @Relation(
        parentColumn = "vegetable_uid",
        entityColumn = "vegetable_uid"
    )
    val periods: List<PeriodWithPhaseAndHistory>
)