package ru.bingosoft.taxInspector.ui.checkuplist_bottom

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import dagger.android.support.AndroidSupportInjection
import ru.bingosoft.taxInspector.R
import ru.bingosoft.taxInspector.db.CheckupGuide.CheckupGuide
import ru.bingosoft.taxInspector.ui.mainactivity.MainActivity
import ru.bingosoft.taxInspector.util.Toaster
import timber.log.Timber
import javax.inject.Inject


class CheckupListBottomSheet: BottomSheetDialogFragment(), CheckupListBottomSheetContractView, View.OnClickListener  {
    private lateinit var mbSpinner: MaterialBetterSpinner

    @Inject
    lateinit var clbsPresenter: CheckupListBottomSheetPresenter
    @Inject
    lateinit var toaster: Toaster

    private var checkupGuideCurrent: CheckupGuide?=null
    private lateinit var rootView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun showKindObject(checkupGuides: List<CheckupGuide>) {
        Timber.d("Выводим список объектов")

        val dataSpinner=arrayListOf<String>()
        checkupGuides.forEach {
            dataSpinner.add(it.kindCheckup)
        }

        Timber.d(dataSpinner.toString())

        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this.requireContext(),
            R.layout.template_multiline_spinner_item,
            dataSpinner
        )

        mbSpinner.setAdapter(spinnerArrayAdapter)
        // Вешаем обработчик на spinner последним, иначе сбрасывается цвет шага
        mbSpinner.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            Timber.d("position $position")
            checkupGuideCurrent=checkupGuides[position]

        }
    }

    override fun saveNewObjectOk() {
        Timber.d("Сохранили_объект_обследования")
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btnSaveNewObject -> {
                    val stNameObject =
                        rootView.findViewById<TextInputEditText>(R.id.tietNameObject).text

                    if (checkupGuideCurrent!=null && !stNameObject.isNullOrEmpty()) {
                        if (::clbsPresenter.isInitialized) {
                            (activity as MainActivity).currentOrder.id

                            clbsPresenter.saveObject(
                                checkupGuideCurrent!!,
                                stNameObject.toString(),
                                (activity as MainActivity).currentOrder.id
                            )

                            val params =
                                (rootView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
                            val behavior = params.behavior
                            if (behavior != null && behavior is BottomSheetBehavior) {
                                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                            }

                        }
                    } else {
                        toaster.showToast(R.string.btmSheetSaveInvalid)
                    }


                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")

        AndroidSupportInjection.inject(this)

        val view=LayoutInflater.from(context).inflate(R.layout.checkuplist_bottom_sheet, null)
        rootView=view
        // Заполним комбобокс Тип Объекта
        mbSpinner=view.findViewById(R.id.spinner_kindobject)

        dialog?.setOnShowListener {
            Handler().postDelayed({
                Timber.d("xxxxxxxxxxxx")
                val d = dialog as BottomSheetDialog
                val bottomSheet = d.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                val bottomSheetBehavior =BottomSheetBehavior.from(bottomSheet!!)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }, 0)
        }

        /*val bsDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bsDialog.setOnShowListener { dialog ->
            Handler().postDelayed({
                Timber.d("xxxxxxxxxxxx")
                val d = dialog as BottomSheetDialog
                val bottomSheet = d.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                val bottomSheetBehavior =BottomSheetBehavior.from(bottomSheet!!)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }, 0)
        }*/

        //bsDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        val btnSave = view.findViewById(R.id.btnSaveNewObject) as MaterialButton
        btnSave.setOnClickListener(this)

        clbsPresenter.attachView(this)
        clbsPresenter.getKindObject()


        return rootView

    }

    override fun onDestroy() {
        super.onDestroy()
        clbsPresenter.onDestroy()
    }


}