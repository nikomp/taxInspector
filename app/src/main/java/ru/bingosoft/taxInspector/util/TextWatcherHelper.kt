package ru.bingosoft.taxInspector.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import ru.bingosoft.taxInspector.R
import ru.bingosoft.taxInspector.models.Models
import timber.log.Timber

class TextWatcherHelper(private val control: Models.TemplateControl, private val uiCreator: UICreator, val v: View): TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        control.checked=false
        control.resvalue=s.toString()
        if (control.type=="multilevel_combobox") {
            val materialSpinner=v.findViewById<MaterialBetterSpinner>(R.id.android_material_design_spinner)
            //val materialSubSpinner=v.findViewById<MaterialBetterSpinner>(R.id.subspinner)
            control.resvalue=s.toString()
            control.resmainvalue=materialSpinner.text.toString()
        }
        uiCreator.changeChecked(v,control)

        val parent=control.parent
        if (parent!=null) {
            Timber.d("parent=${parent.id}")
            val parentView=uiCreator.parentFragment.root.findViewById<View>(parent.id)
            parent.checked=false
            uiCreator.changeChecked(parentView, parent)
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //TODO реализую при необходимости
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //TODO реализую при необходимости
    }
}