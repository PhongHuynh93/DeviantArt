package com.wind.domain.art

import com.wind.data.model.ArtList
import com.wind.data.repository.art.BrowseRepository
import com.wind.domain.UseCase
import com.wind.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
data class GetPopularArtParam(val catePath: String? = null, val query:
String? = null, val offset: Int? = null, val limit: Int? = null)

class GetPopularArtUseCase @Inject constructor(@IoDispatcher dispatcher: CoroutineDispatcher,
                                              private val browseRepository: BrowseRepository
):
    UseCase<GetPopularArtParam, ArtList>(dispatcher) {
    override suspend fun execute(parameters: GetPopularArtParam): ArtList {
        return browseRepository.getNewestDeviations()
    }
}