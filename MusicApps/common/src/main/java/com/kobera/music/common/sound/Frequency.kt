package com.kobera.music.common.sound

/**
 * A frequency.
 *
 * @param value The value of the frequency.
 */
data class Frequency(val value: Double) {

    init {
        check(value >= 0) {"Frequency can not be negative"}
    }

    @Suppress("EmptyClassBlock") // its there for extension functions
    companion object {
    }
}
