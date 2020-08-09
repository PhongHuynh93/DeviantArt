package com.wind.domain.art

import com.wind.data.RestRepository
import com.wind.domain.PageUseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Art
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
data class GetTopicDetailArtParam(val pageSize: Int, val topicName: String)

class GetTopicDetailUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val restRepository: RestRepository
) : PageUseCase<GetTopicDetailArtParam, Art>(dispatcher) {
    override fun execute(parameters: GetTopicDetailArtParam) =
        restRepository.getTopicDetail(parameters.pageSize, parameters.topicName)
}
