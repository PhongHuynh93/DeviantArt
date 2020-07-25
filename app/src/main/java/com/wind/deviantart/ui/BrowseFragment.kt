package com.wind.deviantart.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.wind.deviantart.R
import com.wind.deviantart.databinding.FragmentBrowseBinding
import dagger.hilt.android.AndroidEntryPoint

private const val NUMB_PAGE = 2
private const val NEWEST_POS = 1
private const val POPULAR_POS = 0
@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var viewBinding: FragmentBrowseBinding

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
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                private var prevMenuItem: MenuItem? = null
                override fun onPageSelected(position: Int) {
                    prevMenuItem?.isChecked = false
                    prevMenuItem = viewBinding.botNav.menu.getItem(position)
                    prevMenuItem!!.isChecked = true
                }
            })

        }
        viewBinding.botNav.apply {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_popular -> {
                        viewBinding.vpager.currentItem = POPULAR_POS
                        true
                    }
                    R.id.nav_newest -> {
                        viewBinding.vpager.currentItem = NEWEST_POS
                        true
                    }
                    else -> false
                }
            }
        }
    }
}

class BrowsePagerAdapter(frag: Fragment) : FragmentStateAdapter(frag) {
    override fun getItemCount(): Int {
        return NUMB_PAGE
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            NEWEST_POS -> {
                BrowseNewestFragment.newInstance()
            }
            POPULAR_POS -> {
                BrowsePopularFragment.newInstance()
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }
}

