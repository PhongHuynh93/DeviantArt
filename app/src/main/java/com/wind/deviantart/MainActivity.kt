package com.wind.deviantart

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.transition.Hold
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.wind.deviantart.ui.artdetail.ArtDetailActivity
import com.wind.deviantart.ui.main.MainFragment
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import util.Event
import util.EventObserver
import util.addFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val vmArtToDetailNav by viewModels<ArtToDetailNavViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        // Keep system bars (status bar, navigation bar) persistent throughout the transition.
        window.sharedElementsUseOverlay = false

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)

        if (savedInstanceState == null) {
            addFragment(MainFragment().apply {
                exitTransition = Hold()
            }, R.id.root)
        }
        vmArtToDetailNav.clickArt.observe(this, EventObserver {
            startActivity(Intent(this, ArtDetailActivity::class.java).apply {
                putExtra(ArtDetailActivity.TRANSITION_NAME, it.transitionName)
                putExtra(ArtDetailActivity.DATA, it.art)
            }, ActivityOptions.makeSceneTransitionAnimation
                (this, it.view, it.transitionName).toBundle())
        })
    }
}

class ArtToDetailNavViewModel @ViewModelInject constructor() : ViewModel() {
    data class ArtToDetailNavModel(val view: View, val art: Art, val transitionName: String)
    val clickArt: MutableLiveData<Event<ArtToDetailNavModel>> by lazy {
        MutableLiveData<Event<ArtToDetailNavModel>>()
    }
}