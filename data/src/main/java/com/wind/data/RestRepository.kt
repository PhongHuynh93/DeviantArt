package com.wind.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wind.data.source.PopularArtDataSource
import com.wind.model.Art
import com.wind.model.ArtList
import kotlinx.coroutines.flow.Flow

/**
 * Created by Phong Huynh on 7/22/2020
 */

interface RestRepository {
    suspend fun getNewestDeviations(): ArtList
    fun getPopularDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int): Flow<PagingData<Art>>
}

internal class RestRepositoryImpl constructor(private val authApi: AuthApi) : RestRepository {
    override suspend fun getNewestDeviations(): ArtList {
//        return remoteDataSource.getNewestDeviations()
        // FIXME: 7/25/2020 temperately
        return ArtList(false, 0, emptyList())
    }

    override fun getPopularDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            PopularArtDataSource(authApi)
        }.flow
}