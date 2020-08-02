package com.wind.deviantart.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnNextLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.wind.deviantart.R
import jp.wasabeef.glide.transformations.BlurTransformation
import timber.log.Timber
import ui.RatioImageView
import util.HtmlImageText

/**
 * Created by Phong Huynh on 7/12/2020.
 */
@BindingAdapter("imageUrl", "smallImageUrl", "useFade", requireAll = false)
fun loadImage(imageView: ImageView, url: String?, smallImageUrl: String?, useFade: Boolean) {
    val requestOptions = RequestOptions().format(DecodeFormat.PREFER_RGB_565)

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
        .apply(requestOptions)
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
    val requestOptions = RequestOptions().format(DecodeFormat.PREFER_RGB_565)
    Glide.with(imageView.context).load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.image_placeholder)
        .apply(requestOptions)
        .into(imageView)
}

/**
 * ratio: height / width
 */
@BindingAdapter("imageUrl", "smallImageUrl", "w", "h", "useFade", "useBlur", "maxRatio", requireAll = false)
fun loadImageWithRatio(
    imageView: RatioImageView, url: String?, smallImageUrl: String?, w: Int,
    h: Int, useFade: Boolean, useBlur: Boolean = false, maxRatio: Float = -1f) {
    if (w > 0) {
        val requestOptions = RequestOptions().format(DecodeFormat.PREFER_RGB_565).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
        imageView.setRatio((h / w.toFloat()).let {
            return@let if (maxRatio > 0) {
                it.coerceAtMost(maxRatio)
            } else {
                it
            }
        })

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
                        .apply(requestOptions)
                )
                .apply {
                    if (useFade) {
                        transition(withCrossFade())
                    }
                }
                .placeholder(R.drawable.image_placeholder)
                .apply(requestOptions)
                .into(imageView)
        }
    } else {
        Timber.e("width must be > 0")
    }
}

@BindingAdapter("textHtml", "scope")
fun loadTextHtml(textView: TextView, textHtml: String, lifecycleCoroutineScope: LifecycleCoroutineScope) {
    textView.text = HtmlImageText.getTextFromHtml(html = textHtml, view = textView, lifecycleCoroutineScope = lifecycleCoroutineScope)
}

