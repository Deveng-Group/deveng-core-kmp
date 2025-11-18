package core.data.di

import core.util.multiplatform.MultiPlatformUtils
import core.util.CustomDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::MultiPlatformUtils)
    single<CoroutineDispatcher>(named(CustomDispatchers.IO)) { Dispatchers.Default }
}