package ru.bingosoft.taxInspector.util

import android.util.Log
import io.reactivex.schedulers.Schedulers
import ru.bingosoft.taxInspector.api.ApiService
import timber.log.Timber

class ServerLoggingTree(val apiService: ApiService): Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Log.d("myLogs","log")

        val disposable=apiService.sendLog("sendLog",tag,message)
            .subscribeOn(Schedulers.io())
            .subscribe({},{
            Log.d("myLogs","Failed_to_send_log")
        })
        super.log(priority, tag, message, t)
    }


}