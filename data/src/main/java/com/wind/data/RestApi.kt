package com.wind.data

import android.util.Log
import com.wind.data.model.ArtList
import com.wind.data.model.Token
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import timber.log.Timber
import javax.inject.Singleton

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

