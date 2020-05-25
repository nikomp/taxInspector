package ru.bingosoft.taxInspector.di

import dagger.Module
import dagger.Provides
import ru.bingosoft.taxInspector.db.AppDatabase
import ru.bingosoft.taxInspector.ui.map.MapPresenter

@Module
class PresentersModule {
    @Provides
    fun providesMapPresenter(database: AppDatabase)=
        MapPresenter(database)

}