package com.kobera.music.violin.feature.recognize_note

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.model.GamesAudioSensitivityStorage
import com.kobera.music.common.notes.Notes
import com.kobera.music.common.notes.TwelvetoneNote
import com.kobera.music.common.notes.frequency.FrequencyToNote
import com.kobera.music.common.notes.frequency.NoteWithFrequency
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import com.kobera.music.common.score.ScoreRepository
import com.kobera.music.common.score.data.ScoreEntity
import com.kobera.music.common.score.data.ScoreType
import com.kobera.music.common.sound.SingleFrequencyReader
import com.kobera.music.common.sound.SingleFrequencyReader.FrequencyState
import com.kobera.music.common.sound.frequency_baseline.A4Frequency
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.recognize_note.model.RecognizeNoteScales
import com.kobera.music.violin.feature.recognize_note.model.RecognizeNoteSerializer
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant.now
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

val Context.recognizeNoteDataStore by dataStore("recognize_note_data_store.json", serializer = RecognizeNoteSerializer)
class RecognizeNoteViewModel(
    private val singleFrequencyReader: SingleFrequencyReader,
    applicationContext: Context,
    a4Frequency: A4Frequency,
    private val gamesAudioSensitivityStorage: GamesAudioSensitivityStorage,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val scalesDataStore : DataStore<RecognizeNoteScales> = applicationContext.recognizeNoteDataStore

    private val _scales: MutableStateFlow<RecognizeNoteScaleState> = MutableStateFlow(
        RecognizeNoteScaleState.Loading
    )

    //val scales = _scales.asStateFlow()

    private val _generatedNote: MutableStateFlow<GeneratedNoteState> = MutableStateFlow(
        GeneratedNoteState.Loading
    )
    private val _recognizeNoteState: MutableStateFlow<RecognizeNoteState> =
        MutableStateFlow(RecognizeNoteState.NotEntered)

    private val _sensitivity = MutableStateFlow(0.0f)

    val generatedNote = _generatedNote.asStateFlow()

    val sensitivity = _sensitivity.asStateFlow()

    val recognizeNoteState = _recognizeNoteState.asStateFlow()

    init {
        viewModelScope.launch {
            scalesDataStore.data.collect {
                val first = _scales.value is RecognizeNoteScaleState.Loading
                _scales.value = RecognizeNoteScaleState.Ready(it)
                if (first) {
                    generateRandomNote()
                }
            }
        }

        viewModelScope.launch {
           readFrequency(a4Frequency)
        }

        viewModelScope.launch {
            gamesAudioSensitivityStorage.sensitivity.collect { sensitivity ->
                setSilenceTreashold(sensitivity, init = true)
                cancel()
            }
        }

        viewModelScope.launch {
            gamesAudioSensitivityStorage.sensitivity.collect { sensitivity ->
                _sensitivity.value = sensitivity
            }
        }
    }

    private var lastNote: NoteWithFrequency? = null
    private suspend fun readFrequency(a4Frequency: A4Frequency){
        singleFrequencyReader.frequency.collect { frequencyState ->
            if (_recognizeNoteState.value !is RecognizeNoteState.NotEntered) {
                return@collect
            }
            when (frequencyState) {
                is FrequencyState.Silence -> {
                    lastNote = null
                }
                is FrequencyState.HasFrequency -> {
                    (_generatedNote.value as? GeneratedNoteState.Ready)?.let { ready ->
                        if (_recognizeNoteState.value !is RecognizeNoteState.NotEntered) {
                            return@collect
                        }
                        val noteFromFrequency = FrequencyToNote.findClosestNote(
                            frequency = frequencyState.frequency,
                            notes = Notes.getNotes(a4Frequency.frequency.value, inTunePrecision = Notes.InTunePrecision.MEDIUM).values
                        )

                        //playing can ocure in middle of searched sector therefor 1.st note is not correct
                        if(lastNote == null || ! (lastNote!! sameNoteAs noteFromFrequency)) {
                            lastNote = noteFromFrequency
                            return@collect
                        }


                        _recognizeNoteState.value =
                            if (noteFromFrequency sameNoteAs ready.noteAndKeySignature.note.toTwelveTone()) {
                                if (noteFromFrequency.isInTune(frequencyState.frequency)) {
                                    val res = RecognizeNoteState.InTune()
                                    scoreRepository.insertScore(
                                        ScoreEntity(
                                            0,
                                            ScoreType.RecognizeNote,
                                            res.scoreAdded,
                                            now().toEpochMilli()
                                        )
                                    )
                                    res
                                } else {
                                    val differenceAngle = noteFromFrequency.getDifferenceAngle(
                                        frequencyState.frequency,
                                        180.0
                                    )
                                    scoreRepository.insertScore(
                                        ScoreEntity(
                                            0,
                                            ScoreType.RecognizeNote,
                                            60,
                                            now().toEpochMilli()
                                        )
                                    )
                                    if (differenceAngle > 0) {
                                        RecognizeNoteState.NotInTuneAbove()
                                    } else {
                                        RecognizeNoteState.NotInTuneBelow()
                                    }
                                }
                            } else {
                                RecognizeNoteState.Wrong(SheetNote.fromTwelveTone(noteFromFrequency))
                            }
                        resetAnimation()
                    }
                }
            }
        }
    }

    private fun resetAnimation(){
        viewModelScope.launch {
            delay(3.seconds)
            _recognizeNoteState.value =
                (_recognizeNoteState.value as RecognizeNoteState.ToShow).copy(
                    visible = false
                )
            delay(1.seconds)
            setSilence()
            generateRandomNote()
        }
    }

    fun startListeningFrequencies() {
        viewModelScope.launch {
            singleFrequencyReader.start()
        }
    }
    fun stopListeningResponses() {
        singleFrequencyReader.stop()
    }
    fun setSilenceTreashold(to: Float, init : Boolean = false) {
        if(!init) {
            viewModelScope.launch {
                gamesAudioSensitivityStorage.updateSensitivity(to)
            }
        }
        singleFrequencyReader.setSilenceThreshold((to * 15_000_000).toLong())
    }

    fun updateScales(scales: RecognizeNoteScales) {
        viewModelScope.launch {
            scalesDataStore.updateData { scales }
        }
    }

    fun keyboardInput(input: TwelvetoneNote) {
        if (_recognizeNoteState.value !is RecognizeNoteState.NotEntered) {
            return
        }
        when (generatedNote.value) {
            is GeneratedNoteState.Loading -> return
            is GeneratedNoteState.Ready -> {
                val generatedNote =
                    (generatedNote.value as GeneratedNoteState.Ready).noteAndKeySignature.note.toTwelveTone()
                if (generatedNote sameNoteAs input) {
                    val newState = RecognizeNoteState.InTuneKeyInput()
                    viewModelScope.launch {
                        scoreRepository.insertScore(
                            ScoreEntity(
                                0,
                                ScoreType.RecognizeNote,
                                newState.scoreAdded,
                                now().toEpochMilli()
                            )
                        )
                    }
                    _recognizeNoteState.value = newState
                }
                else{
                    _recognizeNoteState.value = RecognizeNoteState.Wrong(SheetNote.fromTwelveTone(twelveTone = input), true )
                }
                viewModelScope.launch {
                   resetAnimation()
                }
            }
        }
    }

    private fun generateRandomNote() {
        when (_scales.value) {
            is RecognizeNoteScaleState.Loading -> {}
            is RecognizeNoteScaleState.Ready -> {
                val scales =
                    (_scales.value as RecognizeNoteScaleState.Ready).scales.majorScales +
                            (_scales.value as RecognizeNoteScaleState.Ready).scales.minorScales

                val scale = scales[Random.nextInt(scales.size)]

                var note = scale.getNotes()[Random.nextInt(scale.getNotes().size)]
                if (note < SheetNote.D) {
                    note = note.copy(octave = note.octave + 1)
                }
                _generatedNote.value =
                    GeneratedNoteState.Ready(NoteAndKeySignature(note, scale.getKeySignature()))
            }
        }
    }

    private fun setSilence() {
        _recognizeNoteState.value = RecognizeNoteState.NotEntered
    }
}

