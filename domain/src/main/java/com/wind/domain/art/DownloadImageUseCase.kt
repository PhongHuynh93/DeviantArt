package com.wind.domain.art

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.wind.data.Repository
import com.wind.domain.LiveDataWorkManagerUseCase
import javax.inject.Inject

/**
 * Created by Phong Huynh on 8/13/2020
 */
data class DownloadImageParam(val url: String?, val fileName: String? = null)
class DownloadImageUseCase @Inject constructor(private val repository: Repository
): LiveDataWorkManagerUseCase<DownloadImageParam>() {
    override fun execute(parameters: DownloadImageParam): LiveData<WorkInfo> {
        return repository.downloadImage(parameters.url, parameters.fileName)
    }
}