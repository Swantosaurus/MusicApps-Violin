package com.kobera.music.violin.feature.recognizeNote.model

import com.kobera.music.common.notes.scale.MajorScale
import com.kobera.music.common.notes.scale.MinorScale
import kotlinx.serialization.Serializable

/**
 * Represents all scales that are used to recognize a note.
 */
@Serializable
data class RecognizeNoteScales(
    val majorScales: List<MajorScale> = listOf(),
    val minorScales: List<MinorScale> = listOf()
)
