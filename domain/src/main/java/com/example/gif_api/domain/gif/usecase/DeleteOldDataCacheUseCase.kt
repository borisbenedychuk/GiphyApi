package com.example.gif_api.domain.gif.usecase

import com.example.gif_api.domain.gif.repository.GifRepository
import javax.inject.Inject

class DeleteOldDataCacheUseCase @Inject constructor(
    private val repository: GifRepository,
) {

    suspend operator fun invoke() = repository.deleteOldData()
}