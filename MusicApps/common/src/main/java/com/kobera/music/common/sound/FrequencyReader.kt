package com.kobera.music.common.sound

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

class FrequencyReader(private val pcmAudioRecorder: PcmAudioRecorder) {
    private val _frequency : MutableSharedFlow<List<Double>> = MutableSharedFlow()

    private val TAG = this.javaClass.simpleName

    val frequency = _frequency.asSharedFlow()

    private val _state: MutableStateFlow<FrequencyReaderState> = MutableStateFlow(
        FrequencyReaderState.Stopped
    )

    val state = _state.asStateFlow()

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private var job : Job? = null

    fun start() {
        if (_state.value == FrequencyReaderState.Recording) {
            Log.w(TAG, "Already recording")
            return
        }

        _state.value = FrequencyReaderState.Recording
        pcmAudioRecorder.start()

        job = scope.launch {
            pcmAudioRecorder.pcmAudioDataFlow.collect { pcmAudioData ->
                val frequencyDomainFFT : MutableList<Double> =
                    FourierTransform.fft(pcmAudioData).map { complexNumber ->
                        complexNumber!!.magnitude()
                    }.toMutableList()


                var maxIndex = -1
                var maxValue = -1.0
                frequencyDomainFFT.forEachIndexed { index, it ->
                    if(index > PcmAudioRecorder.readSize / 2 - 1){
                        return@forEachIndexed
                    }
                    if(it > silenceThreshold){
                        Log.d(TAG, "Index: $index Frequency: ${index.toDouble() / PcmAudioRecorder.readSize  * PcmAudioRecorder.sampleRate}")
                    }
                    if(it > maxValue) {
                        maxValue = it
                        maxIndex = index
                    }
                }
                frequencyDomainFFT[maxIndex] = 0.0
                var maxIndex2 = -1
                var maxValue2 = -1.0

                frequencyDomainFFT.forEachIndexed { index, it ->
                    if(index > PcmAudioRecorder.readSize / 2 - 1){
                        return@forEachIndexed
                    }

                    if(it > maxValue2) {
                        maxValue2 = it
                        maxIndex2 = index
                    }
                }




                //Log.d(TAG, "Max value: $maxValue at index: $maxIndex in $timeOfDFT")

                //Log.d(TAG, "Max value v2: $maxValueV2 at index: $maxIndexV2 in $timeOfFFT")
                //Log.d(TAG, "End of frequency domain")

                val mostRelevantFrequency = maxIndex.toDouble() / PcmAudioRecorder.readSize  * PcmAudioRecorder.sampleRate
                val secondMostRelevantFrequency = maxIndex2.toDouble() / PcmAudioRecorder.readSize  * PcmAudioRecorder.sampleRate

                if(maxValue > silenceThreshold) {
                    Log.d(TAG, "Frequency: $mostRelevantFrequency")
                    Log.d(TAG, "SecondMostRelevant $secondMostRelevantFrequency")
                } else {
                    Log.d(TAG, "Silence")
                }
            }
        }
    }

    fun stop() {
        if(_state.value == FrequencyReaderState.Stopped) {
            Log.w(TAG, "Already stopped")
            return
        }

        pcmAudioRecorder.stop()
        job?.cancel()

        _state.value = FrequencyReaderState.Stopped
    }

    fun setSilenceThreshold(silenceThreshold: Int) {
        Companion.silenceThreshold = silenceThreshold
    }

    private fun ComplexNumber.magnitude() : Double = sqrt(real * real + imag * imag)

    companion object {
        private var silenceThreshold = 1_000_000
    }


    enum class FrequencyReaderState {
        Stopped,
        Recording
    }
}

