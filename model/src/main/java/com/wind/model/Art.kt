package com.wind.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

// https://www.deviantart.com/developers/http/v1/20200519/object/deviation
object ArtType {
    const val TYPE_LITERATURE = "Literature"
    const val TYPE_PERSONAL = "Personal"
}
@Parcelize
data class Art (
    @SerializedName("allows_comments")
    val allowsComments: Boolean,
    @SerializedName("author")
    val author: Author?,
    @SerializedName("category")
    val category: String,
    @SerializedName("category_path")
    val categoryPath: String,
    @SerializedName("content")
    val content: Thumb?,
    @SerializedName("preview")
    val preview: Thumb?,
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
    val thumbs: List<Thumb> = emptyList(),
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("daily_deviation")
    val dailyDeviation: DailyDeviation?,
    // Literature type
    @SerializedName("excerpt")
    val excerpt: String?

) : Parcelable

@Parcelize
data class DailyDeviation(
    @SerializedName("body")
    val body: String,
    @SerializedName("giver")
    val giver: Giver?,
    @SerializedName("time")
    val time: String
): Parcelable

@Parcelize
data class Giver(
    @SerializedName("type")
    val type: String,
    @SerializedName("usericon")
    val usericon: String,
    @SerializedName("userid")
    val userid: String,
    @SerializedName("username")
    val username: String
): Parcelable

@Parcelize
data class Author(
    @SerializedName("type")
    val type: String,
    @SerializedName("usericon")
    val userIcon: String,
    @SerializedName("userid")
    val id: String,
    @SerializedName("username")
    val name: String
) : Parcelable

@Parcelize
data class Thumb(
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
) : Parcelable

@Parcelize
data class Stats(
    @SerializedName("comments")
    val comments: Int,
    @SerializedName("favourites")
    val favourites: Int
) : Parcelable