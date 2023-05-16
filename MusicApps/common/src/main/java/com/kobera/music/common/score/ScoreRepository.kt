package com.kobera.music.common.score

import com.kobera.music.common.score.data.ScoreDao
import com.kobera.music.common.score.data.ScoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.Calendar


/**
 * provides access to Score data from database
 */
class ScoreRepository(
    private val scoreDao: ScoreDao,
) {
    fun getTotalScore(): Flow<Int> =
        scoreDao.getAll().map { scores ->
            scores.sumOf { it.score }
        }

    fun getTotalWinsAndLoses(): Flow<WinsAndLoses> =
        scoreDao.getAll().map { scores ->
            Timber.d(scores.toString())
            val wins = scores.count{
                it.score > 0
            }
            WinsAndLoses(
                wins = wins,
                loses = scores.size - wins
            )
        }

    private fun getScoresToday(): Flow<List<ScoreEntity>> =
        scoreDao.getAll().map { scores ->
            scores.filter {
                (Calendar.getInstance().timeInMillis - it.timestamp) < MillisInADay
            }
        }

    fun getTotalScoreToday(): Flow<Int> =
        getScoresToday().map { it.sumOf { it.score } }

    fun getWinsAndLosesToday(): Flow<WinsAndLoses>  =
        getScoresToday().map { scores ->
            val wins = scores.count { it.score > 0 }
            WinsAndLoses(
                wins = wins,
                loses = scores.size - wins
            )
        }

    data class WinsAndLoses(val wins: Int, val loses: Int)
    companion object {
        const val MillisInADay = 1000 * 60 * 60 * 24
    }

    suspend fun insertScore(score: ScoreEntity) = scoreDao.insert(score)
}
