package com.wind.data.model


import com.google.gson.annotations.SerializedName

data class ArtList(
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("next_offset")
    val nextOffset: Int,
    @SerializedName("results")
    val arts: List<Art>
)
data class Art(
    @SerializedName("allows_comments")
    val allowsComments: Boolean,
    @SerializedName("author")
    val author: Author,
    @SerializedName("category")
    val category: String,
    @SerializedName("category_path")
    val categoryPath: String,
    @SerializedName("content")
    val content: Content,
    @SerializedName("deviationid")
    val id: String,
    @SerializedName("is_deleted")
    val isDeleted: Boolean,
    @SerializedName("is_downloadable")
    val isDownloadable: Boolean,
    @SerializedName("is_favourited")
    val isFavourited: Boolean,
    @SerializedName("is_mature")
    val isMature: Boolean,
    @SerializedName("printid")
    val printId: String?,
    @SerializedName("published_time")
    val publishedTime: Int,
    @SerializedName("stats")
    val stats: Stats,
    @SerializedName("thumbs")
    val thumbs: List<Thumb>?,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)
data class Author(
    @SerializedName("type")
    val type: String,
    @SerializedName("usericon")
    val userIcon: String,
    @SerializedName("userid")
    val id: String,
    @SerializedName("username")
    val name: String
)
data class Content(
    @SerializedName("filesize")
    val fileSize: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("src")
    val src: String,
    @SerializedName("transparency")
    val transparency: Boolean,
    @SerializedName("width")
    val width: Int
)
data class Stats(
    @SerializedName("comments")
    val comments: Int,
    @SerializedName("favourites")
    val favourites: Int
)
data class Thumb(
    @SerializedName("height")
    val height: Int,
    @SerializedName("src")
    val src: String,
    @SerializedName("transparency")
    val transparency: Boolean,
    @SerializedName("width")
    val width: Int
)
