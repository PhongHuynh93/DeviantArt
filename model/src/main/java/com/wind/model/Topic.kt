package com.wind.model


import com.google.gson.annotations.SerializedName

data class Topic(
    @SerializedName("canonical_name")
    val canonicalName: String,
    @SerializedName("name")
    val name: String,
    var listArt: List<Art> = emptyList()
)