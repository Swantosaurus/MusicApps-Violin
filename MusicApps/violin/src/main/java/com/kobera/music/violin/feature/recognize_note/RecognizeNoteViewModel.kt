package com.kobera.music.violin.feature.recognize_note

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.model.GamesAudioSensitivityStorage
import com.kobera.music.common.notes.frequency.FrequencyToNote
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import com.kobera.music.common.resource.ResourceProvider
import com.kobera.music.common.sound.SingleFrequencyReader
import com.kobera.music.common.sound.SingleFrequencyReader.FrequencyState
import com.kobera.music.common.sound.frequency_baseline.A4Frequency
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.recognize_note.model.RecognizeNoteScales
import com.kobera.music.violin.feature.recognize_note.model.RecognizeNoteSerializer
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

val Context.recognizeNoteDataStore by dataStore("recognize_note_data_store.json", serializer = RecognizeNoteSerializer)
class RecognizeNoteViewModel(
    private val singleFrequencyReader: SingleFrequencyReader,
    applicationContext: Context,
    a4Frequency: A4Frequency,
    resourceProvider: ResourceProvider,
    private val gamesAudioSensitivityStorage: GamesAudioSensitivityStorage
) : ViewModel() {

    private val scalesDataStore : DataStore<RecognizeNoteScales> = applicationContext.recognizeNoteDataStore

    private val _scales: MutableStateFlow<RecognizeNoteScaleState> = MutableStateFlow(
        RecognizeNoteScaleState.Loading
    )

    private val _generatedNote: MutableStateFlow<GeneratedNoteState> = MutableStateFlow(
        GeneratedNoteState.Loading
    )
    private val _recognizeNoteState: MutableStateFlow<RecognizeNoteState> =
        MutableStateFlow(RecognizeNoteState.Silence)

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
           readFrequency(a4Frequency, resourceProvider)
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

    private suspend fun readFrequency(a4Frequency: A4Frequency, resourceProvider: ResourceProvider){
        singleFrequencyReader.frequency.collect { frequencyState ->
            if(_recognizeNoteState.value !is RecognizeNoteState.Silence){
                return@collect
            }
            when (frequencyState) {
                is FrequencyState.Silence -> {}
                is FrequencyState.HasFrequency -> {
                    (_generatedNote.value as? GeneratedNoteState.Ready)?.let { ready ->
                        val noteFromFrequency = FrequencyToNote.transform(
                            frequency = frequencyState.frequency,
                            a4Frequency = a4Frequency.frequency.value,
                            unknownString = resourceProvider.getString(R.string.unknown)
                        )
                        _recognizeNoteState.value = if (noteFromFrequency sameNoteAs ready.noteAndKeySignature.note.toTwelveTone()) {
                            if(noteFromFrequency.isInTune()){
                                RecognizeNoteState.InTune
                            } else {
                                RecognizeNoteState.CorrectNotInTune
                            }
                        } else {
                            RecognizeNoteState.Wrong
                        }
                    }
                }
            }
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

    fun generateRandomNote() {
        setSilence()
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

    fun setSilence() {
        _recognizeNoteState.value = RecognizeNoteState.Silence
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

    object InTune : RecognizeNoteState
    object CorrectNotInTune : RecognizeNoteState
    object Silence : RecognizeNoteState
    object Wrong : RecognizeNoteState
}