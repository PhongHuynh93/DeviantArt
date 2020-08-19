package com.wind.deviantart.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wind.deviantart.databinding.FragmentUserBinding
import dagger.hilt.android.AndroidEntryPoint

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
//        viewBinding.vPager.apply {
//            adapter =
//        }
//        TabLayoutMediator(viewBinding.tabLayout, viewBinding.vPager) { tab, pos ->
//            // do nothing
//        }.attach()
    }
}