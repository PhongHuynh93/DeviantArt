package com.wind.domain.art

import com.wind.data.RestRepository
import com.wind.domain.PageUseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Art
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 8/11/2020
 */
data class GetTagArtListParam(val pageSize: Int, val tag: String)

class GetTagArtListUseCase @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val restRepository: RestRepository)
    : PageUseCase<GetTagArtListParam, Art>(coroutineDispatcher) {
    override fun execute(parameters: GetTagArtListParam) =
        restRepository.getTagArtDataSource(parameters.pageSize, parameters.tag)
}