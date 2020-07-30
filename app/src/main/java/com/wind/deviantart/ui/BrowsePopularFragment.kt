package com.wind.deviantart.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.wind.deviantart.ArtToDetailNavViewModel
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentPopularArtBinding
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import util.SpacesItemDecoration
import util.dp
import java.lang.ref.WeakReference

/**
 * Created by Phong Huynh on 7/22/2020
 */
private const val NUMB_COLUMN: Int = 2
@AndroidEntryPoint
class BrowsePopularFragment: Fragment() {
    private lateinit var viewBinding: FragmentPopularArtBinding
    private val vmPopularArt by viewModels<PopularArtViewModel>()
    private val vmArtToDetailNavViewModel by activityViewModels<ArtToDetailNavViewModel>()
    private val browseNewestAdapter = BrowseNewestAdapter()

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
        viewBinding.rcv.apply {
            layoutManager = StaggeredGridLayoutManager(NUMB_COLUMN, StaggeredGridLayoutManager.VERTICAL)
            adapter = browseNewestAdapter.apply {
                callback = object: BrowseNewestAdapter.Callback {
                    override fun onClick(
                        view: View,
                        pos: Int,
                        art: Art,
                        transitionName: String
                    ) {
                        vmArtToDetailNavViewModel.clickArt.value = ArtToDetailNavViewModel.ArtToDetailNavModel(WeakReference(view), art, transitionName)
                    }
                }
            }
            setHasFixedSize(true)
            addItemDecoration(SpacesItemDecoration((6 * dp()).toInt()))
        }
        var skeleton: RecyclerViewSkeletonScreen? = Skeleton.bind(viewBinding.rcv)
            .adapter(browseNewestAdapter)
            .load(R.layout.item_place_holder)
            .shimmer(true)
            .count(10)
            .color(R.color.greyLight)
            .angle(20)
            .duration(1300)
            .maskWidth(1f)
            .frozen(true)
            .show()

        Timber.e("show skeleton")

        viewLifecycleOwner.lifecycleScope.launch {
                // TODO: 7/25/2020 handle the loading state change
                browseNewestAdapter.loadStateFlow.collectLatest { loadState ->
                    Timber.e("load state $loadState")
                    if (loadState.refresh != LoadState.Loading && browseNewestAdapter.itemCount
                        > 0) {
                        skeleton?.let {
                            Timber.e("hide skeleton")
                            it.hide()
                            skeleton = null
                        }
                    }
                }
            }
        vmPopularArt.dataPaging.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch {
                browseNewestAdapter.submitData(it)
            }
        }
    }
}