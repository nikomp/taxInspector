package ru.bingosoft.taxInspector.ui.order

import ru.bingosoft.taxInspector.db.Orders.Orders

interface OrderContractView {
    fun showOrders(orders: List<Orders>)
    fun showMessageOrders(msg: String)
}