package com.example.natifetesttask.domain.repository.gif

import com.example.natifetesttask.domain.model.gif.GifsPagesModel
import com.example.natifetesttask.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface GifRepository {

    suspend fun getPages(query: String, requestedPage: Int): Result<Flow<GifsPagesModel>>

    suspend fun addGifToBlackList(id: String)

    suspend fun deleteOldData()
}