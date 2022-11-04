package com.example.gif_api.app.di.common

import com.example.gif_api.domain.gif.usecase.*
import dagger.Binds
import dagger.Module

@Module
interface GifUseCaseBindingModule {

    @Binds
    fun bindDeleteOldDataUseCase(
        deleteOldDataCacheUseCaseImpl: DeleteOldDataCacheUseCaseImpl
    ): DeleteOldDataCacheUseCase

    @Binds
    fun bindAddToBlacklistUseCase(
        addToBlacklistUseCaseImpl: AddToBlacklistUseCaseImpl
    ): AddToBlacklistUseCase

    @Binds
    fun bindPager(pagerImpl: PagerImpl): Pager
}