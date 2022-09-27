package com.example.gif_api.domain.usecase.gif

import com.example.gif_api.domain.repository.gif.GifRepository
import javax.inject.Inject

class AddToBlacklistUseCase @Inject constructor(
    private val repository: GifRepository,
) {

    suspend operator fun invoke(id: String) = repository.addGifToBlackList(id)
}