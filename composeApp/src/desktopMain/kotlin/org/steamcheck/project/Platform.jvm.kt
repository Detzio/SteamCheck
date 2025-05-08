package org.steamcheck.project

class JVMPlatform: Platform {
    override val platform: String = "Desktop"
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()