package util.recyclerview.pool

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Phong Huynh on 9/19/2020
 */
typealias HolderCreator = (fakeParent: ViewGroup, viewType: Int) -> RecyclerView.ViewHolder

interface HolderPrefetcher {
    fun setViewsCount(viewType: Int, count: Int, holderCreator: HolderCreator)
}