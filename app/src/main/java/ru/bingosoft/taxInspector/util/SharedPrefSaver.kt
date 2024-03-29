package ru.bingosoft.taxInspector.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import ru.bingosoft.taxInspector.models.Models
import ru.bingosoft.taxInspector.util.Const.LogTags.SPS
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.APP_PREFERENCES
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.DATESYNC
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.FIREBASE_MESSAGE
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.LOCATION_TRACKING
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.LOGIN
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.PASSWORD
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.SESSION
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.TOKEN
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.USER_FULLNAME
import ru.bingosoft.taxInspector.util.Const.SharedPrefConst.USER_PHOTO_URL
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SharedPrefSaver(ctx: Context) {
    private val sharedPreference: SharedPreferences = ctx.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)


    fun saveLogin(login: String) {
        Timber.d("saveLogin")
        Log.d(SPS, "===saveLogin")
        Log.d(SPS, login)
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putString(LOGIN, login)
        editor.apply()
        Log.d(SPS, login)

    }

    fun getLogin(): String {
        return sharedPreference.getString(LOGIN, "") ?: ""
    }

    fun savePassword(password: String) {
        Log.d(SPS, "savePassword")
        Log.d(SPS, password)

        /*val coder = Coder(ctx) // Создадим экземпляр шифратора
        //ШИФРУЕМ
        val eList = coder.encode(password)*/

        val editor: SharedPreferences.Editor = this.sharedPreference.edit()
        editor.putString(PASSWORD, password) //eList[0]
        //editor.putString(IVPASS, ) //eList[1]
        editor.apply()

    }


    fun getPassword(): String {
        Log.d(SPS, "getPassword")
        //СЧИТАЕМ
        if (sharedPreference.contains(PASSWORD)) {
            /*val coder = Coder(ctx) // Создадим экземпляр шифратора

            return coder.decode(
                sharedPreference.getString(PASSWORD, "") ?: "",
                sharedPreference.getString(IVPASS, "") ?: ""
            )*/

            return sharedPreference.getString(PASSWORD, "") ?: ""
        }

        return ""

    }

    fun clearAuthData() {
        val editor: SharedPreferences.Editor = this.sharedPreference.edit()
        editor.remove("login")
        editor.remove("password")
        editor.apply()
    }

    fun saveSessionId(sessionId: String) {
        Log.d(SPS, "saveSessionId")
        //СОХРАНИМ
        val editor: SharedPreferences.Editor = this.sharedPreference.edit()
        editor.putString(SESSION, sessionId)
        editor.apply()
    }

    fun getSessionId(): String? {
        Log.d(SPS, "getSessionId")
        //СЧИТАЕМ
        if (sharedPreference.contains(SESSION)) {
            return sharedPreference.getString(SESSION, "")
        }

        return ""

    }

    fun getToken(): String {
        return sharedPreference.getString(TOKEN, "") ?: ""
    }

    fun saveToken(token: String) {
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun saveDateSyncDB(date: Date) {
        //СОХРАНИМ
        val editor: SharedPreferences.Editor = this.sharedPreference.edit()

        val dateFormat =
            SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("ru","RU"))
        try {
            val dateTime = dateFormat.format(date)
            editor.putString(DATESYNC, dateTime)
            editor.apply()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    fun getDateSyncDB():String? {
        Log.d(SPS, "getDateSyncDB")
        if (sharedPreference.contains(DATESYNC)) {
            return sharedPreference.getString(DATESYNC,"")
        }
        return ""
    }

    fun saveUser(user: Models.User) {
        Log.d(SPS, "saveUser")
        //СОХРАНИМ
        val editor: SharedPreferences.Editor = this.sharedPreference.edit()
        editor.putString(USER_FULLNAME, user.fullname)
        editor.putString(USER_PHOTO_URL, user.photoUrl)
        editor.apply()
    }

    fun getUser(): Models.User {
        Log.d(SPS, "saveUser")
        val user=Models.User()

        if (sharedPreference.contains(USER_FULLNAME)) {
            user.fullname=sharedPreference.getString(USER_FULLNAME, "") ?: ""
        }
        if (sharedPreference.contains(USER_PHOTO_URL)) {
            user.photoUrl=sharedPreference.getString(USER_PHOTO_URL, "") ?: ""
        }

        return user
    }

    fun getTokenGCM() :String {
        if (sharedPreference.contains(FIREBASE_MESSAGE)) {
            return sharedPreference.getString(FIREBASE_MESSAGE, "") ?: ""
        } else {
            return ""
        }
    }

    fun isLocationTracking() :Boolean {
        return if (sharedPreference.contains(LOCATION_TRACKING)) {
            sharedPreference.getBoolean(LOCATION_TRACKING,false)
        } else {
            false
        }
    }
}