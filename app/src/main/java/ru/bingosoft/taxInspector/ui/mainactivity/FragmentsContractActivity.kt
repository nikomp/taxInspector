package ru.bingosoft.taxInspector.ui.mainactivity

import com.yandex.mapkit.geometry.Point
import ru.bingosoft.taxInspector.db.Checkup.Checkup
import ru.bingosoft.taxInspector.db.Orders.Orders

interface FragmentsContractActivity {
    fun setCheckup(checkup: Checkup)
    fun setChecupListOrder(order: Orders)
    fun setCoordinates(point: Point, controlId: Int)
}