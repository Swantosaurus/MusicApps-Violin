package com.kobera.music.common.util

fun String.filterNonDigits(): String {
    return this.filter { it.isDigit() }
}
