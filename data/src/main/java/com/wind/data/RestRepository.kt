package com.wind.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wind.data.source.NewestArtDataSource
import com.wind.data.source.PopularArtDataSource
import com.wind.model.Art
import kotlinx.coroutines.flow.Flow

/**
 * Created by Phong Huynh on 7/22/2020
 */

interface RestRepository {
    fun getNewestDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int): Flow<PagingData<Art>>
    fun getPopularDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int): Flow<PagingData<Art>>
}

internal class RestRepositoryImpl constructor(private val authApi: AuthApi) : RestRepository {
    override fun getNewestDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            NewestArtDataSource(authApi)
        }.flow


    override fun getPopularDeviations(catePath: String?, query: String?, offset: Int?, pageSize: Int) =
        Pager(config = PagingConfig(pageSize = pageSize)) {
            PopularArtDataSource(authApi)
        }.flow
}