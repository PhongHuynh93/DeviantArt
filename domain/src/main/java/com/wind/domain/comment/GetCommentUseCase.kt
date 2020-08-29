package com.wind.domain.comment

import androidx.paging.PagingData
import com.wind.data.Repository
import com.wind.domain.PageUseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Comment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
data class GetCommentParam(val id: String, val pageSize: Int)
class GetCommentUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val repository: Repository
) : PageUseCase<GetCommentParam, Comment>(dispatcher) {
    override fun execute(parameters: GetCommentParam): Flow<PagingData<Comment>> {
        return repository.getComment(parameters.id, parameters.pageSize)
    }
}
