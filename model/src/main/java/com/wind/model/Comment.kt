package com.wind.model


import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("body")
    val body: String,
    @SerializedName("commentid")
    val commentid: String,
    @SerializedName("hidden")
    val hidden: String?,
    @SerializedName("parentid")
    val parentid: String?,
    @SerializedName("posted")
    val posted: String,
    @SerializedName("replies")
    val replies: Int,
    @SerializedName("user")
    val user: Author?
)