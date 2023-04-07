package com.kobera.music.violin.feature.tuner.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import com.kobera.music.common.notes.Notes
import com.kobera.music.common.notes.TwelveToneNoteNames
import com.kobera.music.common.notes.frequency.FrequencyToNote
import com.kobera.music.common.notes.frequency.NoteWithFrequency
import com.kobera.music.common.resource.ResourceProvider
import com.kobera.music.common.sound.SingleFrequencyReader
import com.kobera.music.common.sound.frequency_baseline.A4Frequency
import com.kobera.music.violin.R
import com.kobera.music.violin.sound.notes.violinStrings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import kotlin.math.pow

class TunerViewModel(
    applicationContext: Context,
    private val resourceProvider: ResourceProvider,
    private val frequencyReader: SingleFrequencyReader,
    val a4Frequency: A4Frequency
): ViewModel() {

    private val _note : MutableStateFlow<LastNoteState> = MutableStateFlow(LastNoteState.Silence())

    val note = _note.asStateFlow()

    private val _notesInTuner: MutableStateFlow<NotesInTunerState> =
        MutableStateFlow(NotesInTunerState.init(applicationContext = applicationContext))

    val notesInTuner = _notesInTuner.asStateFlow()

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
                    is SingleFrequencyReader.FrequencyState.HasFrequency -> {
                        val note = FrequencyToNote.transform(
                            frequency = it.frequency,
                            a4Frequency = a4Frequency.frequency.value,
                            unknownString = resourceProvider.getString(R.string.unknown),
                            notes = _notesInTuner.value.notes.values
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

    fun setTunerNotes(to: NotesInTunerState) {
        _notesInTuner.value = to
    }

    fun stopRecording() {
        frequencyReader.stop()
    }
}

abstract class  NotesInTunerState : KoinComponent {
    abstract val notes: Map<String, NoteWithFrequency>
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

    class AllNotes() : NotesInTunerState() {
        init {
            with(preferences!!.edit()) {
                putInt("notes_in_tuner", 0)
                apply()
            }
        }

        override val notes: Map<String, NoteWithFrequency>
            get() = Notes.getNotes(frequencyA4 = a4Frequency.frequency.value)
    }

    class ViolinNotes() : NotesInTunerState() {
        init {
            with(preferences!!.edit()) {
                putInt("notes_in_tuner", 1)
                apply()
            }
        }

        override val notes: Map<String, NoteWithFrequency>
            get() = Notes.getNotesViolin(a4Frequency.frequency.value)
    }

    fun Notes.getNotesViolin(frequencyA4: Double): Map<String, NoteWithFrequency> {
        val onlyViolinNotes = getNotes(frequencyA4 = frequencyA4)
            .filter {
                for (violinStringNote in violinStrings) {
                    if (it.value sameNoteAs violinStringNote) {
                        return@filter true
                    }
                }
                return@filter false
            }.toMutableMap()
        onlyViolinNotes.entries.forEach {
            it.setValue(it.value.copy(rangeInterval = 2.0.pow(7.0/24)))
        }

        return onlyViolinNotes
    }
}

sealed interface LastNoteState {
    class Silence(
        note: NoteWithFrequency = NoteWithFrequency(
            twelveNoteInterpretation = InnerTwelveToneInterpretation.A,
            name = TwelveToneNoteNames.getName(InnerTwelveToneInterpretation.A),
            octave = 4,
            frequency = Notes.defaultA4Frequency,
        ),
        frequency: Double = Notes.defaultA4Frequency,
        differenceAngle: Double = 0.0
    ) : HasNote(
        note = note,
        frequency = frequency,
        differenceAngle = differenceAngle
    )

    open class HasNote(
        val note: NoteWithFrequency,
        val frequency: Double,
        val differenceAngle: Double
    ) : LastNoteState {
        fun isInTune() = note.isInTune(frequency)
    }
}