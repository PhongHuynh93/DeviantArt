package com.wind.deviantart.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wind.data.model.Art
import com.wind.domain.art.GetPopularArtParam
import com.wind.domain.art.GetPopularArtUseCase
import com.wind.domain.result.Result
import com.wind.domain.result.data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Phong Huynh on 7/21/2020
 */
private const val PAGE_SIZE = 10

class PopularArtViewModel @ViewModelInject constructor(
    private val getPopularArtUseCase: GetPopularArtUseCase
) : ViewModel() {
    private val _data = MutableLiveData<List<Art>>()
    val data: LiveData<List<Art>> = _data

//    init {
//        viewModelScope.launch {
//
//            when (val result = getPopularArtUseCase(GetPopularArtParam(pageSize = PAGE_SIZE))) {
//                is Result.Success -> {
//                    _data.value = result.data.cachedIn(viewModelScope)
//                }
//                is Result.Error -> {
//                    // TODO: 7/23/2020 handle error state and loading here
//                }
//                is Result.Loading -> {
//
//                }
//            }
//        }
//    }

    fun getData(): Flow<PagingData<Art>> {
        return getPopularArtUseCase(GetPopularArtParam(pageSize = PAGE_SIZE)).cachedIn(viewModelScope)
    }
}