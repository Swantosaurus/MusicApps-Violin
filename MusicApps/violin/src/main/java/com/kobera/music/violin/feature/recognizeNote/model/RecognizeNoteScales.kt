package com.kobera.music.violin.feature.recognizeNote.model

import com.kobera.music.common.notes.scale.MajorScale
import com.kobera.music.common.notes.scale.MinorScale
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * Represents all scales that are used to recognize a note.
 */
@kotlinx.serialization.Serializable
data class RecognizeNoteScales(
    val majorScales: PersistentList<MajorScale> = persistentListOf(),
    val minorScales: PersistentList<MinorScale> = persistentListOf(),
)
