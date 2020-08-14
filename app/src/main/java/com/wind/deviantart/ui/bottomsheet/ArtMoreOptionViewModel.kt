package com.wind.deviantart.ui.bottomsheet

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import com.wind.domain.art.DownloadImageParam
import com.wind.domain.art.DownloadImageUseCase

/**
 * Created by Phong Huynh on 8/13/2020
 */
class ArtMoreOptionViewModel @ViewModelInject constructor(private val downloadImageUseCase: DownloadImageUseCase) : ViewModel() {

    private val _errorResString = MutableLiveData<Int>()
    val errorEmptyPath: LiveData<Int> = _errorResString
    private val _close = MutableLiveData<Unit>()
    val close: LiveData<Unit> = _close
    private val _downloadImageState = MediatorLiveData<WorkInfo?>()
    val downloadImageState: LiveData<WorkInfo?> = _downloadImageState
    fun downloadImage(url: String?) {
        _close.value = Unit
        _downloadImageState.addSource(downloadImageUseCase.invoke(DownloadImageParam(url))) {
            _downloadImageState.value = it
        }
    }
}