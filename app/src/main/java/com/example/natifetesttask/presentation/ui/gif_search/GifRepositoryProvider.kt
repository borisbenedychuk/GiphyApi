package com.example.natifetesttask.presentation.ui.gif_search

import com.example.natifetesttask.domain.repository.GifRepository

interface GifRepositoryProvider {
    val gifRepository: GifRepository
}
