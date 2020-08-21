package com.wind.deviantart.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.wind.deviantart.databinding.FragmentUserBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException

/**
 * Created by Phong Huynh on 8/17/2020
 */
private const val EXTRA_DATA = "xData"
@AndroidEntryPoint
class UserFragment: Fragment() {
    companion object {
        fun newInstance(id: String): UserFragment {
            return UserFragment().apply {
                arguments = bundleOf(EXTRA_DATA to id)
            }
        }
    }

    private lateinit var viewBinding: FragmentUserBinding
    private val vmUser by viewModels<UserViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentUserBinding.inflate(inflater, container, false).apply {
//            vm = vmUser
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.vPager.apply {
            adapter =
        }
        TabLayoutMediator(viewBinding.tabLayout, viewBinding.vPager) { tab, pos ->
            // do nothing
        }.attach()
    }
}

class UserInfoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {

            else -> {
                throw IllegalStateException()
            }
        }
         ViewHolder(.inflate(LayoutInflater.from(parent.context), parent, false)
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
        fun onClick(view: View, pos: Int, item: Any)
    }

    inner class ViewHolder(val binding: ) : RecyclerView.ViewHolder(binding.root)
}