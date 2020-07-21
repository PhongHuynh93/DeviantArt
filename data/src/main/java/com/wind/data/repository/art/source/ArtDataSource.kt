package com.wind.data.repository.art.source

import com.wind.data.model.ArtList

/**
 * Created by Phong Huynh on 7/20/2020
 */
interface ArtDataSource {
    suspend fun getDailyDeviations(): ArtList
    suspend fun getNewestDeviations(): ArtList
    suspend fun getPopularDeviations(): ArtList
}