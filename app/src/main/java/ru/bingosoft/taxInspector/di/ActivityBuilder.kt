package ru.bingosoft.taxInspector.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.bingosoft.taxInspector.ui.checkup.CheckupFragment
import ru.bingosoft.taxInspector.ui.checkuplist.CheckupListFragment
import ru.bingosoft.taxInspector.ui.checkuplist_bottom.CheckupListBottomSheet
import ru.bingosoft.taxInspector.ui.login.LoginActivity
import ru.bingosoft.taxInspector.ui.mainactivity.MainActivity
import ru.bingosoft.taxInspector.ui.map.MapFragment
import ru.bingosoft.taxInspector.ui.map_bottom.MapBottomSheet
import ru.bingosoft.taxInspector.ui.order.OrderFragment

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [PresentersModule::class])
    abstract fun bindMapActivity(): MapFragment

    @ContributesAndroidInjector(modules = [PresentersModule::class])
    abstract fun bindOrderActivity(): OrderFragment

    @ContributesAndroidInjector(modules = [PresentersModule::class])
    abstract fun bindCheckupActivity(): CheckupFragment

    @ContributesAndroidInjector(modules = [PresentersModule::class])
    abstract fun bindCheckupListFragment(): CheckupListFragment

    @ContributesAndroidInjector(modules = [PresentersModule::class])
    abstract fun bindCheckupListBottomSheet(): CheckupListBottomSheet

    @ContributesAndroidInjector(modules = [PresentersModule::class])
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [PresentersModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [PresentersModule::class])
    abstract fun bindMapBottomSheet(): MapBottomSheet

}