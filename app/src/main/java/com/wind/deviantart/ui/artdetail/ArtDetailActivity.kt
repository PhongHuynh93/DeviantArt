package com.wind.deviantart.ui.artdetail;

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.transition.Hold
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.wind.deviantart.R
import com.wind.deviantart.ui.comment.CommentFragment
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 8/1/2020.
 */

@AndroidEntryPoint
class ArtDetailActivity: AppCompatActivity(), ArtDetailFragment.CallBack {
    companion object {
        val TRANSITION_NAME = "transitionName"
        val DATA = "data"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        val transitionName = intent.getStringExtra(TRANSITION_NAME)
        findViewById<View>(android.R.id.content).transitionName = transitionName
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 250L
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)
        if (savedInstanceState == null) {
            addFragment(ArtDetailFragment.newInstance(intent.getParcelableExtra(DATA)).apply {
                exitTransition = Hold()
            }, R.id.root)
        }
    }

    override fun openComment(id: String) {
        supportFragmentManager.commit {
            add(R.id.root, CommentFragment.newInstance(id))
            addToBackStack(null)
        }
    }
}
