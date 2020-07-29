package com.wind.deviantart.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.view.doOnNextLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.wind.deviantart.R
import com.wind.model.Art
import jp.wasabeef.glide.transformations.BlurTransformation
import timber.log.Timber
import ui.RatioImageView

/**
 * Created by Phong Huynh on 7/12/2020.
 */
@BindingAdapter("imageUrl", "smallImageUrl", "useFade", requireAll = false)
fun loadImage(imageView: ImageView, url: String?, smallImageUrl: String?, useFade: Boolean) {
    Glide.with(imageView.context)
        .load(url)
        .thumbnail(
            Glide.with(imageView.context).load(smallImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
        )
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply {
            if (useFade) {
                transition(withCrossFade())
            }
        }
        .placeholder(R.drawable.image_placeholder)
        .into(imageView)
}

@BindingAdapter("imageUrlCircle")
fun loadImageCircle(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.image_circle_placeholder)
        .circleCrop()
        .into(imageView)
}

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: Drawable) {
    Glide.with(imageView.context).load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.image_placeholder)
        .into(imageView)
}

/**
 * ratio: height / width
 */
@BindingAdapter("imageUrl", "smallImageUrl", "w", "h", "useFade", "useBlur", requireAll = false)
fun loadImageWithRatio(
    imageView: RatioImageView, url: String?, smallImageUrl: String?, w: Int,
    h: Int, useFade: Boolean, useBlur: Boolean = false) {
    if (w > 0) {
        imageView.setRatio(h / w.toFloat())
        imageView.doOnNextLayout {
            Glide.with(imageView.context)
                .load(url)
                .thumbnail(
                    Glide.with(imageView.context).load(smallImageUrl)
                        .apply {
                            if (useBlur) {
                                transform(BlurTransformation())
                            }
                        }
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply {
                    if (useFade) {
                        transition(withCrossFade())
                    }
                }
                .placeholder(R.drawable.image_placeholder)
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

