package com.wind.deviantart

import android.app.Activity
import android.content.Intent
import com.wind.deviantart.ui.search.SearchActivity

/**
 * Created by Phong Huynh on 8/8/2020
 */
object Navigator {
    fun openSearch(activity: Activity) {
        activity.startActivity(Intent(activity, SearchActivity::class.java))
    }
}