package com.wind.data.source

import android.content.Context
import androidx.paging.PagingSource
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.wind.data.AuthApi
import com.wind.model.Art
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception

/**
 * Created by Phong Huynh on 7/25/2020.
 */
internal class NewestArtDataSource constructor(
    private val context: Context,
    private val restApi: AuthApi): PagingSource<Int, Art>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Art> {
        return try {
            // Load page 0 if undefined.
            val nextPageNumber = params.key ?: 0
            val map = mapOf("offset" to nextPageNumber.toString())
            val response = restApi.getNewestDeviations(map)
            // load ahead the preview images
            val requestOptions = RequestOptions().format(DecodeFormat.PREFER_RGB_565)
            response.arts.forEach { art ->
                art.thumbs.let { listThumb ->
                    if (listThumb.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            Timber.e("current thread ${Thread.currentThread()}")
                            try {
                                Glide.with(context)
                                    .load(listThumb[0].src)
                                    .apply(requestOptions)
                                    .transform(BlurTransformation())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .submit().get()
                            } catch (e: Exception) {
                                // just make sure the glide wont crash, do nothing here
                            }

                        }
                    }
                }
            }
            return LoadResult.Page(
                data = response.arts,
                prevKey = null, // Only paging forward.
                nextKey = response.nextOffset
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}