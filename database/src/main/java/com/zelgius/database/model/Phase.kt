package com.zelgius.database.model

import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "phase")
data class Phase(
    @ColumnInfo(name = "phase_uid")
    @PrimaryKey val phaseUid: String = UUID.randomUUID().toString(),
    val name: String,
    val color: String,
    @ColumnInfo(name = "is_deleted")
    val isDelete: Boolean = false,
    @ColumnInfo(name = "string_resource")
    val stringResource: String? = null
)