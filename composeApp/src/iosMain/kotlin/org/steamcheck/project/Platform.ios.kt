package org.steamcheck.project

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val platform: String = "Mobile"
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()