package com.wind.deviantart.ui.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wind.deviantart.R
import com.wind.domain.result.Result
import com.wind.domain.result.data
import com.wind.domain.user.GetUserProfileParam
import com.wind.domain.user.GetUserProfileUseCase
import ui.Message
import util.Event

/**
 * Created by Phong Huynh on 8/17/2020
 */
class UserViewModel @ViewModelInject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase
): ViewModel() {
    val userName = MutableLiveData<String>()

    private val userInfoResult = userName.distinctUntilChanged().switchMap { userName ->
        liveData {
            emit(getUserProfileUseCase.invoke(GetUserProfileParam(userName)))
        }
    }

    // null mean no data
    val userInfo = userInfoResult.map {
        it.data
    }

    val errorMessage = userInfoResult.switchMap {
        liveData {
            if (it is Result.Error) {
                emit(Event(Message(messageId = R.string.user_loading_error)))
            }
        }
    }
}