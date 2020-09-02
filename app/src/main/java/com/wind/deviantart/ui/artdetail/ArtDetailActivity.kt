package com.wind.deviantart.ui.artdetail;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.wind.deviantart.ArtWithCache
import com.wind.deviantart.R
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 9/2/2020
 */
private const val EXTRA_TRANSITION_NAME = "xTransitionName"
private const val EXTRA_ART = "xArt"
private const val START_TRANSFORM_DURATION = 350L
private const val END_TRANSFORM_DURATION = 300L

@AndroidEntryPoint
class ArtDetailActivity: AppCompatActivity(R.layout.fragment)  {
    companion object {
        fun makeExtra(context: Context, artWithCache: ArtWithCache, transitionName: String) = Intent(context, ArtDetailActivity::class.java).apply {
            putExtra(EXTRA_TRANSITION_NAME, transitionName)
            putExtra(EXTRA_ART, artWithCache)
        }
        fun makeExtra(context: Context, artWithCache: ArtWithCache) = Intent(context, ArtDetailActivity::class.java).apply {
            putExtra(EXTRA_ART, artWithCache)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val transitionName = intent.getStringExtra(EXTRA_TRANSITION_NAME)
        transitionName?.let {
            findViewById<View>(android.R.id.content).transitionName = transitionName
            setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
            window.sharedElementEnterTransition = MaterialContainerTransform().apply {
                addTarget(android.R.id.content)
                duration = START_TRANSFORM_DURATION
            }
            window.sharedElementReturnTransition = MaterialContainerTransform().apply {
                addTarget(android.R.id.content)
                duration = END_TRANSFORM_DURATION
            }
        }
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val art = intent.getParcelableExtra<ArtWithCache>(EXTRA_ART)!!
            addFragment(ArtDetailFragment.newInstance(art, null), R.id.root)
        }
    }
}
