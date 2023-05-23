package com.kobera.music.common.sound.f0Readers

import com.kobera.music.common.WorkerState
import com.kobera.music.common.sound.PcmAudioRecorder
import com.kobera.music.common.sound.yin.FastYin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs


/**
 * PDA - Pitch Detection Algorithm - based on YIN
 * returns the frequency of a single frequency from a PCM audio stream.
 *
 * -- less accurate than [NDFTFrequencyReader] method but requires less audio samples.
 *
 * TODO add link to Václav Kobera thesis
 *
 * doesn't uses transforms specific for musical instruments therefore theres no need for version for generator
 */
class YinSingleFrequencyReader(
    private val pcmAudioRecorder: PcmAudioRecorder,
    private val yin: FastYin
) : SingleFrequencyReaderWorker {
    private val _frequency: MutableStateFlow<FrequencyState> = MutableStateFlow(FrequencyState.Silence)

    override val frequency: StateFlow<FrequencyState> = _frequency.asStateFlow()

    private val _state  = MutableStateFlow(WorkerState.Stopped)

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private var job: Job? = null

    private val defaultAmplitudeScale = 0.5
    
    private var amplitudeScale: Double = defaultAmplitudeScale

    override fun start() {
        _frequency.value = FrequencyState.Silence
        if (_state.value == WorkerState.Running) {
            Timber.w( "Already stopped")
            return
        }
        pcmAudioRecorder.start()

        startTransformOnNewCoroutine()
    }

    override fun stop() {
        _frequency.value = FrequencyState.Silence
        if (_state.value == WorkerState.Stopped) {
            Timber.w( "Already stopped")
            return
        }

        pcmAudioRecorder.stop()
        job?.cancel()

        _state.value = WorkerState.Stopped
    }

    override fun setSilenceThreshold(silenceThreshold: Double) {
        assert(silenceThreshold in 0.0.. 1.0){
            "silenceThreshold has to be in range <0.0, 1.0>"
        }

        amplitudeScale = silenceThreshold
    }

    private fun startTransformOnNewCoroutine(){
        job = scope.launch {
            pcmAudioRecorder.pcmAudioDataFlow.collect { audioData ->
                if(audioData.sum() == 0) return@collect //audio recording not started yet

                //TODO we can do better by breaking of for loop when we find amplitude > 0 but that's minor optimization
                if(lookupMaxAmplitudeValue(audioData) < amplitudeScale * PcmAudioRecorder.maxAmplitude){
                    _frequency.value = FrequencyState.Silence
                    return@collect
                }


                val pitch = yin.pitchDetection(audioData = audioData)
                @Suppress("MagicNumber")
                if(pitch <= 80.0){
                    _frequency.value = FrequencyState.Silence
                } else {
                    _frequency.value = FrequencyState.HasFrequency(pitch)
                }
            }
        }
    }

    /** O(N) */
    private fun lookupMaxAmplitudeValue(audioData: ShortArray): Int {
        var maxAmplitude = 0
        for (i in audioData.indices) {
            val amplitude = abs(audioData[i].toInt())
            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude
            }
        }
        return maxAmplitude
    }
}
