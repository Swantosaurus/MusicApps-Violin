package com.kobera.music.common.score.data

import androidx.room.Database
import androidx.room.RoomDatabase


/**
 * Room database for Score
 */
@Database(entities = [ScoreEntity::class], version = 1)
abstract class ScoreDatabase: RoomDatabase() {
    abstract fun getScoreDao() : ScoreDao

}
