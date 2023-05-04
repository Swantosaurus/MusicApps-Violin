package com.kobera.music.violin.feature.tuner.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.notes.Tones
import com.kobera.music.common.notes.frequency.FrequencyToTone
import com.kobera.music.common.notes.frequency.InRangePrecision
import com.kobera.music.common.notes.frequency.ToneWithFrequency
import com.kobera.music.common.sound.SingleFrequencyReader
import com.kobera.music.common.sound.frequency.A4Frequency
import com.kobera.music.common.ui.component.LastNoteState
import com.kobera.music.violin.feature.tuner.model.TunerSensitivityStorage
import com.kobera.music.violin.sound.notes.violinStrings
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class TunerViewModel(
    applicationContext: Context,
    private val frequencyReader: SingleFrequencyReader,
    val a4Frequency: A4Frequency,
    val tunerSensitivityStorage : TunerSensitivityStorage
): ViewModel() {
    private val _note : MutableStateFlow<LastNoteState> = MutableStateFlow(LastNoteState.Silence())

    val note = _note.asStateFlow()

    private val _notesInTuner: MutableStateFlow<NotesInTunerState> =
        MutableStateFlow(NotesInTunerState.init(applicationContext = applicationContext))

    val notesInTuner = _notesInTuner.asStateFlow()

    val sensitivity : Flow<Float> = tunerSensitivityStorage.sensitivity

    private var lastHadNote: LastNoteState.HasNote? = null

    fun startRecording() {
        frequencyReader.start()
        viewModelScope.launch {
            frequencyReader.frequency.collect {
                when(it) {
                    is SingleFrequencyReader.FrequencyState.Silence -> {
                        lastHadNote?.let { lastNote ->
                            _note.value = LastNoteState.Silence(lastNote.note, lastNote.frequency)
                        }
                    }
                    is SingleFrequencyReader.FrequencyState.HasFrequency -> {
                        val note = FrequencyToTone.findClosestTone(
                            frequency = it.frequency,
                            notes = _notesInTuner.value.notes.values
                        )
                        lastHadNote = LastNoteState.HasNote(
                                note = note,
                                frequency = it.frequency,
                            )
                        _note.value = lastHadNote!!
                    }
                }
            }
        }

        viewModelScope.launch {
            sensitivity.collect() {
                setSensitivity(it, init = true)
                cancel()
            }
        }
    }

    fun setA4Frequency(frequency: Double) {
        a4Frequency.set(frequency)
    }

    fun setSensitivity(to: Float, init : Boolean = false) {
        if(!init) {
            viewModelScope.launch {
                tunerSensitivityStorage.storeSensitivity(to)
            }
        }

        frequencyReader.setSilenceThreshold((to*silenceThresholdMultiplier).toLong())
    }

    fun setTunerNotes(to: NotesInTunerState) {
        _notesInTuner.value = to
    }

    fun stopRecording() {
        frequencyReader.stop()
    }
    companion object{
        private const val silenceThresholdMultiplier = 30_000_000
    }
}

abstract class  NotesInTunerState : KoinComponent {
    abstract val notes: Map<String, ToneWithFrequency>
    protected val a4Frequency: A4Frequency by inject()

    companion object {
        private var preferences: SharedPreferences? = null
        fun init(applicationContext: Context): NotesInTunerState {
            preferences =
                applicationContext.getSharedPreferences("notes_in_tuner", Context.MODE_PRIVATE)
            preferences!!.getInt("notes_in_tuner", 0).let {
                return when (it) {
                    0 -> AllNotes()
                    1 -> ViolinNotes()
                    else -> AllNotes()
                }
            }
        }
    }

    class AllNotes : NotesInTunerState() {
        init {
            with(preferences!!.edit()) {
                putInt("notes_in_tuner", 0)
                apply()
            }
        }

        override val notes: Map<String, ToneWithFrequency>
            get() = Tones.getTones(frequencyA4 = a4Frequency.frequency.value)
    }

    class ViolinNotes : NotesInTunerState() {
        init {
            with(preferences!!.edit()) {
                putInt("notes_in_tuner", 1)
                apply()
            }
        }

        override val notes: Map<String, ToneWithFrequency>
            get() = Tones.getNotesViolin(a4Frequency.frequency.value)

        private fun Tones.getNotesViolin(frequencyA4: Double): Map<String, ToneWithFrequency> {
            val onlyViolinNotes = getTones(frequencyA4 = frequencyA4)
                .filter {
                    for (violinStringNote in violinStrings) {
                        if (it.value sameNoteAs violinStringNote) {
                            return@filter true
                        }
                    }
                    return@filter false
                }.toMutableMap()
            onlyViolinNotes.entries.forEach {
                it.setValue(it.value.copy(rangeInterval = InRangePrecision.MEDIUM))
            }

            return onlyViolinNotes
        }
    }
    class PreviewNotes(override val notes: Map<String, ToneWithFrequency>) : NotesInTunerState()
}
