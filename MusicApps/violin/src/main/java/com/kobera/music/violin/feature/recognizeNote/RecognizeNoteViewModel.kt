package com.kobera.music.violin.feature.recognizeNote

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.model.GamesAudioSensitivityStorage
import com.kobera.music.common.notes.Tones
import com.kobera.music.common.notes.TwelvetoneTone
import com.kobera.music.common.notes.frequency.FrequencyToTone
import com.kobera.music.common.notes.frequency.InTunePrecision
import com.kobera.music.common.notes.frequency.ToneWithFrequency
import com.kobera.music.common.notes.scale.MajorScale
import com.kobera.music.common.notes.scale.MinorScale
import com.kobera.music.common.notes.scale.Scale
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.SheetNote.Companion.toSheetNote
import com.kobera.music.common.score.ScoreRepository
import com.kobera.music.common.score.data.ScoreEntity
import com.kobera.music.common.score.data.ScoreType
import com.kobera.music.common.sound.f0Readers.FrequencyState
import com.kobera.music.common.sound.f0Readers.SingleFrequencyReaderWorker
import com.kobera.music.common.sound.frequency.A4Frequency
import com.kobera.music.common.ui.component.TUNER_METER_ANGLE
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.recognizeNote.model.RecognizeNoteScales
import com.kobera.music.violin.feature.recognizeNote.model.RecognizeNoteSerializer
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

val Context.recognizeNoteDataStore by dataStore("recognize_note_data_store.json", serializer = RecognizeNoteSerializer)


