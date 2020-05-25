package ru.bingosoft.taxInspector.ui.map

import ru.bingosoft.taxInspector.db.Orders.Orders

interface MapContractView {
    fun showMarkers(orders: List<Orders>)
}