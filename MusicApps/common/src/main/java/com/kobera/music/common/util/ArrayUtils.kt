package com.kobera.music.common.util


/**
 * Returns the cumulative sum of the array.
 *
 * https://sparkbyexamples.com/numpy/numpy-cumsum-function/#:~:text=Python%20NumPy%20cumsum()%20function%20is%20used%20to%20return%20the,along%20with%20the%20provided%20axes.
 */
fun Array<Double>.cumSum(): Array<Double>{
    var sum = 0.0
    for (i in this.indices) {
        sum += this[i]
        this[i] = sum
    }
    return this
}