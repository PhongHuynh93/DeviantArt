package com.wind.domain.art

import androidx.paging.filter
import com.wind.data.RestRepository
import com.wind.domain.PageUseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Art
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
data class GetNewestArtParam(
    val catePath: String? = null, val query:
    String? = null, val pageSize: Int)

class GetNewestArtUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val restRepository: RestRepository
) : PageUseCase<GetNewestArtParam, Art>(dispatcher) {
    override fun execute(parameters: GetNewestArtParam) =
        restRepository.getNewestDeviations(parameters.catePath, parameters.query, parameters.pageSize)
            .map { pagingData ->
                pagingData.filter { art ->
                    art.preview?.src != null
                }
            }
}
