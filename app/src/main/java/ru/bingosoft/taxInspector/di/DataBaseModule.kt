package ru.bingosoft.taxInspector.di

import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.bingosoft.taxInspector.App
import ru.bingosoft.taxInspector.db.AppDatabase
import timber.log.Timber
import javax.inject.Singleton


@Module
class DataBaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(application: App): AppDatabase {
        Timber.d("provideAppDatabase")
        return Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java, "mydatabase.db"
        )
        .build()
    }

}