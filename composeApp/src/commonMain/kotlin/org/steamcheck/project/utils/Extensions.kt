package org.steamcheck.project.utils

fun String.capitalizeFirstLetter(): String {
    return this.replaceFirstChar { it.uppercase() }
}
