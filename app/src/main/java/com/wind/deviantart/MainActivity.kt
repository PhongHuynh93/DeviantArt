package com.wind.deviantart

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import com.wind.deviantart.ui.artdetail.ArtDetailFragment
import com.wind.deviantart.ui.comment.CommentActivity
import com.wind.deviantart.ui.main.MainFragment
import com.wind.deviantart.ui.main.topic.TopicActivity
import com.wind.deviantart.ui.search.SearchFragment
import com.wind.deviantart.ui.search.SearchSuggestionFragment
import com.wind.deviantart.ui.user.UserActivity
import com.wind.model.Art
import com.wind.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import util.Event
import util.EventObserver
import util.addFragment
import util.replaceFragment

private const val TAG_ART_DETAIL = "art_detail"
private const val TAG_TOPIC_DETAIL = "topic_detail"
private const val TAG_SEARCH = "search"
private const val TAG_SEARCH_TAG = "search_tag"
private const val START_CONTAINER_TRANSFORM_DURATION = 400L

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val vmNav by viewModels<NavViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)
        if (savedInstanceState == null) {
            addFragment(MainFragment().apply {
                exitTransition = Hold()
            }, R.id.root)
        }
        vmNav.apply {
            val lifecycleOwner = this@MainActivity
            openArt.observe(lifecycleOwner, EventObserver {
                val desFrag = ArtDetailFragment.newInstance(it.artWithCache, it.view?.transitionName).apply {
                    sharedElementEnterTransition = MaterialContainerTransform().apply {
                        duration = START_CONTAINER_TRANSFORM_DURATION
                    }
                    exitTransition = Hold()
                }
                supportFragmentManager
                    .beginTransaction()
                    .apply {
                        it.view?.let { view ->
                            addSharedElement(view, view.transitionName)
                        }
                    }
                    .replace(R.id.root, desFrag, TAG_ART_DETAIL)
                    .addToBackStack(null)
                    .commit()
            })
            openComment.observe(lifecycleOwner, EventObserver {
                startActivity(CommentActivity.makeExtra(this@MainActivity, it))
            })
            openSearch.observe(lifecycleOwner, EventObserver {
                replaceFragment(
                    SearchSuggestionFragment.newInstance(), R.id.root, TAG_SEARCH,
                    isAddBackStack = true, useAnim = true
                )
            })
            openSearchTag.observe(lifecycleOwner, EventObserver {
                replaceFragment(
                    SearchFragment.newInstance(it), R.id.root, TAG_SEARCH_TAG,
                    isAddBackStack = true, useAnim = true
                )
            })
            openTopic.observe(lifecycleOwner, EventObserver {
                startActivity(TopicActivity.makeExtra(this@MainActivity, it))
            })
            openUser.observe(lifecycleOwner, EventObserver { userName ->
                startActivity(UserActivity.makeExtra(this@MainActivity, userName))
            })
        }
    }
}

data class OpenArtDetailParam(val view: View? = null, val artWithCache: ArtWithCache)

@Parcelize
data class ArtWithCache(val art: Art, val cacheW: Int = 0, val cacheH: Int = 0, val isThumbCached: Boolean = false) : Parcelable

class NavViewModel @ViewModelInject constructor() : ViewModel() {
    val openUser: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }
    val openSearchTag: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }
    val openSearch: MutableLiveData<Event<Unit>> by lazy {
        MutableLiveData<Event<Unit>>()
    }
    val openArt: MutableLiveData<Event<OpenArtDetailParam>> by lazy {
        MutableLiveData<Event<OpenArtDetailParam>>()
    }
    val openComment: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }
    val openTopic: MutableLiveData<Event<Topic>> by lazy {
        MutableLiveData<Event<Topic>>()
    }
}