package com.wind.data

import com.wind.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * Created by Phong Huynh on 7/20/2020
 */
interface AuthApi {
    @GET("browse/dailydeviations")
    suspend fun getDailyDeviations(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>
    @GET("browse/newest")
    suspend fun getNewestDeviations(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>
    @GET("browse/popular")
    suspend fun getPopularDeviations(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>
    @GET("browse/morelikethis/preview")
    suspend fun getMoreFromThisArtist(@QueryMap queryMap: Map<String, String>): RelatedArt
    @GET("comments/deviation/{id}")
    suspend fun getComment(@Path("id") id: String, @QueryMap queryMap: Map<String, String>): DeviantArtList<Comment>
    @GET("browse/topics")
    suspend fun getTopics(@QueryMap queryMap: Map<String, String>): DeviantArtList<Topic>
    @GET("browse/topic")
    suspend fun getTopic(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>
    @GET("browse/tags/search")
    suspend fun getTagList(@QueryMap queryMap: Map<String, String>): TagList
    @GET("browse/tags")
    suspend fun getTagDeviations(@QueryMap queryMap: Map<String, String>): DeviantArtList<Art>

    // user info
    @GET("user/profile/{username}")
    suspend fun getUserInfo(@Path("username") userName: String, @QueryMap queryMap: Map<String, String>): UserInfo
}

interface NonAuthApi {
    @GET("oauth2/token")
    fun getToken(@QueryMap queryMap: Map<String, String>): Token
}

