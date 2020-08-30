package com.wind.deviantart.ui.user

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Phong Huynh on 8/29/2020
 */
private const val EXTRA_DATA = "xData"
@AndroidEntryPoint
class UserCommentFragment: Fragment() {
    companion object {
        fun newInstance(userName: String): UserCommentFragment {
            return UserCommentFragment().apply {
                arguments = bundleOf(EXTRA_DATA to userName)
            }
        }
    }
}