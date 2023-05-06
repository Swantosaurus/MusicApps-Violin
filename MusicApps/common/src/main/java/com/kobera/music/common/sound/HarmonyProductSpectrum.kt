package com.kobera.music.common.sound

import kotlin.math.sqrt


/**
 * Harmony Product Spectrum algorithm to find the fundamental frequency of a sound
 */
object HarmonyProductSpectrum {
    fun hps(input: List<Double>, iterations: Int): List<Double> {
        check(iterations > 1) { "iterations must be greater than 1" }
        var output = input.toMutableList()
        for (i in 2..iterations) {
            for (j in 0 until (input.size - 1) / i) {
                output[j] = sqrt(output[j] * List(i) {
                    // just rethrowing exception that gives better detail
                    @Suppress("TooGenericExceptionCaught", "SwallowedException")
                    try {
                        input[j * i + it]
                    } catch (e :IndexOutOfBoundsException){
                        throw IndexOutOfBoundsException("j= $j i=$i move=$it")
                    }
                }.average())
            }
        }
        return output
    }
}

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation version 3 of the License,
 * or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */
