package com.maranatha.foodlergic.data.di

import android.app.Application
import android.content.Context
import com.maranatha.foodlergic.domain.repository.UserRepository
import com.maranatha.foodlergic.domain.usecase.AchievementUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideAchievmentUseCase(
        userRepository: UserRepository
    ): AchievementUseCase {
        return AchievementUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideContext(app: Application): Context = app.applicationContext
}
