package com.wind.data.repository.art

import com.wind.data.model.ArtList
import com.wind.data.repository.art.source.ArtDataSource
/**
 * Created by Phong Huynh on 7/22/2020
 */

interface BrowseRepository {
    suspend fun getNewestDeviations(): ArtList
    suspend fun getPopularDeviations(): ArtList
}

class BrowseRepositoryImpl(
    private val remoteDataSource: ArtDataSource
) : BrowseRepository {
    override suspend fun getNewestDeviations(): ArtList {
        return remoteDataSource.getNewestDeviations()
    }

    override suspend fun getPopularDeviations(): ArtList {
        return remoteDataSource.getPopularDeviations()
    }
}