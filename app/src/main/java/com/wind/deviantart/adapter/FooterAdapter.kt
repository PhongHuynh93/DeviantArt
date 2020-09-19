package com.wind.deviantart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wind.deviantart.R
import com.wind.deviantart.util.ViewHolderFactory
import kotlinx.android.synthetic.main.item_footer.view.*

/**
 * Created by Phong Huynh on 8/1/2020.
 * https://github.com/googlecodelabs/android-paging/pull/46/files
 */
class FooterAdapter : RecyclerView.Adapter<FooterAdapter.ViewHolder>() {

    /**
     * LoadState to present in the adapter.
     *
     * Changing this property will immediately notify the Adapter to change the item it's
     * presenting.
     */
    var loadState: LoadState = LoadState.Loading
        set(loadState) {
            if (field != loadState) {
                val displayOldItem = displayLoadStateAsItem(field)
                val displayNewItem = displayLoadStateAsItem(loadState)

                if (displayOldItem && !displayNewItem) {
                    notifyItemRemoved(0)
                } else if (displayNewItem && !displayOldItem) {
                    notifyItemInserted(0)
                } else if (displayOldItem && displayNewItem) {
                    notifyItemChanged(0)
                }
                field = loadState
            }
        }

    /**
     * Returns true if the LoadState should be displayed as a list item when active.
     *
     *  [LoadState.Loading] and [LoadState.Error] present as list items,
     * [LoadState.Done] is not.
     */
    private fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
    }

    override fun getItemViewType(position: Int): Int {
        return ViewHolderFactory.TYPE_FOOTER
    }

    override fun getItemCount(): Int = (if (displayLoadStateAsItem(loadState)) 1 else 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false)).apply {
            itemView.btnRetry.setOnClickListener {
                callback?.retry()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // full span if used in staggered layout
        (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = true
        // bind
        holder.itemView.apply {
            if (loadState is LoadState.Error) {
                tvError.text = (loadState as LoadState.Error).toString()
            }
            progressBar.visibility = toVisibility(loadState == LoadState.Loading)
            btnRetry.visibility = toVisibility(loadState != LoadState.Loading)
            tvError.visibility = toVisibility(loadState != LoadState.Loading)
        }
    }

    private fun toVisibility(constraint: Boolean): Int = if (constraint) {
        View.VISIBLE
    } else {
        View.GONE
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var callback: Callback? = null

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun retry()
    }
}

