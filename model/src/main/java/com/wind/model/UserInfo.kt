package com.wind.model


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("artist_level")
    val artistLevel: String,
    @SerializedName("artist_specialty")
    val artistSpecialty: String,
    @SerializedName("bio")
    val bio: String,
    @SerializedName("collections")
    val collections: List<Collection>,
    @SerializedName("country")
    val country: String,
    @SerializedName("countryid")
    val countryid: Int,
    @SerializedName("cover_photo")
    val coverPhoto: String,
    @SerializedName("galleries")
    val galleries: List<Gallery>,
    @SerializedName("is_watching")
    val isWatching: Boolean,
    @SerializedName("last_status")
    val lastStatus: LastStatus,
    @SerializedName("profile_pic")
    val profilePic: Art,
    @SerializedName("profile_url")
    val profileUrl: String,
    @SerializedName("real_name")
    val realName: String,
    @SerializedName("stats")
    val stats: UserStats,
    @SerializedName("tagline")
    val tagline: String,
    @SerializedName("user")
    val user: Author,
    @SerializedName("user_is_artist")
    val userIsArtist: Boolean,
    @SerializedName("website")
    val website: String
)

data class Collection(
    @SerializedName("folderid")
    val folderid: String,
    @SerializedName("name")
    val name: String
)

data class Gallery(
    @SerializedName("folderid")
    val folderid: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("parent")
    val parent: Any?
)

data class LastStatus(
    @SerializedName("author")
    val author: Author,
    @SerializedName("body")
    val body: String,
    @SerializedName("comments_count")
    val commentsCount: Int,
    @SerializedName("is_deleted")
    val isDeleted: Boolean,
    @SerializedName("is_share")
    val isShare: Boolean,
    @SerializedName("items")
    val items: List<Any>,
    @SerializedName("statusid")
    val statusid: String,
    @SerializedName("ts")
    val ts: String,
    @SerializedName("url")
    val url: String
)

data class UserStats(
    @SerializedName("profile_comments")
    val profileComments: Int,
    @SerializedName("profile_pageviews")
    val profilePageviews: Int,
    @SerializedName("user_comments")
    val userComments: Int,
    @SerializedName("user_deviations")
    val userDeviations: Int,
    @SerializedName("user_favourites")
    val userFavourites: Int
)
