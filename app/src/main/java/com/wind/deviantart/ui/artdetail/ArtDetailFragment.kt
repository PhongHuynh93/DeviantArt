package com.wind.deviantart.ui.artdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.transition.MaterialContainerTransform
import com.wind.deviantart.ArtToDetailNavViewModel
import com.wind.deviantart.databinding.FragmentArtDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtDetailFragment : Fragment() {
    private lateinit var viewBinding: FragmentArtDetailBinding
    private val vmArtToDetailNavViewModel by activityViewModels<ArtToDetailNavViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentArtDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            val data = vmArtToDetailNavViewModel.clickArt.value
            containerTransitionName = data?.transitionName
            item = data?.art
        }
        return viewBinding.root
    }
}