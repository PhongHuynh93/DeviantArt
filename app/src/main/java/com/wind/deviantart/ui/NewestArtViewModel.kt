package com.wind.deviantart.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wind.data.model.Art
import com.wind.data.model.ArtList
import com.wind.domain.art.GetNewestArtUseCase
import com.wind.domain.result.data
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Phong Huynh on 7/21/2020
 */
class NewestArtViewModel @ViewModelInject constructor(private val getNewestArtUseCase:
                                                   GetNewestArtUseCase): ViewModel() {
    private val _data = MutableLiveData<List<Art>>()
    val data: LiveData<List<Art>> = _data

    init {
        viewModelScope.launch {
            // TODO: 7/22/2020 param here
            _data.value = getNewestArtUseCase(null).data?.arts
            Timber.e("$data")
        }
    }
}