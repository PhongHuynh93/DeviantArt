package com.wind.deviantart.ui.artdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.*
import com.google.android.material.transition.MaterialContainerTransform
import com.wind.deviantart.ArtToDetailNavViewModel
import com.wind.deviantart.databinding.FragmentArtDetailBinding
import com.wind.deviantart.databinding.ItemArtInfoBinding
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_art_detail.view.*
import util.SpacesItemDecoration
import util.dp

@AndroidEntryPoint
class ArtDetailFragment : Fragment() {
    private lateinit var viewBinding: FragmentArtDetailBinding
    private val vmArtToDetailNavViewModel by activityViewModels<ArtToDetailNavViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentArtDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            val data = vmArtToDetailNavViewModel.clickArt.value
            containerTransitionName = data?.transitionName
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.rcv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            // TODO: 7/28/2020 use merge adapter here
            adapter = ArtDetailAdapter().apply {
                submitList(listOf(vmArtToDetailNavViewModel.clickArt.value!!.art))
            }
        }
    }
}

class ArtDetailAdapter : ListAdapter<Art, ArtDetailAdapter.ViewHolder>(object: DiffUtil
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArtInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .apply {
        }).apply {
            itemView.setOnClickListener {view ->
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
        holder.binding.item = item
        holder.binding.executePendingBindings()
    }

    interface Callback {
        fun onClick(view: View, pos: Int, item: Art)
    }

    inner class ViewHolder(val binding: ItemArtInfoBinding): RecyclerView.ViewHolder(binding.root)
}

