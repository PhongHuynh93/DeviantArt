package com.wind.deviantart.ui.artdetail

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wind.deviantart.R
import com.wind.deviantart.util.ViewHolderFactory
import util.dp
import util.getColorAttr
import util.getDimen

/**
 * Created by Phong Huynh on 9/5/2020
 */
class ArtDetailItemDecoration(context: Context, private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) : RecyclerView.ItemDecoration() {
    private val spaceArt = context.getDimen(R.dimen.space_art).toInt()
    private val spaceOffsetBetweenSection = (4 * context.dp()).toInt()
    private val spaceTitleVer = context.getDimen(R.dimen.space_pretty_small).toInt()
    private val spaceTitleVerTop = spaceTitleVer + spaceOffsetBetweenSection
    private val spaceTitleHoz = context.getDimen(R.dimen.space_normal).toInt()
    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColorAttr(R.attr.colorPrimary)
    }
    private val radius = context.getDimen(R.dimen.large_radius)
    private val path = Path()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val pos = parent.getChildAdapterPosition(view)
        if (pos == RecyclerView.NO_POSITION)
            return
        when (adapter.getItemViewType(pos)) {
            ViewHolderFactory.TYPE_TITLE -> {
                outRect.left = spaceTitleHoz
                outRect.right = spaceTitleHoz
                outRect.top = spaceTitleVerTop
                outRect.bottom = spaceTitleVer - spaceArt / 2
            }
            ViewHolderFactory.TYPE_ART -> {
                outRect.top = spaceArt / 2
                outRect.bottom = spaceArt / 2
                outRect.left = spaceArt / 2
                outRect.right = spaceArt / 2
            }
        }
    }

    override fun onDrawOver(canvas: Canvas, rv: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, rv, state)
        val count: Int = rv.childCount

        val layoutManager = rv.layoutManager as StaggeredGridLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPositions(null).let {
            if (it.isNotEmpty()) {
                it[0]
            } else {
                -1
            }
        }
        if (firstVisibleItemPosition == -1) return
        for (i in 0 until count) {
            val currentPos = firstVisibleItemPosition + i
            val currentType = try {
                // concat adapter may throw java.lang.IllegalArgumentException: Cannot find wrapper for global position
                adapter.getItemViewType(currentPos)
            } catch (ignored: Exception) {
                continue
            }

            if (currentType == ViewHolderFactory.TYPE_TITLE || currentType == ViewHolderFactory.TYPE_ART_HEADER) {
                val holder: RecyclerView.ViewHolder = rv.findViewHolderForAdapterPosition(currentPos) ?: continue
                val left = 0f
                var top = if (currentType == ViewHolderFactory.TYPE_TITLE) {
                    holder.itemView.top - spaceTitleVerTop
                } else {
                    holder.itemView.top - spaceOffsetBetweenSection
                }.toFloat()
                val right = rv.measuredWidth.toFloat()
                var bottom = top + spaceOffsetBetweenSection
                val drawDividerFunc: () -> Unit =  {
                    path.reset()
                    path.moveTo(left, top - radius)
                    path.arcTo(left, top - radius * 2, left + radius * 2, top, 180f, -90f, false)
                    path.arcTo(right - radius * 2, top - radius * 2, right, top, 90f, -90f, false)
                    path.arcTo(right - radius * 2, bottom, right, bottom + radius * 2, 0f, -90f, false)
                    path.arcTo(left, bottom, left + radius * 2, bottom + radius * 2, 270f, -90f, false)
                    path.close()
                    canvas.drawPath(path, paintBg)
                }

                drawDividerFunc()

                // draw another path at the bottom of art header
                if (currentType == ViewHolderFactory.TYPE_ART_HEADER) {
                    top = holder.itemView.bottom.toFloat()
                    bottom = top + spaceOffsetBetweenSection
                    drawDividerFunc()
                }
            }
        }
    }
}