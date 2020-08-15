package com.wind.domain

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo

/**
 * Created by Phong Huynh on 8/15/2020.
 */
abstract class LiveDataWorkManagerUseCase<in P>() {
    operator fun invoke(parameters: P): LiveData<WorkInfo> = execute(parameters)

    protected abstract fun execute(parameters: P): LiveData<WorkInfo>
}