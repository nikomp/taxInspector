package ru.bingosoft.taxInspector.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.bingosoft.taxInspector.App
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideApplication(app: App): Context = app.applicationContext
}
