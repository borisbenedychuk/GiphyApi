package com.example.natifetesttask.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blacklist")
data class BlackListEntry(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String
)