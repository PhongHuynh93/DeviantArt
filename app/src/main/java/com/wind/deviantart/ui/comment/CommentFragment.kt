package com.wind.deviantart.ui.comment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wind.deviantart.R
import com.wind.deviantart.databinding.ItemCommentBinding
import com.wind.model.Comment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.coroutines.launch
import util.getDimen
import util.setUpToolbar

/**
 * Created by Phong Huynh on 8/1/2020.
 */
private const val ID = "id"

@AndroidEntryPoint
class CommentFragment: Fragment(R.layout.fragment_comment) {
    companion object {
        fun newInstance(id: String): Fragment {
            return CommentFragment().apply {
                arguments = bundleOf(ID to id)
            }
        }
    }

    private val vmCommentViewModel by viewModels<CommentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val commentAdapter = CommentAdapter(viewLifecycleOwner.lifecycleScope)
        setUpToolbar(toolbar, getString(R.string.comment))
        rcv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
            setHasFixedSize(true)
            val verSpace = getDimen(R.dimen.space_small).toInt()
            val hozSpace = getDimen(R.dimen.space_pretty_small).toInt()
            addItemDecoration(object: RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.top = verSpace
                    outRect.bottom = verSpace
                    outRect.left = hozSpace
                    outRect.right = hozSpace
                }
            })
        }
        vmCommentViewModel.apply {
            getComment(requireArguments().getString(ID)!!)
            dataPaging.observe(viewLifecycleOwner) {
                viewLifecycleOwner.lifecycleScope.launch {
                    commentAdapter.submitData(it)
                }
            }
        }
    }
}

class CommentAdapter(private val lifecycleScope: LifecycleCoroutineScope) : PagingDataAdapter<Comment, CommentAdapter
.ViewHolder>(object : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
            scope = lifecycleScope
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item == null) {
            // bind the placeholder ?? what is it
        } else {
            holder.binding.comment = item
            holder.binding.executePendingBindings()
        }
    }

    inner class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)
}