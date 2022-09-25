package com.example.natifetesttask.app.di.providers

import com.example.natifetesttask.domain.repository.GifRepository

interface GifRepositoryProvider {
    val gifRepository: GifRepository
}