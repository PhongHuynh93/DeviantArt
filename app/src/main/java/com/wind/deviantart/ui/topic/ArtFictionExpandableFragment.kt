package com.wind.deviantart.ui.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import com.wind.deviantart.R
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_expandable_page_layout.*
import me.saket.inboxrecyclerview.InboxRecyclerView
import me.saket.inboxrecyclerview.page.PageStateChangeCallbacks
import timber.log.Timber
import util.addFragment
import util.findFragmentByTag
import util.removeFragment
import util.replaceFragment

/**
 * Created by Phong Huynh on 9/6/2020
 */
private const val TAG_FICTION_FRAG = "tag_fiction_frag"
private const val TAG_CHILD_FRAG = "tag_child_frag"
private const val MAX_ALPHA = 0.5f
@AndroidEntryPoint
class ArtFictionExpandableFragment: Fragment() {
    companion object {
        fun newInstance(): ArtFictionExpandableFragment {
            return ArtFictionExpandableFragment()
        }

        fun addViewToRootViewTree(activity: FragmentActivity): ArtFictionExpandableFragment {
            // view created
            var frag = activity.findFragmentByTag(TAG_FICTION_FRAG)
            if (frag == null) {
                frag = newInstance()
                activity.addFragment(frag, android.R.id.content, TAG_FICTION_FRAG)
            }
            return frag as ArtFictionExpandableFragment
        }

        fun removeViewToRootViewTree(activity: FragmentActivity) {
            activity.removeFragment(TAG_FICTION_FRAG)
        }
    }

    private lateinit var inboxRcv: InboxRecyclerView
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Timber.e("onbackpress")
            if (expandPage.isExpanded) {
                Timber.e("onbackpress view is expand, collapse the view")
                expandPage.addStateChangeCallbacks(object: PageStateChangeCallbacks {
                    override fun onPageAboutToExpand(expandAnimDuration: Long) {

                    }

                    override fun onPageExpanded() {
                    }

                    override fun onPageAboutToCollapse(collapseAnimDuration: Long) {
                    }

                    override fun onPageCollapsed() {
                        Timber.e("remove fiction fraction")
                        expandPage.removeStateChangeCallbacks(this)
                        removeFragment(TAG_CHILD_FRAG)
                    }

                    override fun onPageChangeAlpha(expand: Boolean, alpha: Float) {

                    }
                })
                inboxRcv.collapse()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expandable_page_layout, container, false)
    }

    private val onPageStateChangeCallbacks = object: PageStateChangeCallbacks {
        override fun onPageAboutToExpand(expandAnimDuration: Long) {

        }

        override fun onPageExpanded() {
        }

        override fun onPageAboutToCollapse(collapseAnimDuration: Long) {
        }

        override fun onPageCollapsed() {
        }

        override fun onPageChangeAlpha(expand: Boolean, alpha: Float) {
            Timber.e("alpha $alpha")
            alphaView.alpha = (1 - alpha).coerceAtMost(MAX_ALPHA)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandPage.pullToCollapseEnabled = false
        expandPage.animationDurationMillis = 400
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        expandPage.addStateChangeCallbacks(onPageStateChangeCallbacks)
    }

    override fun onDestroyView() {
        expandPage.removeStateChangeCallbacks(onPageStateChangeCallbacks)
        super.onDestroyView()
    }

    fun setRcv(rcv: InboxRecyclerView) {
        inboxRcv = rcv
        viewLifecycleOwnerLiveData.observe(this) {
            it?.let {
                rcv.expandablePage = expandPage
            }
        }
    }

    fun finish() {
        removeViewToRootViewTree(requireActivity())
    }

    fun expandItemAtPos(pos: Int, art: Art) {
        replaceFragment(ArtFictionFragment.newInstance(art), R.id.expandContainer, TAG_CHILD_FRAG)
        inboxRcv.expandItem(pos)
        onBackPressedCallback.isEnabled = true
    }
}