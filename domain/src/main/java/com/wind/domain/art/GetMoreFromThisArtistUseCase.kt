package com.wind.domain.art

import com.wind.data.Repository
import com.wind.domain.UseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.Art
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/21/2020
 */
class GetMoreFromThisArtistUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val repository: Repository
) : UseCase<String, List<Art>>(dispatcher) {
    override suspend fun execute(parameters: String): List<Art> {
        return repository.getArtFromThisArtist(parameters).moreFromArtist
    }
}
