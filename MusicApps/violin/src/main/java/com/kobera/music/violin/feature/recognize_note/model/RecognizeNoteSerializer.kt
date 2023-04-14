package com.kobera.music.violin.feature.recognize_note.model

import androidx.datastore.core.Serializer
import com.kobera.music.common.notes.scale.MajorScale
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream


@Suppress("BlockingMethodInNonBlockingContext")
object RecognizeNoteSerializer : Serializer<RecognizeNoteScales> {
    override val defaultValue: RecognizeNoteScales = RecognizeNoteScales(
        persistentListOf(MajorScale.D),
        persistentListOf()
    )

    override suspend fun readFrom(input: InputStream): RecognizeNoteScales {
        return try {
            Json.decodeFromString(
                deserializer = RecognizeNoteScales.serializer(),
                string = input.bufferedReader().readText()
            )
        } catch (e: SerializationException) {
            Timber.e(e.toString())
            defaultValue
        }
    }

    override suspend fun writeTo(t: RecognizeNoteScales, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = RecognizeNoteScales.serializer(),
                value = t
            ).toByteArray()
        )
    }
}


