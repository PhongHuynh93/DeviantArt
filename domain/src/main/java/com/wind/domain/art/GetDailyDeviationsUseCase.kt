package com.wind.domain.art

import com.wind.data.RestRepository
import com.wind.domain.UseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Art
import com.wind.model.RelatedArt
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
class GetDailyDeviationsUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val restRepository: RestRepository
) : UseCase<Unit, List<Art>>(dispatcher) {
    override suspend fun execute(parameters: Unit): List<Art> {
        return restRepository.getDailyDeviations().data
    }
}
