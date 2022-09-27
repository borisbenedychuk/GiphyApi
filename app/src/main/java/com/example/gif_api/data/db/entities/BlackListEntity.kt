package com.example.gif_api.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blacklist")
data class BlackListEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String
)