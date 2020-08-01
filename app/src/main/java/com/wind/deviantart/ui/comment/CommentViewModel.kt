package com.wind.deviantart.ui.comment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wind.domain.comment.GetCommentParam
import com.wind.domain.comment.GetCommentUseCase
import com.wind.model.Comment

/**
 * Created by Phong Huynh on 8/1/2020.
 */
private const val PAGE_SIZE = 10
class CommentViewModel @ViewModelInject constructor(private val getCommentUseCase: GetCommentUseCase) : ViewModel() {
    var dataPaging: LiveData<PagingData<Comment>> = MutableLiveData<PagingData<Comment>>()

    fun getComment(id: String) {
        dataPaging = getCommentUseCase(GetCommentParam(id, pageSize = PAGE_SIZE)).cachedIn(viewModelScope).asLiveData()
    }
}