package com.wind.data.di

import com.wind.data.RestApi
import com.wind.data.repository.art.BrowseRepository
import com.wind.data.repository.art.BrowseRepositoryImpl
import com.wind.data.repository.art.source.ArtDataSource
import com.wind.data.repository.art.source.local.ArtLocalDataSource
import com.wind.data.repository.art.source.remote.ArtRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

/**
 * Created by Phong Huynh on 7/20/2020
 */

@Module
@InstallIn(ApplicationComponent::class)
object AggregationDataModule {
    @Singleton
    @Provides
    fun getBrowseRepository(
        @RemoteDataSource remoteDataSource: ArtDataSource,
        @LocalDataSource localDataSource: ArtDataSource
    ): BrowseRepository {
        return BrowseRepositoryImpl(
            remoteDataSource,
            localDataSource
        )
    }

    @Singleton
    @Provides
    @RemoteDataSource
    fun getArtRemoteDataSource(restApi: RestApi): ArtDataSource {
        return ArtRemoteDataSource(restApi)
    }

    @Singleton
    @Provides
    @LocalDataSource
    fun getArtLocalDataSource(): ArtDataSource {
        return ArtLocalDataSource()
    }

    @Provides
    @Singleton
    fun provideRestApi(): RestApi {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.deviantart.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RestApi::class.java)
    }
}