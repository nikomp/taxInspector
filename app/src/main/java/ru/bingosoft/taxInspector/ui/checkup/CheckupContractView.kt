package ru.bingosoft.taxInspector.ui.checkup

import ru.bingosoft.taxInspector.db.Checkup.Checkup

interface CheckupContractView {
    fun dataIsLoaded(checkup: Checkup)
    fun showCheckupMessage(resID: Int)
}