package com.example.gif_api.domain.gif.usecase

import com.example.gif_api.domain.gif.repository.GifRepository
import javax.inject.Inject

class AddToBlacklistUseCaseImpl @Inject constructor(
    private val repository: GifRepository,
) : AddToBlacklistUseCase {

    override suspend operator fun invoke(id: String) = repository.addGifToBlackList(id)
}