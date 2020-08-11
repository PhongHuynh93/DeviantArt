package com.wind.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wind.data.source.*
import com.wind.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by Phong Huynh on 7/22/2020
 */

interface RestRepository {
    fun getNewestDeviations(catePath: String?, query: String?, pageSize: Int): Flow<PagingData<Art>>
    fun getPopularDeviations(catePath: String?, query: String?, pageSize: Int): Flow<PagingData<Art>>
    suspend fun getArtFromThisArtist(id: String): RelatedArt
    fun getComment(artId: String, pageSize: Int): Flow<PagingData<Comment>>
    suspend fun getDailyDeviations(): DeviantArtList<Art>
    fun getTopics(pageSize: Int): Flow<PagingData<Topic>>
    fun getTopicDetail(pageSize: Int, topicName: String): Flow<PagingData<Art>>
    suspend fun getTag(tag: String): TagList
    fun getTagArtDataSource(pageSize: Int, tag: String): Flow<PagingData<Art>>
}

internal class RestRepositoryImpl internal constructor(
    private val context: Context,
    private val authApi: AuthApi
) : RestRepository {
    override fun getNewestDeviations(catePath: String?, query: String?, pageSize: Int) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            NewestArtDataSource(context, authApi)
        }.flow


    override fun getPopularDeviations(catePath: String?, query: String?, pageSize: Int) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            PopularArtDataSource(context, authApi)
        }.flow

    override suspend fun getArtFromThisArtist(id: String): RelatedArt {
        return authApi.getMoreFromThisArtist(mapOf("seed" to id))
    }

    override suspend fun getDailyDeviations(): DeviantArtList<Art> {
        return authApi.getDailyDeviations(emptyMap())
    }

    override fun getComment(artId: String, pageSize: Int) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            CommentDataSource(authApi, artId)
        }.flow

    override fun getTopics(pageSize: Int) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            TopicListDataSource(authApi)
        }.flow

    override fun getTopicDetail(pageSize: Int, topicName: String) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            TopicDetailDataSource(context, authApi, topicName)
        }.flow

    override suspend fun getTag(tag: String): TagList {
        return authApi.getTagList(mapOf("tag_name" to tag))
    }

    override fun getTagArtDataSource(pageSize: Int, tag: String) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            TagArtDataSource(context, authApi, tag)
        }.flow
}