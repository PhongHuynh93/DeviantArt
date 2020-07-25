package com.wind.deviantart.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wind.model.Art

/**
 * Created by Phong Huynh on 7/12/2020.
 */
@BindingAdapter("imageUrl", "smallImageUrl", requireAll = false)
fun loadImage(imageView: ImageView, url: String?, smallImageUrl: String?) {
    Glide.with(imageView.context)
        .load(url)
        .thumbnail(Glide.with(imageView.context).load(smallImageUrl))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(imageView)
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

