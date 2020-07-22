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
data class GetPopularArtParam(val catePath: String?, val query: String?, val offset: Int?, val
limit: Int?)

class GetPopularArtUseCase @Inject constructor(@IoDispatcher dispatcher: CoroutineDispatcher,
                                              private val browseRepository: BrowseRepository):
    UseCase<GetPopularArtParam?, ArtList>(dispatcher) {
    override suspend fun execute(parameters: GetPopularArtParam?): ArtList {
        return browseRepository.g()
    }
}