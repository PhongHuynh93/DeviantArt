package com.wind.deviantart.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentTagBinding
import com.wind.deviantart.databinding.FragmentTopicDetailBinding
import com.wind.deviantart.ui.main.ArtListFragment
import com.wind.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_comment.*
import util.addFragment
import util.setUpToolbar

/**
 * Created by Phong Huynh on 8/7/2020.
 */
private const val EXTRA_DATA = "xData"
@AndroidEntryPoint
class SearchFragment: Fragment() {
    companion object {
        fun newInstance(tag: String): SearchFragment {
            return SearchFragment().apply {
                arguments = bundleOf(EXTRA_DATA to tag)
            }
        }
    }

    private lateinit var tagName: String
    private lateinit var viewBinding: FragmentTagBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tagName = requireArguments()[EXTRA_DATA] as String
        viewBinding = FragmentTagBinding.inflate(inflater, container, false).apply {
            item = tagName
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(ArtListFragment.makeTagInstance(tagName), R.id.container)
        }
        setUpToolbar(toolbar, tagName, true)
    }
}