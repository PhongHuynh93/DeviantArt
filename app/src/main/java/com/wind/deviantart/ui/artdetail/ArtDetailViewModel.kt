package com.wind.deviantart.ui.artdetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wind.domain.art.GetMoreFromThisArtistUseCase
import com.wind.domain.result.Result
import com.wind.domain.result.data
import com.wind.domain.result.successOr
import com.wind.model.Art
import com.wind.model.RelatedArt
import kotlinx.coroutines.launch
import util.Event

/**
 * Created by Phong Huynh on 7/28/2020
 */
class ArtDetailViewModel @ViewModelInject constructor(private val getMoreFromThisArtistUseCase: GetMoreFromThisArtistUseCase) :
    ViewModel() {
    private val _close: MutableLiveData<Event<Unit>> = MutableLiveData()
    val close: LiveData<Event<Unit>> = _close
    private val _openComment: MutableLiveData<Event<String>> = MutableLiveData()
    val openComment: LiveData<Event<String>> = _openComment
    val id = MutableLiveData<String>()
    private val getMoreFromArtist: LiveData<Result<List<Art>>> = id.distinctUntilChanged()
        .switchMap { id ->
            liveData {
                emit(getMoreFromThisArtistUseCase(id))
            }
        }
    val data: LiveData<List<Art>> = getMoreFromArtist.map {
        it.successOr(emptyList())
    }

    fun clickClose() {
        _close.value = Event(Unit)
    }

    fun clickComment(id: String) {
        _openComment.value = Event(id)
    }
}