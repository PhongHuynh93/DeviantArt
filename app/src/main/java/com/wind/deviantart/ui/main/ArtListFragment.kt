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
import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.wind.deviantart.ArtWithCache
import com.wind.deviantart.NavViewModel
import com.wind.deviantart.OpenArtDetailParam
import com.wind.deviantart.R
import com.wind.deviantart.adapter.FooterAdapter
import com.wind.deviantart.ui.search.TagViewModel
import com.wind.deviantart.ui.topic.TopicDetailViewModel
import com.wind.deviantart.util.ArtViewHolder
import com.wind.deviantart.util.ViewHolderFactory
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import util.Event
import util.dp
import util.getDimen
import util.recyclerview.SpacesItemDecoration
import util.recyclerview.pool.HolderPrefetcher
import util.recyclerview.pool.PrefetchRecycledViewPool

/**
 * Created by Phong Huynh on 7/22/2020
 */
private const val NUMB_COLUMN: Int = 2
private const val EXTRA_TYPE = "type"
private const val EXTRA_TOPIC_NAME = "xTopicName"
private const val EXTRA_TAG = "xTag"
private const val POPULAR_TYPE = 1
private const val NEWEST_TYPE = 2
private const val TOPIC_TYPE = 3
private const val TAG_TYPE = 4

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ArtListFragment: Fragment(R.layout.recyclerview) {
    private var type: Int = -1
    private val vmPopularArt by viewModels<PopularArtViewModel>()
    private val vmNewestArt by viewModels<NewestArtViewModel>()
    private val vmTopic by viewModels<NewestArtViewModel>()
    private val vmTopicDetail by viewModels<TopicDetailViewModel>()
    private val vmTagViewModel by viewModels<TagViewModel>()

    private val vmArtToDetailNavViewModel by activityViewModels<NavViewModel>()
    private val browseNewestAdapter = BrowseNewestAdapter()

    companion object {
        private fun newInstance(type: Int): ArtListFragment {
            return ArtListFragment().apply {
                arguments = bundleOf(EXTRA_TYPE to type)
            }
        }

        fun makePopularInstance() = newInstance(POPULAR_TYPE)
        fun makeNewestInstance() = newInstance(NEWEST_TYPE)
        fun makeTopicInstance(topicName: String): ArtListFragment {
            return ArtListFragment().apply {
                arguments = bundleOf(EXTRA_TYPE to TOPIC_TYPE, EXTRA_TOPIC_NAME to topicName)
            }
        }
        fun makeTagInstance(tag: String): ArtListFragment {
            return ArtListFragment().apply {
                arguments = bundleOf(EXTRA_TYPE to TAG_TYPE, EXTRA_TAG to tag)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = requireArguments().getInt(EXTRA_TYPE)
        when (type) {
            TOPIC_TYPE -> {
                vmTopicDetail.id.value = requireArguments()[EXTRA_TOPIC_NAME] as String
            }
            TAG_TYPE -> {
                vmTagViewModel.tag.value = requireArguments()[EXTRA_TAG] as String
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val footerAdapter = FooterAdapter().apply {
            setCallback(object : FooterAdapter.Callback {
                override fun retry() {
                    browseNewestAdapter.retry()
                }
            })
        }
        rcv.apply {
//        create the viewpool ahead of time
            val viewPool = PrefetchRecycledViewPool(
                requireContext(),
                viewLifecycleOwner.lifecycleScope
            ).also { pool ->
                pool.prepare()
                viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroy() {
                        Timber.e("onDestroyView, clear the pool")
                        viewLifecycleOwner.lifecycle.removeObserver(this)
                        pool.clear()
                    }
                })
            }
            setRecycledViewPool(viewPool)
            prefetchItems(viewPool)
            layoutManager = StaggeredGridLayoutManager(NUMB_COLUMN, StaggeredGridLayoutManager.VERTICAL)

            val config = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()
            adapter = ConcatAdapter(config, browseNewestAdapter.apply {
                callback = object : BrowseNewestAdapter.Callback {
                    override fun onClick(
                        view: View,
                        pos: Int,
                        art: Art,
                        transitionName: String
                    ) {
                        vmArtToDetailNavViewModel.openArt.value =
                            Event(
                                OpenArtDetailParam(
                                    view = view, artWithCache = ArtWithCache(
                                        art = art, cacheW = view.measuredWidth,
                                        cacheH = view.measuredHeight, isThumbCached = view.getTag(R.id.tagThumb) != null
                                    )
                                )
                            )
                    }
                }
            }, footerAdapter)
            setHasFixedSize(true)
            val spaceArt = getDimen(R.dimen.space_art).toInt()
            val halfSpace = spaceArt / 2
            setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
            addItemDecoration(SpacesItemDecoration(halfSpace))
            // top and bot is 16dp, currently top is 10 plus 6 is 16
            val spaceTopFooter = (10 * dp()).toInt()
            val spaceBotFooter = getDimen(R.dimen.space_normal).toInt()
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
                        ViewHolderFactory.TYPE_FOOTER -> {
                            outRect.top = spaceTopFooter
                            outRect.bottom = spaceBotFooter
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

        viewLifecycleOwner.lifecycleScope.launch {
                browseNewestAdapter.loadStateFlow.collectLatest { loadState ->
                    if (loadState.refresh != LoadState.Loading && browseNewestAdapter.itemCount
                        > 0) {
                        skeleton?.let {
                            it.hide()
                            skeleton = null
                        }
                    }
                    footerAdapter.loadState = loadState.append
                }
            }
        when (type) {
            POPULAR_TYPE -> {
                vmPopularArt.dataPaging.observe(viewLifecycleOwner) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        browseNewestAdapter.submitData(it)
                    }
                }
            }
            NEWEST_TYPE -> {
                vmNewestArt.dataPaging.observe(viewLifecycleOwner) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        browseNewestAdapter.submitData(it)
                    }
                }
            }
            TOPIC_TYPE -> {
                vmTopicDetail.dataPaging.observe(viewLifecycleOwner) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        browseNewestAdapter.submitData(it)
                    }
                }
            }
            TAG_TYPE -> {
                vmTagViewModel.dataPaging.observe(viewLifecycleOwner) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        browseNewestAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun prefetchItems(holderPrefetcher: HolderPrefetcher) {
        holderPrefetcher.apply {
            val artCount = 10
            val layoutInflater = LayoutInflater.from(requireContext())
            setViewsCount(ViewHolderFactory.TYPE_ART, artCount) { parent, viewType ->
                Timber.e("create ahead view holder with viewType $viewType")
                ViewHolderFactory.createHolder(layoutInflater, parent, viewType)
            }
        }
    }
}

private const val ART_TO_DETAIL_TRANSITION_NAME = "art_to_detail"
class BrowseNewestAdapter: PagingDataAdapter<Art, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Art>() {
    override fun areItemsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem == newItem
    }

}) {

    var callback: Callback? = null
    private lateinit var layoutInflater: LayoutInflater
    override fun getItemViewType(position: Int): Int {
        return ViewHolderFactory.TYPE_ART
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Timber.e("onCreateViewHolder viewType %d", viewType)
        if (!this::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        return ViewHolderFactory.createHolder(layoutInflater, parent, viewType).apply {
            itemView.setOnClickListener { view ->
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    getItem(pos)?.let {
                        callback?.onClick(view, pos, it, view.transitionName)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.transitionName = "$ART_TO_DETAIL_TRANSITION_NAME$position"
        if (item == null) {
            // bind the placeholder ?? what is it
        } else if (holder is ArtViewHolder) {
            holder.binding.item = item
            holder.binding.executePendingBindings()
        }
    }


    interface Callback {
        fun onClick(view: View, pos: Int, art: Art, transitionName: String)
    }
}
