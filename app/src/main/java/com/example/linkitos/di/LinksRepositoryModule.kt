package com.example.linkitos.di

import com.example.linkitos.linklist.LinksRepository
import com.example.linkitos.linklist.LinksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class LinksRepositoryModule {

    @Binds
    abstract fun bindLinksRepository(
        companyRepositoryImpl: LinksRepositoryImpl
    ): LinksRepository
}