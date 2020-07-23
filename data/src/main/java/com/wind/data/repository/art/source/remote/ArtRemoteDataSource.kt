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
class ArtRemoteDataSource constructor(private val restApi: RestApi): ArtDataSource {

    // https://www.deviantart.com/developers/http/v1/20200519/browse_dailydeviations/3de083a0c0a7733a46a53ae9cee74544
    override suspend fun getDailyDeviations(accessToken: String): ArtList {
        val map = mapOf("access_token" to accessToken)
        return restApi.getDailyDeviations(map)
    }

    override suspend fun getNewestDeviations(accessToken: String): ArtList {
        val map = mapOf("access_token" to accessToken)
        return restApi.getNewestDeviations(map);
    }

    override suspend fun getPopularDeviations(accessToken: String): ArtList {
        val map = mapOf("access_token" to accessToken)
        return restApi.getPopularDeviations(map)
    }
}