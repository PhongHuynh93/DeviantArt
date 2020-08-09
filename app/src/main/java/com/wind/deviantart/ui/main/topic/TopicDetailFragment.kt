package com.wind.deviantart.ui.main.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentTopicDetailBinding
import com.wind.deviantart.ui.main.ArtListFragment
import com.wind.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_comment.*
import util.addFragment
import util.setUpToolbar

/**
 * Created by Phong Huynh on 8/9/2020.
 */
private const val EXTRA_DATA = "xData"
@AndroidEntryPoint
class TopicDetailFragment: Fragment() {
    companion object {
        fun newInstance(topic: Topic): Fragment {
            return TopicDetailFragment().apply {
                arguments = bundleOf(EXTRA_DATA to topic)
            }
        }
    }

    private lateinit var topic: Topic
    private lateinit var viewBinding: FragmentTopicDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        topic = requireArguments()[EXTRA_DATA] as Topic
        viewBinding = FragmentTopicDetailBinding.inflate(inflater, container, false).apply {
            item = topic
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(ArtListFragment.makeTopicInstance(topic.name), R.id.container)
        }
        setUpToolbar(toolbar, getString(R.string.comment), true)
    }
}