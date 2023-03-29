package com.kobera.music.violin.feature.tuner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.resource.ResourceProvider
import com.kobera.music.common.sound.SingleFrequencyReader
import com.kobera.music.common.sound.frequency_baseline.A4Frequency
import com.kobera.music.common.sound.notes.FrequencyToNote
import com.kobera.music.common.sound.notes.Note
import com.kobera.music.violin.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class TunerViewModel(
    private val resourceProvider: ResourceProvider,
    private val frequencyReader: SingleFrequencyReader,
    val a4Frequency: A4Frequency
): ViewModel() {

    private val _note : MutableStateFlow<LastNoteState> = MutableStateFlow(LastNoteState.Silence())

    val note = _note.asStateFlow()



    val sensitivity : Flow<Float> = frequencyReader.silenceThreshold.map { (it.toDouble()/10_000_000).toFloat() }

    private var lastHadNote: LastNoteState.HasNote? = null

    fun startRecording() {
        frequencyReader.startSingleToneReading()
        viewModelScope.launch {
            frequencyReader.frequency.collect { it ->
                when(it) {
                    is SingleFrequencyReader.FrequencyState.Silence -> {
                        lastHadNote?.let { lastNote ->
                            _note.value = LastNoteState.Silence(lastNote.note, lastNote.frequency, lastNote.differenceAngle)
                        }
                    }
                    is SingleFrequencyReader.FrequencyState.Frequency -> {
                        val note = FrequencyToNote.transform(
                            frequency = it.frequency,
                            a4Frequency = a4Frequency.frequency.value,
                            unknownString = resourceProvider.getString(R.string.unknown)
                        )
                        lastHadNote = LastNoteState.HasNote(
                                note = note,
                                frequency = it.frequency,
                                differenceAngle = note.getDifferenceAngle(it.frequency, 180.0 / 2 / 8 * 7)
                            )
                        _note.value = lastHadNote!!
                    }
                }
            }
        }
    }

    fun setA4Frequency(frequency: Double) {
        a4Frequency.set(frequency)
    }

    fun setSensitivity(to: Double) {
        Timber.d("setting sensitivity to : $to")
        frequencyReader.setSilenceThreshold((to*10_000_000).toLong())
    }

    fun stopRecording() {
        frequencyReader.stop()
    }
}

sealed interface LastNoteState {
    class Silence(
        note: Note = Note("A4", 440.0),
        frequency: Double = 440.0,
        differenceAngle: Double = 0.0
    ): HasNote(
        note = note,
        frequency = frequency,
        differenceAngle = differenceAngle
    )
    open class HasNote(val note: Note, val frequency: Double, val differenceAngle: Double): LastNoteState {
        fun isInTune() = note.isInTune(frequency)
    }
}