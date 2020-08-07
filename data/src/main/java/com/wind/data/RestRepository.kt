package com.wind.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wind.data.source.CommentDataSource
import com.wind.data.source.NewestArtDataSource
import com.wind.data.source.PopularArtDataSource
import com.wind.model.Art
import com.wind.model.Comment
import com.wind.model.DeviantArtList
import com.wind.model.RelatedArt
import kotlinx.coroutines.flow.Flow

/**
 * Created by Phong Huynh on 7/22/2020
 */

interface RestRepository {
    fun getNewestDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int): Flow<PagingData<Art>>
    fun getPopularDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int): Flow<PagingData<Art>>
    suspend fun getArtFromThisArtist(id: String): RelatedArt
    fun getComment(artId: String, pageSize: Int): Flow<PagingData<Comment>>
    suspend fun getDailyDeviations(): DeviantArtList<Art>
}

internal class RestRepositoryImpl internal constructor(
    private val context: Context,
    private val authApi: AuthApi
) : RestRepository {
    override fun getNewestDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            NewestArtDataSource(context, authApi)
        }.flow


    override fun getPopularDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int) =
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
}