package com.wind.deviantart.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wind.domain.art.GetNewestArtParam
import com.wind.domain.art.GetNewestArtUseCase
import com.wind.model.Art
import kotlinx.coroutines.flow.Flow

/**
 * Created by Phong Huynh on 7/21/2020
 */
private const val PAGE_SIZE = 10

class NewestArtViewModel @ViewModelInject constructor(private val getNewestArtUseCase:
                                                   GetNewestArtUseCase): ViewModel() {

    fun getData(): Flow<PagingData<Art>> {
        return getNewestArtUseCase(GetNewestArtParam(pageSize = PAGE_SIZE)).cachedIn(viewModelScope)
    }
}