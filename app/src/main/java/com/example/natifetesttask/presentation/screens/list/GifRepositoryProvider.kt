package com.example.natifetesttask.presentation.screens.list

import com.example.natifetesttask.domain.repository.GifRepository

interface GifRepositoryProvider {
    val gifRepository: GifRepository
}
