package com.wind.deviantart.ui.artdetail;

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.transition.Hold
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.wind.deviantart.R
import com.wind.deviantart.ui.comment.CommentActivity
import com.wind.deviantart.ui.comment.CommentFragment
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 8/1/2020.
 */

@AndroidEntryPoint
class ArtDetailActivity: AppCompatActivity(), ArtDetailFragment.CallBack {
    companion object {
        const val TRANSITION_NAME = "transitionName"
        const val DATA = "data"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        // add share transition when click art
        val transitionName = intent.getStringExtra(TRANSITION_NAME)
        findViewById<View>(android.R.id.content).transitionName = transitionName
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 400L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }

        // add shared axis transition when click comment
        val exit = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            addTarget(android.R.id.content)
        }
        window.exitTransition = exit

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)
        if (savedInstanceState == null) {
            addFragment(ArtDetailFragment.newInstance(intent.getParcelableExtra(DATA)), R.id.root)
        }
    }

    override fun openComment(id: String) {
        startActivity(Intent(this, CommentActivity::class.java).apply {
            putExtra(CommentActivity.ID, id)
        })
    }
}
