package com.wind.deviantart.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.*
import com.wind.deviantart.databinding.FragmentDailyDeviationBinding
import com.wind.deviantart.databinding.ItemArtBinding
import com.wind.deviantart.databinding.ItemArtBindingImpl
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview.*
import util.Event

/**
 * Created by Phong Huynh on 8/6/2020
 */
@AndroidEntryPoint
class DailyDeviationFragment: Fragment() {
    companion object {
        fun newInstance(): DailyDeviationFragment {
            return DailyDeviationFragment()
        }
    }

    private lateinit var viewBinding: FragmentDailyDeviationBinding
    private val vmDailyDeviation by viewModels<DailyDeviationViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentDailyDeviationBinding.inflate(inflater, container, false).apply {
            vm = vmDailyDeviation
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rcv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = DailyDeviationAdapter()
            setHasFixedSize(true)
        }
    }
}

class DailyDeviationAdapter : ListAdapter<Art, DailyDeviationAdapter.ViewHolder>(object : DiffUtil
.ItemCallback<Art>() {
    override fun areItemsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem == newItem
    }
}) {

    init {
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }
    var callback: Callback? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .apply {
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
        holder.binding.item = item
        holder.binding.executePendingBindings()
    }

    interface Callback {
        fun onClick(view: View, pos: Int, item: Art)
    }

    inner class ViewHolder(val binding: ItemArtBinding) : RecyclerView.ViewHolder(binding.root)
}