sealed interface RecognizeNoteScaleState {
    object Loading : RecognizeNoteScaleState
    class Ready(val scales: RecognizeNoteScales) : RecognizeNoteScaleState
}

sealed interface GeneratedNoteState {
    object Loading : GeneratedNoteState
    class Ready(val noteAndKeySignature: NoteAndKeySignature) : GeneratedNoteState
}


data class NoteAndKeySignature(
    val note: SheetNote,
    val keySignature: KeySignature
) {}

sealed interface RecognizeNoteState {
    sealed interface ToShow : RecognizeNoteState {
        val visible: Boolean
        val iconAndColor: IconAndColor
        val scoreAdded: Int

        data class IconAndColor(@DrawableRes val icon: Int, val color: Color)

        fun copy(visible: Boolean): RecognizeNoteState
    }

    class InTuneKeyInput(visible: Boolean = true) : InTune(visible = visible){
        override val scoreAdded: Int
            get() = 50

        override fun copy(visible: Boolean): RecognizeNoteState {
            return InTuneKeyInput(visible)
        }
    }

    open class InTune(
        override val visible: Boolean = true,
        override val iconAndColor: ToShow.IconAndColor = ToShow.IconAndColor(
            R.drawable.baseline_check_24,
            Color.Green
        ), override val scoreAdded: Int = 200
    ) : ToShow {

        override fun copy(visible: Boolean): RecognizeNoteState {
            return InTune(visible)
        }
    }

    class NotInTuneAbove(
        override val visible: Boolean = true,
        override val iconAndColor: ToShow.IconAndColor = ToShow.IconAndColor(
            R.drawable.baseline_arrow_downward_24,
            Color.Yellow
        ), override val scoreAdded: Int = 60
    ) : ToShow {
        override fun copy(visible: Boolean): RecognizeNoteState {
            return NotInTuneAbove(visible)
        }
    }

    class NotInTuneBelow(
        override val visible: Boolean = true,
        override val iconAndColor: ToShow.IconAndColor = ToShow.IconAndColor(
            R.drawable.baseline_arrow_upward_24,
            Color.Yellow
        ), override val scoreAdded: Int = 60
    ) : ToShow {
        override fun copy(visible: Boolean): RecognizeNoteState {
            return NotInTuneBelow(visible)
        }
    }

    object NotEntered : RecognizeNoteState {}

    class Wrong(
        val wrongNote: SheetNote,
        override val visible: Boolean = true,
        override val iconAndColor: ToShow.IconAndColor = ToShow.IconAndColor(
            R.drawable.baseline_close_24,
            Color.Red
        ), override val scoreAdded: Int = 0
    ) : ToShow {
        override fun copy(visible: Boolean): RecognizeNoteState {
            return Wrong(wrongNote, visible)
        }
    }
}