package com.wind.deviantart

import android.app.ActivityOptions
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.wind.deviantart.ui.artdetail.ArtDetailActivity
import com.wind.deviantart.ui.comment.CommentActivity
import com.wind.deviantart.ui.topic.TopicActivity
import com.wind.deviantart.ui.user.UserActivity
import util.EventObserver

/**
 * Created by Phong Huynh on 8/8/2020
 */
private const val TAG: String = "NavigatorHelperFragment"

class NavigatorHelper(private val fragmentManager: FragmentManager) {

    fun run() {
        getLazySingleton(fragmentManager).get()
    }

    private fun getLazySingleton(fragmentManager: FragmentManager): Lazy<NavigatorHelperFragment> {
        return object : Lazy<NavigatorHelperFragment> {
            private var dlFrag: NavigatorHelperFragment? = null

            @Synchronized
            override fun get(): NavigatorHelperFragment {
                if (dlFrag == null) {
                    dlFrag = getNavigatorHelperFragment(fragmentManager)
                }
                return dlFrag!!
            }
        }
    }

    private fun getNavigatorHelperFragment(fragmentManager: FragmentManager): NavigatorHelperFragment {
        var fragment = fragmentManager.findFragmentByTag(TAG)
        val isNewInstance = fragment == null
        if (isNewInstance) {
            fragment = NavigatorHelperFragment()
            fragmentManager
                .beginTransaction()
                .add(fragment, TAG)
                .commitNowAllowingStateLoss()
        }
        return fragment as NavigatorHelperFragment
    }
}

@FunctionalInterface
private fun interface Lazy<V> {
    fun get(): V
}

class NavigatorHelperFragment : Fragment() {
    private val vmNav by activityViewModels<NavViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        vmNav.apply {
            val lifecycleOwner = this@NavigatorHelperFragment
            openArt.observe(lifecycleOwner, EventObserver {
                if (it.view != null) {
                    val intent = ArtDetailActivity.makeExtra(requireActivity(), it.artWithCache, it.view.transitionName)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        requireActivity(),
                        it.view,
                        it.view.transitionName
                    )
                    startActivity(intent, options.toBundle())
                } else {
                    startActivity(ArtDetailActivity.makeExtra(requireActivity(), it.artWithCache))
                }
            })
            openComment.observe(lifecycleOwner, EventObserver {
                startActivity(CommentActivity.makeExtra(requireActivity(), it))
            })
            openSearch.observe(lifecycleOwner, EventObserver {
                // TODO: 9/5/2020 handle open search
//                replaceFragment(
//                    SearchSuggestionFragment.newInstance(), R.id.root, TAG_SEARCH,
//                    isAddBackStack = true, useAnim = true
//                )
            })
            openSearchTag.observe(lifecycleOwner, EventObserver {
                // TODO: 9/5/2020 handle open search tag
//                replaceFragment(
//                    SearchFragment.newInstance(it), R.id.root, TAG_SEARCH_TAG,
//                    isAddBackStack = true, useAnim = true
//                )
            })
            openTopic.observe(lifecycleOwner, EventObserver {
                startActivity(TopicActivity.makeExtra(requireActivity(), it))
            })
            openUser.observe(lifecycleOwner, EventObserver { userName ->
                startActivity(UserActivity.makeExtra(requireActivity(), userName))
            })
        }
    }

}