package com.wind.deviantart

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import com.google.android.material.transition.Hold
import com.wind.deviantart.ui.MainFragment
import com.wind.deviantart.ui.artdetail.ArtDetailFragment
import com.wind.model.Art
import dagger.hilt.android.AndroidEntryPoint
import util.SingleLiveEvent
import util.addFragment
import java.lang.ref.WeakReference

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val vmArtToDetailNav by viewModels<ArtToDetailNavViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            addFragment(MainFragment().apply {
                exitTransition = Hold()
            }, R.id.root)
        }
        vmArtToDetailNav.clickArt.observe(this) {
            supportFragmentManager.commit {
                addSharedElement(it.weakRefView.get()!!, it.transitionName)
                replace(R.id.root, ArtDetailFragment())
                addToBackStack(null)
            }
        }
    }
}

class ArtToDetailNavViewModel @ViewModelInject constructor() : ViewModel() {
    data class ArtToDetailNavModel(val weakRefView: WeakReference<View>, val art: Art, val transitionName: String)
    val clickArt: SingleLiveEvent<ArtToDetailNavModel> by lazy {
        SingleLiveEvent<ArtToDetailNavModel>()
    }
}