package com.example.natifetesttask.domain.usecase.gif

import com.example.natifetesttask.domain.repository.gif.GifRepository
import javax.inject.Inject

class DeleteOldDataCacheUseCase @Inject constructor(
    private val repository: GifRepository,
) {

    suspend operator fun invoke() = repository.deleteOldData()
}