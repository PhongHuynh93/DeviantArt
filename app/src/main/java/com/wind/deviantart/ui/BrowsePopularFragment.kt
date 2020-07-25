package com.wind.deviantart.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.wind.deviantart.databinding.FragmentPopularArtBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ui.PreloadLinearLayoutManager

/**
 * Created by Phong Huynh on 7/22/2020
 */
@AndroidEntryPoint
class BrowsePopularFragment: Fragment() {
    private lateinit var viewBinding: FragmentPopularArtBinding
    private val vmPopularArt by viewModels<PopularArtViewModel>()

    companion object {
        fun newInstance(): BrowsePopularFragment {
            return BrowsePopularFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentPopularArtBinding.inflate(inflater, container, false).apply {
            vm = vmPopularArt
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val browseNewestAdapter = BrowseNewestAdapter()
        viewBinding.rcv.apply {
            layoutManager = PreloadLinearLayoutManager(requireContext()).apply {
                setPreloadItemCount(3)
            }
            adapter = browseNewestAdapter
        }

        viewLifecycleOwner.lifecycleScope.apply {
            launch {
                browseNewestAdapter.loadStateFlow.collectLatest { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Loading -> {

                        }
                        is LoadState.NotLoading -> {

                        }
                        is LoadState.Error -> {

                        }
                    }
                }
            }
            launchWhenCreated {
                vmPopularArt.getData().collectLatest {
                    browseNewestAdapter.submitData(it)
                }
            }
        }
    }
}