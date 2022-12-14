package com.example.gif_api.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gif_api.data.db.dao.GifDao
import com.example.gif_api.data.db.dao.GifInfoDao
import com.example.gif_api.data.db.entities.BlackListEntity
import com.example.gif_api.data.db.entities.GifEntity
import com.example.gif_api.data.db.entities.QueryInfoEntity

@Database(
    entities = [GifEntity::class, QueryInfoEntity::class, BlackListEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {
    abstract val gifDao: GifDao
    abstract val gifInfoDao: GifInfoDao
}

