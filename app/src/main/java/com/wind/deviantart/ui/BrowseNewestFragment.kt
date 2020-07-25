package com.wind.deviantart.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wind.deviantart.databinding.FragmentNewestArtBinding
import com.wind.deviantart.databinding.ItemBrowseArtBinding
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import ui.PreloadLinearLayoutManager

/**
 * Created by Phong Huynh on 7/22/2020
 */
@AndroidEntryPoint
class BrowseNewestFragment: Fragment() {
    private lateinit var viewBinding: FragmentNewestArtBinding
    private val vmNewestArt by viewModels<NewestArtViewModel>()

    companion object {
        fun newInstance(): BrowseNewestFragment {
            return BrowseNewestFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentNewestArtBinding.inflate(inflater, container, false).apply {
            vm = vmNewestArt
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.rcv.apply {
            layoutManager = PreloadLinearLayoutManager(requireContext()).apply {
                setPreloadItemCount(3)
            }
            adapter = BrowseNewestAdapter()
        }
    }
}

class BrowseNewestAdapter: PagingDataAdapter<Art, ViewHolder>(object: DiffUtil.ItemCallback<Art>() {
    override fun areItemsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem == newItem
    }

}) {
    var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBrowseArtBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
        }).apply {
            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    getItem(pos)?.let {
                        callback?.onClick(pos, it)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item == null) {
            // bind the placeholder ?? what is it
        } else {
            holder.binding.item = item
            holder.binding.executePendingBindings()
        }
    }


    interface Callback {
        fun onClick(pos: Int, art: Art)
    }
}

class ViewHolder(val binding: ItemBrowseArtBinding): RecyclerView.ViewHolder(binding.root)