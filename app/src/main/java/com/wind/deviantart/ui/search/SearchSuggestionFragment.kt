package com.wind.deviantart.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wind.deviantart.databinding.FragmentSearchSuggestionBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Phong Huynh on 8/7/2020.
 */
@AndroidEntryPoint
class SearchSuggestionFragment: Fragment() {
    companion object {
        fun newInstance(): Fragment {
            return SearchSuggestionFragment()
        }
    }

    private lateinit var viewBinding: FragmentSearchSuggestionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentSearchSuggestionBinding.inflate(inflater, container, false).apply {
//            vm = vmSearchSuggestion
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}