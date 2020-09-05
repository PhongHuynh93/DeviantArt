package com.wind.deviantart

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.wind.deviantart.ui.main.MainFragment
import com.wind.model.Art
import com.wind.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import util.Event
import util.addFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // setup transition
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)
        if (savedInstanceState == null) {
            addFragment(MainFragment(), R.id.root)
        }

        NavigatorHelper(fragmentManager = supportFragmentManager).run()
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