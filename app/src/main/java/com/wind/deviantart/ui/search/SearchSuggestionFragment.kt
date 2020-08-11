package com.wind.deviantart.ui.search

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wind.deviantart.NavViewModel
import com.wind.deviantart.databinding.FragmentSearchSuggestionBinding
import com.wind.deviantart.databinding.ItemTagBinding
import com.wind.model.TagList
import com.wind.model.TagList.Tag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.Event
import util.dp
import util.showKeyboard

/**
 * Created by Phong Huynh on 8/7/2020.
 */
@AndroidEntryPoint
class SearchSuggestionFragment : Fragment() {
    companion object {
        fun newInstance(): Fragment {
            return SearchSuggestionFragment()
        }
    }

    private lateinit var viewBinding: FragmentSearchSuggestionBinding
    private val vmSearchSuggestionViewModel by viewModels<SearchSuggestionViewModel>()
    private val vmNavViewModel by activityViewModels<NavViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentSearchSuggestionBinding.inflate(inflater, container, false).apply {
//            vm = vmSearchSuggestion
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
        val tagAdapter = TagAdapter().apply {
            callback = object: TagAdapter.Callback {
                override fun onClick(pos: Int, item: Tag) {
                    vmNavViewModel.openSearchTag.value = Event(item.tagName)
                }

            }
        }
        viewBinding.rcv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tagAdapter
            val spaceBetweenRow = 2 * dp()
            addItemDecoration(object: RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.top = spaceBetweenRow.toInt()
                }
            })
        }
        viewBinding.edt.apply {
            if (requestFocus()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    showKeyboard()
                }
            }
            doAfterTextChanged {
                vmSearchSuggestionViewModel.suggestTag(it.toString())
            }
        }
        vmSearchSuggestionViewModel.apply {
            tagList.observe(viewLifecycleOwner) {
                tagAdapter.submitList(it)
            }
        }
    }
}

class TagAdapter : ListAdapter<Tag, TagAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Tag>() {
    override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return true
    }
}) {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    var callback: Callback? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ).apply {
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
        holder.binding.item = item.tagName
        holder.binding.executePendingBindings()
    }

    interface Callback {
        fun onClick(pos: Int, item: Tag)
    }

    inner class ViewHolder(val binding: ItemTagBinding) : RecyclerView.ViewHolder(binding.root)
}