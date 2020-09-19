package com.wind.deviantart.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wind.deviantart.databinding.ItemArtBinding

/**
 * Created by Phong Huynh on 9/19/2020
 */
object ViewHolderFactory {
    const val TYPE_ART = 1
    const val TYPE_TITLE = 70
    const val TYPE_FOOTER = 72
    const val TYPE_ART_HEADER = 73

    fun createHolder(layoutInflater: LayoutInflater, parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ART -> {
                ArtViewHolder(ItemArtBinding.inflate(layoutInflater, parent, false))
            }
            else -> {
                throw IllegalArgumentException("View type not found")
            }
        }
    }
}

class ArtViewHolder(val binding: ItemArtBinding): RecyclerView.ViewHolder(binding.root)
