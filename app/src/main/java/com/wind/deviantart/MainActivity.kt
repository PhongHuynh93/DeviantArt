package com.wind.deviantart

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wind.deviantart.ui.artdetail.ArtDetailFragment
import com.wind.deviantart.ui.comment.CommentFragment
import com.wind.deviantart.ui.main.MainFragment
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import util.Event
import util.EventObserver
import util.addFragment
import util.replaceFragment

private const val TAG_ART_DETAIL = "art_detail"
private const val TAG_COMMENT = "comment"
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val vmNav by viewModels<NavViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)

        if (savedInstanceState == null) {
            addFragment(MainFragment(), R.id.root)
        }
        vmNav.apply {
            openArt.observe(this@MainActivity, EventObserver {
                replaceFragment(ArtDetailFragment.newInstance(it), R.id.root, TAG_ART_DETAIL,
                    isAddBackStack = true, useAnim = true)
            })
            openComment.observe(this@MainActivity, EventObserver {
                replaceFragment(CommentFragment.newInstance(it), R.id.root, TAG_COMMENT,
                    isAddBackStack = true, useAnim = true)
            })
        }
    }
}

class NavViewModel @ViewModelInject constructor() : ViewModel() {
    val openArt: MutableLiveData<Event<Art>> by lazy {
        MutableLiveData<Event<Art>>()
    }
    val openComment: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }
}