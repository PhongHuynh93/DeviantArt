package util

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Spanned
import android.view.View
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleCoroutineScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Phong Huynh on 8/2/2020.
 */
object HtmlImageText {
    fun getTextFromHtml(view: View, lifecycleCoroutineScope: LifecycleCoroutineScope, html: String): Spanned =
        html.parseAsHtml(imageGetter = HtmlImageGetter(view, lifecycleCoroutineScope))
}

class HtmlImageGetter(private val view: View, private val lifecycleCoroutineScope: LifecycleCoroutineScope) :
    Html.ImageGetter {
    override fun getDrawable(source: String?): Drawable {
        val drawable = UrlDrawable()
        lifecycleCoroutineScope.launch {
            withContext(Dispatchers.IO) {
                Glide.with(view)
                    .asDrawable()
                    .load(source)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit().get()
            }.let {
                it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
                drawable.bounds = it.bounds
                drawable.setDrawable(it)
                view.requestLayout()
                view.invalidate()
                if (it is GifDrawable) {
                    it.callback = object: Drawable.Callback {
                        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                        }

                        override fun invalidateDrawable(who: Drawable) {
                            view.invalidate()
                        }

                        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                        }

                    }
                    it.setLoopCount(GifDrawable.LOOP_FOREVER)
                    it.start()
                }
            }
        }
        return drawable
    }
}

internal class UrlDrawable : Drawable(), Drawable.Callback {
    private var mDrawable: Drawable? = null
    override fun draw(canvas: Canvas) {
        mDrawable?.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        mDrawable?.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mDrawable?.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return mDrawable?.opacity ?: 0
    }

    fun setDrawable(drawable: Drawable) {
        mDrawable = drawable
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        callback?.unscheduleDrawable(who, what)
    }

    override fun invalidateDrawable(who: Drawable) {
        callback?.invalidateDrawable(who)
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        callback?.scheduleDrawable(who, what, `when`)
    }
}