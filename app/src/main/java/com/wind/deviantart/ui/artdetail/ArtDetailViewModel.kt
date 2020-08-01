package com.wind.deviantart.ui.artdetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wind.domain.art.GetMoreFromThisArtistUseCase
import com.wind.domain.result.Result
import com.wind.model.RelatedArt
import kotlinx.coroutines.launch
import util.Event

/**
 * Created by Phong Huynh on 7/28/2020
 */
class ArtDetailViewModel @ViewModelInject constructor(private val getMoreFromThisArtistUseCase: GetMoreFromThisArtistUseCase) :
    ViewModel() {
    private val _relatedArtLiveData: MutableLiveData<RelatedArt> = MutableLiveData()
    val relatedArtLiveData: LiveData<RelatedArt> = _relatedArtLiveData
    private val _close: MutableLiveData<Event<Unit>> = MutableLiveData()
    val close: LiveData<Event<Unit>> = _close
    private val _openComment: MutableLiveData<Event<String>> = MutableLiveData()
    val openComment: LiveData<Event<String>> = _openComment

    fun getRelatedArtUseCase(id: String) {
        viewModelScope.launch {
            getMoreFromThisArtistUseCase.invoke(id).let {
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

    fun clickClose() {
        _close.value = Event(Unit)
    }

    fun clickComment(id: String) {
        _openComment.value = Event(id)
    }
}