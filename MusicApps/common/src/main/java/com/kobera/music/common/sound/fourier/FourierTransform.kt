package com.kobera.music.common.sound.fourier

import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

object FourierTransform {

    fun dft(sampleData : ShortArray) : Array<ComplexNumber> =
        dft(sampleData.map { ComplexNumber(it.toDouble(), 0.0) }.toTypedArray())

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

    fun fineTuneDFT(sampleData: ShortArray, from: Int, to: Int, accuracy: Double): Array<ComplexNumber> =
        fineTuneDFT(sampleData.map{ ComplexNumber(it.toDouble(), 0.0) }.toTypedArray(), from, to, accuracy)

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

    fun fineTuneInBetweenIndexes(sampleData: ShortArray, from: Int, to: Int, accuracy: Double)
        = fineTuneInBetweenIndexes(sampleData, from.toDouble(), to.toDouble(), accuracy).index


    private fun fineTuneInBetweenIndexes(sampleData: ShortArray, from: Double, to: Double, accuracy: Double): IndexAndRelevancy{
        val output = Array(sampleData.size) { ComplexNumber(0.0, 0.0) }

        require(from <= to) { "from > to : $from > $to" }

        val average = from average to
        if(to - from < accuracy) {
            //Timber.d("resultIndex = $average")
            return IndexAndRelevancy(average, relevancyOfIndex(sampleData, average))
        }

        val relevancyFrom = relevancyOfIndex(sampleData, from)
        val relevancyAverage = relevancyOfIndex(sampleData, average)
        val relevancyTo = relevancyOfIndex(sampleData, to)

        var newFrom = from
        var newTo = to

        if(relevancyAverage > relevancyFrom && relevancyAverage > relevancyTo){
            if(relevancyFrom > relevancyTo) {
                newTo = average
            } else if(relevancyTo > relevancyFrom) {
                newFrom = average
            } else {
                return IndexAndRelevancy(average, relevancyOfIndex(sampleData, average))
            }
        } else if(relevancyAverage > relevancyFrom) {
            newFrom = average
        } else if(relevancyAverage > relevancyTo) {
            newTo = average
        } else {
            //TODO maybe there are 2 maximums near each other
            val bottom = fineTuneInBetweenIndexes(sampleData, from, average, accuracy)
            val top = fineTuneInBetweenIndexes(sampleData, average, to, accuracy)

            Timber.d("bottom $bottom top $top" )

            return IndexAndRelevancy(index = (bottom.index * bottom.relevancy + top.relevancy * top.index)/(bottom.relevancy + top.relevancy),
                relevancy = (bottom.relevancy + top.relevancy)/2)
        }

        return fineTuneInBetweenIndexes(sampleData, newFrom, newTo, accuracy)
    }

    private fun relevancyOfIndex(sampleData: ShortArray, index: Double): Double {
        var sumReal = 0.0
        var sumImag = 0.0
        for (n in sampleData.indices) {
            val angle = 2.0 * Math.PI * index * n / sampleData.size
            sumReal += sampleData[n].toDouble() * cos(angle) + sampleData[n].toDouble() * sin(angle)
            sumImag += -sampleData[n].toDouble() * sin(angle) + sampleData[n].toDouble() * cos(angle)
        }

        return ComplexNumber(sumReal, sumImag).magnitude()
    }

    private data class IndexAndRelevancy(val index: Double, val relevancy: Double)
    infix fun Double.average(other: Double): Double =
         (this + other) / 2


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



