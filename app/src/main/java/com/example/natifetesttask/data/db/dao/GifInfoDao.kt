package com.example.natifetesttask.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.natifetesttask.data.db.entities.BlackListEntity
import com.example.natifetesttask.data.db.entities.QueryInfoEntity

@Dao
interface GifInfoDao {

    @Query("SELECT * FROM queryInfo WHERE `query` = :query")
    suspend fun getQueryInfoEntity(query: String): QueryInfoEntity?

    @Query("SELECT * FROM queryInfo")
    suspend fun getQueryInfoEntities(): List<QueryInfoEntity>

    @Query("DELETE FROM queryInfo WHERE `query` IN (:queries) ")
    suspend fun deleteQueryInfoEntities(vararg queries: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQueryInfo(info: QueryInfoEntity)

    @Insert
    suspend fun addIdToBlacklist(entity: BlackListEntity)

    @Query("SELECT id FROM blacklist")
    suspend fun getBlacklistIds(): List<String>
}