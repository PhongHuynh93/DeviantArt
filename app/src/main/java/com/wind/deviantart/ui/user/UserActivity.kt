package com.wind.deviantart.ui.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wind.deviantart.R
import dagger.hilt.android.AndroidEntryPoint
import util.addFragment

/**
 * Created by Phong Huynh on 8/19/2020
 */
@AndroidEntryPoint
class UserActivity: AppCompatActivity(R.layout.fragment) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addFragment(UserFragment(), R.id.root)
        }
    }
}