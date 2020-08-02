package com.wind.deviantart.ui.main

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.wind.deviantart.ArtToDetailNavViewModel
import com.wind.deviantart.R
import com.wind.deviantart.adapter.FooterAdapter
import com.wind.deviantart.databinding.ItemBrowseArtBinding
import com.wind.deviantart.util.AdapterType
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import util.Event
import util.SpacesItemDecoration
import util.dp
import util.getDimen

/**
 * Created by Phong Huynh on 7/22/2020
 */
private const val NUMB_COLUMN: Int = 2
private const val EXTRA_TYPE = "type"
@AndroidEntryPoint
class ArtListFragment: Fragment(R.layout.recyclerview) {
    private val vmPopularArt by viewModels<PopularArtViewModel>()
    private val vmNewestArt by viewModels<NewestArtViewModel>()
    private val vmArtToDetailNavViewModel by activityViewModels<ArtToDetailNavViewModel>()
    private val browseNewestAdapter = BrowseNewestAdapter()

    companion object {
        fun newInstance(type: Int): ArtListFragment {
            return ArtListFragment().apply {
                arguments = bundleOf(EXTRA_TYPE to type)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val footerAdapter = FooterAdapter().apply {
            setCallback(object: FooterAdapter.Callback {
                override fun retry() {
                    browseNewestAdapter.retry()
                }
            })
        }
        rcv.apply {
            layoutManager = StaggeredGridLayoutManager(NUMB_COLUMN, StaggeredGridLayoutManager.VERTICAL)
            adapter = ConcatAdapter(browseNewestAdapter.apply {
                callback = object: BrowseNewestAdapter.Callback {
                    override fun onClick(
                        view: View,
                        pos: Int,
                        art: Art,
                        transitionName: String
                    ) {
                        vmArtToDetailNavViewModel.clickArt.value = Event(ArtToDetailNavViewModel.ArtToDetailNavModel(
                            view, art, transitionName))                    }
                }
            }, footerAdapter)
            setHasFixedSize(true)
            addItemDecoration(SpacesItemDecoration((6 * dp()).toInt()))
            // top and bot is 16dp, currently top is 10 plus 6 is 16
            val spaceTop = (10 * dp()).toInt()
            val spaceBot = getDimen(R.dimen.space_normal).toInt()
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val pos = parent.getChildAdapterPosition(view)
                    if (pos == RecyclerView.NO_POSITION)
                        return
                    when (adapter?.getItemViewType(pos)) {
                        AdapterType.TYPE_FOOTER -> {
                            outRect.top = spaceTop
                            outRect.bottom = spaceBot
                        }
                    }
                }
            })
        }
        var skeleton: RecyclerViewSkeletonScreen? = Skeleton.bind(rcv)
            .adapter(rcv.adapter)
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

        viewLifecycleOwner.lifecycleScope.launch {
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
                    footerAdapter.loadState = loadState.append
                }
            }
        val type = requireArguments().getInt(EXTRA_TYPE)
        if (type == POPULAR_POS) {
            vmPopularArt.dataPaging.observe(viewLifecycleOwner) {
                viewLifecycleOwner.lifecycleScope.launch {
                    browseNewestAdapter.submitData(it)
                }
            }
        } else if (type == NEWEST_POS) {
            vmNewestArt.dataPaging.observe(viewLifecycleOwner) {
                viewLifecycleOwner.lifecycleScope.launch {
                    browseNewestAdapter.submitData(it)
                }
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
