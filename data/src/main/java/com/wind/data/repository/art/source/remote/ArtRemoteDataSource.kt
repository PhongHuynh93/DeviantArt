package com.wind.data.repository.art.source.remote

import com.wind.data.AuthApi
import com.wind.data.di.RemoteDataSource
import com.wind.data.model.ArtList
import com.wind.data.repository.art.source.ArtDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Phong Huynh on 7/20/2020
 */
class ArtRemoteDataSource constructor(private val restApi: AuthApi): ArtDataSource {

    // https://www.deviantart.com/developers/http/v1/20200519/browse_dailydeviations/3de083a0c0a7733a46a53ae9cee74544
    override suspend fun getDailyDeviations(): ArtList {
        val map = emptyMap<String, String>()
        return restApi.getDailyDeviations(map)
    }

    override suspend fun getNewestDeviations(): ArtList {
        val map = emptyMap<String, String>()
        return restApi.getNewestDeviations(map);
    }

    override suspend fun getPopularDeviations(): ArtList {
        val map = emptyMap<String, String>()
        return restApi.getPopularDeviations(map)
    }
}