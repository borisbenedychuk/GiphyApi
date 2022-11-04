package com.example.gif_api.domain.gif.usecase

import com.example.gif_api.domain.gif.repository.GifRepository
import javax.inject.Inject

class DeleteOldDataCacheUseCaseImpl @Inject constructor(
    private val repository: GifRepository,
): DeleteOldDataCacheUseCase {

    override suspend operator fun invoke() = repository.deleteOldData()
}