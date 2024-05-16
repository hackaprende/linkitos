package com.example.linkitos.di

import com.example.linkitos.api.Network
import com.example.linkitos.api.NetworkImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    abstract fun bindNetwork(
        companyRepositoryImpl: NetworkImpl
    ): Network
}