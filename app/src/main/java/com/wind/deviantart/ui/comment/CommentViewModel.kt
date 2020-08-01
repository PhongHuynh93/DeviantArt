package com.wind.deviantart.ui.comment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wind.domain.art.GetCommentUseCase
import com.wind.domain.result.Result
import com.wind.model.Comment
import kotlinx.coroutines.launch

/**
 * Created by Phong Huynh on 8/1/2020.
 */
class CommentViewModel @ViewModelInject constructor(private val getCommentUseCase: GetCommentUseCase) : ViewModel() {
    private val _getCommentLiveData: MutableLiveData<List<Comment>> = MutableLiveData()
    val getCommentLiveData: LiveData<List<Comment>> = _getCommentLiveData

    fun getComment(id: String) {
        viewModelScope.launch {
            getCommentUseCase.invoke(id).let {
                when (it) {
                    is Result.Success -> {
                        _getCommentLiveData.value = it.data
                    }
                    is Result.Error -> {

                    }
                    is Result.Loading -> {

                    }
                }
            }
        }
    }
}