package com.wind.deviantart.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wind.data.model.Art
import com.wind.deviantart.databinding.FragmentNewestArtBinding
import com.wind.deviantart.databinding.ItemBrowseArtBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.PreloadLinearLayoutManager

/**
 * Created by Phong Huynh on 7/22/2020
 */
@AndroidEntryPoint
class BrowseNewestFragment: Fragment() {
    private lateinit var viewBinding: FragmentNewestArtBinding
    private val vmNewestArt by viewModels<NewestArtViewModel>()

    companion object {
        fun newInstance(): BrowseNewestFragment {
            return BrowseNewestFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentNewestArtBinding.inflate(inflater, container, false).apply {
            vm = vmNewestArt
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.rcv.apply {
            layoutManager = PreloadLinearLayoutManager(requireContext()).apply {
                setPreloadItemCount(3)
            }
            adapter = BrowseNewestAdapter()
        }
    }
}

class BrowseNewestAdapter: RecyclerView.Adapter<ViewHolder>() {
    private var data: List<Art> = emptyList()
    var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBrowseArtBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
        }).apply {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos >= 0) {
                    callback?.onClick(pos, data[pos])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = data[position]
        holder.binding.executePendingBindings()
    }

    fun setData(data: List<Art>) {
        this.data = data
        notifyDataSetChanged()
    }



    interface Callback {
        fun onClick(pos: Int, art: Art)
    }
}

class ViewHolder(val binding: ItemBrowseArtBinding): RecyclerView.ViewHolder(binding.root)