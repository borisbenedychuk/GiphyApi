package com.example.gif_api.domain.repository.gif

import com.example.gif_api.domain.model.gif.GifModel
import com.example.gif_api.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface GifRepository {

    suspend fun getPages(query: String, pages: List<Int>): Flow<List<GifModel>>

    suspend fun addGifToBlackList(id: String)

    suspend fun deleteOldData()

    suspend fun isGifCacheFresh(query: String): Boolean

    suspend fun loadPageAndCheckIfHasMore(query: String, page: Int): Result<Boolean>

    suspend fun loadInitialPagesAndCheckIfHasMore(query: String): Result<Boolean>
}