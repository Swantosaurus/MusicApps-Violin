package com.kobera.music.violin.feature.scales

import android.content.Context
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.notes.scale.MajorScale
import com.kobera.music.common.notes.scale.MinorScale
import com.kobera.music.common.notes.scale.Scale
import com.kobera.music.violin.feature.recognizeNote.model.RecognizeNoteScales
import com.kobera.music.violin.feature.recognizeNote.model.RecognizeNoteSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

val Context.getScalesSheets by dataStore("scales_sheets.json", serializer = RecognizeNoteSerializer)

class ScalesViewModel(applicationContext: Context) : ViewModel() {
    private val scalesDataSore = applicationContext.getScalesSheets

    private val _scaleState = MutableStateFlow<ScaleState>(ScaleState.Loading)

    val scaleState = _scaleState.asStateFlow()

    init {
        viewModelScope.launch {
            scalesDataSore.data.collect { scales ->
                val scale: Scale = scales.majorScales.firstOrNull() ?: scales.minorScales.first()
                _scaleState.value = ScaleState.Ready(scale = scale)
            }
        }
    }

    fun changeScale(scale: Scale){
        viewModelScope.launch {
            scalesDataSore.updateData {
                when(scale) {
                    is MajorScale -> RecognizeNoteScales(majorScales = listOf(scale))
                    is MinorScale -> RecognizeNoteScales(minorScales = listOf(scale))
                    else -> error("Unknown scale type")
                }
            }
        }
    }
}

sealed interface ScaleState{
    object Loading : ScaleState
    data class Ready(val scale: Scale) : ScaleState
}
