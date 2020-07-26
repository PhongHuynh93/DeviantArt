package com.wind.deviantart.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentNewestArtBinding
import com.wind.deviantart.databinding.ItemBrowseArtBinding
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ui.SpacesItemDecoration


/**
 * Created by Phong Huynh on 7/22/2020
 */
private const val NUMB_COLUMN: Int = 2
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
        val browseNewestAdapter = BrowseNewestAdapter()
        viewBinding.rcv.apply {
            layoutManager = StaggeredGridLayoutManager(NUMB_COLUMN, StaggeredGridLayoutManager.VERTICAL).apply {
            }
            adapter = browseNewestAdapter
            setHasFixedSize(true)
            addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelOffset(R.dimen.space_tiny)))
        }

        viewLifecycleOwner.lifecycleScope.apply {
            launch {
                // TODO: 7/25/2020 handle the loading state change
                browseNewestAdapter.loadStateFlow.collectLatest { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Loading -> {

                        }
                        is LoadState.NotLoading -> {

                        }
                        is LoadState.Error -> {

                        }
                    }
                }
            }
            launchWhenCreated {
                vmNewestArt.getData().collectLatest {
                    browseNewestAdapter.submitData(it)
                }
            }
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