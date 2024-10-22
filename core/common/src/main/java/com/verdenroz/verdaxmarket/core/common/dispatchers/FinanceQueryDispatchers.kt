package com.verdenroz.verdaxmarket.core.common.dispatchers

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val FinanceQueryDispatcher: FinanceQueryDispatchers)

enum class FinanceQueryDispatchers {
    Main,
    Default,
    IO,
}
