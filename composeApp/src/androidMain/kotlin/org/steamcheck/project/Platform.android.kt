package org.steamcheck.project

import android.os.Build

class AndroidPlatform : Platform {
    override val platform: String = "Mobile"
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()