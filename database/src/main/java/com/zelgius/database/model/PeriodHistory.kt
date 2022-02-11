package com.zelgius.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate

@Entity(
    tableName = "period_history",
    foreignKeys = [ForeignKey(
        entity = Period::class,
        parentColumns = ["period_uid"],
        childColumns = ["period_uid"]
    ), ForeignKey(
        entity = Seed::class,
        parentColumns = ["seed_uid"],
        childColumns = ["seed_uid"]
    )],
    indices = [Index("period_uid"), Index("seed_uid")],
    primaryKeys = ["period_uid", "seed_uid"]
)
data class PeriodHistory(
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate = LocalDate.now(),
    @ColumnInfo(name = "end_date")
    val endDate: LocalDate? = null,
    @ColumnInfo(name = "period_uid")
    val periodUid: String,
    @ColumnInfo(name = "seed_uid")
    val seedUid: String
)