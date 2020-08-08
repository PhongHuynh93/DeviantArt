package com.wind.domain.art

import com.wind.data.RestRepository
import com.wind.domain.PageUseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Topic
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
data class GetTopicsArtParam(val pageSize: Int)

class GetTopicsUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val restRepository: RestRepository
) : PageUseCase<GetTopicsArtParam, Topic>(dispatcher) {
    override fun execute(parameters: GetTopicsArtParam) =
        restRepository.getTopics(parameters.pageSize)
}
