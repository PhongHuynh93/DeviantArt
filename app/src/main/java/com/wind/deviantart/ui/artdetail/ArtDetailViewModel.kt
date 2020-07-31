package com.wind.deviantart.ui.artdetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wind.domain.art.GetRelatedArtUseCase
import com.wind.domain.result.Result
import com.wind.model.RelatedArt
import kotlinx.coroutines.launch

/**
 * Created by Phong Huynh on 7/28/2020
 */
class ArtDetailViewModel @ViewModelInject constructor(private val getRelatedArtUseCase: GetRelatedArtUseCase) :
    ViewModel() {
    private val _relatedArtLiveData: MutableLiveData<RelatedArt> = MutableLiveData()
    val relatedArtLiveData: LiveData<RelatedArt> = _relatedArtLiveData

    fun getRelatedArtUseCase(id: String) {
        viewModelScope.launch {
            getRelatedArtUseCase.invoke(id).let {
                when (it) {
                    is Result.Success -> {
                        _relatedArtLiveData.value = it.data
                    }
                    is Result.Error -> {

                    }
                    is Result.Loading -> {

                    }
                }
            }
        }
    }
}