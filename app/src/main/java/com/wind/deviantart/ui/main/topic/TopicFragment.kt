package com.wind.deviantart.ui.main.topic

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentTopicBinding
import com.wind.deviantart.databinding.ItemTopicListBinding
import com.wind.deviantart.databinding.ItemTopicTitleBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import util.dp
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
        val topicAdapter = TopicAdapter()

        viewBinding.rcv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (topicAdapter.getItemViewType(position)) {
                            UiTopic.TYPE_TITLE -> {
                               2
                            }
                            UiTopic.TYPE_ART -> {
                                1
                            }
                            else -> {
                                2
                            }
                        }
                    }
                }
            }
            adapter = topicAdapter
            setHasFixedSize(true)
            addItemDecoration(object: RecyclerView.ItemDecoration() {
                val spaceLarge = getDimen(R.dimen.space_large).toInt()
                val spaceNormal = getDimen(R.dimen.space_normal).toInt()
                val spaceSmall = getDimen(R.dimen.space_small).toInt()
                val spaceHoz = (6 * dp()).toInt()

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
                            outRect.left = spaceNormal
                            outRect.right = spaceNormal
                            outRect.top = spaceLarge
                            outRect.bottom = spaceSmall
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

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }
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
                            getItem(pos)?.let {
                                callback?.onClick(pos, it)
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
                            getItem(pos)?.let {
                                callback?.onClick(pos, it)
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
        fun onClick(pos: Int, art: UiTopic)
    }

    inner class TitleTopicViewHolder(val binding: ItemTopicTitleBinding): RecyclerView.ViewHolder(binding.root)
    inner class TopicViewHolder(val binding: ItemTopicListBinding): RecyclerView.ViewHolder(binding.root)
}
