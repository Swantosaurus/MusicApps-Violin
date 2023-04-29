package com.kobera.music.common.util

/**
 * Prints a double with a given number of decimals.
 */
fun Double.toStringWithNDecimals(n : Int = 2) =
    @Suppress("ImplicitDefaultLocale") String.format("%.${n}f", this)
