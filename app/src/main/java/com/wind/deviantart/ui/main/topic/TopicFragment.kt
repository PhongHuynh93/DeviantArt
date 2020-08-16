package com.wind.deviantart.ui.main.topic

import android.graphics.Rect
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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.wind.deviantart.ArtWithCache
import com.wind.deviantart.NavViewModel
import com.wind.deviantart.OpenArtDetailParam
import com.wind.deviantart.R
import com.wind.deviantart.adapter.FooterAdapter
import com.wind.deviantart.databinding.FragmentTopicBinding
import com.wind.deviantart.databinding.ItemTopicListBinding
import com.wind.deviantart.databinding.ItemTopicTitleBinding
import com.wind.deviantart.util.AdapterType
import com.wind.model.Art
import com.wind.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import util.Event
import util.getDimen

/**
 * Created by Phong Huynh on 8/8/2020.
 */
@AndroidEntryPoint
class TopicFragment: Fragment() {
    companion object {
        fun newInstance(): Fragment {
            return TopicFragment()
        }
    }

    private lateinit var viewBinding: FragmentTopicBinding
    private val vmTopicViewModel by viewModels<TopicViewModel>()
    private val vmNavViewModel by activityViewModels<NavViewModel>()

    private val topicAdapter = TopicAdapter().apply {
        callback = object : TopicAdapter.Callback {
            override fun onClickTopic(pos: Int, topic: Topic) {
                vmNavViewModel.openTopic.value = Event(topic)
            }

            override fun onClickArt(pos: Int, art: Art) {
                vmNavViewModel.openArt.value = Event(OpenArtDetailParam(artWithCache = ArtWithCache(art = art)))
            }
        }
    }

    private val footerAdapter = FooterAdapter().apply {
        setCallback(object: FooterAdapter.Callback {
            override fun retry() {
                topicAdapter.retry()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentTopicBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.rcv.apply {
            val config = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()
            val concatAdapter = ConcatAdapter(config, topicAdapter, footerAdapter)
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (try {
                            concatAdapter.getItemViewType(position)
                        } catch (ignored: Exception) {
                            -1
                        }) {
                            UiTopic.TYPE_TITLE -> {
                               2
                            }
                            UiTopic.TYPE_ART -> {
                                1
                            }
                            AdapterType.TYPE_FOOTER -> {
                                2
                            }
                            else -> {
                                2
                            }
                        }
                    }
                }
            }
            adapter = concatAdapter
            setHasFixedSize(true)
            addItemDecoration(object: RecyclerView.ItemDecoration() {
                val spaceLarge = getDimen(R.dimen.space_large).toInt()
                val spaceNormal = getDimen(R.dimen.space_normal).toInt()
                val spaceSmall = getDimen(R.dimen.space_small).toInt()
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val pos = parent.getChildAdapterPosition(view)
                    if (pos == RecyclerView.NO_POSITION)
                        return
                    when (adapter?.getItemViewType(pos)) {
                        UiTopic.TYPE_TITLE -> {
                            outRect.top = spaceNormal
                        }
                    }
                }
            })
        }
        vmTopicViewModel.dataPaging.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch {
                topicAdapter.submitData(it)
            }
        }
        initSkeletonView()
    }

    private fun initSkeletonView() {
        var skeleton: RecyclerViewSkeletonScreen? = Skeleton.bind(viewBinding.rcv)
            .adapter(viewBinding.rcv.adapter)
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
            topicAdapter.loadStateFlow.collectLatest { loadState ->
                Timber.e("load state $loadState")
                if (loadState.refresh != LoadState.Loading && topicAdapter.itemCount > 0) {
                    skeleton?.let {
                        Timber.e("hide skeleton")
                        it.hide()
                        skeleton = null
                    }
                }
                footerAdapter.loadState = loadState.append
            }
        }
    }
}


class TopicAdapter: PagingDataAdapter<UiTopic, RecyclerView.ViewHolder>(object: DiffUtil.ItemCallback<UiTopic>() {
    override fun areItemsTheSame(oldItem: UiTopic, newItem: UiTopic): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UiTopic, newItem: UiTopic): Boolean {
        return true
    }

}) {

    var callback: Callback? = null

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UiTopic.TYPE_TITLE -> {
                TitleTopicViewHolder(ItemTopicTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
                }).apply {
                    itemView.setOnClickListener {view ->
                        val pos = bindingAdapterPosition
                        if (pos >= 0) {
                            (getItem(pos) as? UiTopic.TitleModel)?.let {
                                callback?.onClickTopic(pos, it.topic)
                            }
                        }
                    }
                }
            }
            UiTopic.TYPE_ART -> {
                TopicViewHolder(ItemTopicListBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
                }).apply {
                    itemView.setOnClickListener {view ->
                        val pos = bindingAdapterPosition
                        if (pos >= 0) {
                            (getItem(pos) as? UiTopic.ArtModel)?.let {
                                callback?.onClickArt(pos, it.art)
                            }
                        }
                    }
                }
            }
            else -> {
                throw IllegalStateException()
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item == null) {
            // bind the placeholder ?? what is it
        } else {
            when (getItemViewType(position)) {
                UiTopic.TYPE_TITLE -> {
                    (holder as TitleTopicViewHolder).apply {
                        binding.item = (item as UiTopic.TitleModel).topic
                        binding.executePendingBindings()
                    }
                }
                UiTopic.TYPE_ART -> {
                    (holder as TopicViewHolder).apply {
                        binding.item = (item as UiTopic.ArtModel).art
                        binding.executePendingBindings()
                    }
                }
            }

        }
    }

    interface Callback {
        fun onClickTopic(pos: Int, topic: Topic)
        fun onClickArt(pos: Int, art: Art)
    }

    inner class TitleTopicViewHolder(val binding: ItemTopicTitleBinding): RecyclerView.ViewHolder(binding.root)
    inner class TopicViewHolder(val binding: ItemTopicListBinding): RecyclerView.ViewHolder(binding.root)
}
