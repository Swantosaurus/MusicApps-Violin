@file:Suppress("MagicNumber")

package com.kobera.music.common.sound.fourier

import com.kobera.music.common.sound.Frequency
import com.kobera.music.common.sound.toFourierIndexDouble
import kotlin.math.cos
import kotlin.math.sin

/**
 *
 */
object FourierTransform {
    fun dft(sampleData : ShortArray) : Array<ComplexNumber> =
        dft(sampleData.map { ComplexNumber(it.toDouble(), 0.0) }.toTypedArray())

    @Suppress("UnusedPrivateProperty")
    private val TAG = this::class.simpleName

    /**
     * transforms PCM 16Bit audio using naive Discrete Fourier Transformation O(n^2)
     */
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

    /**
     * this is not extension of dft because rows in the matrix doesn't make up base
     * just trying to make biggest scalar sum with vectors and the max is most simular to the
     */
    fun fineTuneDFT(sampleData: ShortArray, from: Int, to: Int, accuracy: Frequency): Array<ComplexNumber> =
        fineTuneDFT(
            sampleData = sampleData.map {
                ComplexNumber(it.toDouble(), 0.0)
            }.toTypedArray(),
            from = from,
            to = to,
            accuracy = accuracy.toFourierIndexDouble()
        )

    fun fineTuneDFT(sampleData: Array<ComplexNumber>, from: Int, to: Int, accuracy: Double): Array<ComplexNumber>{
        val steps = ((to-from)/accuracy).toInt()
        val output = Array(steps) { ComplexNumber(0.0, 0.0) }
        for (i in 0 until steps) {
            var sumReal = 0.0
            var sumImag = 0.0
            for (n in sampleData.indices) {
                val angle = 2.0 * Math.PI * (from + i * accuracy) * n / sampleData.size
                sumReal += sampleData[n].real * cos(angle) + sampleData[n].imag * sin(angle)
                sumImag += -sampleData[n].real * sin(angle) + sampleData[n].imag * cos(angle)
            }

            output[i] = ComplexNumber(sumReal, sumImag)
        }
        return output
    }

    fun fft(sampleData: ShortArray): Array<ComplexNumber?> =
        fft(sampleData.map { ComplexNumber(it.toDouble(), 0.0) }.toTypedArray())

    /**
     *  radix 2 Cooley-Tukey FastFourierTransform O(n*log(n))
     */
    fun fft(sampleData: Array<ComplexNumber?>): Array<ComplexNumber?> {
        //recursion break
        if (sampleData.size == 1) return arrayOf(sampleData[0])

        // check if sampleData size is a power of 2
        require(sampleData.size % 2 == 0) { "${sampleData.size} is not a power of 2" }

        // even
        val even: Array<ComplexNumber?> = arrayOfNulls<ComplexNumber?>( sampleData.size / 2)
        for (k in 0 until sampleData.size / 2) {
            even[k] = sampleData[2 * k]
        }
        val evenFFT: Array<ComplexNumber?> = fft(even)

        // odd
        val odd: Array<ComplexNumber?> = even
        for (k in 0 until sampleData.size / 2) {
            odd[k] = sampleData[2 * k + 1]
        }
        val oddFFT: Array<ComplexNumber?> = fft(odd)

        // combine
        val result: Array<ComplexNumber?> = arrayOfNulls(sampleData.size)
        for (position in 0 until sampleData.size / 2) {
            val kth = -2 * position * Math.PI / sampleData.size
            val wk = ComplexNumber(cos(kth), sin(kth))
            result[position] = evenFFT[position]!! + (wk * oddFFT[position]!!)
            result[position + sampleData.size / 2] = evenFFT[position]!! - (wk * oddFFT[position]!!)
        }
        return result
    }
}



