package ru.bingosoft.taxInspector.api

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import ru.bingosoft.taxInspector.models.Models

interface ApiService {
    @POST("redis-session-php/login.php")
    @FormUrlEncoded
    fun getAuthorization(
        @Field("mobile") mobile: Boolean=false,
        @Field("login") login: String?,
        @Field("password") password: String?
    ): Single<Models.Auth>

    @GET("procs/androidAPI.php")
    fun getListOrder(
        @Query("action") action: String
    ): Single<Models.OrderList>

    @GET("procs/androidAPI.php")
    fun getInfoAboutCurrentUser(
        @Query("action") action: String
    ): Single<Models.User>

    @GET("procs/androidAPI.php")
    fun getCheckups(
        @Query("action") action: String
    ): Single<Models.CheckupList>

    @GET("procs/androidAPI.php")
    fun getCheckupGuide(
        @Query("action") action: String
    ): Single<Models.CheckupGuideList>

    @GET("procs/androidAPI.php")
    fun sendMessageToAdmin(
        @Query("action") action: String,
        @Query("codeMessage") codeMessage: Int
    ): Single<Models.CheckupGuideList>

    @GET("procs/androidAPI.php")
    fun sendLog(
        @Query("action") action: String,
        @Query("tag") tag: String?="",
        @Query("message") message: String
    ): Single<Unit>

    @POST("procs/androidAPI.php")
    @Multipart
    fun doReverseSync(
        @Part("action") action: RequestBody?,
        @Part("jsonData") jsonData: RequestBody?,
        @Part file: MultipartBody.Part?
        //@Part("filemap") filemap: RequestBody?
    ): Single<Models.SimpleMsg>


    @POST("procs/androidAPI.php")
    @Multipart
    fun sendTrackingUserLocation(
        @Part("action") action: RequestBody?,
        @Part("jsonData") jsonData: RequestBody?
    ): Single<Models.SimpleMsg>

    @POST("procs/androidAPI.php")
    @FormUrlEncoded
    fun saveGCMToken(
        @Field("action") action: String,
        @Field("token") token: String
    ): Single<Models.SimpleMsg>

    @GET("procs/androidAPI.php")
    fun saveUserLocation(
        @Query("action") action: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<Models.SimpleMsg>
}