package com.kobera.music.violin.feature.recognize_note.model

import com.kobera.music.common.notes.scale.MajorScale
import com.kobera.music.common.notes.scale.MinorScale
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@kotlinx.serialization.Serializable
data class RecognizeNoteScales(
    val majorScales: PersistentList<MajorScale> = persistentListOf(),
    val minorScales: PersistentList<MinorScale> = persistentListOf(),
)