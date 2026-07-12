package com.sujoy.smartfarm.Domain.di

import com.sujoy.smartfarm.Data.Repo.GeminiRepositoryImpl
import com.sujoy.smartfarm.Domain.repo.GeminiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GeminiRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGeminiRepository(

        impl: GeminiRepositoryImpl

    ): GeminiRepository

}