package com.kobera.music.common.score.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score")
data class ScoreEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val description: ScoreType,
    val score: Int,
    val timestamp: Long,
)

enum class ScoreType {
    RecognizeNote
}