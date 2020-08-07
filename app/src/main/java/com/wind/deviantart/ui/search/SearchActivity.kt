package com.wind.deviantart.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wind.deviantart.R
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 8/7/2020.
 */
@AndroidEntryPoint
class SearchActivity: AppCompatActivity(R.layout.fragment) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(SearchSuggestionFragment(), R.id.root)
        }
    }
}