package com.wind.domain

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.wind.domain.result.Result
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

/**
 * Executes business logic synchronously or asynchronously using Coroutines.
 */
abstract class PageUseCase<in P, R: Any>() {

    operator fun invoke(parameters: P): Flow<PagingData<R>> {
        return execute(parameters)
    }

    protected abstract fun execute(parameters: P): Flow<PagingData<R>>
}
