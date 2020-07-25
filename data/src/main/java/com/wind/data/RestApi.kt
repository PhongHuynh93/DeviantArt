package com.wind.data

import com.wind.model.ArtList
import com.wind.model.Token
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Created by Phong Huynh on 7/20/2020
 */
interface AuthApi {
    @GET("/api/v1/oauth2/browse/dailydeviations")
    suspend fun getDailyDeviations(@QueryMap queryMap: Map<String, String>): ArtList
    @GET("/api/v1/oauth2/browse/newest")
    suspend fun getNewestDeviations(@QueryMap queryMap: Map<String, String>): ArtList
    @GET("/api/v1/oauth2/browse/popular")
    suspend fun getPopularDeviations(@QueryMap queryMap: Map<String, String>): ArtList
}

interface NonAuthApi {
    @GET("/oauth2/token")
    fun getToken(@QueryMap queryMap: Map<String, String>): Token
}

