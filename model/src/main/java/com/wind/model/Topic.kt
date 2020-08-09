package com.wind.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Topic(
    @SerializedName("canonical_name")
    val canonicalName: String,
    @SerializedName("name")
    val name: String,
    var listArt: List<Art> = emptyList()
): Parcelable