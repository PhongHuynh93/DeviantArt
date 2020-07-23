package com.wind.data.repository.art

import com.wind.data.model.ArtList
import com.wind.data.repository.art.source.ArtDataSource
/**
 * Created by Phong Huynh on 7/22/2020
 */

interface BrowseRepository {
    suspend fun getNewestDeviations(accessToken: String): ArtList
    suspend fun getPopularDeviations(accessToken: String): ArtList
}

class BrowseRepositoryImpl(
    private val remoteDataSource: ArtDataSource,
    private val localDataSource: ArtDataSource
) : BrowseRepository {
    override suspend fun getNewestDeviations(accessToken: String): ArtList {
        return remoteDataSource.getNewestDeviations(accessToken)
    }

    override suspend fun getPopularDeviations(accessToken: String): ArtList {
        return remoteDataSource.getPopularDeviations(accessToken)
    }
}