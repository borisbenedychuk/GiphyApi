package com.example.natifetesttask.domain.repository

import com.example.natifetesttask.domain.model.GifModel
import com.example.natifetesttask.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface GifRepository {

    suspend fun getPages(query: String, currentPage: Int): Result<Flow<List<GifModel>>>

    suspend fun addGifToBlackList(id: String)
}