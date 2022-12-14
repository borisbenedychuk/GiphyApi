package com.example.gif_api.domain.gif.repository

import com.example.gif_api.domain.gif.model.GifModel
import com.example.gif_api.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface GifRepository {

    fun getPages(query: String, pages: List<Int>): Flow<List<GifModel>>

    suspend fun addGifToBlackList(id: String)

    suspend fun deleteOldData()

    suspend fun isGifCacheFresh(query: String): Boolean

    suspend fun loadPageAndCheckIfHasMore(query: String, page: Int): Result<Boolean>

    suspend fun loadInitialPagesAndCheckIfHasMore(query: String): Result<Boolean>
}