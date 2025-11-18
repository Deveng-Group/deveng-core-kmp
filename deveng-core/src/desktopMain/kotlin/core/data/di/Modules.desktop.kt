package core.data.di

import core.util.multiplatform.MultiPlatformUtils
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::MultiPlatformUtils)
}
