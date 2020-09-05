package com.wind.deviantart.ui.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.wind.deviantart.databinding.FragmentArtFictionBinding
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Phong Huynh on 9/5/2020
 */
private const val EXTRA_DATA = "xData"
@AndroidEntryPoint
class ArtFictionFragment: Fragment() {
    companion object {
        fun newInstance(art: Art): ArtFictionFragment {
            return ArtFictionFragment().apply {
                arguments = bundleOf(EXTRA_DATA to art)
            }
        }
    }

    private lateinit var viewBinding: FragmentArtFictionBinding
    private val vmArtFiction by viewModels<ArtFictionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentArtFictionBinding.inflate(inflater, container, false).apply {
            vm = vmArtFiction
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.item = requireArguments()[EXTRA_DATA] as Art
    }
}