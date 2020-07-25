package util

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

const val EXTRA_DATA = "xData"
private const val MIN_CLICK_INTERVAL = 600

class Utils {
   companion object {
       /** Show an event in the LogCat view, for debugging  */
       fun dumpEvent(event: MotionEvent): String {
           val names = arrayOf("DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?")
           val sb = StringBuilder()
           val action = event.action
           val actionCode = action and MotionEvent.ACTION_MASK
           sb.append("event ACTION_").append(names[actionCode])
           if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
               sb.append("(pid ").append(
                       action shr MotionEvent.ACTION_POINTER_ID_SHIFT)
               sb.append(")")
           }
           sb.append("[")
           for (i in 0 until event.pointerCount) {
               sb.append("#").append(i)
               sb.append("(pid ").append(event.getPointerId(i))
               sb.append(")=").append(event.getX(i).toInt())
               sb.append(",").append(event.getY(i).toInt())
               if (i + 1 < event.pointerCount)
                   sb.append(";")
           }
           sb.append("]")
           return sb.toString()
       }

       fun getResId(resName: String, c: Class<*>): Int {

           return try {
               val idField = c.getDeclaredField(resName)
               idField.getInt(idField)
           } catch (e: Exception) {
               e.printStackTrace()
               -1
           }
       }

       fun findAllScrollViews(mViewGroup: ViewGroup): View? {
           for (i in 0 until mViewGroup.childCount) {
               var mView: View? = mViewGroup.getChildAt(i)
               if (mView!!.visibility != View.VISIBLE) {
                   continue
               }
               if (isScrollableView(mView)) {
                   return mView
               }
               if (mView is ViewGroup) {
                   mView = findAllScrollViews((mView as ViewGroup?)!!)
                   if (mView != null) {
                       return mView
                   }
               }
           }
           return null
       }

       private fun isScrollableView(mView: View): Boolean {
           return (!(mView !is ScrollView && mView !is HorizontalScrollView && mView !is NestedScrollView && mView !is AbsListView && mView !is RecyclerView && mView !is ViewPager && mView !is WebView))
       }

       fun contains(mView: View, x: Float, y: Float): Boolean {
           val localRect = Rect()
           mView.getGlobalVisibleRect(localRect)
           return localRect.contains(x.toInt(), y.toInt())
       }

       fun canViewScrollDown(mView: View?, x: Float, y: Float, defaultValueForNull: Boolean): Boolean {
           return if (mView == null || !contains(mView, x, y)) {
               defaultValueForNull
           } else mView.canScrollVertically(1)
       }

       fun canViewScrollUp(mView: View?, x: Float, y: Float, defaultValueForNull: Boolean): Boolean {
           return if (mView == null || !contains(mView, x, y)) {
               defaultValueForNull
           } else mView.canScrollVertically(-1)
       }

       private var sLastClickTime: Long = 0
       fun isAllowClick(): Boolean {
           val currentClickTime = SystemClock.uptimeMillis()
           val elapsedTime = currentClickTime - sLastClickTime
           sLastClickTime = currentClickTime
           if (elapsedTime in 0..MIN_CLICK_INTERVAL) {
               return false
           }
           return true
       }
   }
}

class BitmapUtil {
    companion object {
        /**
         *
         * return ratio: bitmap / device size
         * isPortrait; is the video portrait
         */
         fun setSrcRect(bitmap: Bitmap, srcRect: Rect, widthDes: Float, heightDes: Float) {
            var leftSrc = 0
            var topSrc = 0
            var rightSrc = 0
            var bottomSrc = 0
            val ratio = widthDes / heightDes > bitmap.width / bitmap.height.toFloat()

            if (ratio) {
                // video doc, cut top/bottom
                val newBitmapW = bitmap.width
                val newBitmapH = newBitmapW * heightDes / widthDes
                leftSrc = 0
                topSrc = (bitmap.height / 2f - newBitmapH / 2f).toInt()
                rightSrc = leftSrc + newBitmapW
                bottomSrc = topSrc + newBitmapH.toInt()
            } else {
                // video ngang, cut left, right
                val newBitmapH = bitmap.height
                val newBitmapW = newBitmapH * widthDes / heightDes
                leftSrc = (bitmap.width / 2f - newBitmapW / 2f).toInt()
                topSrc = 0
                rightSrc = leftSrc + newBitmapW.toInt()
                bottomSrc = topSrc + newBitmapH
            }
            srcRect.set(leftSrc, topSrc, rightSrc, bottomSrc)
        }
    }
}

open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}
