package com.wind.deviantart.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wind.deviantart.databinding.FragmentUserGalleryBinding
import com.wind.deviantart.databinding.ItemUserGalleryBinding
import com.wind.deviantart.ui.main.topic.UiTopic
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by Phong Huynh on 8/29/2020
 */
private const val EXTRA_DATA = "xData"
@AndroidEntryPoint
class UserGalleryFragment: Fragment() {
    private lateinit var userName: String
    private lateinit var viewBinding: FragmentUserGalleryBinding

    companion object {
        fun newInstance(userName: String): UserGalleryFragment {
            return UserGalleryFragment().apply {
                arguments = bundleOf(EXTRA_DATA to userName)
            }
        }
    }

    private val vmUserGallery by viewModels<UserGalleryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userName = requireArguments()[EXTRA_DATA] as String
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentUserGalleryBinding.inflate(inflater, container, false).apply {
            vm = vmUserGallery
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userGalleryAdapter = UserGalleryAdapter()
        viewBinding.rcv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = userGalleryAdapter
            setHasFixedSize(true)
        }
        vmUserGallery.userName.value = userName
    }
}


@BindingAdapter("data")
fun RecyclerView.loadData(data: PagingData<Art>?) {
    data?.let {
        (adapter as? UserGalleryAdapter)?.apply {
            findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                submitData(it)
            }
        }
    }
}

class UserGalleryAdapter : PagingDataAdapter<Art, RecyclerView.ViewHolder>(object: DiffUtil.ItemCallback<Art>() {
    override fun areItemsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Art, newItem: Art): Boolean {
        return oldItem == newItem
    }
}) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserGalleryViewHolder).apply {
            binding.item = getItem(position)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserGalleryViewHolder(ItemUserGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
        }).apply {
            itemView.setOnClickListener {view ->
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    (getItem(pos) as? UiTopic.ArtModel)?.let {
//                        callback?.onClickArt(view, pos, it.art)
                    }
                }
            }
        }
    }

    class UserGalleryViewHolder(val binding: ItemUserGalleryBinding): RecyclerView.ViewHolder(binding.root)
}