package com.example.natifetesttask.domain.repository

import com.example.natifetesttask.domain.model.GifModel
import com.example.natifetesttask.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface GifRepository {

    suspend fun getGifs(query: String, limit: Int, offset: Int): Result<Unit>

    fun observeGifs(query: String): Flow<List<GifModel>>
}