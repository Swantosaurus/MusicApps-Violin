package com.kobera.music.common.notes.sheet.ui.compose

import androidx.lifecycle.ViewModel
import com.kobera.music.common.notes.sheet.InnerSheetNote
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScaleDetailViewModel : ViewModel() {
    private val _scoreState = MutableStateFlow<ScoreState>(
        ScoreState.Loading
    )

    val scoreState = _scoreState.asStateFlow()

    init {
        setScore(
            listOf(
                SheetNote(InnerSheetNote.D, octave = 4),
                SheetNote(InnerSheetNote.E, octave = 4),
                SheetNote(
                    InnerSheetNote.F,
                    noteParams = SheetNote.Params(
                        duration = SheetNote.Params.Duration.Half,
                        accidental = SheetNote.Params.Accidental.Sharp
                    ),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.G,
                    noteParams = SheetNote.Params(duration = SheetNote.Params.Duration.Half),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.G,
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.G,
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.G,
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.F,
                    noteParams = SheetNote.Params(accidental = SheetNote.Params.Accidental.Sharp),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.E,
                    noteParams = SheetNote.Params(duration = SheetNote.Params.Duration.Half),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.F,
                    noteParams = SheetNote.Params(
                        duration = SheetNote.Params.Duration.Half,
                        accidental = SheetNote.Params.Accidental.Sharp
                    ),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.F,
                    noteParams = SheetNote.Params(accidental = SheetNote.Params.Accidental.Sharp),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.F,
                    noteParams = SheetNote.Params(accidental = SheetNote.Params.Accidental.Sharp),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.F,
                    noteParams = SheetNote.Params(accidental = SheetNote.Params.Accidental.Sharp),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.E,
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.D,
                    noteParams = SheetNote.Params(duration = SheetNote.Params.Duration.Half),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.E,
                    noteParams = SheetNote.Params(duration = SheetNote.Params.Duration.Half),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.E,
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.E,
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.E,
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.F,
                    noteParams = SheetNote.Params(accidental = SheetNote.Params.Accidental.Sharp),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.E,
                    noteParams = SheetNote.Params(duration = SheetNote.Params.Duration.Half),
                    octave = 4
                ),

                SheetNote(
                    InnerSheetNote.D,
                    noteParams = SheetNote.Params(duration = SheetNote.Params.Duration.Half),
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.D,
                    octave = 4
                ),
                SheetNote(
                    InnerSheetNote.D,
                    octave = 4
                ),
            ),
            keySignature = KeySignature.Sharps(2)
        )
    }

    fun setScore(score: List<SheetNote>, keySignature: KeySignature) {
        _scoreState.value = ScoreState.Success(score, keySignature)
    }
}

sealed interface ScoreState {
    object Loading : ScoreState
    data class Error(val message: String) : ScoreState
    data class Success(val score: List<SheetNote>, val keySignature: KeySignature) : ScoreState
}
