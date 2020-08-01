package com.wind.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Phong Huynh on 8/1/2020.
 */
data class DeviantArtList<T> (
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("next_offset")
    val nextOffset: Int?,
    @SerializedName(value = "results", alternate = ["thread"])
    val data: List<T> = emptyList()
)
