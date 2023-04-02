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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PcmAudioRecorder(private val applicationContext : Context) {
    private var _pcmAudioDataFlow : MutableSharedFlow<ShortArray> = MutableSharedFlow()

    val pcmAudioDataFlow = _pcmAudioDataFlow.asSharedFlow()

    private var _state : MutableStateFlow<PcmAudioRecorderState> = MutableStateFlow(
        PcmAudioRecorderState.Stopped
    )

    val state = _state.asStateFlow()

    private var audioRecord: AudioRecord? = null

    private var audioSamples = ShortArray(readSize)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private var job : Job? = null

    fun start () {
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

    private fun initializeAudioRecord(){
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw IllegalStateException("No permission to record audio")
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

    fun stop(){
        assert(_state.value == PcmAudioRecorderState.Recording)
        _state.value = PcmAudioRecorderState.Stopped
        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null
    }

    companion object {
        const val sampleRate = 192_000
        const val readSize = 32_768 // 2 pow 15
    }
}
fun Frequency.Companion.fromFourierIndex(index: Double): Frequency =
    Frequency(index* PcmAudioRecorder.sampleRate/PcmAudioRecorder.readSize)

fun Frequency.toFourierIndexIntRoundDown(): Int =
    toFourierIndexDouble().toInt()

fun Frequency.toFourierIndexDouble(): Double =
    value/PcmAudioRecorder.sampleRate*PcmAudioRecorder.readSize

enum class PcmAudioRecorderState {
    Recording,
    Stopped
}