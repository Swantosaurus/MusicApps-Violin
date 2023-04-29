package com.kobera.music.common.metronome

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _metronomeState : MutableStateFlow<MetronomeState> = MutableStateFlow(
        MetronomeState.Stropped
    )
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
         metronomeJob = viewModelScope.launch {
             while (true){
                 var index = 2
                 _metronomeState.value = MetronomeState.PrimaryTick
                 delay(tickShowDuration)
                 _metronomeState.value = MetronomeState.HideTick
                 delay( (speed.value.seconds - tickShowDuration) / secondsInMinute )

                 while(index < _numberOfTicks.value) {
                     _metronomeState.value = MetronomeState.SecondaryTick
                     delay(tickShowDuration)
                     _metronomeState.value = MetronomeState.HideTick
                     delay( (speed.value.seconds - tickShowDuration) / secondsInMinute )
                 }
             }
        }
    }

    fun stopMetronome(){
        metronomeJob?.cancel()
        _metronomeState.value = MetronomeState.Stropped
    }

    @Suppress("MagicNumber")
    fun changeNumberOfTicks(to: Int) {
        //to avoid nonsense value sets
        val setTo = when(true){
            (to > 8) -> 8
            (to < 1) -> 1
            else -> to
        }
        viewModelScope.launch {
            with(dataStore){
                edit { preferences ->
                    preferences[numberOfTicksKey] = setTo
                }
            }
        }
    }

    @Suppress("MagicNumber")
    fun changeSpeedOfMetronome(to :Int) {
        // to avoid nonsense value sets
        val setTo = when(true){
             (to > 400) -> 400
            (to < 40) -> 40
            else -> to
        }
        viewModelScope.launch {
            with(dataStore){
                edit { preferences ->
                    preferences[speedKey] = setTo
                }
            }
        }
    }

    companion object {
        private const val secondsInMinute = 60
    }
}

sealed interface MetronomeState{

    sealed interface Running : MetronomeState
    object PrimaryTick : Running
    object SecondaryTick: Running
    object HideTick: Running

    object Stropped: MetronomeState
}
