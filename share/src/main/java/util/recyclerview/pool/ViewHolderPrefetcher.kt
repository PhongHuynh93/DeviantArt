package util.recyclerview.pool

import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by Phong Huynh on 9/19/2020
 */
typealias HolderCreator = (parent: ViewGroup?, viewType: Int) -> RecyclerView.ViewHolder

interface ViewHolderPrefetcher {
    fun setViewsCount(viewType: Int, count: Int, holderCreator: HolderCreator)
}

@ExperimentalCoroutinesApi
fun RecyclerView.setPrefetchRecycledViewPool(lifecycleOwner: LifecycleOwner, func: (viewHolder: ViewHolderPrefetcher) -> Unit) {
    val viewPool = PrefetchRecycledViewPool(
        lifecycleOwner.lifecycleScope
    ).also { pool ->
        pool.prepare()
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                Log.e("Prefetch Recyclerview", "onDestroyView, clear the pool")
                lifecycleOwner.lifecycle.removeObserver(this)
                pool.clear()
            }
        })
    }.apply {
        func(this)
    }
    setRecycledViewPool(viewPool)
}