package com.kobera.music.common.metronome

import android.content.Context
import android.media.MediaPlayer
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

val Context.metronomeDataStore by preferencesDataStore("metronome")

class MetronomeViewModel(applicationContext: Context) : ViewModel() {
    private val _metronomeState: MutableStateFlow<MetronomeState> = MutableStateFlow(
        MetronomeState.Stopped
    )

    private val primaryTickSound = MediaPlayer.create(applicationContext, R.raw.metronome1)
    private val secondaryTickSound = MediaPlayer.create(applicationContext, R.raw.metronome2)
    private val tickShowDuration = 100.milliseconds
    private val dataStore = applicationContext.metronomeDataStore
    private val speedKey = intPreferencesKey("metronome_speed")

    private val numberOfTicksKey = intPreferencesKey("metronome_number_of_ticks")

    @Suppress("MagicNumber")
    private val defaultMetronomeSpeed = 80

    @Suppress("MagicNumber")
    private val defaultNumberOfTicks = 4


    private val _speedDS = dataStore.data.map { preferences ->
        preferences[speedKey] ?: defaultMetronomeSpeed
    }


    private val _numberOfTicksDS = dataStore.data.map { preferences ->
        preferences[numberOfTicksKey] ?: defaultNumberOfTicks
    }

    private val _numberOfTicks = MutableStateFlow(defaultNumberOfTicks)

    val numberOfTicks = _numberOfTicks.asStateFlow()


    private val _speed: MutableStateFlow<Int> = MutableStateFlow(defaultMetronomeSpeed)

    val speed = _speed.asStateFlow()



    init {
        viewModelScope.launch {
            _speedDS.collect{
                _speed.value = it
            }
        }
        viewModelScope.launch {
            _numberOfTicksDS.collect{
                _numberOfTicks.value = it
            }
        }
    }

    var metronomeJob : Job? = null

    val tickState = _metronomeState.asStateFlow()

    fun startMetronome(){
        if (metronomeJob?.isActive == true) return
         metronomeJob = viewModelScope.launch {
             while (true){
                 var index = 2
                 _metronomeState.value = MetronomeState.PrimaryTick
                 primaryTickSound.seekTo(0)
                 primaryTickSound.start()
                 delay(tickShowDuration)
                 _metronomeState.value = MetronomeState.HideTick
                 delay(((60.seconds / speed.value) - tickShowDuration))

                 while(index <=_numberOfTicks.value) {
                     _metronomeState.value = MetronomeState.SecondaryTick
                     secondaryTickSound.seekTo(0)
                     secondaryTickSound.start()
                     delay(tickShowDuration)
                     _metronomeState.value = MetronomeState.HideTick
                     delay(((60.seconds / speed.value) - tickShowDuration))
                     index++
                 }
             }
        }
    }

    fun stopMetronome(){
        metronomeJob?.cancel()
        _metronomeState.value = MetronomeState.Stopped
    }

    fun changeNumberOfTicks(to: Int) {
        //to avoid nonsense value sets
        val setTo = when (true) {
            (to > MAX_NUMBER_OF_TICKS) -> MAX_NUMBER_OF_TICKS
            (to < MIN_NUMBER_OF_TICKS) -> MIN_NUMBER_OF_TICKS
            else -> to
        }
        viewModelScope.launch {
            with(dataStore) {
                edit { preferences ->
                    preferences[numberOfTicksKey] = setTo
                }
            }
        }
    }

    fun changeSpeedOfMetronome(to :Int) {
        // to avoid nonsense value sets
        val setTo = when (true) {
            (to > MAX_SPEED) -> MAX_SPEED
            (to < MIN_SPEED) -> MIN_SPEED
            else -> to
        }
        viewModelScope.launch {
            with(dataStore) {
                edit { preferences ->
                    preferences[speedKey] = setTo
                }
            }
        }
    }

    companion object {
        const val MAX_SPEED = 400
        const val MIN_SPEED = 40
        const val MAX_NUMBER_OF_TICKS = 8
        const val MIN_NUMBER_OF_TICKS = 1
    }
}

sealed interface MetronomeState{

    sealed interface Running : MetronomeState
    object PrimaryTick : Running
    object SecondaryTick: Running
    object HideTick: Running

    object Stopped: MetronomeState
}
