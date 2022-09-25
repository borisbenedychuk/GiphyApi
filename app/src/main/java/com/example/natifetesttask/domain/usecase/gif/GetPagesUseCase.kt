package com.example.natifetesttask.domain.usecase.gif

import com.example.natifetesttask.domain.repository.gif.GifRepository
import javax.inject.Inject

class GetPagesUseCase @Inject constructor(
    private val repository: GifRepository
) {

    suspend operator fun invoke(query: String, page: Int) = repository.getPages(query, page)
}