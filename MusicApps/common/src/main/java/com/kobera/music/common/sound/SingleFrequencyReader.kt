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
 * @param fineTuneLookupRange The fine tune lookup range.
 * @param hpsIterations The number of HPS iterations. (if less than 2 HPS is not used)
 */


@Suppress("MagicNumber","LongParameterList")
open class SingleFrequencyReader(
    private val pcmAudioRecorder: PcmAudioRecorder,
    minSearchedFrequency: Frequency = Frequency(150.0),
    maxSearchedFrequency: Frequency = Frequency(2000.0),
    private val accuracy: Frequency = Frequency(0.05),
    silenceThreshold: Long = 3_000_000L,
    private val fineTuneLookupRange: Int = 1,
    private val hpsIterations: Int = 1
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


    private var _silenceThreshold: MutableStateFlow<Long> = MutableStateFlow(silenceThreshold)

    private val _state: MutableStateFlow<FrequencyReaderState> =
        MutableStateFlow(
            FrequencyReaderState.Stopped
        )

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private var job: Job? = null

    private val hpsActive = hpsIterations > 1

    init {
        check(fineTuneLookupRange > 0) { "fineTuneLookupRange has to be greater than zero" }
    }

    fun stop() {
        _frequency.value = FrequencyState.Silence
        if (_state.value == FrequencyReaderState.Stopped) {
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
                var frequencyDomain: List<Double> =
                    FourierTransform.fft(pcmAudioData).map { complexNumber ->
                        complexNumber!!.magnitude()
                    }.toList()

                if (hpsActive) {
                    frequencyDomain = cleanupUsingHPS(frequencyDomain)
                }

                var fftIndexAndRelevancy = lookupFrequencyDomain(
                    frequencyDomain = frequencyDomain,
                    threashold = _silenceThreshold.value.toDouble()
                )

                if (fftIndexAndRelevancy is RelevancyAndIndex.NotFound) {
                    _frequency.value = FrequencyState.Silence
                    return@collect
                }

                fftIndexAndRelevancy = fftIndexAndRelevancy as RelevancyAndIndex.Found

                val fineTuneIndexAndRelevancy =
                    fineTuneLookup(pcmAudioData, fftIndexAndRelevancy.index.toInt(), accuracy)

                val mostRelevantFrequency: Double =
                    fineTuneIndexAndRelevancy.index * (PcmAudioRecorder.sampleRate.toDouble()
                            / PcmAudioRecorder.readSize.toDouble())

                //Timber.d("Result Index: ${fineTuneIndexAndRelevancy.index} " +
                //        "Result Frequency: $mostRelevantFrequency")
                _frequency.value = FrequencyState.HasFrequency(mostRelevantFrequency)
            }
        }
    }

    private fun cleanupUsingHPS(fftFrequencyDomain: List<Double>): List<Double> =
        HarmonyProductSpectrum.hps(fftFrequencyDomain, hpsIterations)

    private fun lookupFrequencyDomainAfterHPS(
        frequencyDomain: List<Double>,
        threashold: Double
    ): RelevancyAndIndex {
        var maxValue = threashold
        var indexFromBottom = -1
        for (index in minFourierIndexSearched until maxFourierIndexSearched) {
            if (frequencyDomain[index] > maxValue) {
                indexFromBottom = index
                maxValue = frequencyDomain[index]
            }
        }
        Timber.d("index$indexFromBottom")
        return if (indexFromBottom == -1) {
            RelevancyAndIndex.NotFound
        } else {
            RelevancyAndIndex.Found(maxValue, indexFromBottom)
        }

    }

    private fun lookupFrequencyDomain(
        frequencyDomain: List<Double>,
        threashold: Double
    ): RelevancyAndIndex {
        if (hpsActive) return lookupFrequencyDomainAfterHPS(frequencyDomain, threashold)

        var maxValue = threashold
        var counter: Int? = null // to skip rest of indexes when we fond result
        var indexFromBottom = -1
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
        return if (indexFromBottom == -1) {
            RelevancyAndIndex.NotFound
        } else {
            RelevancyAndIndex.Found(maxValue, indexFromBottom)
        }
    }


    private fun fineTuneLookup(
        pcmAudioData: ShortArray,
        fftMostRelevantIndex: Int,
        accuracy: Frequency
    ): RelevancyAndIndex.Found {
        val fineTuneDomain =
            FourierTransform.fineTuneDFT(
                pcmAudioData,
                from = fftMostRelevantIndex - fineTuneLookupRange,
                to = fftMostRelevantIndex + fineTuneLookupRange,
                accuracy
            ).map { it.magnitude() }

        var fineTuneIndex = -1
        var finalMaxValue = -1.0

        for (i in fineTuneDomain.indices) {
            if (fineTuneDomain[i] > finalMaxValue) {
                finalMaxValue = fineTuneDomain[i]
                fineTuneIndex = i
            }
        }

        check (fineTuneIndex != -1) { "Fine tune index is -1. This should not happen." }

        return RelevancyAndIndex.Found(
            finalMaxValue,
            fftMostRelevantIndex - fineTuneLookupRange + fineTuneIndex * accuracy.toFourierIndexDouble()
        )
    }

    /**
     * value in frequency domain with its idnex
     */
    sealed interface RelevancyAndIndex {
        object NotFound : RelevancyAndIndex
        data class Found(val relevancy: Double, val index: Double) : RelevancyAndIndex {
            constructor(relevancy: Double, indexFromBottom: Int) : this(
                relevancy,
                indexFromBottom.toDouble()
            )
        }
    }


    sealed interface FrequencyState {
        object Silence : FrequencyState
        class HasFrequency(val frequency: Double) : FrequencyState
    }

    enum class FrequencyReaderState {
        Stopped,
        Recording
    }

    @Suppress("VariableNaming")
    private val TAG = this.javaClass.simpleName
}


class ViolinSingleFrequencyReader(pcmAudioRecorder: PcmAudioRecorder): SingleFrequencyReader(
    pcmAudioRecorder = pcmAudioRecorder,
    hpsIterations = 4
)

class SoundGeneratorFrequencyReader(pcmAudioRecorder: PcmAudioRecorder): SingleFrequencyReader(
    pcmAudioRecorder = pcmAudioRecorder,
    hpsIterations = 1
)