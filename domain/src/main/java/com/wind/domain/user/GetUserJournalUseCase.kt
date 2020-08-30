package com.wind.domain.user

import com.wind.data.Repository
import com.wind.domain.PageUseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Art
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 8/29/2020
 */
data class GetUserJournalListParam(val pageSize: Int, val userName: String)

class GetUserJournalUseCase @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val repository: Repository)
    : PageUseCase<GetUserJournalListParam, Art>(coroutineDispatcher) {
    override fun execute(parameters: GetUserJournalListParam) =
        repository.getUserGallery(parameters.userName, parameters.pageSize)
}