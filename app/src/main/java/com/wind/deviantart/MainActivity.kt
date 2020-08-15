package com.wind.deviantart

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import com.wind.deviantart.ui.artdetail.ArtDetailFragment
import com.wind.deviantart.ui.comment.CommentFragment
import com.wind.deviantart.ui.main.MainFragment
import com.wind.deviantart.ui.main.topic.TopicDetailFragment
import com.wind.deviantart.ui.search.SearchFragment
import com.wind.deviantart.ui.search.SearchSuggestionFragment
import com.wind.model.Art
import com.wind.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment.*
import timber.log.Timber
import util.*
import java.lang.ref.WeakReference

private const val TAG_ART_DETAIL = "art_detail"
private const val TAG_COMMENT = "comment"
private const val TAG_TOPIC_DETAIL = "topic_detail"
private const val TAG_SEARCH = "search"
private const val TAG_SEARCH_TAG = "search_tag"
private const val START_CONTAINER_TRANSFORM_DURATION = 400L
private const val END_CONTAINER_TRANSFORM_DURATION = 300L

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val vmNav by viewModels<NavViewModel>()

    private var fragList = mutableListOf<WeakReference<Fragment>>()
    override fun onAttachFragment(childFragment: Fragment) {
        fragList.add(WeakReference(childFragment))
    }

    private fun getActiveFragments(): List<Fragment> {
        val ret = ArrayList<Fragment>()
        for (ref in fragList) {
            val f = ref.get()
            if (f != null) {
                if (f.isVisible) {
                    ret.add(f)
                }
            }
        }
        return ret
    }

    private fun handleBackStack() {
        supportFragmentManager.addOnBackStackChangedListener {
            val curFragment = supportFragmentManager.findFragmentById(R.id.root)
            (curFragment as? BackPressListener)?.apply {
                setUserVisible(true)
            }
            for (frag in getActiveFragments()) {
                if (frag != curFragment) {
                    (frag as? BackPressListener)?.apply {
                        setUserVisible(false)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)
        handleBackStack()
        if (savedInstanceState == null) {
            addFragment(MainFragment(), R.id.root)
        }
        vmNav.apply {
            val lifecycleOwner = this@MainActivity
            openArt.observe(lifecycleOwner, EventObserver {
                val desFrag = ArtDetailFragment.newInstance(it.art)
                addFragment(
                    desFrag, R.id.root, TAG_ART_DETAIL,
                    isAddBackStack = true
                )
                it.view?.let { startView ->
                    startContainerTransformAnimation(startView, desFrag)
                }
            })
            openComment.observe(lifecycleOwner, EventObserver {
                replaceFragment(
                    CommentFragment.newInstance(it), R.id.root, TAG_COMMENT,
                    isAddBackStack = true, useAnim = true
                )
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
                replaceFragment(
                    TopicDetailFragment.newInstance(it), R.id.root, TAG_TOPIC_DETAIL,
                    isAddBackStack = true, useAnim = true
                )
            })
        }
    }

    private fun startContainerTransformAnimation(viewA: View, desFrag: Fragment) {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                supportFragmentManager.backStackEntryCount
                remove()
                val viewB = desFrag.requireView()
                val transform = MaterialContainerTransform().apply {
                    this.startView = viewB
                    this.endView = viewA
                    duration = END_CONTAINER_TRANSFORM_DURATION
                    addTarget(viewA)
                    addListener(object : TransitionListenerAdapter() {
                        override fun onTransitionEnd(transition: Transition) {
                            super.onTransitionEnd(transition)
                            removeListener(this)
                            Timber.e("onTransitionEnd onBackPress")
                            onBackPressed()
                        }
                    })
                }

                TransitionManager.beginDelayedTransition(root, transform)
                viewB.gone()
                viewA.show()
            }
        }
        desFrag.viewLifecycleOwnerLiveData.observe(this) {
            if (it != null) {
                // the view is created
                it.lifecycle.addObserver(object: LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                    fun onCreated() {
                        val viewB = desFrag.requireView()
                        val transform = MaterialContainerTransform().apply {
                            this.startView = viewA
                            this.endView = viewB
                            duration = START_CONTAINER_TRANSFORM_DURATION
                            addTarget(viewB)
                        }

                        TransitionManager.beginDelayedTransition(root, transform)
                        viewA.gone()
                        viewB.show()
                    }
                    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    fun onResume() {
                        onBackPressedDispatcher.addCallback(this@MainActivity, onBackPressedCallback)
                    }
                    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                    fun onPause() {
                        onBackPressedCallback.remove()
                        Timber.e("onPause")
                    }
                    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                    fun onStop() {
                        Timber.e("onStop")
                    }
                })
            } else {
                desFrag.viewLifecycleOwnerLiveData.removeObservers(this)
            }
        }
    }
}

interface BackPressListener {
    fun setUserVisible(userVisible: Boolean)
}

data class OpenArtDetailParam(val view: View? = null, val art: Art)
class NavViewModel @ViewModelInject constructor() : ViewModel() {
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