package ru.bingosoft.taxInspector.ui.checkuplist_bottom

import ru.bingosoft.taxInspector.db.CheckupGuide.CheckupGuide

interface CheckupListBottomSheetContractView {
    fun showKindObject(checkupGuides: List<CheckupGuide>)
    fun saveNewObjectOk()
}