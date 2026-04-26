package com.puri.app.di

import com.puri.app.data.repository.AddressRepositoryImpl
import com.puri.app.data.repository.HistoryRepositoryImpl
import com.puri.app.data.repository.PreferencesRepositoryImpl
import com.puri.app.data.repository.SavedGuidesRepositoryImpl
import com.puri.app.data.repository.SolveRepositoryImpl
import com.puri.app.domain.repository.AddressRepository
import com.puri.app.domain.repository.HistoryRepository
import com.puri.app.domain.repository.PreferencesRepository
import com.puri.app.domain.repository.SavedGuidesRepository
import com.puri.app.domain.repository.SolveRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSolveRepository(impl: SolveRepositoryImpl): SolveRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindSavedGuidesRepository(impl: SavedGuidesRepositoryImpl): SavedGuidesRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: AddressRepositoryImpl): AddressRepository
}