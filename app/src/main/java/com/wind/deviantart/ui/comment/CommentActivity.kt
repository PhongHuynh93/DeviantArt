package com.wind.deviantart.ui.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wind.deviantart.R
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 8/19/2020
 */
private const val EXTRA_DATA = "xData"
@AndroidEntryPoint
class CommentActivity: AppCompatActivity(R.layout.fragment) {
    companion object {
        fun makeExtra(context: Context, id: String) = Intent(context, CommentActivity::class.java).apply {
            putExtra(EXTRA_DATA, id)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(CommentFragment.newInstance(intent.getStringExtra(EXTRA_DATA)!!), R.id.root)
        }
    }
}