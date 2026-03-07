package global.deveng.core

import android.content.Context
import core.util.multiplatform.MultiPlatformUtils

actual fun createMultiPlatformUtils(context: Any?): MultiPlatformUtils? =
    (context as? Context)?.let { MultiPlatformUtils(it) }
