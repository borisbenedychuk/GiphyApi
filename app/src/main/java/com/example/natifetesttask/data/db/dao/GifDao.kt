package com.example.natifetesttask.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.natifetesttask.data.db.entities.BlackListEntity
import com.example.natifetesttask.data.db.entities.GifEntity
import com.example.natifetesttask.data.db.entities.QueryInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GifDao {

    @Query("SELECT * FROM gif WHERE `query` = :query AND page IN (:pages)")
    fun getGifs(query: String, pages: List<Int>): Flow<List<GifEntity>>

    @Query("SELECT * FROM queryInfo WHERE `query` = :query")
    suspend fun getQueryInfoEntity(query: String): QueryInfoEntity?

    @Query("SELECT * FROM queryInfo")
    suspend fun getQueryInfoEntities(): List<QueryInfoEntity>

    @Query("DELETE FROM gif WHERE `query` IN (:queries) ")
    suspend fun deleteQueryGifs(queries: List<String>)

    @Query("DELETE FROM queryInfo WHERE `query` IN (:queries) ")
    suspend fun deleteQueryInfoEntities(queries: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGifs(entities: List<GifEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQueryInfo(info: QueryInfoEntity)

    @Query("DELETE FROM gif WHERE id =:id")
    suspend fun deleteGif(id: String)

    @Insert
    suspend fun addIdToBlacklist(entity: BlackListEntity)

    @Query("SELECT id FROM blacklist")
    suspend fun getBlacklistIds(): List<String>
}