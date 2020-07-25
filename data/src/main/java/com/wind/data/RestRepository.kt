package com.wind.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wind.data.model.Art
import com.wind.data.model.ArtList
import com.wind.data.source.PopularArtDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Phong Huynh on 7/22/2020
 */

interface RestRepository {
    suspend fun getNewestDeviations(): ArtList
    fun getPopularDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int): Flow<PagingData<Art>>
}

@Singleton
class RestRepositoryImpl @Inject constructor() : RestRepository {
    @Inject
    lateinit var authApi: AuthApi
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