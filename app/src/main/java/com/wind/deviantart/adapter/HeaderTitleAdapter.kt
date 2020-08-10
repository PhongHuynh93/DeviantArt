package com.wind.deviantart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wind.deviantart.databinding.ItemTitleBinding
import com.wind.deviantart.util.AdapterType

/**
 * Created by Phong Huynh on 8/1/2020.
 */
class HeaderTitleAdapter : ListAdapter<String, HeaderTitleAdapter.ViewHolder>(object: DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return true
    }
}) {

    override fun getItemViewType(position: Int): Int {
        return AdapterType.TYPE_TITLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // full span if used in staggered layout
        (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = true
        // bind data
        val item = getItem(position)
        holder.binding.text = item
        holder.binding.executePendingBindings()
    }

    inner class ViewHolder(val binding: ItemTitleBinding): RecyclerView.ViewHolder(binding.root)
}

