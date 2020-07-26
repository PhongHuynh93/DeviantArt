package com.wind.deviantart.ui.artdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialContainerTransform
import com.wind.deviantart.databinding.FragmentArtDetailBinding

private const val EXTRA_CONTAINER_TRANSITION_NAME = "containerTransition"
class ArtDetailFragment : Fragment() {
    private lateinit var viewBinding: FragmentArtDetailBinding

    companion object {

        fun newInstance(transitionName: String): Fragment {
            return ArtDetailFragment().apply {
                arguments = bundleOf(EXTRA_CONTAINER_TRANSITION_NAME to transitionName)
            }
        }
    }

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
            containerTransitionName = arguments?.getString(EXTRA_CONTAINER_TRANSITION_NAME)
        }
        return viewBinding.root
    }
}