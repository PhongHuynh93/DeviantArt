package com.wind.deviantart.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wind.domain.art.GetNewestArtParam
import com.wind.domain.art.GetNewestArtUseCase

/**
 * Created by Phong Huynh on 7/21/2020
 */
private const val PAGE_SIZE = 10

class NewestArtViewModel @ViewModelInject constructor(private val getNewestArtUseCase:
                                                   GetNewestArtUseCase): ViewModel() {
    val dataPaging = getNewestArtUseCase(GetNewestArtParam(pageSize = PAGE_SIZE)).cachedIn(viewModelScope).asLiveData()
}