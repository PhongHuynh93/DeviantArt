package com.wind.deviantart.ui.comment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.transition.Hold
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.wind.deviantart.R
import com.wind.deviantart.ui.artdetail.ArtDetailActivity
import com.wind.deviantart.ui.artdetail.ArtDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 8/4/2020
 */
@AndroidEntryPoint
class CommentActivity: AppCompatActivity() {
    companion object {
        const val ID = "xId";
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val enter = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            addTarget(android.R.id.content)
        }
        window.enterTransition = enter
        // Allow Activity A’s exit transition to play at the same time as this Activity’s
        // enter transition instead of playing them sequentially.
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment)
        if (savedInstanceState == null) {
            addFragment(CommentFragment.newInstance(intent.getStringExtra(ID)), R.id.root)
        }
    }
}