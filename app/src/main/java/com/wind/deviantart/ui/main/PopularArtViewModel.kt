package com.wind.deviantart.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wind.domain.art.GetPopularArtParam
import com.wind.domain.art.GetPopularArtUseCase

/**
 * Created by Phong Huynh on 7/21/2020
 */
private const val PAGE_SIZE = 10

class PopularArtViewModel @ViewModelInject constructor(
    private val getPopularArtUseCase: GetPopularArtUseCase
) : ViewModel() {

    val dataPaging = getPopularArtUseCase(GetPopularArtParam(pageSize = PAGE_SIZE)).cachedIn(viewModelScope).asLiveData()
}