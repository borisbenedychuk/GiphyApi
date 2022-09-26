package com.example.natifetesttask.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.natifetesttask.data.db.entities.GifEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GifDao {

    @Query("SELECT * FROM gif WHERE `query` = :query AND page IN (:pages)")
    fun getGifs(query: String, pages: List<Int>): Flow<List<GifEntity>>

    @Query("SELECT * FROM gif WHERE `query` = :query")
    suspend fun getFirstGif(query: String): GifEntity?

    @Query("DELETE FROM gif WHERE `query` IN (:queries) ")
    suspend fun deleteQueryGifs(vararg queries: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGifs(entities: List<GifEntity>)

    @Query("DELETE FROM gif WHERE id =:id")
    suspend fun deleteGif(id: String)
}