package com.wind.deviantart.ui.artdetail;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Transition
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.wind.deviantart.ArtWithCache
import com.wind.deviantart.NavigatorHelper
import com.wind.deviantart.R
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 9/2/2020
 */
private const val EXTRA_TRANSITION_NAME = "xTransitionName"
private const val EXTRA_ART = "xArt"
private const val START_TRANSFORM_DURATION = 400L
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
        val art = intent.getParcelableExtra<ArtWithCache>(EXTRA_ART)!!
        val frag = ArtDetailFragment.newInstance(art, null)

        // transition
        val transitionName = intent.getStringExtra(EXTRA_TRANSITION_NAME)
        transitionName?.let {
            findViewById<View>(android.R.id.content).transitionName = transitionName
            setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
            window.sharedElementEnterTransition = MaterialContainerTransform().apply {
                addTarget(android.R.id.content)
                duration = START_TRANSFORM_DURATION
                addListener(object: Transition.TransitionListener {
                    override fun onTransitionStart(transition: Transition?) {
                        frag.onTransitionStart(transition)
                    }

                    override fun onTransitionEnd(transition: Transition?) {
                        frag.onTransitionEnd(transition)
                    }

                    override fun onTransitionCancel(transition: Transition?) {
                    }

                    override fun onTransitionPause(transition: Transition?) {
                    }

                    override fun onTransitionResume(transition: Transition?) {
                    }

                })
            }
            window.sharedElementReturnTransition = MaterialContainerTransform().apply {
                addTarget(android.R.id.content)
                duration = END_TRANSFORM_DURATION
            }
        }
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(frag, R.id.root)
        }
        NavigatorHelper(fragmentManager = supportFragmentManager).run()
    }
}
