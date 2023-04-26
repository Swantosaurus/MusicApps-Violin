package com.kobera.music.common.sound

import kotlin.math.sqrt


object HarmonyProductSpectrum {
    fun hps(input: List<Double>, iterations: Int): List<Double> {
        check(iterations > 1) { "iterations must be greater than 1" }
        var output = input.toMutableList()
        for (i in 2..iterations) {
            for (j in 0 until (input.size - 1) / i) {
                output[j] = sqrt(output[j] * List(i) {
                    try {
                        input[j * i + it]
                    } catch (e :IndexOutOfBoundsException){
                        throw IndexOutOfBoundsException("j= $j i=$i move=$it")
                    }
                }.max())
            }
        }
        return output
    }
}