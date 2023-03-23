package com.kobera.music.common.sound

import kotlin.math.sqrt

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
}


