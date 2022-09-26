package com.example.natifetesttask.domain.usecase.gif

import com.example.natifetesttask.domain.model.gif.GifsPagesModel
import com.example.natifetesttask.domain.repository.gif.GifRepository
import com.example.natifetesttask.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagesUseCase @Inject constructor(
    private val repository: GifRepository
) {

    suspend operator fun invoke(query: String, page: Int): Result<Flow<GifsPagesModel>> =
        repository.getPages(query, page)
}