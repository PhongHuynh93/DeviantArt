package com.wind.data

import com.wind.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * Created by Phong Huynh on 7/20/2020
 */
interface AuthApi {
    @GET("/api/v1/oauth2/browse/dailydeviations")
    suspend fun getDailyDeviations(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>
    @GET("/api/v1/oauth2/browse/newest")
    suspend fun getNewestDeviations(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>
    @GET("/api/v1/oauth2/browse/popular")
    suspend fun getPopularDeviations(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>
    @GET("api/v1/oauth2/browse/morelikethis/preview")
    suspend fun getMoreFromThisArtist(@QueryMap queryMap: Map<String, String>): RelatedArt
    @GET("api/v1/oauth2/comments/deviation/{id}")
    suspend fun getComment(@Path("id") id: String, @QueryMap queryMap: Map<String, String>): DeviantArtList<Comment>
    @GET("/api/v1/oauth2/browse/topics")
    suspend fun getTopics(@QueryMap queryMap: Map<String, String>): DeviantArtList<Topic>
    @GET("/api/v1/oauth2/browse/topic")
    suspend fun getTopic(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>
}

interface NonAuthApi {
    @GET("/oauth2/token")
    fun getToken(@QueryMap queryMap: Map<String, String>): Token
}

