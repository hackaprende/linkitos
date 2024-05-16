package com.example.linkitos.di

import com.example.linkitos.linklist.ShortenLinkUseCase
import com.example.linkitos.linklist.ShortenLinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCasesModule {

    @Binds
    abstract fun bindLinksUseCase(
        companyRepositoryImpl: ShortenLinkUseCaseImpl
    ): ShortenLinkUseCase
}