package com.wind.deviantart.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wind.data.model.Art
import com.wind.data.model.ArtList
import com.wind.domain.art.GetNewestArtUseCase
import com.wind.domain.art.GetPopularArtParam
import com.wind.domain.art.GetPopularArtUseCase
import com.wind.domain.result.Result
import com.wind.domain.result.data
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Phong Huynh on 7/21/2020
 */
class PopularArtViewModel @ViewModelInject constructor(
    private val getPopularArtUseCase:
    GetPopularArtUseCase
) : ViewModel() {
    private val _data = MutableLiveData<List<Art>>()
    val data: LiveData<List<Art>> = _data

    init {
        viewModelScope.launch {
            when (val result = getPopularArtUseCase(GetPopularArtParam())) {
                is Result.Success -> {
                    _data.value = result.data.arts
                }
                is Result.Error -> {
                    // TODO: 7/23/2020 handle error state and loading here
                }
                is Result.Loading -> {

                }
            }
        }
    }
}