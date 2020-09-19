package widget

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import util.hide
import util.show

private const val TRANSLATEX = "translateX"
private const val TRANSLATEY = "translateY"
private const val SCALEX = "scaleX"
private const val SCALEY = "scaleY"
private const val ANIM_DUR = 300L

class ShareElementView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var isAnimating: Boolean = false
    private lateinit var srcView: View
    private var callback: Callback? = null
    private var rectGlobal: Rect = Rect()
    private var bitmap: Bitmap? = null
    private var matrixShare: Matrix? = null
    private val shareInterpolator = DecelerateInterpolator()
    private var desRectGlobal: Rect? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            if (matrixShare != null) {
                canvas.drawBitmap(it, matrixShare!!, null)
            }
        }
    }

    fun setImageBitmap(bitmap: Bitmap, srcView: View) {
        this.bitmap = bitmap
        this.srcView = srcView
    }

    private fun setSrcRect() {
        srcView.getGlobalVisibleRect(rectGlobal)
        rectGlobal.apply {
            if (left == 0) {
                left = right - srcView.measuredWidth
            } else if (right == resources.displayMetrics.widthPixels) {
                right = left + srcView.measuredWidth
            }
        }
    }

    fun startEnterAnim(des: View, callback: Callback? = null) {
        val srcScaleX = srcView.scaleX
        val srcScaleY = srcView.scaleY
        setSrcRect()
        srcView.animate().cancel()
        srcView.hide()
        srcView.scaleX = 1f
        srcView.scaleY = 1f
        this.callback = callback
        if (desRectGlobal == null) {
            desRectGlobal = Rect()
        }
        des.getGlobalVisibleRect(desRectGlobal)
        val animTranslateX = PropertyValuesHolder.ofInt(TRANSLATEX, rectGlobal.left, desRectGlobal!!.left)
        val animTranslateY = PropertyValuesHolder.ofInt(TRANSLATEY, rectGlobal.top, desRectGlobal!!.top)
        val animScaleX = PropertyValuesHolder.ofFloat(SCALEX, srcScaleX, desRectGlobal!!.width().toFloat() / bitmap!!.width.toFloat())
        val animScaleY = PropertyValuesHolder.ofFloat(SCALEY, srcScaleY, desRectGlobal!!.height().toFloat() / bitmap!!.height.toFloat())
        startAnim(animTranslateX, animTranslateY, animScaleX, animScaleY, onEnd = {
            srcView.show()
        })
    }

    fun startExitAnim(des: View, callback: Callback? = null) {
        val curRectGlobal = Rect()
        des.getGlobalVisibleRect(curRectGlobal)
        this.callback = callback
        setSrcRect()
        val animTranslateX = PropertyValuesHolder.ofInt(TRANSLATEX, curRectGlobal.left, rectGlobal.left)
        val animTranslateY = PropertyValuesHolder.ofInt(TRANSLATEY, curRectGlobal.top, rectGlobal.top)
        val animScaleX = PropertyValuesHolder.ofFloat(SCALEX, curRectGlobal.width().toFloat() / rectGlobal.width().toFloat(), 1f)
        val animScaleY = PropertyValuesHolder.ofFloat(SCALEY, curRectGlobal.height().toFloat() / rectGlobal.height().toFloat(), 1f)
        startAnim(animTranslateX, animTranslateY, animScaleX, animScaleY, {
            srcView.hide()
        },  {
            srcView.show()
        })
    }

    private fun startAnim(animTranslateX: PropertyValuesHolder, animTranslateY: PropertyValuesHolder,
                          animScaleX: PropertyValuesHolder, animScaleY: PropertyValuesHolder,
                          onStart: () -> Unit = {},
                          onEnd: () -> Unit = {}) {
        isAnimating = true
        ValueAnimator().apply {
            setValues(animTranslateX, animTranslateY, animScaleX, animScaleY)
            interpolator = shareInterpolator
            duration = ANIM_DUR
            addUpdateListener {
                val translateX = it.getAnimatedValue(TRANSLATEX) as Int
                val translateY = it.getAnimatedValue(TRANSLATEY) as Int
                val scaleX = it.getAnimatedValue(SCALEX) as Float
                val scaleY = it.getAnimatedValue(SCALEY) as Float
                if (matrixShare == null) {
                    matrixShare = Matrix()
                }
                matrixShare!!.setScale(scaleX, scaleY)
                matrixShare!!.postTranslate(translateX.toFloat(), translateY.toFloat())
                invalidate()
            }
            addListener(
                    onStart = {
                        callback?.onStartAnim()
                        onStart()
                    },
                    onEnd = {
                        callback?.onEndAnim()
                        desRectGlobal = null
                        onEnd()
                        isAnimating = false
                    })
            start()
        }
    }

    interface Callback {
        fun onStartAnim()
        fun onEndAnim()
    }
}
