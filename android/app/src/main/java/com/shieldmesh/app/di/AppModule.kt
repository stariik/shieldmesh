package com.shieldmesh.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.shieldmesh.app.data.local.AppDatabase
import com.shieldmesh.app.data.local.dao.BountyDao
import com.shieldmesh.app.data.local.dao.StakerDao
import com.shieldmesh.app.data.local.dao.ThreatDao
import com.shieldmesh.app.mesh.PollinetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "shieldmesh_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shieldmesh.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideThreatDao(db: AppDatabase): ThreatDao = db.threatDao()

    @Provides
    @Singleton
    fun provideBountyDao(db: AppDatabase): BountyDao = db.bountyDao()

    @Provides
    @Singleton
    fun provideStakerDao(db: AppDatabase): StakerDao = db.stakerDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun providePollinetManager(@ApplicationContext context: Context): PollinetManager {
        return PollinetManager(context)
    }
}
