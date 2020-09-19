package com.wind.deviantart.ui.artdetail

import android.os.Bundle
import android.os.Handler
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.*
import com.wind.deviantart.ArtWithCache
import com.wind.deviantart.NavViewModel
import com.wind.deviantart.OpenArtDetailParam
import com.wind.deviantart.R
import com.wind.deviantart.adapter.HeaderTitleAdapter
import com.wind.deviantart.databinding.FragmentArtDetailBinding
import com.wind.deviantart.databinding.ItemArtBinding
import com.wind.deviantart.databinding.ItemArtInfoBinding
import com.wind.deviantart.ui.bottomsheet.ArtMoreOptionDialog
import com.wind.deviantart.util.ViewHolderFactory
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import util.Event
import util.EventObserver

private const val NUMB_COLUMN: Int = 2
private const val EXTRA_DATA = "xData"
private const val EXTRA_TRANSITION_NAME = "xTransitionName"

@AndroidEntryPoint
class ArtDetailFragment : Fragment() {
    companion object {
        fun newInstance(data: ArtWithCache, transitionName: String?): ArtDetailFragment {
            return ArtDetailFragment().apply {
                arguments = bundleOf(EXTRA_DATA to data, EXTRA_TRANSITION_NAME to transitionName)
            }
        }
    }

    private val handler = Handler()
    private lateinit var viewBinding: FragmentArtDetailBinding
    private val vmArtDetailViewModel by viewModels<ArtDetailViewModel>()
    private val vmNav by activityViewModels<NavViewModel>()

    private val config = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()
    private val headerAdapter = HeaderAdapter().apply {
        lifecycleScope.launchWhenCreated {
            val artWithCache: ArtWithCache = requireArguments().getParcelable(EXTRA_DATA)!!
            submitList(listOf(artWithCache))
            callback = object : HeaderAdapter.Callback {
                override fun onClickComment(pos: Int, item: Art) {
                    vmNav.openComment.value = Event(item.id)
                }

                override fun onClickMore(pos: Int, it: Art) {
                    ArtMoreOptionDialog.newInstance(it.preview?.src).show(childFragmentManager, null)
                }

                override fun onClickUser(pos: Int, art: Art) {
                    art.author?.name?.let {
                        vmNav.openUser.value = Event(it)
                    }
                }
            }
        }
    }
    private val headerTitleAdapter = HeaderTitleAdapter()
    private val stagGridArtAdapter = StagGridArtAdapter().apply {
        callback = object : StagGridArtAdapter.Callback {
            override fun onClick(view: View, pos: Int, art: Art) {
                vmNav.openArt.value = Event(OpenArtDetailParam(view = view, artWithCache = ArtWithCache(art = art, cacheW = view.measuredWidth,
                    cacheH = view.measuredHeight, isThumbCached = view.getTag(R.id.tagThumb) != null)))
            }
        }
    }
    private val artDetailAdapter = ConcatAdapter(config, headerAdapter)
    private val showDetailRunnable = Runnable {
        artDetailAdapter.addAdapter(headerTitleAdapter)
        artDetailAdapter.addAdapter(stagGridArtAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentArtDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = vmArtDetailViewModel
            transitionName = requireArguments().getString(EXTRA_TRANSITION_NAME)
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.rcv.apply {
            layoutManager =
                StaggeredGridLayoutManager(NUMB_COLUMN, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = artDetailAdapter
            addItemDecoration(ArtDetailItemDecoration(requireContext(), artDetailAdapter))
        }
        vmArtDetailViewModel.apply {
            val artWithCache: ArtWithCache = requireArguments().getParcelable(EXTRA_DATA)!!
            id.value = artWithCache.art.id
            data.observe(viewLifecycleOwner) { data ->
                if (headerTitleAdapter.itemCount == 0) {
                    headerTitleAdapter.submitList(listOf(getString(R.string.more_from_artist_header)))
                }
                stagGridArtAdapter.submitList(data)
            }
            close.observe(viewLifecycleOwner, EventObserver {
                requireActivity().onBackPressed()
            })
            openComment.observe(viewLifecycleOwner, EventObserver {
                vmNav.openComment.value = Event(it)
            })
        }
        handler.postDelayed(showDetailRunnable, 600)
    }



    fun onTransitionStart(transition: Transition?) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            handler.removeCallbacks(showDetailRunnable)
        }
    }

    fun onTransitionEnd(transition: Transition?) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            handler.removeCallbacks(showDetailRunnable)
            handler.post(showDetailRunnable)
        }
    }
}


private const val ART_TO_DETAIL_TRANSITION_NAME = "art_detail_to_detail_transition"

class StagGridArtAdapter : ListAdapter<Art, StagGridArtAdapter.ViewHolder>(object : DiffUtil
.ItemCallback<Art>() {
    override fun areItemsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem == newItem
    }

}) {
    var callback: Callback? = null

    override fun getItemViewType(position: Int): Int {
        return ViewHolderFactory.TYPE_ART
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArtBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
            }).apply {
            itemView.setOnClickListener { view ->
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    getItem(pos)?.let {
                        callback?.onClick(view, pos, it)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.transitionName =
            "$ART_TO_DETAIL_TRANSITION_NAME$position"
        holder.binding.item = item
        holder.binding.executePendingBindings()
    }

    interface Callback {
        fun onClick(view: View, pos: Int, art: Art)
    }

    inner class ViewHolder(val binding: ItemArtBinding) : RecyclerView.ViewHolder(binding.root)
}

class HeaderAdapter : ListAdapter<ArtWithCache, HeaderAdapter.ViewHolder>(object : DiffUtil
.ItemCallback<ArtWithCache>() {
    override fun areItemsTheSame(oldItem: ArtWithCache, newItem: ArtWithCache): Boolean {
        return oldItem.art.id == newItem.art.id
    }

    override fun areContentsTheSame(oldItem: ArtWithCache, newItem: ArtWithCache): Boolean {
        return oldItem == newItem
    }
}) {
    var callback: Callback? = null

    override fun getItemViewType(position: Int): Int {
        return ViewHolderFactory.TYPE_ART_HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArtInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            binding.btnComment.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    getItem(pos)?.let {
                        callback?.onClickComment(pos, it.art)
                    }
                }
            }
            binding.imgvMore.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    getItem(pos)?.let {
                        callback?.onClickMore(pos, it.art)
                    }
                }
            }
            val clickUserInfo: (View) -> Unit = {
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    getItem(pos)?.let {
                        callback?.onClickUser(pos, it.art)
                    }
                }
            }
            binding.imgvUserAvatar.setOnClickListener {
                clickUserInfo(it)
            }
            binding.tvUserName.setOnClickListener {
                clickUserInfo(it)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // full span if used in staggered layout
        (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = true
        val item = getItem(position)
        holder.binding.item = item
        holder.binding.executePendingBindings()
    }

    interface Callback {
        fun onClickComment(pos: Int, item: Art)
        fun onClickMore(pos: Int, it: Art)
        fun onClickUser(pos: Int, art: Art)
    }

    inner class ViewHolder(val binding: ItemArtInfoBinding) :
        RecyclerView.ViewHolder(binding.root)
}
