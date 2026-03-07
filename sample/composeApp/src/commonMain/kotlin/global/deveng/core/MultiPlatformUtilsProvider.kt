package global.deveng.core

import core.util.multiplatform.MultiPlatformUtils

/**
 * Creates platform-specific [MultiPlatformUtils] instance.
 * @param context Optional context (e.g. Android Context); ignored on other platforms.
 */
expect fun createMultiPlatformUtils(context: Any?): MultiPlatformUtils?
