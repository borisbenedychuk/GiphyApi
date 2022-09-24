package com.example.natifetesttask.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "queryInfo")
data class QueryInfo(
    @PrimaryKey
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "total_size")
    val totalSize: Int,
    @ColumnInfo(name = "total_pages")
    val totalPages: Int,
    @ColumnInfo(name = "saved_pages")
    val cachedPages: Int,
)