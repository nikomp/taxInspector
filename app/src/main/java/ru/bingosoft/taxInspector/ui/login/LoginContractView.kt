package ru.bingosoft.taxInspector.ui.login

import ru.bingosoft.taxInspector.models.Models
import java.util.*

interface LoginContractView {
    fun showMessageLogin(resID: Int)
    fun showMessageLogin(msg: String)
    fun saveLoginPasswordToSharedPreference(stLogin: String, stPassword: String)
    fun saveToken(token:String)
    fun showFailureTextView()
    fun alertRepeatSync()
    fun saveDateSyncToSharedPreference(date: Date)
    fun saveInfoUserToSharedPreference(user: Models.User)
    fun startLocationService()
}