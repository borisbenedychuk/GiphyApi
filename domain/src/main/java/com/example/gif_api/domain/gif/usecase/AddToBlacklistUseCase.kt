package com.example.gif_api.domain.gif.usecase

import com.example.gif_api.domain.gif.repository.GifRepository
import javax.inject.Inject

class AddToBlacklistUseCase @Inject constructor(
    private val repository: GifRepository,
) {

    suspend operator fun invoke(id: String) = repository.addGifToBlackList(id)
}