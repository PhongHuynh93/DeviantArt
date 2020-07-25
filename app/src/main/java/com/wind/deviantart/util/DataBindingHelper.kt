package com.wind.deviantart.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wind.data.model.Art
import com.wind.deviantart.ui.BrowseNewestAdapter

/**
 * Created by Phong Huynh on 7/12/2020.
 */
@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
}

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: Drawable) {
    Glide.with(imageView.context).load(url).into(imageView)
}

@BindingAdapter("data")
fun bindAdapter(recyclerView: RecyclerView, data: List<Art>?) {
//    data?.let {
//        val adapter = recyclerView.adapter
//        (adapter as? BrowseNewestAdapter)?.setData(it)
//    }
}

