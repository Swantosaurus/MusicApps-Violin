package com.kobera.music.common.sound

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


/**
 * A PCM audio recorder. Start recording audio on IO thread and emit the audio data as a flow.
 *
 * @param applicationContext The application context.
 */
class PcmAudioRecorder(private val applicationContext : Context) {
    private var _pcmAudioDataFlow : MutableSharedFlow<ShortArray> = MutableSharedFlow()

    /**
     * The audio data flow. Emits the audio data as a flow. Supports collecten on multiple locations
     */
    val pcmAudioDataFlow = _pcmAudioDataFlow.asSharedFlow()

    private var _state : MutableStateFlow<PcmAudioRecorderState> = MutableStateFlow(
        PcmAudioRecorderState.Stopped
    )

    private var audioRecord: AudioRecord? = null

    private var audioSamples = ShortArray(readSize)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private var job : Job? = null


    /**
     * starts recording audio
     */
    fun start () {
        if(_state.value == PcmAudioRecorderState.Recording){
            return
        }

        _state.value = PcmAudioRecorderState.Recording

        job = scope.launch {
            while(_state.value == PcmAudioRecorderState.Recording) {
                if(audioRecord == null){
                    initializeAudioRecord()
                }

                audioRecord!!.read(audioSamples, 0, audioSamples.size)

                _pcmAudioDataFlow.emit(audioSamples)
            }
            cancel()
        }
    }

    /**
     * stops recording audio
     */
    fun stop(){
        assert(_state.value == PcmAudioRecorderState.Recording)
        _state.value = PcmAudioRecorderState.Stopped
        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null
        job!!.cancel()
        job = null
    }

    private fun initializeAudioRecord(){
        check (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            "No permission to record audio"
        }

        audioRecord = AudioRecord(
            /*audioSource =*/ MediaRecorder.AudioSource.MIC,
            /*sampleRateHz =*/ sampleRate,
            /*channel =*/ AudioFormat.CHANNEL_IN_MONO,
            /* audioFormat = */ AudioFormat.ENCODING_PCM_16BIT,
            /* bufferSizeInBytes = */ readSize * 2
        )
        audioRecord!!.startRecording()
    }

    companion object {
        const val sampleRate = 192_000
        const val readSize = 32_768 // 2 pow 15
    }

    /**
     * The state of the recorder
     */
    enum class PcmAudioRecorderState {
        Recording,
        Stopped
    }
}

/**
 * Defines translation between frequency and fourier index
 */
fun Frequency.Companion.fromFourierIndex(index: Double): Frequency =
    Frequency(index* PcmAudioRecorder.sampleRate/PcmAudioRecorder.readSize)

fun Frequency.toFourierIndexIntRoundDown(): Int =
    toFourierIndexDouble().toInt()

fun Frequency.toFourierIndexDouble(): Double =
    value/PcmAudioRecorder.sampleRate*PcmAudioRecorder.readSize

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation version 3 of the License, or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */