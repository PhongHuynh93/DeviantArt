package util.recyclerview.pool

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.attachToPreventViewPoolFromClearing
import androidx.recyclerview.widget.factorInCreateTime
import androidx.recyclerview.widget.viewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by Phong Huynh on 9/19/2020
 */
@ExperimentalCoroutinesApi
internal class PrefetchRecycledViewPool(coroutineScope: CoroutineScope): RecyclerView.RecycledViewPool(), ViewHolderPrefetcher {
    private val viewHolderCreator = ViewHolderCreator(coroutineScope, ::putViewFromCreator)

    override fun setViewsCount(viewType: Int, count: Int, holderCreator: (parent: ViewGroup?, viewType: Int) -> RecyclerView.ViewHolder) {
        require(count > 0)
        viewHolderCreator.setPrefetchBound(holderCreator, viewType, count)
    }

    fun prepare() {
        viewHolderCreator.prepare()
        attachToPreventViewPoolFromClearing()
    }

    override fun putRecycledView(scrap: RecyclerView.ViewHolder) {
        val viewType = scrap.itemViewType
        setMaxRecycledViews(viewType, 20)
        super.putRecycledView(scrap)
    }

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        val holder = super.getRecycledView(viewType)
        if (holder == null) {
            viewHolderCreator.itemCreatedOutside(viewType)
        }
        return holder
    }

    override fun clear() {
        viewHolderCreator.clear()
        super.clear()
    }

    private fun putViewFromCreator(scrap: RecyclerView.ViewHolder, creationTimeNs: Long) {
        factorInCreateTime(scrap.viewType, creationTimeNs)
        putRecycledView(scrap)
    }
}