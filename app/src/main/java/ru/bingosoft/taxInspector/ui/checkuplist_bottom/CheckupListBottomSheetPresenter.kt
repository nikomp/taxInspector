package ru.bingosoft.taxInspector.ui.checkuplist_bottom

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.bingosoft.taxInspector.db.AppDatabase
import ru.bingosoft.taxInspector.db.Checkup.Checkup
import ru.bingosoft.taxInspector.db.CheckupGuide.CheckupGuide
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class CheckupListBottomSheetPresenter @Inject constructor(val db: AppDatabase) {
    var view: CheckupListBottomSheetContractView?=null

    private lateinit var disposable: Disposable

    fun attachView(view: CheckupListBottomSheetContractView) {
        this.view=view
    }

    fun getKindObject() {
        disposable=db.checkupGuideDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                Timber.d("Справочник чеклистов получили из БД")
                Timber.d(it.toString())
                view?.showKindObject(it)
            },{throwable ->
                throwable.printStackTrace()
            })
    }

    fun saveObject(checkupGuide: CheckupGuide, nameObject: String, idOrder:Long) {
        Timber.d("Сохраняем_новый_объект ${checkupGuide.kindCheckup} $nameObject")

        val checkup=Checkup(0,UUID.randomUUID().toString(),checkupGuide.kindCheckup,nameObject,checkupGuide.text,idOrder = idOrder)

        Single.fromCallable{
            db.checkupDao().insert(checkup)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe{_->
            view?.saveNewObjectOk()
        }
    }

    fun onDestroy() {
        this.view = null
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }

    }

    /**
     * Метод для генерации ГУИДа, нужен для первичного формирования fingerprint
     *
     * @return - возвращается строка содержащая ГУИД
     */
    private fun random(): String {
        var stF = UUID.randomUUID().toString()
        //stF = stF.replace("-".toRegex(), "")
        stF = stF.substring(0, 32)

        return stF
    }
}