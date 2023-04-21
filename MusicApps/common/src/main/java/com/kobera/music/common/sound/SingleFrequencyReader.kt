package com.kobera.music.common.sound

import com.kobera.music.common.sound.fourier.FourierTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * A single frequency reader. Reads the frequency of a single frequency from a PCM audio stream.
 *
 * @param pcmAudioRecorder The PCM audio recorder.
 * @param minSearchedFrequency The minimum frequency to search for.
 * @param maxSearchedFrequency The maximum frequency to search for.
 * @param accuracy The accuracy of the frequency search.
 * @param silenceThreshold The silence threshold.
 */
class SingleFrequencyReader(
    private val pcmAudioRecorder: PcmAudioRecorder,
    minSearchedFrequency: Frequency = Frequency(150.0),
    maxSearchedFrequency: Frequency = Frequency(2000.0),
    private val accuracy: Double = 0.01,
    silenceThreshold: Long = 3_000_000L
) {
    private val _frequency : MutableStateFlow<FrequencyState> = MutableStateFlow(
        FrequencyState.Silence
    )

    /**
     * The frequency flow. Emits the frequency as a flow.
     */
    val frequency = _frequency.asStateFlow()

    private val maxFourierIndexSearched = maxSearchedFrequency.toFourierIndexIntRoundDown()

    private val minFourierIndexSearched = minSearchedFrequency.toFourierIndexIntRoundDown()


    private var _silenceThreshold : MutableStateFlow<Long> = MutableStateFlow(silenceThreshold)

    private val _state: MutableStateFlow<FrequencyReaderState> =
        MutableStateFlow(
            FrequencyReaderState.Stopped
        )

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private var job : Job? = null

    fun stop() {
        _frequency.value = FrequencyState.Silence
        if(_state.value == FrequencyReaderState.Stopped) {
            Timber.w(TAG, "Already stopped")
            return
        }

        pcmAudioRecorder.stop()
        job?.cancel()

        _state.value = FrequencyReaderState.Stopped
    }

    fun setSilenceThreshold(silenceThreshold: Long) {
        this._silenceThreshold.value = silenceThreshold
    }

    fun start() {
        if (_state.value == FrequencyReaderState.Recording) {
            Timber.w("Already recording")
            return
        }
        _state.value = FrequencyReaderState.Recording
        pcmAudioRecorder.start()

        startTransformOnNewThread()
    }

    private fun startTransformOnNewThread() {
        job = scope.launch {
            pcmAudioRecorder.pcmAudioDataFlow.collect { pcmAudioData ->
                val frequencyDomain: MutableList<Double> =
                    FourierTransform.fft(pcmAudioData).map { complexNumber ->
                        complexNumber!!.magnitude()
                    }.toMutableList()


                var indexFromBottom = -1
                var maxValue = _silenceThreshold.value.toDouble()
                var counter: Int? = null // to skip rest of indexes when we fond result

                for (index in minFourierIndexSearched until maxFourierIndexSearched) {
                    var currentThreshold = maxValue
                    if (maxValue == _silenceThreshold.value.toDouble()) {
                        if (Frequency.fromFourierIndex(index = index.toDouble()).value < 240) {
                            currentThreshold *= 0.6
                        }
                    }

                    if (frequencyDomain[index] > currentThreshold) {
                        if (counter == null) {
                            counter = (index * 0.2).roundToInt()
                        }
                        counter += 1
                        indexFromBottom = index
                        maxValue = frequencyDomain[index]
                    }
                    if (counter != null) {
                        counter -= 1
                        if (counter <= 0) {
                            break
                        }
                    }
                }

                if (indexFromBottom <= 0) {
                    _frequency.value = FrequencyState.Silence
                    return@collect
                }

                val resultIndexes =
                    FourierTransform.fineTuneDFT(
                        pcmAudioData,
                        from = indexFromBottom - 1,
                        to = indexFromBottom + 1,
                        accuracy
                    ).map { it.magnitude() }

                var finalIndex = -1
                var finalMaxValue = -1.0

                for (i in resultIndexes.indices) {
                    if (resultIndexes[i] > finalMaxValue) {
                        finalMaxValue = resultIndexes[i]
                        finalIndex = i
                    }
                }

                val resultIndex: Double = (indexFromBottom - 1) + finalIndex * accuracy

                val mostRelevantFrequency: Double =
                    resultIndex * (PcmAudioRecorder.sampleRate.toDouble() / PcmAudioRecorder.readSize.toDouble())

                //Timber.d("Result Index: $resultIndex Result Frequency: $mostRelevantFrequency")
                _frequency.value = FrequencyState.HasFrequency(mostRelevantFrequency)
            }
        }
    }

    sealed interface FrequencyState {
        object Silence: FrequencyState
        class HasFrequency(val frequency: Double) : FrequencyState
    }

    enum class FrequencyReaderState {
        Stopped,
        Recording
    }

    private val TAG = this.javaClass.simpleName
}

