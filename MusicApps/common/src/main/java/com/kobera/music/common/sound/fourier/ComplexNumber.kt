package com.kobera.music.common.sound.fourier

import kotlin.math.sqrt

/**
 * Complex number for Fourier transform
 */
data class ComplexNumber(val real: Double, val imag: Double){
    infix operator fun plus(other: ComplexNumber) : ComplexNumber {
        return ComplexNumber(real + other.real, imag + other.imag)
    }
    infix operator fun times(other: ComplexNumber) : ComplexNumber {
        return ComplexNumber(real * other.real - imag * other.imag, real * other.imag + imag * other.real)
    }

    infix operator fun minus(other: ComplexNumber) : ComplexNumber {
        return ComplexNumber(real - other.real, imag - other.imag)
    }

    fun scale(alpha: Double) =
        ComplexNumber(real * alpha, imag * alpha)

    fun conjugate(): ComplexNumber {
        return ComplexNumber(real, -imag)
    }
    fun magnitude() : Double = sqrt(real * real + imag * imag)

    companion object {
        val Zero = ComplexNumber(0.0, 0.0)
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
