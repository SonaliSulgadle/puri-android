package com.puri.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    // Analytics is @Singleton @Inject constructor — Hilt handles automatically
    // No manual binding needed
}