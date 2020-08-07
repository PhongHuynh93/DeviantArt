package com.wind.deviantart.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.wind.deviantart.NavViewModel
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentBrowseBinding
import dagger.hilt.android.AndroidEntryPoint
import util.Event

private const val NUMB_PAGE = 3
const val NEWEST_POS = 2
const val POPULAR_POS = 0
const val DAILY_DEVIATION = 1
@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var viewBinding: FragmentBrowseBinding
    private val vmNavViewModel by activityViewModels<NavViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                vmNavViewModel.openSearch.value = Event(Unit)
                true
            }
            else -> {
                false
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentBrowseBinding.inflate(inflater, container, false).apply {
            title = "Browse"
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.vpager.apply {
            adapter = BrowsePagerAdapter(this@MainFragment)
        }

        TabLayoutMediator(viewBinding.tabLayout, viewBinding.vpager) { tab, pos ->
            when (pos) {
                NEWEST_POS -> {
                    tab.text = getString(R.string.title_newest)
                }
                POPULAR_POS -> {
                    tab.text = getString(R.string.title_popular)
                }
                DAILY_DEVIATION -> {
                    tab.text = getString(R.string.title_daily_deviation)
                }
            }
        }.attach()
    }
}

class BrowsePagerAdapter(frag: Fragment) : FragmentStateAdapter(frag) {
    override fun getItemCount(): Int {
        return NUMB_PAGE
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            DAILY_DEVIATION -> {
                DailyDeviationFragment.newInstance()
            }
            else -> {
                ArtListFragment.newInstance(position)
            }
        }
    }
}

