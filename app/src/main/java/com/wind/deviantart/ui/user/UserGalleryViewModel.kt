package com.wind.deviantart.ui.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.wind.domain.user.GetUserGalleryListParam
import com.wind.domain.user.GetUserGalleryUseCase

/**
 * Created by Phong Huynh on 8/29/2020
 */
private const val PAGE_SIZE = 10

class UserGalleryViewModel @ViewModelInject constructor(
    private val getUserGalleryUseCase: GetUserGalleryUseCase
) : ViewModel() {
    val userName = MutableLiveData<String>()
    val userGalleryDataPaging = userName.distinctUntilChanged().switchMap { userName ->
        getUserGalleryUseCase.invoke(GetUserGalleryListParam(PAGE_SIZE, userName)).cachedIn(viewModelScope).asLiveData()
    }
}