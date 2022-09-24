package com.example.natifetesttask.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.natifetesttask.data.db.entities.BlackListEntry
import com.example.natifetesttask.data.db.entities.GifEntity
import com.example.natifetesttask.data.db.entities.QueryInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface GifDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entities: List<GifEntity>)

    @Query("SELECT * FROM gif WHERE `query` = :query AND page IN (:pages)")
    fun getGifs(query: String, pages: List<Int>): Flow<List<GifEntity>>

    @Query("SELECT * FROM queryInfo WHERE `query` = :query")
    suspend fun getQueryInfo(query: String): QueryInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQueryInfo(info: QueryInfo)

    @Query("DELETE FROM gif WHERE id =:id")
    suspend fun deleteGif(id: String)

    @Query("SELECT id FROM blacklist")
    suspend fun getBlacklistIds(): List<String>

    @Insert
    suspend fun addIdToBlacklist(entity: BlackListEntry)
}