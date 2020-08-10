package com.wind.deviantart.ui.artdetail

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.*
import com.google.android.material.transition.MaterialContainerTransform
import com.wind.deviantart.NavViewModel
import com.wind.deviantart.R
import com.wind.deviantart.adapter.HeaderTitleAdapter
import com.wind.deviantart.databinding.FragmentArtDetailBinding
import com.wind.deviantart.databinding.ItemArtBinding
import com.wind.deviantart.databinding.ItemArtInfoBinding
import com.wind.deviantart.util.AdapterType
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
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
    private val vmNav by activityViewModels<NavViewModel>()

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
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stagGridArtAdapter = StagGridArtAdapter().apply {
            callback = object : StagGridArtAdapter.Callback {
                override fun onClick(pos: Int, art: Art) {
                    vmNav.openArt.value = Event(art)
                }
            }
        }
        val headerAdapter = HeaderAdapter().apply {
            submitList(listOf(art))
        }
        val headerTitleAdapter = HeaderTitleAdapter()
        viewBinding.rcv.apply {
            layoutManager =
                StaggeredGridLayoutManager(NUMB_COLUMN, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            val config = ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()
            adapter = ConcatAdapter(config, headerAdapter, headerTitleAdapter, stagGridArtAdapter)
            val spaceArt = getDimen(R.dimen.space_art).toInt()
            val spaceOffsetBetweenSection = (4 * dp()).toInt()
            val spaceTitleVer = getDimen(R.dimen.space_pretty_small).toInt()
            val spaceTitleVerTop = spaceTitleVer + spaceOffsetBetweenSection
            val spaceTitleHoz = getDimen(R.dimen.space_normal).toInt()
            val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = getColorAttr(R.attr.colorPrimary)
            }
            val radius = getDimen(R.dimen.large_radius)
            val path = Path()
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val pos = parent.getChildAdapterPosition(view)
                    if (pos == RecyclerView.NO_POSITION)
                        return
                    when (adapter?.getItemViewType(pos)) {
                        AdapterType.TYPE_TITLE -> {
                            outRect.left = spaceTitleHoz
                            outRect.right = spaceTitleHoz
                            outRect.top = spaceTitleVerTop
                            outRect.bottom = spaceTitleVer - spaceArt / 2
                        }
                        AdapterType.TYPE_ART -> {
                            outRect.top = spaceArt / 2
                            outRect.bottom = spaceArt / 2
                            outRect.left = spaceArt / 2
                            outRect.right = spaceArt / 2
                        }
                    }
                }

                override fun onDrawOver(canvas: Canvas, rv: RecyclerView, state: RecyclerView.State) {
                    super.onDrawOver(canvas, rv, state)
                    val count: Int = rv.childCount
                    if (count < 2 || adapter == null) return

                    val layoutManager = rv.layoutManager as StaggeredGridLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPositions(null).let {
                        if (it.isNotEmpty()) {
                            it[0]
                        } else {
                            -1
                        }
                    }
                    if (firstVisibleItemPosition == -1) return
                    for (i in 0 until count) {
                        val currentPos = firstVisibleItemPosition + i
                        val currentType = try {
                            // concat adapter may throw java.lang.IllegalArgumentException: Cannot find wrapper for global position
                            adapter!!.getItemViewType(currentPos)
                        } catch (ignored: Exception) {
                            continue
                        }

                        if (currentType == AdapterType.TYPE_TITLE || currentType == AdapterType.TYPE_ART_HEADER) {
                            val holder: RecyclerView.ViewHolder = rv.findViewHolderForAdapterPosition(currentPos) ?: continue
                            val left = 0f
                            val top = if (currentType == AdapterType.TYPE_TITLE) {
                                holder.itemView.top - spaceTitleVerTop
                            } else {
                                holder.itemView.top - spaceOffsetBetweenSection
                            }.toFloat()
                            val right = this@apply.measuredWidth.toFloat()
                            val bottom = top + spaceOffsetBetweenSection

                            path.reset()
                            path.moveTo(left, top - radius)
                            path.arcTo(left, top - radius * 2, left + radius * 2, top, 180f, -90f, false)
                            path.arcTo(right - radius * 2, top - radius * 2, right, top, 90f, -90f, false)
                            path.arcTo(right - radius * 2, bottom, right, bottom + radius * 2, 0f, -90f, false)
                            path.arcTo(left, bottom, left + radius * 2, bottom + radius * 2, 270f, -90f, false)
                            path.close()
                            canvas.drawPath(path, paintBg)
                        }
                    }
                }
            })
        }
        vmArtDetailViewModel.apply {
            id.value = art.id
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

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    var callback: Callback? = null

    override fun getItemViewType(position: Int): Int {
        return AdapterType.TYPE_ART
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArtBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
            }).apply {
            itemView.setOnClickListener { view ->
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
        holder.itemView.transitionName =
            "$ART_TO_DETAIL_TRANSITION_NAME$position${holder.hashCode()}"
        holder.binding.item = item
        holder.binding.executePendingBindings()
    }

    interface Callback {
        fun onClick(pos: Int, art: Art)
    }

    inner class ViewHolder(val binding: ItemArtBinding) : RecyclerView.ViewHolder(binding.root)
}

class HeaderAdapter : ListAdapter<Art, HeaderAdapter.ViewHolder>(object : DiffUtil
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
        return AdapterType.TYPE_ART_HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArtInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
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
        // full span if used in staggered layout
        (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = true
        val item = getItem(position)
        holder.binding.item = item
        holder.binding.executePendingBindings()
    }

    interface Callback {
        fun onClick(view: View, pos: Int, item: Art)
    }

    inner class ViewHolder(val binding: ItemArtInfoBinding) :
        RecyclerView.ViewHolder(binding.root)
}
