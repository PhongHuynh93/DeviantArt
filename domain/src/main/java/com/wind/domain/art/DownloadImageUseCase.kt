package com.wind.domain.art

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.wind.data.RestRepository
import com.wind.domain.LiveDataWorkManagerUseCase
import javax.inject.Inject

/**
 * Created by Phong Huynh on 8/13/2020
 */
data class DownloadImageParam(val url: String?, val fileName: String? = null)
class DownloadImageUseCase @Inject constructor(private val restRepository: RestRepository
): LiveDataWorkManagerUseCase<DownloadImageParam>() {
    override fun execute(parameters: DownloadImageParam): LiveData<WorkInfo> {
        return restRepository.downloadImage(parameters.url, parameters.fileName)
    }
}