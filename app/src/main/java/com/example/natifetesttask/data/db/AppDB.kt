package com.example.natifetesttask.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.natifetesttask.data.db.dao.GifDao
import com.example.natifetesttask.data.db.entities.BlackListEntity
import com.example.natifetesttask.data.db.entities.GifEntity
import com.example.natifetesttask.data.db.entities.QueryInfoEntity

@Database(
    entities = [GifEntity::class, QueryInfoEntity::class, BlackListEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {
    abstract val gifDao: GifDao
}

