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

    private val _totalScore = MutableStateFlow<ScoreState>(
        ScoreState.Loading
    )

    val totalScore = _totalScore.asStateFlow()

    private val _totalWinsAndLoses = MutableStateFlow<WinsAndLosesState>(
        WinsAndLosesState.Loading
    )

    val totalWinsAndLoses = _totalWinsAndLoses.asStateFlow()

    private val _scoreToday = MutableStateFlow<ScoreState>(
        ScoreState.Loading
    )

    val scoreToday = _scoreToday.asStateFlow()

    private val _winsAndLosesToday = MutableStateFlow<WinsAndLosesState>(
        WinsAndLosesState.Loading
    )

    val winsAndLosesToday = _winsAndLosesToday.asStateFlow()

    init {
        setScoreListener()
    }


    @Suppress("TooGenericExceptionCaught")
    private fun setScoreListener() {
        viewModelScope.launch {
            try {
                scoreRepository.getTotalScore().collect {
                    _totalScore.value = ScoreState.Success(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _totalScore.value = ScoreState.Error(e)
            }
        }
        viewModelScope.launch {
            try {
                scoreRepository.getTotalScoreToday().collect {
                    _scoreToday.value = ScoreState.Success(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _scoreToday.value = ScoreState.Error(e)
            }
        }
        viewModelScope.launch {
            try {
                scoreRepository.getWinsAndLosesToday().collect {
                    _winsAndLosesToday.value = WinsAndLosesState.Success(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _winsAndLosesToday.value = WinsAndLosesState.Error(e)
            }
        }
        viewModelScope.launch {
            try {
                scoreRepository.getTotalWinsAndLoses().collect {
                    _totalWinsAndLoses.value = WinsAndLosesState.Success(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _totalWinsAndLoses.value = WinsAndLosesState.Error(e)
            }
        }
    }
}

sealed interface ScoreState{
    object Loading: ScoreState
    data class Success(val score: Int): ScoreState
    data class Error(val error: Throwable): ScoreState
}

sealed interface WinsAndLosesState{
    object Loading: WinsAndLosesState
    data class Success(val winsAndLoses: ScoreRepository.WinsAndLoses): WinsAndLosesState

    data class Error(val error: Throwable): WinsAndLosesState
}
