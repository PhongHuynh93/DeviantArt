package com.wind.domain

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

/**
 * Executes business logic synchronously or asynchronously using Coroutines.
 */
abstract class PageUseCase<in P, R: Any>(private val coroutineDispatcher: CoroutineDispatcher) {

    operator fun invoke(parameters: P): Flow<PagingData<R>> {
        return execute(parameters)
            .flowOn(coroutineDispatcher)
    }

    protected abstract fun execute(parameters: P): Flow<PagingData<R>>
}
