package ru.bingosoft.taxInspector.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.bingosoft.taxInspector.db.Checkup.Checkup
import ru.bingosoft.taxInspector.db.Checkup.CheckupDao
import ru.bingosoft.taxInspector.db.CheckupGuide.CheckupGuide
import ru.bingosoft.taxInspector.db.CheckupGuide.CheckupGuideDao
import ru.bingosoft.taxInspector.db.Orders.Orders
import ru.bingosoft.taxInspector.db.Orders.OrdersDao
import ru.bingosoft.taxInspector.db.User.TrackingUserLocation
import ru.bingosoft.taxInspector.db.User.TrackingUserLocationDao


@Database(entities=arrayOf(Orders::class, Checkup::class, CheckupGuide::class, TrackingUserLocation::class),version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ordersDao(): OrdersDao
    abstract fun checkupDao(): CheckupDao
    abstract fun checkupGuideDao(): CheckupGuideDao
    abstract fun trackingUserDao(): TrackingUserLocationDao
}