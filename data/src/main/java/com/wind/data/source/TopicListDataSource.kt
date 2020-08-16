package com.wind.data.source

import androidx.paging.PagingSource
import com.wind.data.AuthApi
import com.wind.model.Topic
import retrofit2.HttpException
import java.io.IOException

/**
 * Created by Phong Huynh on 7/25/2020.
 */
internal class TopicListDataSource(
    private val restApi: AuthApi
): PagingSource<Int, Topic>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Topic> {
        return try {
            // Load page 0 if undefined.
            val nextPageNumber = params.key ?: 0
            // max topic is 10 followed by BE document
            val response = restApi.getTopics(mapOf("offset" to nextPageNumber.toString(), "limit" to 10.coerceAtMost(params.loadSize)
                .toString()))
            // load addition api to get the image in each topic
            for (topic in response.data) {
                try {
                    topic.listArt = restApi.getTopic(mapOf("topic" to topic.canonicalName)).data
                } catch (ignored: Exception) {
                    // just ignore this list art
                    topic.listArt = emptyList()
                }
            }
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