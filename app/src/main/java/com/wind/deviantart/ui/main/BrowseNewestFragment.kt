package com.wind.deviantart.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.wind.deviantart.ArtToDetailNavViewModel
import com.wind.deviantart.R
import com.wind.deviantart.databinding.ItemBrowseArtBinding
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import util.Event
import util.SpacesItemDecoration
import util.dp


/**
 * Created by Phong Huynh on 7/22/2020
 */
private const val NUMB_COLUMN: Int = 2
@AndroidEntryPoint
class BrowseNewestFragment: Fragment(R.layout.recyclerview) {
    private val vmNewestArt by viewModels<NewestArtViewModel>()
    private val vmArtToDetailNavViewModel by activityViewModels<ArtToDetailNavViewModel>()

    companion object {
        fun newInstance(): BrowseNewestFragment {
            return BrowseNewestFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val browseNewestAdapter = BrowseNewestAdapter().apply {
            callback = object: BrowseNewestAdapter.Callback {
                override fun onClick(
                    view: View,
                    pos: Int,
                    art: Art,
                    transitionName: String
                ) {
                    vmArtToDetailNavViewModel.clickArt.value = Event(ArtToDetailNavViewModel.ArtToDetailNavModel(
                        view, art, transitionName))
                }
            }
        }
        rcv.apply {
            layoutManager = StaggeredGridLayoutManager(NUMB_COLUMN, StaggeredGridLayoutManager.VERTICAL).apply {
            }
            adapter = browseNewestAdapter
            setHasFixedSize(true)
            addItemDecoration(SpacesItemDecoration((6 * dp()).toInt()))
        }

        var skeleton: RecyclerViewSkeletonScreen? = Skeleton.bind(rcv)
            .adapter(browseNewestAdapter)
            .load(R.layout.item_place_holder)
            .shimmer(true)
            .count(10)
            .color(R.color.greyLight)
            .angle(20)
            .duration(1300)
            .maskWidth(1f)
            .frozen(true)
            .show()

        Timber.e("show skeleton")
        viewLifecycleOwner.lifecycleScope.apply {
            launch {
                // TODO: 7/25/2020 handle the loading state change
                browseNewestAdapter.loadStateFlow.collectLatest { loadState ->
                    Timber.e("load state $loadState")
                    if (loadState.refresh != LoadState.Loading && browseNewestAdapter.itemCount
                        > 0) {
                        skeleton?.let {
                            Timber.e("hide skeleton")
                            it.hide()
                            skeleton = null
                        }
                    }
                }
            }
        }

        vmNewestArt.dataPaging.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch {
                browseNewestAdapter.submitData(it)
            }
        }
    }
}

private const val ART_TO_DETAIL_TRANSITION_NAME = "art_to_detail"
class BrowseNewestAdapter: PagingDataAdapter<Art, BrowseNewestAdapter.ViewHolder>(object: DiffUtil.ItemCallback<Art>() {
    override fun areItemsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem == newItem
    }

}) {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }
    var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBrowseArtBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
        }).apply {
            itemView.setOnClickListener {view ->
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    getItem(pos)?.let {
                        callback?.onClick(view, pos, it, view.transitionName)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.transitionName = "$ART_TO_DETAIL_TRANSITION_NAME$position"
        if (item == null) {
            // bind the placeholder ?? what is it
        } else {
            holder.binding.item = item
            holder.binding.executePendingBindings()
        }
    }


    interface Callback {
        fun onClick(view: View, pos: Int, art: Art, transitionName: String)
    }

    inner class ViewHolder(val binding: ItemBrowseArtBinding): RecyclerView.ViewHolder(binding.root)
}

