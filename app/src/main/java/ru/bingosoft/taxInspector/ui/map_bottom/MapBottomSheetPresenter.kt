package ru.bingosoft.taxInspector.ui.map_bottom

import io.reactivex.disposables.Disposable
import ru.bingosoft.taxInspector.db.AppDatabase
import javax.inject.Inject

class MapBottomSheetPresenter @Inject constructor(
    val db: AppDatabase
) {

    var view: MapBottomSheetContractView?=null
    private lateinit var disposable: Disposable

    fun attachView(view: MapBottomSheetContractView) {
        this.view=view
    }

    fun onDestroy() {
        this.view = null
        if (this::disposable.isInitialized) {
            disposable.dispose()
        }

    }

    /*fun loadData(symbolNumber: String) {
        Timber.d("loadData $symbolNumber")
        disposable=db.ordersDao().getByNumber(symbolNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Timber.d("Данные получили из БД")
                Timber.d(it.toString())
                view?.showOrder(it)
            }
    }*/
}