package ru.bingosoft.taxInspector.di

import dagger.Module
import dagger.Provides
import ru.bingosoft.taxInspector.App
import ru.bingosoft.taxInspector.util.PhotoHelper
import ru.bingosoft.taxInspector.util.SharedPrefSaver
import ru.bingosoft.taxInspector.util.Toaster
import javax.inject.Singleton

@Module
class UtilModule {
    @Provides
    @Singleton
    fun provideToaster(application: App): Toaster {
        return Toaster(application.applicationContext)
    }

    @Provides
    @Singleton
    fun provideSharedPrefSaver(application: App): SharedPrefSaver {
        return SharedPrefSaver(application.applicationContext)
    }

    @Provides
    @Singleton
    fun providePhotoHelper(): PhotoHelper {
        return PhotoHelper()
    }
}