package com.wind.domain.art

import com.wind.data.model.ArtList
import com.wind.data.repository.art.BrowseRepository
import com.wind.domain.FlowUseCase
import com.wind.domain.UseCase
import com.wind.domain.di.IoDispatcher
import com.wind.domain.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
data class GetNewestArtParam(val catePath: String?, val query: String?, val offset: Int?, val
limit: Int?)

class GetNewestArtUseCase @Inject constructor(@IoDispatcher dispatcher: CoroutineDispatcher,
                                              private val browseRepository: BrowseRepository):
    UseCase<GetNewestArtParam?, ArtList>(dispatcher) {
    override suspend fun execute(parameters: GetNewestArtParam?): ArtList {
        return browseRepository.getNewestDeviations()
    }
}