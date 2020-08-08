package com.wind.deviantart.ui.main.topic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.wind.domain.art.GetTopicsArtParam
import com.wind.domain.art.GetTopicsUseCase
import com.wind.model.Topic
import kotlinx.coroutines.flow.map

/**
 * Created by Phong Huynh on 7/21/2020
 */
private const val PAGE_SIZE = 10
sealed class UiTopic(val topic: Topic, val type: Int) {
    class TopicModel(topic: Topic): UiTopic(topic, TYPE_TOPIC)
    class TitleTopicModel(topic: Topic): UiTopic(topic, TYPE_TITLE)
    companion object {
        const val TYPE_TITLE = 1;
        const val TYPE_TOPIC = 2;
    }
}

class TopicViewModel @ViewModelInject constructor(
    private val getTopicsUseCase: GetTopicsUseCase
) : ViewModel() {

    val dataPaging = getTopicsUseCase(GetTopicsArtParam(pageSize = PAGE_SIZE))
        .map {
            it.map {topic ->
                UiTopic.TopicModel(topic)
            }.insertSeparators<UiTopic.TopicModel, UiTopic> { before: UiTopic.TopicModel?, after: UiTopic.TopicModel? ->
                if (after == null) {
                    // we're at the end of the list
                    null
                } else if (before == null || before.topic != after.topic) {
                    // breed above and below different, show separator
                    UiTopic.TitleTopicModel(after.topic)
                } else {
                    // no separator
                    null
                }
            }
        }
        .cachedIn(viewModelScope)
        .asLiveData()
}