package com.wind.deviantart.ui.main.topic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.flatMap
import com.wind.domain.art.GetTopicsArtParam
import com.wind.domain.art.GetTopicsUseCase
import com.wind.model.Art
import com.wind.model.Topic
import kotlinx.coroutines.flow.map

/**
 * Created by Phong Huynh on 7/21/2020
 */
private const val PAGE_SIZE = 10
sealed class UiTopic(val type: Int) {
    data class ArtModel(val art: Art): UiTopic(TYPE_ART)
    data class TitleModel(val topic: Topic): UiTopic(TYPE_TITLE)
    companion object {
        const val TYPE_TITLE = 1;
        const val TYPE_ART = 2;
    }
}
private const val MAX_SHOW_ART_IN_TOPIC = 4
class TopicViewModel @ViewModelInject constructor(
    private val getTopicsUseCase: GetTopicsUseCase
) : ViewModel() {

    val dataPaging = getTopicsUseCase(GetTopicsArtParam(pageSize = PAGE_SIZE))
        .map {
            it.flatMap {topic ->
                val listTopic = mutableListOf<UiTopic>(UiTopic.TitleModel(topic))
                for ((i, art) in topic.listArt.withIndex()) {
                    if (i < MAX_SHOW_ART_IN_TOPIC) {
                        listTopic.add(UiTopic.ArtModel(art))
                    } else {
                        break
                    }
                }
                listTopic
            }
        }
        .cachedIn(viewModelScope)
        .asLiveData()
}