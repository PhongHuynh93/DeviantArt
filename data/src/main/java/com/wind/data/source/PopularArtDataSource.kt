package com.wind.data.source

import androidx.paging.PagingSource
import com.wind.data.AuthApi
import com.wind.data.model.Art
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Phong Huynh on 7/25/2020.
 */
class PopularArtDataSource constructor(private val restApi: AuthApi): PagingSource<Int, Art>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Art> {
        return try {
            // Load page 0 if undefined.
            val nextPageNumber = params.key ?: 0
            val map = mapOf("offset" to nextPageNumber.toString())
            val response = restApi.getPopularDeviations(map)
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