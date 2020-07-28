package com.wind.deviantart.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.view.doOnNextLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wind.model.Art
import timber.log.Timber
import ui.RatioImageView

/**
 * Created by Phong Huynh on 7/12/2020.
 */
@BindingAdapter("imageUrl", "smallImageUrl", requireAll = false)
fun loadImage(imageView: ImageView, url: String?, smallImageUrl: String?) {
    Glide.with(imageView.context)
        .load(url)
        .thumbnail(Glide.with(imageView.context).load(smallImageUrl).diskCacheStrategy(DiskCacheStrategy.ALL))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(imageView)
}

@BindingAdapter("imageUrlCircle")
fun loadImageCircle(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .circleCrop()
        .into(imageView)
}

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: Drawable) {
    Glide.with(imageView.context).load(url).into(imageView)
}

/**
 * ratio: height / width
 */
@BindingAdapter("imageUrl", "smallImageUrl", "w", "h")
fun loadImageWithRatio(imageView: RatioImageView, url: String?, smallImageUrl: String?, w: Int, h: Int) {
    if (w > 0) {
        imageView.setRatio(h / w.toFloat())
        imageView.doOnNextLayout {
            Glide.with(imageView.context)
                .load(url)
                .thumbnail(Glide.with(imageView.context).load(smallImageUrl))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    } else {
        Timber.e("width must be > 0")
    }
}

@BindingAdapter("data")
fun bindAdapter(recyclerView: RecyclerView, data: List<Art>?) {
//    data?.let {
//        val adapter = recyclerView.adapter
//        (adapter as? BrowseNewestAdapter)?.setData(it)
//    }
}

