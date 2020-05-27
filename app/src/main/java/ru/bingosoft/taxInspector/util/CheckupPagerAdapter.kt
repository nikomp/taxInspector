package ru.bingosoft.taxInspector.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import ru.bingosoft.taxInspector.R
import ru.bingosoft.taxInspector.models.Models
import ru.bingosoft.taxInspector.ui.checkup.CheckupFragment
import timber.log.Timber

class CheckupPagerAdapter(private val control: Models.TemplateControl, private val parentFragment: CheckupFragment) :PagerAdapter() {
    val adapterControlList=Models.CommonControlList()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Timber.d("instantiateItem")
        val itemView=LayoutInflater.from(parentFragment.context).inflate(
            R.layout.pager_checkup_item, container, false) as LinearLayout
        container.addView(itemView)

        // Генерируем вопросы группы
        Timber.d("${control.subcheckup[position].textResult}")
        val uiCreator= UICreator(parentFragment, control.subcheckup[position])
        val cl=uiCreator.create(rootView = itemView,parent = control)


        adapterControlList.list.add(cl)
        Timber.d("adapterControlList=${adapterControlList.list}")

        if (control.subcheckup.size==position+1) {
            control.groupControlList=adapterControlList
        }

        return itemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun getCount(): Int {
        return if (control.subcheckup!=null) {
            control.subcheckup.size
        } else {
            0
        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        //return "Объект ${position+1}"
        return parentFragment.getString(R.string.pageTitle, position+1)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}