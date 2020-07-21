package com.wind.data.repository.art.source.remote

import com.wind.data.RestApi
import com.wind.data.di.RemoteDataSource
import com.wind.data.model.ArtList
import com.wind.data.repository.art.source.ArtDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Phong Huynh on 7/20/2020
 */
@Singleton
@RemoteDataSource
class ArtRemoteDataSource @Inject constructor(): ArtDataSource {
    @Inject
    lateinit var restApi: RestApi

    override suspend fun getDailyDeviations(): ArtList {
        return restApi.getDailyDeviations()
    }

    override suspend fun getNewestDeviations(): ArtList {
        return restApi.getNewestDeviations();
    }

    override suspend fun getPopularDeviations(): ArtList {
        return restApi.getPopularDeviations()
    }
}