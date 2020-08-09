package com.wind.deviantart.ui.main.topic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.wind.domain.art.GetPopularArtParam
import com.wind.domain.art.GetPopularArtUseCase
import com.wind.domain.art.GetTopicDetailArtParam
import com.wind.domain.art.GetTopicDetailUseCase

/**
 * Created by Phong Huynh on 7/21/2020
 */
private const val PAGE_SIZE = 10

class TopicDetailViewModel @ViewModelInject constructor(
    private val getTopicDetailUseCase: GetTopicDetailUseCase
) : ViewModel() {
    val id = MutableLiveData<String>()

    val dataPaging = id.distinctUntilChanged().switchMap {id ->
        getTopicDetailUseCase(GetTopicDetailArtParam(pageSize = PAGE_SIZE, topicName = id))
            .cachedIn(viewModelScope)
            .asLiveData()
    }
}