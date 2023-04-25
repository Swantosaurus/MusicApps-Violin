package com.kobera.music.common.score.di

import androidx.room.Room
import com.kobera.music.common.score.ScoreRepository
import com.kobera.music.common.score.data.ScoreDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val scoreModule = module {
    factory {
        get<ScoreDatabase>().getScoreDao()
    }
    single {
        ScoreRepository(get())
    }

    single {
        Room.databaseBuilder(androidContext(), ScoreDatabase::class.java, "stats").build()
    }

}