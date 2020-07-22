package com.wind.data.repository.art

import com.wind.data.di.LocalDataSource
import com.wind.data.di.RemoteDataSource
import com.wind.data.model.ArtList
import com.wind.data.repository.art.source.local.ArtLocalDataSource
import com.wind.data.repository.art.source.remote.ArtRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Phong Huynh on 7/20/2020
 */

@Module
@InstallIn(ApplicationComponent::class)
object BrowseRepositoryModule {
    @Singleton
    @Provides
    fun getBrowseRepository(@RemoteDataSource remoteDataSource: ArtRemoteDataSource,
                            @LocalDataSource localDataSource: ArtLocalDataSource): BrowseRepository {
        return BrowseRepositoryImpl(remoteDataSource, localDataSource)
    }
}

interface BrowseRepository {
    suspend fun getNewestDeviations(): ArtList
    suspend fun getPopularDeviations(): ArtList
}

class BrowseRepositoryImpl(private val remoteDataSource: ArtRemoteDataSource,
    private val localDataSource: ArtLocalDataSource) : BrowseRepository {
    override suspend fun getNewestDeviations(): ArtList {
        return remoteDataSource.getNewestDeviations()
    }

    override suspend fun getPopularDeviations(): ArtList {
        return remoteDataSource.getPopularDeviations()
    }
}