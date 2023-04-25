package com.kobera.music.common.score

import com.kobera.music.common.score.data.ScoreDao
import com.kobera.music.common.score.data.ScoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ScoreRepository(
    private val scoreDao: ScoreDao,
) {
    fun getTotalScore(): Flow<Int> =
        scoreDao.getAll().map { scores ->
            scores.sumOf { it.score }
    }

    suspend fun insertScore(score: ScoreEntity) = scoreDao.insert(score)
}