class RecognizeNoteViewModel(
    private val singleFrequencyReader: SingleFrequencyReaderWorker,
    applicationContext: Context,
    a4Frequency: A4Frequency,
    private val gamesAudioSensitivityStorage: GamesAudioSensitivityStorage,
    private val scoreRepository: ScoreRepository
) : ViewModel() {
    private val scalesDataStore : DataStore<RecognizeNoteScales> = applicationContext.recognizeNoteDataStore

    private val _scales: MutableStateFlow<RecognizeNoteScaleState> = MutableStateFlow(
        RecognizeNoteScaleState.Loading
    )

    val scales = _scales.asStateFlow()

    private val _microphoneEnabled : MutableStateFlow<Boolean> = MutableStateFlow(false)

    val microphoneEnabled = _microphoneEnabled.asStateFlow()

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
                _scales.value = RecognizeNoteScaleState.Ready(it)
                generateRandomNote()
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

    private var lastNote: ToneWithFrequency? = null
    private suspend fun readFrequency(a4Frequency: A4Frequency){
        singleFrequencyReader.frequency.collect { frequencyState ->
            if(_microphoneEnabled.value.not()) {
                return@collect
            }
            if (_recognizeNoteState.value !is RecognizeNoteState.NotEntered) {
                return@collect
            }
            when (frequencyState) {
                is FrequencyState.Silence -> {
                    lastNote = null
                }
                is FrequencyState.HasFrequency -> {
                    if(_generatedNote.value !is GeneratedNoteState.Ready) {
                        return@collect
                    }
                    if (_recognizeNoteState.value !is RecognizeNoteState.NotEntered) {
                        return@collect
                    }
                    val noteFromFrequency = FrequencyToTone.findClosestTone(
                        frequency = frequencyState.frequency,
                        notes = Tones.getTones(
                            a4Frequency.frequency.value,
                            inTunePrecision = InTunePrecision.MEDIUM
                        ).values
                    )

                    //playing can ocure in middle of searched sector therefor 1.st note is not correct
                    if(lastNote == null || ! (lastNote!! sameNoteAs noteFromFrequency)) {
                        lastNote = noteFromFrequency
                        return@collect
                    }
                    val toShow = newToShowState(
                        noteFromFrequency = noteFromFrequency,
                        ready = _generatedNote.value as GeneratedNoteState.Ready,
                        frequencyState = frequencyState
                    )

                    _recognizeNoteState.value = toShow
                    insertScoreToDb(toShow.scoreAdded)
                    resetAnimation()
                }
            }
        }
    }



    private fun newToShowState(
        noteFromFrequency: ToneWithFrequency,
        ready: GeneratedNoteState.Ready,
        frequencyState: FrequencyState.HasFrequency
    ): RecognizeNoteState.ToShow {
        return if (noteFromFrequency sameNoteAs ready.noteAndScale.note.toTwelveTone()) {
            if (noteFromFrequency.isInTune(frequencyState.frequency)) {
                RecognizeNoteState.InTune()
            } else {
                val differenceAngle = noteFromFrequency.getDifferenceAngle(
                    frequencyState.frequency,
                    TUNER_METER_ANGLE
                )
                if (differenceAngle > 0) {
                    RecognizeNoteState.NotInTuneAbove()
                } else {
                    RecognizeNoteState.NotInTuneBelow()
                }
            }
        } else {
            RecognizeNoteState.Wrong(noteFromFrequency.toSheetNote())
        }

    }

    private fun resetAnimation() {
        viewModelScope.launch {
            delay(3.seconds)
            _recognizeNoteState.value =
                (_recognizeNoteState.value as RecognizeNoteState.ToShow).copy(
                    visible = false
                )
            delay(1.seconds)
            resetRecognizeNoteState()
            generateRandomNote()
        }
    }

    fun startListeningFrequencies() {
        _microphoneEnabled.value = true
        viewModelScope.launch {
            singleFrequencyReader.start()
        }
    }
    fun stopListeningResponses() {
        _microphoneEnabled.value = false
        singleFrequencyReader.stop()
    }
    fun setSilenceTreashold(to: Float, init : Boolean = false) {
        if(!init) {
            viewModelScope.launch {
                gamesAudioSensitivityStorage.updateSensitivity(to)
            }
        }
        singleFrequencyReader.setSilenceThreshold(
            (to * GamesAudioSensitivityStorage.gamesSensitivityMultiplayer).toDouble())
    }

    fun addScale(scale: Scale){
        (scales.value as? RecognizeNoteScaleState.Ready)?.scales?.let {
            updateScales(
                when(scale){
                    is MajorScale -> {
                        check(!it.majorScales.contains(scale)){ "scale already added"}
                        it.copy(majorScales = it.majorScales + scale)
                    }
                    is MinorScale -> {
                        check(!it.minorScales.contains(scale)){ "scale already added"}
                        it.copy(minorScales = it.minorScales + scale)
                    }
                    else -> error("not supported scale")
                }
            )
        }
    }

    fun removeScale(scale: Scale){
        (scales.value as? RecognizeNoteScaleState.Ready)?.scales?.let {
            if(it.majorScales.size + it.minorScales.size == 1){
                return
            }
            updateScales(
                when(scale){
                    is MajorScale -> {
                        check(it.majorScales.contains(scale)){ "scale not added"}
                        it.copy(majorScales = it.majorScales - scale)
                    }
                    is MinorScale -> {
                        check(it.minorScales.contains(scale)){ "scale not added"}
                        it.copy(minorScales = it.minorScales - scale)
                    }
                    else -> error("not supported scale")
                }
            )
        }
    }

    fun updateScales(scales: RecognizeNoteScales) {
        viewModelScope.launch {
            scalesDataStore.updateData { scales }
        }
    }

    fun keyboardInput(input: TwelvetoneTone) {
        if (_recognizeNoteState.value !is RecognizeNoteState.NotEntered) {
            return
        }
        when (generatedNote.value) {
            is GeneratedNoteState.Loading -> return
            is GeneratedNoteState.Ready -> {
                val generatedNote =
                    (generatedNote.value as GeneratedNoteState.Ready).noteAndScale.note.toTwelveTone()

                val newState = if (generatedNote sameNoteAs input) {
                    RecognizeNoteState.InTuneKeyInput()
                } else {
                    RecognizeNoteState.Wrong(input.toSheetNote(), true)
                }

                insertScoreToDb(newState.scoreAdded)

                _recognizeNoteState.value = newState

                viewModelScope.launch {
                    resetAnimation()
                }
            }
        }
    }


    fun setMicrophoneEnabled(enabled: Boolean, context: Context): Boolean  {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (_microphoneEnabled.value) {
                _microphoneEnabled.value = false
            }
            return false
        }
        _microphoneEnabled.value = enabled
        return true
    }


    private fun insertScoreToDb(score: Int) {
        viewModelScope.launch {
            scoreRepository.insertScore(
                ScoreEntity(
                    0,
                    ScoreType.RecognizeNote,
                    score,
                    Calendar.getInstance().timeInMillis
                )
            )
        }
    }

    //TODO selectScales
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
                    GeneratedNoteState.Ready(NoteAndScale(note, scale))
            }
        }
    }

    @Suppress("EmptyClassBlock") // for extension functions
    companion object {}
    private fun resetRecognizeNoteState() {
        _recognizeNoteState.value = RecognizeNoteState.NotEntered
    }
}

sealed interface RecognizeNoteScaleState {
    object Loading : RecognizeNoteScaleState
    class Ready(val scales: RecognizeNoteScales) : RecognizeNoteScaleState
}

sealed interface GeneratedNoteState {
    object Loading : GeneratedNoteState
    class Ready(val noteAndScale: NoteAndScale) : GeneratedNoteState
}


data class NoteAndScale(
    val note: SheetNote,
    val scale: Scale
) {
    val keySignature
        get() = scale.getKeySignature()
}


sealed interface RecognizeNoteState {
    sealed interface ToShow : RecognizeNoteState {
        val visible: Boolean
        val iconAndColor: IconAndColor
        @Suppress("MagicNumber")
        val scoreAdded: Int

        data class IconAndColor(@DrawableRes val icon: Int, val color: Color)

        fun copy(visible: Boolean): RecognizeNoteState
    }

    class InTuneKeyInput(visible: Boolean = true) : InTune(visible = visible){
        @Suppress("MagicNumber")
        override val scoreAdded: Int = 50

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

    object NotEntered : RecognizeNoteState

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
