package me.saket.inboxrecyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * Freezing layout using [setLayoutFrozen] isn't sufficient for blocking programmatic scrolls
 * i.e., scrolls not initiated by the user. These scrolls are either requested by the app or
 * by RV when a child View requests focus.
 * */
abstract class ScrollSuppressibleRecyclerView(
    context: Context,
    attrs: AttributeSet?
) : RecyclerView(context, attrs) {

  abstract fun canScrollProgrammatically(): Boolean

  override fun scrollToPosition(position: Int) {
    if (canScrollProgrammatically()) {
      super.scrollToPosition(position)
    }
  }

  override fun smoothScrollToPosition(position: Int) {
    if (canScrollProgrammatically()) {
      super.smoothScrollToPosition(position)
    }
  }

  override fun smoothScrollBy(dx: Int, dy: Int) {
    if (canScrollProgrammatically()) {
      super.smoothScrollBy(dx, dy)
    }
  }

  override fun scrollTo(x: Int, y: Int) {
    if (canScrollProgrammatically()) {
      super.scrollTo(x, y)
    }
  }

  override fun scrollBy(x: Int, y: Int) {
    if (canScrollProgrammatically()) {
      super.scrollBy(x, y)
    }
  }
}
