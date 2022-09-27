package com.example.gif_api.domain.usecase.gif

import com.example.gif_api.domain.repository.gif.GifRepository
import javax.inject.Inject

class DeleteOldDataCacheUseCase @Inject constructor(
    private val repository: GifRepository,
) {

    suspend operator fun invoke() = repository.deleteOldData()
}