package ru.bingosoft.taxInspector.ui.order

import android.view.View

interface OrdersRVClickListeners {
    fun recyclerViewListClicked(v: View?, position: Int)
}