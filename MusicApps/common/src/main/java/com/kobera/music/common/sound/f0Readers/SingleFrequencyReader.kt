package com.kobera.music.common.sound.f0Readers

import com.kobera.music.common.RunningWorker
import kotlinx.coroutines.flow.StateFlow

/**
 * Returns the frequency of a F_0 frequency from a PCM audio stream.
 */
interface SingleFrequencyReader {
    val frequency: StateFlow<FrequencyState>
    fun setSilenceThreshold(silenceThreshold: Double)
}

/**
 * Combination of [SingleFrequencyReader] and [RunningWorker]
 */
interface SingleFrequencyReaderWorker : RunningWorker, SingleFrequencyReader

/**
 *  returning frequency value state from [SingleFrequencyReader]
 */
sealed interface FrequencyState {
    object Silence : FrequencyState
    class HasFrequency(val frequency: Double) : FrequencyState
}
