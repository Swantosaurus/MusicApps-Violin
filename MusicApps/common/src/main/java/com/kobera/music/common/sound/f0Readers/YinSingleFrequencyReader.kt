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


/**
 * PDA - Pitch Detection Algorithm - based on YIN
 * erturns the frequency of a single frequency from a PCM audio stream.
 *
 * -- less acurate than [NDFTFrequencyReader] method but requires less audio samples.
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
       //TODO("Not yet implemented")
    }

    private fun startTransformOnNewCoroutine(){
        job = scope.launch {
            pcmAudioRecorder.pcmAudioDataFlow.collect { audioData ->
                if(audioData.sum() == 0) return@collect //audio recording not started yet

                val pitch = yin.pitchDetection(audioData = audioData)
                if(pitch <= 80.0){
                    _frequency.value = FrequencyState.Silence
                } else {
                    _frequency.value = FrequencyState.HasFrequency(pitch)
                }
            }
        }
    }
}
