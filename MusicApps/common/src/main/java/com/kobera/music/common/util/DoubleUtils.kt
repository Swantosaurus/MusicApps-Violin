package com.kobera.music.common.util

fun Double.toStringWithNDecimals(n : Int = 2) =
    String.format("%.${n}f", this)
