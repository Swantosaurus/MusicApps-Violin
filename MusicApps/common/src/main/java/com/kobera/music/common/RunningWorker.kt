package com.kobera.music.common


/**
 * Interface for workers that can be started and stopped
 */
interface RunningWorker {
    fun start()
    fun stop()
}


/**
 * State of the worker
 */
enum class WorkerState {
    Stopped,
    Running
}
