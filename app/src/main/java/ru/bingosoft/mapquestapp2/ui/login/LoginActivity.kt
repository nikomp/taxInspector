package ru.bingosoft.mapquestapp2.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import ru.bingosoft.mapquestapp2.BuildConfig
import ru.bingosoft.mapquestapp2.R
import ru.bingosoft.mapquestapp2.util.Toaster
import java.net.InetAddress
import javax.inject.Inject



class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var stUrl: String = ""
    private var stLogin: String = ""
    private var stPassword: String = ""

    @Inject
    lateinit var toaster: Toaster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar=logintoolbar
        setSupportActionBar(toolbar)

        stUrl = edUrl.text.toString()
        stLogin = edLogin.text.toString()
        stPassword = edPassword.text.toString()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btnGo -> {

                    if (isNetworkConnected()) {
                        if (isInternetAvailable()) {
                            // Авторизация
                            val intent = Intent()
                            intent.putExtra("login", stLogin)
                            intent.putExtra("password", stPassword)
                            intent.putExtra("url", stUrl)
                            setResult(Activity.RESULT_OK, intent)

                            this.finish()
                        } else {
                            toaster.showToast(R.string.server_not_avaliable)
                        }

                    } else {
                        toaster.showToast(R.string.not_internet)
                    }
                }
            }
        }

    }

    @Suppress("DEPRECATION") //activeNetworkInfo, isConnected - deprecated, нужно для старых API
    private fun isNetworkConnected(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager?.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //для других устройств, которые умеют соединяться с Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager?.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName(BuildConfig.urlServer)
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }

}