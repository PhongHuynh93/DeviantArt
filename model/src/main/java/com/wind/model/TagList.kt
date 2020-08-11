package com.wind.model


import com.google.gson.annotations.SerializedName

data class TagList(
    @SerializedName("results")
    val tags: List<Tag>
) {
    data class Tag(
        @SerializedName("tag_name")
        val tagName: String
    )
}