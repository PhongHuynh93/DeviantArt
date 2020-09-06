package com.wind.deviantart.ui.topic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.flatMap
import com.wind.domain.art.GetTopicsArtParam
import com.wind.domain.art.GetTopicsUseCase
import com.wind.model.Art
import com.wind.model.ArtType
import com.wind.model.Topic
import kotlinx.coroutines.flow.map

/**
 * Created by Phong Huynh on 9/5/2020
 */

class ArtFictionViewModel @ViewModelInject constructor(
    private val getTopicsUseCase: GetTopicsUseCase
) : ViewModel() {

}