package org.steamcheck.project

interface Platform {
    val platform: String
    val name: String
}

expect fun getPlatform(): Platform