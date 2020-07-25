package com.wind.domain.art

import com.wind.data.RestRepository
import com.wind.domain.PageUseCase
import com.wind.model.Art
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
data class GetPopularArtParam(val catePath: String? = null, val query:
String? = null, val offset: Int? = null, val pageSize: Int)

class GetPopularArtUseCase @Inject constructor(private val restRepository: RestRepository)
    : PageUseCase<GetPopularArtParam, Art>() {
    override fun execute(parameters: GetPopularArtParam) =
        restRepository.getPopularDeviations(parameters.catePath, parameters.query, parameters.offset, parameters.pageSize)
}