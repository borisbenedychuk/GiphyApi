package com.example.natifetesttask.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.natifetesttask.data.db.entities.GifEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GifDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(entities: List<GifEntity>)

    @Query("SELECT * FROM gif WHERE `query` = :query")
    fun getGifs(query: String): Flow<List<GifEntity>>
}