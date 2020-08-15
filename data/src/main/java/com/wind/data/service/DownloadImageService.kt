package com.wind.data.service

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import util.saveFile

/**
 * Created by Phong Huynh on 8/13/2020
 */
private const val EXTRA_URL = "xUrl"
private const val EXTRA_PATH = "xPathName"

@Suppress("BlockingMethodInNonBlockingContext")
class DownloadImageService @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        fun makeParam(url: String?, fileName: String?) = arrayOf(EXTRA_URL to url, EXTRA_PATH to fileName)
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val url = inputData.getString(EXTRA_URL) ?: return@withContext Result.failure()
            val path = inputData.getString(EXTRA_PATH)
            try {
                Glide.with(applicationContext).asBitmap().load(url).submit().get().saveFile(applicationContext)
                Result.success()
            } catch (exception: Exception) {
                Result.failure()
            }
        }

    }
}
