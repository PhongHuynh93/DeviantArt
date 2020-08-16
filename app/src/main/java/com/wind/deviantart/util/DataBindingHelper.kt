package com.wind.deviantart.util

import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.wind.deviantart.R
import com.wind.model.Art
import ui.RatioImageView
import util.HtmlImageText

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
@BindingAdapter(
    "imageUrl",
    "smallImageUrl",
    "w",
    "h",
    "maxRatio",
    "cacheW",
    "cacheH",
    "isThumbCached",
    requireAll = false
)
fun loadImage(
    imageView: ImageView, url: String?, smallImageUrl: String?, w: Int,
    h: Int, maxRatio: Float = -1f, cacheW: Int = 0, cacheH: Int = 0, isThumbCached: Boolean = false
) {
    val checkUseThumbnail2 = cacheW > 0 && cacheH > 0
    val checkUseThumbnail1 = !checkUseThumbnail2 && smallImageUrl != null
    val requestOptions = RequestOptions().format(DecodeFormat.PREFER_RGB_565)

    val useThumbnail = checkUseThumbnail1 || checkUseThumbnail2

    // first if we use mem cache, set the matrix to full fill the bitmap
    if (checkUseThumbnail2) {
        imageView.apply {
            // set scale type to matrix and fill the image
            scaleType = ImageView.ScaleType.MATRIX
            Matrix().also { matrix ->
                val cacheRect = RectF(0f, 0f, cacheW.toFloat(), cacheH.toFloat())
                doOnLayout {
                    val h2FollowCacheRatio = measuredWidth.toFloat() / (cacheW.toFloat() / cacheH.toFloat())
                    val realRect = RectF(0f, 0f, measuredWidth.toFloat(), h2FollowCacheRatio)
                    matrix.setRectToRect(cacheRect, realRect, Matrix.ScaleToFit.CENTER)
                    imageMatrix = matrix
                }
            }
        }
    }

    imageView.setTag(R.id.tagThumb, null)
    if (w > 0 && imageView is RatioImageView) {
        imageView.setRatio((h / w.toFloat()).let {
            return@let if (maxRatio > 0) {
                it.coerceAtMost(maxRatio)
            } else {
                it
            }
        })
        imageView.doOnNextLayout {
            // priority load the smallImageFirst
            val loadImage = Glide.with(imageView.context)
                .load(url)
                .priority(Priority.LOW)
                .apply {
                    if (useThumbnail) {
                        if (checkUseThumbnail1) {
                            thumbnail(
                                Glide.with(imageView.context).load(smallImageUrl)
                                    .priority(Priority.IMMEDIATE)
                                    .apply(requestOptions)
                                    // must use override with the width and height here because it will be used later for caching
                                    .override(imageView.measuredWidth, imageView.measuredHeight)
                            )
                        } else if (checkUseThumbnail2) {
                            // check if the mem cache if from small thumb or big thumb
                            val thumbnail = if (isThumbCached) {
                                url
                            } else {
                                smallImageUrl
                            }
                            thumbnail(
                                Glide.with(imageView.context)
                                    .load(thumbnail)
                                    .priority(Priority.IMMEDIATE)
                                    .override(cacheW, cacheH)
                                    .apply(requestOptions)
                            )
                        }
                    }
                }

                .placeholder(R.drawable.image_placeholder)
                .apply(requestOptions)

            loadImage
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageView.apply {
                            scaleType = ImageView.ScaleType.CENTER
                            setTag(R.id.tagThumb, true)
                        }
                        return false
                    }
                })
                // must use override with the width and height here because it will be used later for caching
                .override(it.measuredWidth, it.measuredHeight)
                .into(imageView)
        }
    }
    // TODO: 8/16/2020 currently not support image is not ratio
//    else {
//        loadImage.into(imageView)
//    }
}

@BindingAdapter("textHtml", "scope")
fun loadTextHtml(
    textView: TextView,
    textHtml: String,
    lifecycleCoroutineScope: LifecycleCoroutineScope
) {
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

