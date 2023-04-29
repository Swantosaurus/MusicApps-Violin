package com.kobera.music.common.score.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for Score in score database
 */
@Dao
abstract class ScoreDao {
    @Query("SELECT * FROM score ORDER BY timestamp DESC")
    abstract fun getAll(): Flow<List<ScoreEntity>>

    @Query("SELECT * FROM score WHERE score.`id`=:key")
    abstract fun getById(key: Int): Flow<ScoreEntity>

    @Insert
    abstract suspend fun insert(data: ScoreEntity)

    @Query("DELETE FROM score")
    protected abstract suspend fun deleteAll()
}
