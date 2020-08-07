package com.wind.deviantart.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.wind.domain.art.GetDailyDeviationsUseCase
import com.wind.domain.art.GetMoreFromThisArtistUseCase
import com.wind.domain.result.successOr
import com.wind.model.Art

/**
 * Created by Phong Huynh on 8/6/2020
 */
class DailyDeviationViewModel @ViewModelInject constructor(
    private val getDailyDeviationsUseCase: GetDailyDeviationsUseCase
) :
    ViewModel() {
    val data = liveData {
        emit(getDailyDeviationsUseCase(Unit).successOr(emptyList()))
    }
}