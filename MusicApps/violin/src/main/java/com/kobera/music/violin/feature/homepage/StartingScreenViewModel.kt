package com.kobera.music.violin.feature.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.score.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StartingScreenViewModel(
    private val scoreRepository: ScoreRepository
): ViewModel() {

    private val _score = MutableStateFlow<ScoreState>(
        ScoreState.Loading
    )

    val score = _score.asStateFlow()
    init {
        setScoreListener()
    }

    private fun setScoreListener() =
        viewModelScope.launch {
            @Suppress("TooGenericExceptionCaught")
            try {
                scoreRepository.getTotalScore().collect{
                    _score.value = ScoreState.Success(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _score.value = ScoreState.Error(e)
            }
        }
}

sealed interface ScoreState{
    object Loading: ScoreState
    data class Success(val score: Int): ScoreState
    data class Error(val error: Throwable): ScoreState
}
