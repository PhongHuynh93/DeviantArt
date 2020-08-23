package com.wind.domain.user

import com.wind.data.RestRepository
import com.wind.domain.UseCase
import com.wind.domain.di.IoDispatcher
import com.wind.model.UserInfo
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by Phong Huynh on 8/22/2020
 */
data class GetUserProfileParam(val userName: String, val extCollection: Boolean = false, val extGallery: Boolean = false)
class GetUserProfileUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val restRepository: RestRepository
) : UseCase<GetUserProfileParam, UserInfo>(dispatcher) {

    override suspend fun execute(parameters: GetUserProfileParam): UserInfo {
        return restRepository.getUserInfo(parameters.userName, parameters.extCollection, parameters.extGallery)
    }
}
