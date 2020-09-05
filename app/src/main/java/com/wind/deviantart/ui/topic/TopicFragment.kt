package com.wind.deviantart.ui.topic

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
import com.wind.deviantart.databinding.ItemTopicLiteratureBinding
import com.wind.deviantart.databinding.ItemTopicTitleBinding
import com.wind.deviantart.util.AdapterType
import com.wind.model.Art
import com.wind.model.ArtType
import com.wind.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import util.Event
import util.getDimen
import util.replaceFragment

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

            override fun onClickArt(view: View, pos: Int, art: Art) {
                vmNavViewModel.openArt.value =
                Event(OpenArtDetailParam(view = view, artWithCache = ArtWithCache(art = art, cacheW = view.measuredWidth,
                    cacheH = view.measuredHeight, isThumbCached = view.getTag(R.id.tagThumb) != null)
                ))
            }

            override fun onClickFiction(pos: Int, art: Art) {
                replaceFragment(ArtFictionFragment.newInstance(art), R.id.expandContainer, isAddBackStack = true)
                viewBinding.rcv.expandItem(pos)
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
            expandablePage = viewBinding.expandPage
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
                            UiTopic.TYPE_TITLE, AdapterType.TYPE_FOOTER, UiTopic.TYPE_LITERATURE, UiTopic.TYPE_PERSONAL -> {
                               2
                            }
                            UiTopic.TYPE_ART -> {
                                1
                            }
                            else -> {
                                1
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
                    try {
                        val itemViewType = adapter?.getItemViewType(pos)
                        val itemNextViewType = adapter?.getItemViewType(pos + 1)
                        when (itemViewType) {
                            UiTopic.TYPE_TITLE -> {
                                outRect.top = spaceLarge
                            }
                            UiTopic.TYPE_LITERATURE, UiTopic.TYPE_PERSONAL -> {
                                if (itemNextViewType == UiTopic.TYPE_LITERATURE || itemNextViewType == UiTopic.TYPE_PERSONAL) {
                                    outRect.bottom = spaceSmall
                                }
                            }
                        }
                        if (pos == 0) {
                            outRect.top = spaceNormal
                        }
                    } catch (ignored: Exception) {

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
            .load(R.layout.item_topic_place_holder)
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

private const val TOPIC_ART_ITEM_TO_DETAIL_TRANSITION_NAME = "topic_art_item_to_detail"
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
                                callback?.onClickArt(view, pos, it.art)
                            }
                        }
                    }
                }
            }
            UiTopic.TYPE_LITERATURE, UiTopic.TYPE_PERSONAL -> {
                TopicLiteratureViewHolder(ItemTopicLiteratureBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
                }).apply {
                    itemView.setOnClickListener {view ->
                        val pos = bindingAdapterPosition
                        if (pos >= 0) {
                            (getItem(pos) as? UiTopic.LiteratureModel)?.let {
                                callback?.onClickFiction(pos, it.art)
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
            when (val itemViewType = getItemViewType(position)) {
                UiTopic.TYPE_TITLE -> {
                    (holder as TitleTopicViewHolder).apply {
                        binding.item = (item as UiTopic.TitleModel).topic
                        binding.executePendingBindings()
                    }
                }
                UiTopic.TYPE_ART -> {
                    (holder as TopicViewHolder).apply {
                        itemView.transitionName = "$TOPIC_ART_ITEM_TO_DETAIL_TRANSITION_NAME$position"
                        binding.item = (item as UiTopic.ArtModel).art
                        binding.executePendingBindings()
                    }
                }
                UiTopic.TYPE_LITERATURE, UiTopic.TYPE_PERSONAL -> {
                    (holder as TopicLiteratureViewHolder).apply {
                        val art = if (itemViewType == UiTopic.TYPE_LITERATURE) {
                            (item as UiTopic.LiteratureModel).art
                        } else {
                            (item as UiTopic.PersonalModel).art
                        }
                        binding.item = art
                        binding.isLiterature = art.category == ArtType.TYPE_LITERATURE
                        binding.executePendingBindings()
                    }
                }
            }

        }
    }

    interface Callback {
        fun onClickTopic(pos: Int, topic: Topic)
        fun onClickArt(pos1: View, pos: Int, art: Art)
        fun onClickFiction(pos: Int, art: Art)
    }

    class TitleTopicViewHolder(val binding: ItemTopicTitleBinding): RecyclerView.ViewHolder(binding.root)
    class TopicViewHolder(val binding: ItemTopicListBinding): RecyclerView.ViewHolder(binding.root)
    class TopicLiteratureViewHolder(val binding: ItemTopicLiteratureBinding): RecyclerView.ViewHolder(binding.root)
}
