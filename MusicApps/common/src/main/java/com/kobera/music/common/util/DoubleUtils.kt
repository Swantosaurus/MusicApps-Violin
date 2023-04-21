package com.kobera.music.common.util

/**
 * Prints a double with a given number of decimals.
 */
fun Double.toStringWithNDecimals(n : Int = 2) =
    String.format("%.${n}f", this)
