package com.wind.deviantart

import android.app.ActivityOptions
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.wind.deviantart.ui.artdetail.ArtDetailActivity
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val vmNav by viewModels<NavViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // setup transition
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)
        if (savedInstanceState == null) {
            addFragment(MainFragment(), R.id.root)
        }

        vmNav.apply {
            val lifecycleOwner = this@MainActivity
            openArt.observe(lifecycleOwner, EventObserver {
                if (it.view != null) {
                    val intent = ArtDetailActivity.makeExtra(this@MainActivity, it.artWithCache, it.view.transitionName)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        this@MainActivity,
                        it.view,
                        it.view.transitionName
                    )
                    startActivity(intent, options.toBundle())
                } else {
                    startActivity(ArtDetailActivity.makeExtra(this@MainActivity, it.artWithCache))
                }
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