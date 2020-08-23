package com.wind.deviantart.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.wind.deviantart.databinding.*
import com.wind.model.UserInfo
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Phong Huynh on 8/17/2020
 */
private const val EXTRA_DATA = "xData"

@AndroidEntryPoint
class UserFragment : Fragment() {
    companion object {
        fun newInstance(userName: String): UserFragment {
            return UserFragment().apply {
                arguments = bundleOf(EXTRA_DATA to userName)
            }
        }
    }

    private lateinit var viewBinding: FragmentUserBinding
    private val vmUser by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmUser.userName.value = requireArguments()[EXTRA_DATA] as String
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentUserBinding.inflate(inflater, container, false).apply {
            vm = vmUser
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.vPager.apply {
            adapter = UserInfoAdapter()
        }
    }
}

@BindingAdapter("data")
fun ViewPager2.setData(userInfo: UserInfo?) {
    userInfo?.let {
        (adapter as? UserInfoAdapter)?.setData(it)
    }
}

private const val TYPE_MAIN = 1
private const val TYPE_PERSONAL = 2
private const val TYPE_INTRODUCTION = 3
private const val TYPE_SOCIAL = 4
class UserInfoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val types = mutableListOf<Int>()
    private val data = mutableListOf<UserInfo>()

    var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MAIN -> {
                UserMainViewHolder(ItemUserInfoMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                    .apply {
                        itemView.setOnClickListener { view ->
                            val pos = bindingAdapterPosition
                            if (pos >= 0) {
                            }
                        }
                    }
            }
            TYPE_PERSONAL -> {
                UserPersonalViewHolder(ItemUserInfoPersonalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                    .apply {
                        itemView.setOnClickListener { view ->
                            val pos = bindingAdapterPosition
                            if (pos >= 0) {
                            }
                        }
                    }
            }
            TYPE_INTRODUCTION -> {
                UserIntroductionViewHolder(ItemUserInfoIntroductionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                    .apply {
                        itemView.setOnClickListener { view ->
                            val pos = bindingAdapterPosition
                            if (pos >= 0) {
                            }
                        }
                    }
            }
            TYPE_SOCIAL -> {
                UserSocialViewHolder(ItemUserInfoSocialBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                    .apply {
                        itemView.setOnClickListener { view ->
                            val pos = bindingAdapterPosition
                            if (pos >= 0) {
                            }
                        }
                    }
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return types[position]
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (types[position]) {
            TYPE_MAIN -> {
                (holder as UserMainViewHolder).apply {
                    binding.item = item
                    binding.executePendingBindings()
                }
            }
            TYPE_INTRODUCTION -> {
                (holder as UserIntroductionViewHolder).apply {
                    binding.item = item
                    binding.executePendingBindings()
                }
            }
            TYPE_PERSONAL -> {
                (holder as UserPersonalViewHolder).apply {
                    binding.item = item
                    binding.executePendingBindings()
                }
            }
            TYPE_SOCIAL -> {
                (holder as UserSocialViewHolder).apply {
                    binding.item = item
                    binding.executePendingBindings()
                }
            }
        }
    }

    fun setData(userInfo: UserInfo) {
        types.clear()
        data.clear()
        types.add(TYPE_MAIN)
        types.add(TYPE_INTRODUCTION)
        types.add(TYPE_PERSONAL)
        types.add(TYPE_SOCIAL)
        data.add(userInfo)
        data.add(userInfo)
        data.add(userInfo)
        data.add(userInfo)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return types.size
    }

    interface Callback {
        fun onClick(view: View, pos: Int, item: Any)
    }

    private inner class UserMainViewHolder(val binding: ItemUserInfoMainBinding) : RecyclerView.ViewHolder(binding.root)
    private inner class UserPersonalViewHolder(val binding: ItemUserInfoPersonalBinding) : RecyclerView.ViewHolder(binding.root)
    private inner class UserIntroductionViewHolder(val binding: ItemUserInfoIntroductionBinding) : RecyclerView.ViewHolder(binding.root)
    private inner class UserSocialViewHolder(val binding: ItemUserInfoSocialBinding) : RecyclerView.ViewHolder(binding.root)


}