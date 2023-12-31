package com.kobera.music.common.sound.yin

import com.kobera.music.common.sound.Frequency
import com.kobera.music.common.sound.fourier.ComplexNumber
import com.kobera.music.common.sound.fourier.FourierTransform
import com.kobera.music.common.util.cumSum


/**
 * fastYIN algorithm to detect pitch of the audioData
 *
 *
 * initialize YIN and than call [pitchDetection]
 *
 * this implementation is O(N*ln(N))
 *
 * inspired by this papers:
 * http://phys.uri.edu/nigh/NumRec/bookfpdf/f13-2.pdf
 * https://pubs.aip.org/asa/jasa/article/111/4/1917/547221/YIN-a-fundamental-frequency-estimator-for-speech
 *
 * and this implementation:
 * https://github.com/patriceguyot/Yin (MIT license)
 */
class FastYin(
    private val sampleRate: Int,
    minFrequency: Frequency = @Suppress("MagicNumber") Frequency(80.0),
    maxFrequency: Frequency = @Suppress("MagicNumber") Frequency(3000.0),
    private val threshold: Double = 0.15, //0.15 works fine and paper commands 0.1 - 0.15
) {
    private val tauMin = sampleRate / maxFrequency.value
    private val tauMax = sampleRate / minFrequency.value


    //TODO do we need smaller window????
    //TODO whats the precision??
    /**
     * should be O(N*ln(N))
     *
     * @param audioData audio data
     * @return pitch in Hz
     */
    fun pitchDetection(audioData: ShortArray): Double {
        val df = difference(audioData.map { it.toDouble() })
        val cmdf = comutativeMeanNormalizedDifferenceFunction(df)
        val pitchIndex = getPitch(cmdf = cmdf)
        val parabolicEstimatePitch = parabolicInterpolation(pitchIndex, cmdf, audioData)

        if(cmdf.min() > tauMin){
            println("argMins = ${cmdf.min()}")
        }
        if(pitchIndex != 0) {
            println("pitch ${sampleRate/parabolicEstimatePitch}")
            println("harmonic rates = ${cmdf[pitchIndex]}")
        } else {
            println("harmonic rates = ${cmdf.min()}")
            return 0.0
        }
       return sampleRate/parabolicEstimatePitch
   }

    private fun parabolicInterpolation(pitch: Int, cmdf: Array<Double>, audioData: ShortArray): Double {
        val parabolicTau: Double
        val x0: Int = (pitch - 1).let { if(it < 0)0 else it }
        val x2: Int = (pitch + 1).let { if(it > audioData.size) audioData.size else it }

        parabolicTau = if (x0 == pitch) {
            if (cmdf[pitch] <= cmdf[x2]) {
                pitch.toDouble()
            } else {
                x2.toDouble()
            }
        } else if (x2 == pitch) {
            if (cmdf[pitch] <= cmdf[x0]) {
                pitch.toDouble()
            } else {
                x0.toDouble()
            }
        } else {
            val s0: Double = cmdf[x0]
            val s1: Double = cmdf[pitch]
            val s2: Double = cmdf[x2]

            pitch + (s2 - s0) / (2 * (2 * s1 - s2 - s0))
        }
        return parabolicTau
    }

    private fun getPitch(cmdf: Array<Double>): Int{
        var tau = tauMin.toInt()
        while (tau < tauMax){
            if(cmdf[tau]  < threshold){
                while (tau + 1 <tauMax && cmdf[tau + 1] < cmdf[tau]){
                    tau += 1
                }
                return tau
            }
            tau += 1
        }
        return 0
    }

    private fun comutativeMeanNormalizedDifferenceFunction(df: DoubleArray): Array<Double>{
        val dfCpy = df.toTypedArray()
        val dfCumSum = df.toTypedArray().cumSumIgnoreFirst()
        for(i in 1 until dfCpy.size){
            dfCpy[i] = dfCpy[i] * i.toDouble() / dfCumSum[i]
        }
        dfCpy[0] = 1.0
        return dfCpy
    }

    private fun difference(audioData: List<Double>): DoubleArray {
        val w = audioData.size
        val audioArray = audioData.toTypedArray()
        val audioComplex = audioArray.map { ComplexNumber(it, 0.0) }.toTypedArray()

        //TODO 2 much copies
        val audioArraySquared = audioData.map {
            it * it
        }.toTypedArray()
        val cumSum = audioArraySquared.cumSum().toMutableList()
        cumSum.reverse()
        cumSum.add(0.0)
        cumSum.reverse()
        val conv = FourierTransform.convolve(audioComplex, audioComplex.reversedArray()) //# x[::-1] is revered

        val tmpRes = Array(w) { i ->
            cumSum[w - i] + cumSum[w] - cumSum[i] - 2 * conv[w - 1 + i].real
        }
        return tmpRes.toDoubleArray()
    }

    private fun Array<Double>.cumSumIgnoreFirst(): Array<Double>{
        var sum = 0.0
        for (i in 1 until size) {
            sum += this[i]
            this[i] = sum
        }
        return this
    }
}
