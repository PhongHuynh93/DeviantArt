package com.wind.data.di

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.wind.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import javax.inject.Singleton

/**
 * Created by Phong Huynh on 7/20/2020
 */

@Module
@InstallIn(ApplicationComponent::class)
object AggregationDataModule {
    @Provides
    @Singleton
    fun provideNonAuthRestApi(): NonAuthApi {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.deviantart.com")
            .client(client)
            .addCallAdapterFactory(SynchronousCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(NonAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun sharePref(@ApplicationContext context: Context): SharedPreferences {
        val cacheName = "DeviantArtCache"
        return context.getSharedPreferences(cacheName, Context.MODE_PRIVATE)
    }

    @SuppressLint("ApplySharedPref")
    @Provides
    @Singleton
    fun provideRestApi(nonAuthApi: NonAuthApi, pref: SharedPreferences): AuthApi {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor { chain ->
                val keyToken = "token"
                return@addInterceptor chain.proceed(
                    chain.request().let { request ->
                        // 1. sign this request
                        val url = request.url().newBuilder().addQueryParameter(
                            "access_token",
                            pref.getString(keyToken, "") ?: ""
                        ).build()
                        request.newBuilder().url(url).build()
                    }
                ).let {
                    if (it.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        it.close()
                        chain.proceed(
                            it.request().let { request ->
                                // sign the request with the new token and proceed
                                val token = nonAuthApi.getToken(
                                    mapOf(
                                        "client_id" to "12883",
                                        "client_secret" to "9d83bfaec09967d67b7fa7981160ebda",
                                        "grant_type" to "client_credentials"
                                    )
                                )
                                pref.edit().putString(keyToken, token.accessToken).commit()
                                val url = request.url().newBuilder().addQueryParameter(
                                    "access_token",
                                    token.accessToken
                                ).build()
                                request.newBuilder().url(url).build()
                            }
                        )
                    } else {
                        it
                    }
                }
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.deviantart.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(AuthApi::class.java)
    }

    @Singleton
    @Provides
    fun getBrowseRepository(restApi: AuthApi): RestRepository {
        return RestRepositoryImpl(restApi)
    }

}