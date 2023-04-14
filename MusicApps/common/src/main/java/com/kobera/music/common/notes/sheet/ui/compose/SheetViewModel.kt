package com.kobera.music.common.notes.sheet.ui.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

val sheetModule = module {
    viewModel {
        SheetViewModel()
    }
}

class SheetViewModel: ViewModel() {
    val heightFlow = MutableStateFlow(
        0f
    )

    fun setHeight(height: Float) {
        Timber.d("settingHeight")
        if(heightFlow.value == height) return
        heightFlow.value = height
    }
}