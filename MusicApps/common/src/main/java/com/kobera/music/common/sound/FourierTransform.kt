package com.kobera.music.common.sound

import kotlin.math.cos
import kotlin.math.sin

object FourierTransform {
    /**
     * transforms PCM 16Bit audio using Discrete Fourier Transformation
     */
    fun dft(sampleData : ShortArray) : Array<ComplexNumber> =
        dft(sampleData.map { ComplexNumber(it.toDouble(), 0.0) }.toTypedArray())


    fun dft(sampleData: Array<ComplexNumber>): Array<ComplexNumber> {
        val output = Array(sampleData.size) { ComplexNumber(0.0, 0.0) }

        for (k in output.indices) {
            var sumReal = 0.0
            var sumImag = 0.0
            for (n in sampleData.indices) {
                val angle = 2.0 * Math.PI * k * n / sampleData.size
                sumReal += sampleData[n].real * cos(angle) + sampleData[n].imag * sin(angle)
                sumImag += -sampleData[n].real * sin(angle) + sampleData[n].imag * cos(angle)

            }

            output[k] = ComplexNumber(sumReal, sumImag)
        }

        return output
    }

    fun fft(sampleData: ShortArray): Array<ComplexNumber?> =
        fft(sampleData.map { ComplexNumber(it.toDouble(), 0.0) }.toTypedArray())

    fun fft(sampleData: Array<ComplexNumber?>): Array<ComplexNumber?> {

        if (sampleData.size == 1) return arrayOf(sampleData[0])

        // radix 2 Cooley-Tukey FFT
        require(sampleData.size % 2 == 0) { "${sampleData.size} is not a power of 2" }

        // compute FFT of even terms
        val even: Array<ComplexNumber?> = arrayOfNulls<ComplexNumber?>( sampleData.size / 2)
        for (k in 0 until sampleData.size / 2) {
            even[k] = sampleData[2 * k]
        }
        val evenFFT: Array<ComplexNumber?> = fft(even)

        // compute FFT of odd terms
        val odd: Array<ComplexNumber?> = even // reuse the array (to avoid n log n space)
        for (k in 0 until sampleData.size / 2) {
            odd[k] = sampleData[2 * k + 1]
        }
        val oddFFT: Array<ComplexNumber?> = fft(odd)

        // combine
        val result: Array<ComplexNumber?> = arrayOfNulls(sampleData.size)
        for (k in 0 until sampleData.size / 2) {
            val kth = -2 * k * Math.PI / sampleData.size
            val wk = ComplexNumber(cos(kth), sin(kth))
            result[k] = evenFFT[k]!! + (wk * oddFFT[k]!!)
            result[k + sampleData.size / 2] = evenFFT[k]!! - (wk * oddFFT[k]!!)
        }
        return result
    }
}
