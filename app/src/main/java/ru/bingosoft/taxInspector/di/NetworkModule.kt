package ru.bingosoft.taxInspector.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.bingosoft.taxInspector.BuildConfig
import ru.bingosoft.taxInspector.api.ApiService
import ru.bingosoft.taxInspector.util.SharedPrefSaver
import java.util.concurrent.TimeUnit

@Module
class NetworkModule {
    @Provides
    fun providesApiService(sharedPrefSaver: SharedPrefSaver) : ApiService {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level= HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(object:Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val token = sharedPrefSaver.getToken()
                    val newRequest = chain.request().newBuilder()
                        //.addHeader("Content-Type","application/json")
                        .addHeader("Authorization", token)
                        .build()

                    return chain.proceed(newRequest)
                }

            })
            .connectTimeout(90, TimeUnit.SECONDS) // Увеличим таймаут ретрофита
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BuildConfig.urlServer)
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)

    }
}