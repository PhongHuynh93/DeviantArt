package com.wind.deviantart.ui.main.topic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wind.deviantart.R
import com.wind.model.Topic
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 9/1/2020
 */
private const val EXTRA_DATA = "xData"
@AndroidEntryPoint
class TopicActivity: AppCompatActivity(R.layout.fragment) {
    companion object {
        fun makeExtra(context: Context, topic: Topic): Intent {
            val intent = Intent(context, TopicActivity::class.java)
            intent.putExtra(EXTRA_DATA, topic.name)
            return intent
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(TopicDetailFragment.newInstance(intent.getStringExtra(EXTRA_DATA)!!), R.id.root)
        }
    }
}