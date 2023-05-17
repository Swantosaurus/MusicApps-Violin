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
     * inspired by NDFT returns array of scalar products of the sampleData and the complex sinusoids
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

    fun fft(sampleData: ShortArray): Array<ComplexNumber> =
        fft(sampleData.map { ComplexNumber(it.toDouble(), 0.0) }.toTypedArray())

    /**
     *  radix 2 Cooley-Tukey FastFourierTransform O(n*log(n))
     */
    fun fft(sampleData: Array<ComplexNumber>): Array<ComplexNumber> {
        //recursion break
        if (sampleData.size == 1) return arrayOf(sampleData[0])

        // check if sampleData size is a power of 2
        require(sampleData.size % 2 == 0) { "${sampleData.size} is not a power of 2" }

        // even
        val even: Array<ComplexNumber> = Array(sampleData.size / 2){
            sampleData[2 * it]
        }
        val evenFFT: Array<ComplexNumber> = fft(even)

        // odd
        val odd: Array<ComplexNumber> = even
        for (k in 0 until sampleData.size / 2) {
            odd[k] = sampleData[2 * k + 1]
        }
        val oddFFT: Array<ComplexNumber> = fft(odd)

        // combine
        val result: Array<ComplexNumber> = Array(sampleData.size){ ComplexNumber.Zero }
        for (position in 0 until sampleData.size / 2) {
            val kth = -2 * position * Math.PI / sampleData.size
            val wk = ComplexNumber(cos(kth), sin(kth))
            result[position] = evenFFT[position] + (wk * oddFFT[position])
            result[position + sampleData.size / 2] = evenFFT[position] - (wk * oddFFT[position])
        }
        return result
    }

    /**
     *  inverse FastFourierTransform O(n*log(n)) inspired by https://introcs.cs.princeton.edu/java/97data/FFT.java.html
     */
    fun ifft(x: Array<ComplexNumber>): Array<ComplexNumber> {
        val n = x.size
        var y: Array<ComplexNumber> = Array(n) { ComplexNumber.Zero }

        // take conjugate
        for (i in 0 until n) {
            y[i] = x[i].conjugate()
        }

        // compute forward FFT
        y = fft(y)

        // take conjugate again
        for (i in 0 until n) {
            y[i] = y[i].conjugate()
        }

        // divide by n
        for (i in 0 until n) {
            y[i] = y[i].scale(1.0 / n)
        }

        return y
    }


    // compute the circular convolution of x and y
    /**
     * inspired by https://introcs.cs.princeton.edu/java/97data/FFT.java.html
     */
    fun cconvolve(x: Array<ComplexNumber>, y: Array<ComplexNumber>): Array<ComplexNumber> {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        require(x.size == y.size) { "Dimensions don't agree" }
        val n = x.size

        // compute FFT of each sequence
        val a: Array<ComplexNumber> = fft(x)
        val b: Array<ComplexNumber> = fft(y)

        // point-wise multiply
        val c: Array<ComplexNumber> = Array<ComplexNumber>(n) { ComplexNumber.Zero }
        for (i in 0 until n) {
            c[i] = a[i].times(b[i])
        }

        // compute inverse FFT
        return ifft(c)
    }

    // compute the linear convolution of x and y
    /**
     * inspired by https://introcs.cs.princeton.edu/java/97data/FFT.java.html
     */
    fun convolve(x: Array<ComplexNumber>, y: Array<ComplexNumber>): Array<ComplexNumber> {
        val a: Array<ComplexNumber> = Array(2 * x.size) { ComplexNumber.Zero }
        for (i in x.indices) a[i] = x[i]
        for (i in x.size until 2 * x.size) a[i] = ComplexNumber.Zero
        val b: Array<ComplexNumber> = Array(2 * x.size) { ComplexNumber.Zero }
        for (i in y.indices) b[i] = y[i]
        for (i in y.size until 2 * y.size) b[i] = ComplexNumber.Zero
        return cconvolve(a, b)
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


