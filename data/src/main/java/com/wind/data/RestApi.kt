package com.wind.data

import android.util.Log
import com.wind.data.model.ArtList
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import timber.log.Timber
import javax.inject.Singleton

/**
 * Created by Phong Huynh on 7/20/2020
 */
interface RestApi {
    @GET("/browse/dailydeviations")
    suspend fun getDailyDeviations(): ArtList
    @GET("/browse/newest")
    suspend fun getNewestDeviations(): ArtList
    @GET("/browse/popular")
    suspend fun getPopularDeviations(): ArtList
}

@Provides
@Singleton
fun provideRestApi(): RestApi {
    val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Timber.d(it) })
    logger.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.deviantart.com/api/v1/oauth2/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(RestApi::class.java)
}