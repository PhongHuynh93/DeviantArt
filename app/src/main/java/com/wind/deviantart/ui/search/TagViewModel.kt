package com.wind.deviantart.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.wind.domain.art.*

/**
 * Created by Phong Huynh on 7/21/2020
 */
private const val PAGE_SIZE = 10

class TagViewModel @ViewModelInject constructor(
    private val getTagArtListUseCase: GetTagArtListUseCase
) : ViewModel() {
    val tag = MutableLiveData<String>()

    val dataPaging = tag.distinctUntilChanged().switchMap {tag ->
        getTagArtListUseCase(GetTagArtListParam(pageSize = PAGE_SIZE, tag = tag))
            .cachedIn(viewModelScope)
            .asLiveData()
    }
}