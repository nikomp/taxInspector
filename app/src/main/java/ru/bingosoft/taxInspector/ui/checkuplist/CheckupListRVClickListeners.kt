package ru.bingosoft.taxInspector.ui.checkuplist

import android.view.View

interface CheckupListRVClickListeners {
    fun recyclerViewListClicked(v: View?, position: Int)
}