package com.wind.deviantart.ui.topic

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wind.deviantart.R
import util.getDimen

/**
 * Created by Phong Huynh on 9/6/2020
 */
class TopicItemDecoration(private val context: Context): RecyclerView.ItemDecoration() {
    val spaceLarge = context.getDimen(R.dimen.space_large).toInt()
    val spaceNormal = context.getDimen(R.dimen.space_normal).toInt()
    val spaceSmall = context.getDimen(R.dimen.space_small).toInt()
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        if (pos == RecyclerView.NO_POSITION)
            return
        try {
            val adapter = parent.adapter
            val itemViewType = adapter?.getItemViewType(pos)
            val itemNextViewType = adapter?.getItemViewType(pos + 1)
            when (itemViewType) {
                UiTopic.TYPE_TITLE -> {
                    if (pos == 0) {
                        outRect.top = spaceNormal
                    } else {
                        outRect.top = spaceLarge
                    }
                }
                UiTopic.TYPE_LITERATURE, UiTopic.TYPE_PERSONAL -> {
                    if (itemNextViewType == UiTopic.TYPE_LITERATURE || itemNextViewType == UiTopic.TYPE_PERSONAL) {
                        outRect.bottom = spaceSmall
                    }
                }
            }
        } catch (ignored: Exception) {

        }
    }
}