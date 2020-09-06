package com.wind.deviantart.ui.topic

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.wind.deviantart.NavViewModel
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentArtFictionBinding
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_comment.*
import timber.log.Timber
import util.Event
import util.setUpToolbar

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
    private val vmNav by activityViewModels<NavViewModel>()

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
        setUpToolbar(viewBinding.toolbar, null, true)
        val art = requireArguments()[EXTRA_DATA] as Art
        viewBinding.item = art
        viewBinding.userHeaderContainer.setOnClickListener {
            art.author?.name?.let {
                vmNav.openUser.value = Event(it)
            }
        }
    }
}