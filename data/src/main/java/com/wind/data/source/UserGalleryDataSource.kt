package com.wind.data.source

import android.content.Context
import androidx.paging.PagingSource
import com.wind.data.AuthApi
import com.wind.model.Art
import retrofit2.HttpException
import java.io.IOException

/**
 * Created by Phong Huynh on 7/25/2020.
 */
internal class UserGalleryDataSource(
    private val context: Context,
    private val restApi: AuthApi,
    private val userName: String
): PagingSource<Int, Art>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Art> {
        return try {
            // Load page 0 if undefined.
            val nextPageNumber = params.key ?: 0
            val map = mapOf(
                "offset" to nextPageNumber.toString(), "limit" to params.loadSize.coerceAtMost(24).toString(),
                "username" to userName
            )
            val response = restApi.getGallery(map)
            return LoadResult.Page(
                data = response.data,
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