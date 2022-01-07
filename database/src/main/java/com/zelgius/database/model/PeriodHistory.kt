package com.zelgius.database.model

import androidx.room.*
import java.time.LocalDate
import java.util.*

@Entity(
    tableName = "period_history",
    foreignKeys = [ForeignKey(
        entity = Period::class,
        parentColumns = ["period_uid"],
        childColumns = ["period_uid"]
    )],
    indices = [Index("period_uid")]
)
data class PeriodHistory(
    @ColumnInfo(name = "period_history_uid")
    @PrimaryKey val periodHistoryUid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,
    @ColumnInfo(name = "end_date")
    val endDate: LocalDate,
    @ColumnInfo(name = "period_uid")
    val periodUid: String
)