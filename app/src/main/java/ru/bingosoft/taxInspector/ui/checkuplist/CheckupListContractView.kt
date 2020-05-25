package ru.bingosoft.taxInspector.ui.checkuplist

import ru.bingosoft.taxInspector.db.Checkup.Checkup

interface CheckupListContractView {
    fun showCheckups(checkups: List<Checkup>)
}