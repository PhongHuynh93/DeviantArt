package com.wind.deviantart.ui.artdetail

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.*
import com.google.android.material.transition.MaterialContainerTransform
import com.wind.deviantart.R
import com.wind.deviantart.adapter.HeaderAdapter
import com.wind.deviantart.databinding.FragmentArtDetailBinding
import com.wind.deviantart.databinding.ItemArtBinding
import com.wind.deviantart.util.AdapterType
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.*

private const val NUMB_COLUMN: Int = 2
private const val ENTER_TRANSITION_DURATION: Long = 300
private const val DATA = "data"

@AndroidEntryPoint
class ArtDetailFragment : Fragment() {
    companion object {
        fun newInstance(data: Art): Fragment {
            return ArtDetailFragment().apply {
                arguments = bundleOf(DATA to data)
            }
        }
    }

    private lateinit var art: Art
    private lateinit var viewBinding: FragmentArtDetailBinding
    private val vmArtDetailViewModel by viewModels<ArtDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = ENTER_TRANSITION_DURATION
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentArtDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            art = requireArguments().getParcelable(DATA)!!
            item = art
            vm = vmArtDetailViewModel
            appBar.clipToOutline = true
            appBar.outlineProvider = ViewOutlineProvider.BACKGROUND
            rcv.clipToOutline = true
            rcv.outlineProvider = ViewOutlineProvider.BACKGROUND
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stagGridArtAdapter = StagGridArtAdapter()
        val headerAdapter = HeaderAdapter()
        viewBinding.rcv.apply {
            layoutManager = StaggeredGridLayoutManager(NUMB_COLUMN, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = ConcatAdapter(headerAdapter, stagGridArtAdapter)
            addItemDecoration(SpacesItemDecoration((6 * dp()).toInt()))
            val space = (10 * dp()).toInt()
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
                        AdapterType.TYPE_TITLE -> {
                            outRect.left = space
                            outRect.right = space
                            outRect.top = space
                            outRect.bottom = space
                        }
                    }
                }
            })
        }
        viewLifecycleOwner.lifecycleScope.launch {
            // prevent measuring the rcv during the animation running
            viewBinding.rcv.gone()
            delay(ENTER_TRANSITION_DURATION)
            viewBinding.rcv.show()
        }
        vmArtDetailViewModel.apply {
            getRelatedArtUseCase(art.id)
            relatedArtLiveData.observe(viewLifecycleOwner) { relatedArt ->
                if (headerAdapter.itemCount == 0) {
                    headerAdapter.submitList(listOf(getString(R.string.more_from_artist_header)))
                }
                stagGridArtAdapter.submitList(relatedArt.moreFromArtist)
            }
            close.observe(viewLifecycleOwner, EventObserver {
                requireActivity().onBackPressed()
            })
            openComment.observe(viewLifecycleOwner, EventObserver {
                callBack?.openComment(it)
            })
        }
    }

    private var callBack: CallBack? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CallBack) {
            callBack = context as CallBack
        }
    }

    override fun onDetach() {
        callBack = null
        super.onDetach()
    }

    interface CallBack {
        fun openComment(id: String)
    }
}


private const val ART_TO_DETAIL_TRANSITION_NAME = "art_detail_to_detail_transition"
class StagGridArtAdapter: ListAdapter<Art, StagGridArtAdapter.ViewHolder>(object: DiffUtil
.ItemCallback<Art>() {
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

    override fun getItemViewType(position: Int): Int {
        return AdapterType.TYPE_ART
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArtBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
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
        holder.binding.item = item
        holder.binding.executePendingBindings()
    }

    interface Callback {
        fun onClick(view: View, pos: Int, art: Art, transitionName: String)
    }

    inner class ViewHolder(val binding: ItemArtBinding): RecyclerView.ViewHolder(binding.root)
}
