package com.wind.deviantart.util

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnNextLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.wind.deviantart.R
import com.wind.model.Art
import jp.wasabeef.glide.transformations.BlurTransformation
import ui.RatioImageView
import util.HtmlImageText
import util.dp

/**
 * Created by Phong Huynh on 7/12/2020.
 */
@BindingAdapter("imageUrlCircle")
fun loadImageCircle(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.image_circle_placeholder)
        .circleCrop()
        .into(imageView)
}

/**
 * ratio: height / width
 */
@BindingAdapter("imageUrl", "smallImageUrl", "w", "h", "useFade", "useBlur", "maxRatio", requireAll = false)
fun loadImage(
    imageView: ImageView, url: String?, smallImageUrl: String?, w: Int,
    h: Int, useFade: Boolean = false, useBlur: Boolean = false, maxRatio: Float = -1f
) {
    val requestOptions =
        RequestOptions().format(DecodeFormat.PREFER_RGB_565).diskCacheStrategy(DiskCacheStrategy.ALL)
    // priority load the smallImageFirst
    val loadImage = Glide.with(imageView.context)
        .load(url)
        .priority(Priority.LOW)
        .thumbnail(
            Glide.with(imageView.context).load(smallImageUrl).priority(Priority.IMMEDIATE)
                .apply {
                    if (useBlur) {
                        transform(CenterCrop(), BlurTransformation(25, 4))
                    }
                }
                .apply(requestOptions)
        )
        .apply {
            if (useFade) {
                transition(withCrossFade(300))
            }
        }
        .placeholder(R.drawable.image_placeholder)
        .apply(requestOptions)
        .centerCrop()
    if (w > 0 && imageView is RatioImageView) {
        imageView.setRatio((h / w.toFloat()).let {
            return@let if (maxRatio > 0) {
                it.coerceAtMost(maxRatio)
            } else {
                it
            }
        })
        imageView.doOnNextLayout {
            loadImage.into(imageView)
        }
    } else {
        loadImage.into(imageView)
    }
}

@BindingAdapter("textHtml", "scope")
fun loadTextHtml(textView: TextView, textHtml: String, lifecycleCoroutineScope: LifecycleCoroutineScope) {
    textView.text = HtmlImageText.getTextFromHtml(
        html = textHtml,
        view = textView,
        lifecycleCoroutineScope = lifecycleCoroutineScope
    )
}

@BindingAdapter("data")
fun loadDataToList(recyclerView: RecyclerView, data: List<Art>?) {
    (recyclerView.adapter as? ListAdapter<Art, *>)?.apply {
        submitList(data)
    }
}

