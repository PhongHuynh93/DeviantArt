package com.wind.data.repository.art.source.local

import com.wind.data.di.RemoteDataSource
import com.wind.data.repository.art.source.ArtDataSource
import javax.inject.Singleton

/**
 * Created by Phong Huynh on 7/20/2020
 */
@Singleton
@RemoteDataSource
class ArtLocalDataSource constructor(): ArtDataSource {
    override suspend fun getDailyDeviations() {
        TODO("Not yet implemented")
    }

    override suspend fun getNewestDeviations() {
        TODO("Not yet implemented")
    }

    override suspend fun getPopularDeviations() {
        TODO("Not yet implemented")
    }
}