package com.wind.deviantart.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.wind.domain.art.GetTagListUseCase
import com.wind.domain.comment.GetCommentParam
import com.wind.domain.result.Result
import com.wind.domain.result.data
import com.wind.domain.result.succeeded
import com.wind.domain.result.successOr
import com.wind.model.Comment
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalStateException

/**
 * Created by Phong Huynh on 8/1/2020.
 * https://www.hellsoft.se/instant-search-with-kotlin-coroutines/
 */
class SearchSuggestionViewModel @ViewModelInject constructor(private val getTagListUseCase: GetTagListUseCase) : ViewModel() {
    private val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    val tagList = queryChannel
        .asFlow()
        .debounce(500)
        .mapLatest { tag ->
            try {
                getTagListUseCase(tag)
            } catch (e: CancellationException) {
                Timber.e("More text received - search operation cancelled!")
                Result.Error(e)
            }
        }.map {
            it.data?.tags ?: emptyList()
        }
        .asLiveData()

    fun suggestTag(tag: String) {
        viewModelScope.launch {
            queryChannel.send(tag)
        }
    }
}