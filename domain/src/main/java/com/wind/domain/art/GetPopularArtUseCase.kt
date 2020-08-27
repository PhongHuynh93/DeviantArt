package com.wind.domain.art

import com.wind.data.Repository
import com.wind.domain.PageUseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Art
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
data class GetPopularArtParam(val catePath: String? = null, val query:
String? = null, val pageSize: Int)

class GetPopularArtUseCase @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val repository: Repository)
    : PageUseCase<GetPopularArtParam, Art>(coroutineDispatcher) {
    override fun execute(parameters: GetPopularArtParam) =
        repository.getPopularDeviations(parameters.catePath, parameters.query, parameters.pageSize)
}