package ru.bingosoft.taxInspector.util

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import ru.bingosoft.taxInspector.R
import ru.bingosoft.taxInspector.db.Checkup.Checkup
import ru.bingosoft.taxInspector.models.Models
import ru.bingosoft.taxInspector.ui.checkup.CheckupFragment
import ru.bingosoft.taxInspector.ui.mainactivity.MainActivity
import ru.bingosoft.taxInspector.ui.map.MapFragment
import ru.bingosoft.taxInspector.util.Const.Photo.DCIM_DIR
import ru.bingosoft.taxInspector.util.SlidingTab.SlidingTabLayout
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Класс, который создает интерфейс Фрагмента Обследования
 */
//private val rootView: View, val checkup: Checkup, private val photoHelper: PhotoHelper, private val checkupPresenter: CheckupPresenter
class UICreator(val parentFragment: CheckupFragment, val checkup: Checkup) {
    lateinit var controlList: Models.ControlList

    private val photoHelper=parentFragment.photoHelper

    fun create(rootView: View, controls:Models.ControlList?=null, parent: Models.TemplateControl?=null): Models.ControlList {
        // Возможно чеклист был ранее сохранен, тогда берем сохраненный и восстанавливаем его
        controlList = controls
            ?: if (checkup.textResult!=null){
                        Gson().fromJson(checkup.textResult, Models.ControlList::class.java)
                    } else {
                        Timber.d("checkup=$checkup")
                        if (checkup.text!=null) {
                            Gson().fromJson(checkup.text, Models.ControlList::class.java)
                        } else {
                            Gson().fromJson("", Models.ControlList::class.java)
                        }
                    }


        //val rootView=parentFragment.root
        val checkupPresenter=parentFragment.checkupPresenter

        controlList.list.forEach controls@ { it ->
            // Для всех зависимых контролов сохраним его родителя
            if (parent!=null) {
                Timber.d("parent!=null")
                Timber.d("parent=${parent.id}_${parent.checked}_${parent.type}")
                it.parent=parent
            }
            when (it.type) {
                // Выпадающий список
                "combobox"->{
                    Timber.d("генерим combobox")

                    val templateStep=LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_material_spinner, rootView.parent as ViewGroup?, false) as LinearLayout

                    attachListenerToFab(templateStep,it)

                    templateStep.id=it.id
                    templateStep.findViewById<TextView>(R.id.question).text=it.question

                    val materialSpinner=templateStep.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                        rootView.context,
                        R.layout.template_multiline_spinner_item,
                        it.value
                    )

                    // Заполним spinner
                    materialSpinner.setAdapter(spinnerArrayAdapter)

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    Timber.d("it.checked=${it.checked}")
                    if (it.checked) {
                        changeChecked(templateStep,it) // Установим цвет шага
                    }
                    if (it.resvalue.isNotEmpty()){
                        materialSpinner.setText(it.resvalue)
                    }
                    // Вешаем обработчик на spinner последним, иначе сбрасывается цвет шага
                    materialSpinner.addTextChangedListener(TextWatcherHelper(it,this,templateStep))

                    return@controls

                }
                // Выпадающий список
                "multilevel_combobox"->{
                    Timber.d("генерим multilevel_combobox")

                    val templateStep=LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_multilevel_spinner, rootView.parent as ViewGroup?, false) as LinearLayout


                    attachListenerToFab(templateStep,it)

                    templateStep.id=it.id
                    templateStep.findViewById<TextView>(R.id.question).text=it.question

                    val materialSpinner=templateStep.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)
                    val materialSubSpinner=templateStep.findViewById<MaterialBetterSpinner>(R.id.subspinner)

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                        rootView.context,
                        R.layout.template_multiline_spinner_item,
                        it.value
                    )

                    // Заполним основной spinner
                    materialSpinner.setAdapter(spinnerArrayAdapter)


                    val subspinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                        rootView.context,
                        R.layout.template_multiline_spinner_item,
                        arrayListOf()
                    )
                    // Запоним пустым значением по-умолчанию, иначе краш, т.к. заранее неизвестно, что будет выбрано
                    materialSubSpinner.setAdapter(subspinnerArrayAdapter)


                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    Timber.d("it.checked=${it.checked}")
                    if (it.checked) {
                        changeChecked(templateStep,it) // Установим цвет шага
                    }
                    if (it.resmainvalue.isNotEmpty()){
                        materialSpinner.setText(it.resmainvalue)
                        Timber.d("it.resmainvalue.isNotEmpty()")
                        // Если выбрано значение в первом списке, заполним второй
                        val index=it.value.indexOf(it.resmainvalue)
                        Timber.d("index=$index")
                        Timber.d("it.subvalue[index].value=${it.subvalue[index].value}")
                        val spinnerSubArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                            rootView.context,
                            R.layout.template_multiline_spinner_item,
                            it.subvalue[index].value
                        )
                        materialSubSpinner.setAdapter(spinnerSubArrayAdapter)
                    }

                    if (it.resvalue.isNotEmpty()){
                        materialSubSpinner.setText(it.resvalue)
                    }

                    materialSpinner.setOnItemClickListener(object:AdapterView.OnItemClickListener {
                        override fun onItemClick(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val spinnerSubArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                                rootView.context,
                                R.layout.template_multiline_spinner_item,
                                it.subvalue[position].value
                            )
                            materialSubSpinner.setAdapter(spinnerSubArrayAdapter)
                        }

                    })


                    // Вешаем обработчик на второй spinner его значение уйдет на сервер
                    materialSubSpinner.addTextChangedListener(TextWatcherHelper(it,this,templateStep))

                    return@controls

                }
                // Строковое поле ввода однострочное
                "textinput"->{
                    // Строковое поле однострочное
                    val templateStep=LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_textinput, rootView.parent as ViewGroup?, false) as LinearLayout


                    attachListenerToFab(templateStep,it)

                    templateStep.id=it.id
                    templateStep.findViewById<TextView>(R.id.question).text=it.question

                    val textInputLayout=templateStep.findViewById<TextInputLayout>(R.id.til)
                    textInputLayout.hint=it.hint


                    val textInputEditText=templateStep.findViewById<TextInputEditText>(R.id.tiet)


                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    Timber.d("it.checked=${it.checked}")
                    if (it.checked) {
                        changeChecked(templateStep,it) // Установим цвет шага
                        Timber.d("it.resvalue=${it.resvalue}")

                    }
                    if (it.resvalue.isNotEmpty()){
                        textInputEditText.setText(it.resvalue)
                    }
                    // Вешаем обработчик на textInputEditText последним, иначе сбрасывается цвет шага
                    textInputEditText.addTextChangedListener(TextWatcherHelper(it,this,templateStep))

                    return@controls
                }
                "photo"->{
                    // контрол с кнопкой для фото
                    val templateStep=LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_photo2, rootView.parent as ViewGroup?, false) as LinearLayout

                    attachListenerToFab(templateStep,it)

                    templateStep.id=it.id
                    templateStep.findViewById<TextView>(R.id.question).text=it.question
                    templateStep.tag=it

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    if (it.checked) {
                        changeChecked(templateStep,it) // Установим цвет шага
                    }

                    val pager = templateStep.findViewById(R.id.pager) as ViewPager

                    // Обработчик для кнопки "Добавить фото"
                    val btnPhoto=templateStep.findViewById<MaterialButton>(R.id.btnPhoto)
                    val stepCheckup=it
                    btnPhoto.tag=templateStep
                    btnPhoto.setOnClickListener{
                        Timber.d("Добавляем фото")
                        Timber.d("stepCheckup=${stepCheckup.parent?.type}")
                        val ts=it.tag
                        val tc=((ts as View).tag as Models.TemplateControl)

                        // Сбрасываем признак Checked
                        if (tc.checked) {
                            tc.checked=!tc.checked
                            changeChecked(ts,tc)

                            val parentTemp=tc.parent
                            if (parentTemp!=null) {
                                val parentView=parentFragment.root.findViewById<View>(parentTemp.id)
                                parentTemp.checked=false
                                changeChecked(parentView, parentTemp)
                            }
                        }


                        val curOrder=(parentFragment.activity as MainActivity).currentOrder
                        (parentFragment.requireActivity() as MainActivity).photoStep=stepCheckup // Сохраним id контрола для которого делаем фото
                        if (stepCheckup.parent!=null && stepCheckup.parent!!.type=="group_questions") {
                            Timber.d("checkup.guid=${checkup.guid}")
                            (parentFragment.requireActivity() as MainActivity).linearLayoutPhotoParent=(it.parent.parent as View)
                            val viewPager=(ts.parent.parent.parent.parent as ViewPager)
                            val pagerIndex=viewPager.currentItem+1
                            val photoDirectory="${curOrder.guid}/${checkup.guid}/${stepCheckup.guid}/$pagerIndex" // Сохраним id контрола для которого делаем фото
                            (parentFragment.requireActivity() as MainActivity).photoDir=photoDirectory
                            photoHelper.createPhoto(photoDirectory)
                        } else {
                            val photoDirectory="${curOrder.guid}/${checkup.guid}/${stepCheckup.guid}" // Сохраним id контрола для которого делаем фото
                            (parentFragment.requireActivity() as MainActivity).photoDir=photoDirectory
                            photoHelper.createPhoto(photoDirectory)
                        }

                    }

                    // Получим список фоток из папки
                    if (it.resvalue.isNotEmpty()) {
                        parentFragment.setPhotoResult(it.id,it.resvalue,templateStep) //rootView
                    } else {
                        /*val curOrder=(parentFragment.activity as MainActivity).currentOrder
                        if (stepCheckup.parent!=null && stepCheckup.parent!!.type=="group_questions") {
                            val viewPager=(templateStep.parent.parent.parent.parent as ViewPager)
                            val pagerIndex=viewPager.currentItem+1
                            val photoDirectory="${curOrder.guid}/${checkup.guid}/${stepCheckup.guid}/$pagerIndex"
                            val view=(parentFragment.activity as MainActivity).linearLayoutPhotoParent

                            parentFragment.setPhotoResult(it.id,photoDirectory,view)
                        } else {
                            val photoDirectory="${curOrder.guid}/${checkup.guid}/${stepCheckup.guid}"
                            parentFragment.setPhotoResult(it.id,photoDirectory)
                        }*/
                    }


                    val btnDeletePhoto = templateStep.findViewById<MaterialButton>(R.id.btnDeletePhoto)
                    btnDeletePhoto.tag=templateStep
                    btnDeletePhoto.setOnClickListener {
                        Timber.d("Удалим фото")
                        val ts=it.tag
                        val tc=((ts as View).tag as Models.TemplateControl)

                        // Сбрасываем признак Checked
                        if (tc.checked) {
                            tc.checked=!tc.checked
                            changeChecked(ts,tc)

                            val parentTemp=tc.parent
                            if (parentTemp!=null) {
                                val parentView=parentFragment.root.findViewById<View>(parentTemp.id)
                                parentTemp.checked=false
                                changeChecked(parentView, parentTemp)
                            }
                        }

                        // Получим текущую страницу
                        val indexPhoto=pager.currentItem
                        Timber.d("pager.adapter.count=${pager.adapter?.count}")
                        if (pager.adapter?.count!! >0) {
                            // Получим список фоток из папки
                            val imagesPhoto: List<String>
                            imagesPhoto = if (tc.resvalue.isNotEmpty()) {
                                OtherUtil().getFilesFromDir("${DCIM_DIR}/PhotoForApp/${tc.resvalue}")
                            } else {
                                val curOrder=(parentFragment.activity as MainActivity).currentOrder
                                val photoDirectory=
                                    if (stepCheckup.parent!=null && stepCheckup.parent!!.type=="group_questions") {
                                        val viewPager=(templateStep.parent.parent.parent.parent as ViewPager)
                                        val pagerIndex=viewPager.currentItem+1
                                        "${curOrder.guid}/${checkup.guid}/${stepCheckup.guid}/$pagerIndex"
                                    } else {
                                        "${curOrder.guid}/${checkup.guid}/${stepCheckup.guid}"
                                    }
                                OtherUtil().getFilesFromDir("${DCIM_DIR}/PhotoForApp/$photoDirectory")
                            }
                            val photoForDelete=imagesPhoto[indexPhoto]
                            Timber.d("photoForDelete=$photoForDelete")
                            if (photoHelper.deletePhoto(photoForDelete)) {
                                val imagesNew= mutableListOf<String>()
                                imagesNew.addAll(imagesPhoto)
                                imagesNew.removeAt(indexPhoto)
                                parentFragment.refreshPhotoViewer(templateStep, imagesNew, rootView.context)
                            }
                        }
                    }

                    val leftBtn = templateStep.findViewById(R.id.left_nav_photo) as ImageButton
                    val rightBtn = templateStep.findViewById(R.id.right_nav_photo) as ImageButton

                    val myList = templateStep.findViewById(R.id.recyclerviewFrag) as RecyclerView

                    leftBtn.setOnClickListener {
                        Timber.d("leftBtn.setOnClickListene")
                        var tab = pager.currentItem
                        if (tab > 0) {
                            tab--
                            pager.currentItem = tab
                        } else if (tab == 0) {
                            pager.currentItem = tab
                        }
                    }

                    rightBtn.setOnClickListener {
                        Timber.d("rightBtn.setOnClickListener")
                        var tab = pager.currentItem
                        tab++
                        pager.currentItem = tab
                    }

                    pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {

                        }

                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {

                        }

                        override fun onPageSelected(position: Int) {
                            myList.scrollToPosition(position)
                        }

                    })

                    return@controls
                }
                "map_coordinates"->{
                    // контрол с кнопкой, открываем карту и забираем с нее координаты клика
                    val templateStep=LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_map_coordinates, rootView.parent as ViewGroup?, false) as LinearLayout

                    attachListenerToFab(templateStep,it)

                    Timber.d("it.id=${it.id}")
                    templateStep.id=it.id
                    templateStep.tag=it
                    Timber.d("templateStep.id=${templateStep.id}")
                    templateStep.findViewById<TextView>(R.id.question).text=it.question

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    if (it.checked) {
                        changeChecked(templateStep,it) // Установим цвет шага
                    }
                    if (it.resvalue.isNotEmpty()){
                        Timber.d("map_coordinates=${it.resvalue}")
                        val point=Gson().fromJson(it.resvalue, Models.MapPoint::class.java)
                        templateStep.findViewById<TextView>(R.id.mapCoordinatesResult).text=parentFragment.getString(R.string.coordinates,BigDecimal(point.lat!!).setScale(5,RoundingMode.HALF_EVEN),BigDecimal(point.lon!!).setScale(5,RoundingMode.HALF_EVEN))

                    } else {
                        // Возьмем координаты от Activity
                        val controlId=(parentFragment.requireActivity() as MainActivity).controlMapId
                        if (it.id==controlId) {
                            val point=(parentFragment.requireActivity() as MainActivity).mapPoint
                            templateStep.findViewById<TextView>(R.id.mapCoordinatesResult).text=parentFragment.getString(R.string.coordinates,BigDecimal(point.latitude).setScale(5,RoundingMode.HALF_EVEN),BigDecimal(point.longitude).setScale(5,RoundingMode.HALF_EVEN))

                            val mapPoint=Models.MapPoint(point.latitude,point.longitude)

                            it.resvalue=Gson().toJson(mapPoint)
                            Timber.d("mapPoint=${it.resvalue}")
                        }
                    }

                    // Обработчик для кнопки "Добавить координаты"
                    val btnMap=templateStep.findViewById<MaterialButton>(R.id.btnMap)
                    btnMap.tag=templateStep
                    btnMap.setOnClickListener{
                        Timber.d("Добавляем координаты")

                        val ts=it.tag
                        val tc=((ts as View).tag as Models.TemplateControl)

                        // Сбрасываем признак Checked
                        if (tc.checked) {
                            tc.checked=!tc.checked
                            changeChecked(ts,tc)
                        }

                        // Сохраним текущее состояние чеклиста
                        checkupPresenter.saveCheckup(this)

                        //Открываем карту
                        val bundle = Bundle()
                        bundle.putBoolean("addCoordinates", true)
                        bundle.putLong("checkupId", checkup.id)
                        bundle.putInt("controlId", templateStep.id)

                        val fragmentMap= MapFragment()
                        fragmentMap.arguments=bundle
                        val fragmentManager=parentFragment.requireActivity().supportFragmentManager

                        fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment, fragmentMap, "fragment_map_tag")
                            .addToBackStack(null)
                            .commit()

                        fragmentManager.executePendingTransactions()

                    }

                    return@controls
                }
                "group_questions"->{
                    // контрол с зависимым чеклистом
                    val templateStep=LayoutInflater.from(rootView.context).inflate(
                        R.layout.template_subcheckup, rootView.parent as ViewGroup?, false) as LinearLayout

                    attachListenerToFab(templateStep,it)

                    Timber.d("it.id=${it.id}")
                    templateStep.id=it.id
                    templateStep.tag=it

                    templateStep.findViewById<TextView>(R.id.question).text=it.question

                    val clCheckupsPager=templateStep.findViewById<ConstraintLayout>(R.id.clCheckupsPager)

                    // Если шаг чеклиста был ранее сохранен восстановим значение
                    if (it.checked) {
                        changeChecked(templateStep,it) // Установим цвет шага
                    }

                    doAssociateParent(templateStep, rootView.findViewById(R.id.mainControl))

                    if (it.resvalue.isNotEmpty()){
                        Timber.d("BBVB")
                        Timber.d(it.resvalue)
                        val controlsForPages=Gson().fromJson(it.resvalue, Models.CommonControlList::class.java)
                        val subcheckup= mutableListOf<Checkup>()
                        controlsForPages.list.forEach{controls ->
                            val gson= GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .create()
                            val resValue=gson.toJson(controls,Models.ControlList::class.java)
                            Timber.d(resValue)
                            Timber.d(checkup.guid)
                            val checkup=Checkup(guid = checkup.guid,
                                kindObject = checkup.kindObject,
                                nameObject = checkup.nameObject,
                                idOrder = checkup.idOrder,
                                textResult = Gson().fromJson(resValue, JsonObject::class.java))
                            subcheckup.add(checkup)
                        }
                        Timber.d("subcheckup=$subcheckup")
                        it.subcheckup=subcheckup
                        refreshCheckupViewer(clCheckupsPager, it)

                    } else {
                        Timber.d("Нет сохраненных результатов")
                        refreshCheckupViewer(clCheckupsPager, it)
                    }

                    val pager = templateStep.findViewById(R.id.viewPager) as ViewPager

                    val mSlidingTabLayout = templateStep.findViewById(R.id.sliding_tabs) as SlidingTabLayout
                    mSlidingTabLayout.setDistributeEvenly(true)
                    mSlidingTabLayout.setViewPager(pager)

                    /*val leftBtn = templateStep.findViewById(R.id.left_nav) as ImageButton
                    val rightBtn = templateStep.findViewById(R.id.right_nav) as ImageButton

                    leftBtn.setOnClickListener {
                        Timber.d("leftBtn")
                        var tab = pager.currentItem
                        if (tab > 0) {
                            tab--
                            pager.currentItem = tab
                        } else if (tab == 0) {
                            pager.currentItem = tab
                        }
                    }

                    rightBtn.setOnClickListener {
                        Timber.d("rightBtn")
                        var tab = pager.currentItem
                        tab++
                        pager.currentItem = tab
                    }*/


                    // Обработчик для кнопки "Добавлям новый чеклист"
                    Timber.d("it=${it.multiplicity}")
                    if (it.multiplicity==1) {
                        val btnNewStep=templateStep.findViewById<MaterialButton>(R.id.addNewStep)
                        //Покажим панель с кнопками
                        val layoutBtn=templateStep.findViewById<LinearLayout>(R.id.llbtnConteiner)
                        val params=layoutBtn.layoutParams
                        params.height=LinearLayout.LayoutParams.WRAP_CONTENT
                        layoutBtn.layoutParams=params

                        btnNewStep.tag=templateStep
                        btnNewStep.setOnClickListener{viewBtn ->
                            Timber.d("Добавлям новый чеклист")

                            val ts=viewBtn.tag
                            val tc=((ts as View).tag as Models.TemplateControl)

                            // Сбрасываем признак Checked
                            if (tc.checked) {
                                tc.checked=!tc.checked
                                changeChecked(ts,tc)
                            }


                            val subcheckupnew= mutableListOf<Checkup>()
                            val controlsForPages=tc.groupControlList
                            controlsForPages?.list?.forEach{ controls ->
                                val gson= GsonBuilder()
                                    .excludeFieldsWithoutExposeAnnotation()
                                    .create()
                                val resValue=gson.toJson(controls,Models.ControlList::class.java)
                                val checkup=Checkup(guid = checkup.guid,
                                    kindObject = checkup.kindObject,
                                    nameObject = checkup.nameObject,
                                    idOrder = checkup.idOrder,
                                    textResult = Gson().fromJson(resValue, JsonObject::class.java))
                                subcheckupnew.add(checkup)
                            }

                            val controls1=Gson().fromJson(checkup.text,Models.ControlList::class.java)
                            val control=controls1.list.filter { it.id==ts.id }


                            val newCheckup=control[0].subcheckup[0]
                            Timber.d("newCheckup=$newCheckup")
                            //newCheckup.guid=control[0].guid
                            subcheckupnew.add(newCheckup) // Добавим еще один такой же
                            tc.subcheckup=subcheckupnew

                            refreshCheckupViewer(clCheckupsPager, tc)
                        }


                        val btnDeleteStep=templateStep.findViewById<MaterialButton>(R.id.deleteStep)
                        btnDeleteStep.tag=templateStep
                        btnDeleteStep.setOnClickListener{
                            Timber.d("Удалим чеклист")

                            // Получим текущую страницу
                            val indexPage=pager.currentItem
                            Timber.d("pager.adapter.count=${pager.adapter?.count}")
                            if (pager.adapter?.count!! >1) {
                                val ts=it.tag
                                val tc=((ts as View).tag as Models.TemplateControl)

                                val subcheckupnew= mutableListOf<Checkup>()
                                val controlsForPages=tc.groupControlList
                                Timber.d("tc.groupControlList=${tc.groupControlList?.list}")
                                controlsForPages?.list?.removeAt(indexPage)
                                controlsForPages?.list?.forEach{ controls ->
                                    val gson= GsonBuilder()
                                        .excludeFieldsWithoutExposeAnnotation()
                                        .create()
                                    val resValue=gson.toJson(controls,Models.ControlList::class.java)
                                    val checkup=Checkup(guid = checkup.guid,
                                        kindObject = checkup.kindObject,
                                        nameObject = checkup.nameObject,
                                        idOrder = checkup.idOrder,
                                        textResult = Gson().fromJson(resValue, JsonObject::class.java))
                                    subcheckupnew.add(checkup)
                                }

                                tc.subcheckup=subcheckupnew

                                refreshCheckupViewer(clCheckupsPager, tc)
                            }


                        }

                    } else {
                        //Скроем панель с кнопками
                        val layoutBtn=templateStep.findViewById<LinearLayout>(R.id.llbtnConteiner)
                        val params=layoutBtn.layoutParams
                        params.height=0
                        layoutBtn.layoutParams=params
                    }

                    pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                        override fun onPageScrollStateChanged(state: Int) {

                        }

                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {

                        }

                        override fun onPageSelected(position: Int) {
                            Timber.d("onPageSelected=$position")
                        }

                    })


                    return@controls
                }
                else -> {
                    Timber.d("Неизвестный элемент интерфейса")
                    return@controls
                }
            }
        }

        return controlList

    }

    /*private fun clearCheckup(checkup: Checkup): Checkup {
        val newcheckup=checkup
        checkup.
        return
    }*/

    private fun refreshCheckupViewer(v: View, control:Models.TemplateControl) {
        Timber.d("refreshCheckupViewer controls=${control.subcheckup.size}")
        val pager = v.findViewById(R.id.viewPager) as ViewPager
        val checkupCount = (v.parent as LinearLayout).findViewById(R.id.countCheckup) as TextView
        checkupCount.text =control.subcheckup.size.toString()

        val adapter =
            CheckupPagerAdapter(
                control,
                parentFragment
            )
        pager.adapter = adapter


        pager.offscreenPageLimit = 4 // сколько чеклистов загружать в память

        val mSlidingTabLayout = v.findViewById(R.id.sliding_tabs) as SlidingTabLayout
        mSlidingTabLayout.setDistributeEvenly(true)
        mSlidingTabLayout.setViewPager(pager)
    }

    /**
     * Метод, в котором осуществляется привязка дочернего View к родительскому
     */
    private fun doAssociateParent(v: View, mainView: View, index: Int?=null){
        if (mainView is LinearLayout) {
            if (index!=null) {
                mainView.addView(v,index)
            } else {
                mainView.addView(v)
            }

        }
    }

    /**
     * Найдем кнопку Fab и добавим для нее обработчик
     */
    private fun attachListenerToFab(v: View, control: Models.TemplateControl) {
        Timber.d("attachListenerToFab")

        val fab: ImageButton = v.findViewById(R.id.fabCheck)
        fab.setOnClickListener {
            Timber.d("Прошли шаг чеклиста")

            var isEmpty=false

            // Проверим введено ли значение
            when (control.type) {
                "combobox"->{
                    val controlView=v.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)
                    if (TextUtils.isEmpty(controlView.text.toString())) {
                        controlView.error = "Нужно выбрать значение из списка"
                        isEmpty=true
                    }
                }
                "multilevel_combobox"->{
                    val controlView=v.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)
                    if (TextUtils.isEmpty(controlView.text.toString())) {
                        controlView.error = "Нужно выбрать значение из списка"
                        isEmpty=true
                    }
                    val controlView2=v.findViewById<MaterialBetterSpinner>(R.id.subspinner)
                    if (TextUtils.isEmpty(controlView2.text.toString())) {
                        controlView2.error = "Нужно выбрать значение из списка"
                        isEmpty=true
                    }
                }
                "textinput"->{
                    val controlView=v.findViewById<TextInputEditText>(R.id.tiet)
                    if (TextUtils.isEmpty(controlView.text.toString())) {
                        controlView.error = "Нужно заполнить поле"
                        isEmpty=true
                    }
                }
                "photo"->{
                    // Проверим были ли добавлены фото
                    val curOrder=(parentFragment.activity as MainActivity).currentOrder
                    val controlViewError=v.findViewById<TextView>(R.id.errorPhoto)
                    if (photoHelper.checkDirAndEmpty("${curOrder.guid}/${checkup.guid}/${control.guid}")) {
                        Timber.d("Папка есть, она не пуста")
                        controlViewError.visibility=View.INVISIBLE
                        // Сохраним filemap в resValue
                        //control.resvalue="${checkup.guid}/${control.guid}"

                        Timber.d(control.resvalue)

                    } else {
                        Timber.d("Папка с фото отсутствует")
                        controlViewError.visibility=View.VISIBLE
                        isEmpty=true
                    }
                }
                "map_coordinates"->{
                    val controlView=v.findViewById<TextView>(R.id.mapCoordinatesResult)
                    val controlViewError=v.findViewById<TextView>(R.id.errorMap)
                    if (controlView.text != parentFragment.resources.getString(R.string.not_coordinates)) {
                        Timber.d("координаты заданы")
                        controlViewError.visibility=View.INVISIBLE
                    } else {
                        Timber.d("координаты не заданы")
                        controlViewError.visibility=View.VISIBLE
                        isEmpty=true
                    }
                }
                "group_questions"->{
                    // Получим общий лиcт контролов, для фильтрации
                    val commonListControls= Models.ControlList()
                    Timber.d("control.controlList=${control.groupControlList}")
                    control.groupControlList?.list?.forEach {
                        commonListControls.list.addAll(it.list)
                    }

                    Timber.d("commonListControls=${commonListControls.list}")


                    //val notcheckedcontrol=control.controlList?.list?.filter { !it.checked }
                    val notcheckedcontrol= commonListControls.list.filter { !it.checked }
                    Timber.d("notcheckedcontrol=${notcheckedcontrol}")
                    //Timber.d("notcheckedcontrol=${notcheckedcontrol[0].resvalue}")
                    if (notcheckedcontrol.isNotEmpty()) {
                        Timber.d("isEmpty")
                        val tvError=v.findViewById<TextView>(R.id.errorSubcheckup)
                        tvError.visibility=View.VISIBLE
                        isEmpty=true
                    } else {
                        val tvError=v.findViewById<TextView>(R.id.errorSubcheckup)
                        tvError.visibility=View.INVISIBLE
                        // Сохраняем результат
                        val controlList2 = control.groupControlList
                        Timber.d("все отмечено!")
                        Timber.d("${controlList2?.list?.get(0)?.list?.get(0)?.resvalue}")
                        val gson= GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create()
                        val resCheckup =gson.toJson(controlList2)
                        control.resvalue=resCheckup.toString()
                    }

                }
                else -> {
                    Timber.d("Неизвестный элемент интерфейса")
                }

            }

            if (!isEmpty) {
                control.checked=!control.checked
                changeChecked(v,control)

                // Обновим контрол для зависимого чеклиста
                val parent=control.parent
                if (parent!=null) {
                    Timber.d("control.parent=${parent}")
                    Timber.d("group_${parent.groupControlList?.list?.get(0)?.list}")
                    parent.groupControlList?.list?.forEach {
                        val index=it.list.indexOf(control)
                        if (index>-1) {
                            Timber.d("НАШЛИ!!")
                            it.list[index] = control
                        }
                    }
                } else {
                    Timber.d("parent==null")
                }
            }
        }
    }


    /**
     * Сменим цвет шага
     */
    fun changeChecked(v: View, control: Models.TemplateControl) {
        val cardView = v.findViewById<CardView>(R.id.cv)
        val fabButton = if (control.type=="group_questions") {
            v.findViewWithTag<ImageButton>("fabGroup")
        } else {
            v.findViewById<ImageButton>(R.id.fabCheck)
        }

        val colorLine=v.findViewById<FrameLayout>(R.id.colorLine)
        if (control.checked) {
            cardView?.setCardBackgroundColor(
                ContextCompat.getColor(
                    v.context,
                    R.color.colorCardSelect
                )
            )
            fabButton.setImageResource(R.drawable.ic_check_blue)
            colorLine.visibility=View.VISIBLE
        } else {
            cardView?.setCardBackgroundColor(
                ContextCompat.getColor(
                    v.context,
                    R.color.colorCardItem
                )
            )
            fabButton.setImageResource(R.drawable.ic_check)
            colorLine.visibility=View.INVISIBLE
        }
    }

}