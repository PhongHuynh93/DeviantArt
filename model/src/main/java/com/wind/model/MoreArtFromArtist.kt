package com.wind.model


import com.google.gson.annotations.SerializedName

data class RelatedArt (
    @SerializedName("author")
    val author: Author,
    @SerializedName("more_from_artist")
    val moreFromArtist: List<Art>,
    @SerializedName("more_from_da")
    val moreFromDa: List<Art>,
    @SerializedName("seed")
    val seed: String
)