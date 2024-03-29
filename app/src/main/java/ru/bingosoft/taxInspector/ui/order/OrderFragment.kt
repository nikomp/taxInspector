package ru.bingosoft.taxInspector.ui.order

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.alert_not_internet.view.*
import kotlinx.android.synthetic.main.alert_syncdb.view.*
import kotlinx.android.synthetic.main.fragment_order.*
import ru.bingosoft.taxInspector.R
import ru.bingosoft.taxInspector.db.Orders.Orders
import ru.bingosoft.taxInspector.models.Models
import ru.bingosoft.taxInspector.ui.checkuplist.CheckupListFragment
import ru.bingosoft.taxInspector.ui.login.LoginActivity
import ru.bingosoft.taxInspector.ui.login.LoginContractView
import ru.bingosoft.taxInspector.ui.login.LoginPresenter
import ru.bingosoft.taxInspector.ui.mainactivity.FragmentsContractActivity
import ru.bingosoft.taxInspector.ui.mainactivity.MainActivity
import ru.bingosoft.taxInspector.util.Const
import ru.bingosoft.taxInspector.util.Const.LogTags.SPS
import ru.bingosoft.taxInspector.util.SharedPrefSaver
import ru.bingosoft.taxInspector.util.Toaster
import ru.bingosoft.taxInspector.util.UserLocationService
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class OrderFragment : Fragment(), LoginContractView, OrderContractView, OrdersRVClickListeners {

    @Inject
    lateinit var loginPresenter: LoginPresenter

    @Inject
    lateinit var orderPresenter: OrderPresenter

    @Inject
    lateinit var toaster: Toaster

    @Inject
    lateinit var sharedPref: SharedPrefSaver

    private lateinit var currentOrder: Orders
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        Timber.d("OrderFragment.onCreateView")

        root = inflater.inflate(R.layout.fragment_order, container, false)

        (this.requireActivity() as AppCompatActivity).supportActionBar?.setTitle(R.string.menu_orders)

        // Авторизуемся всегда иначе данные не будут уходить на сервер
        /*doAuthorization()
        Timber.d("sharedPref.getLogin()=${sharedPref.getLogin()}")
        orderPresenter.attachView(this)
        orderPresenter.loadOrders()

        val pb=root.findViewById<ProgressBar>(R.id.progressBar)
        pb.visibility= View.INVISIBLE*/


        // Если логин/пароль есть не авторизуемся
        if (sharedPref.getLogin()=="" && sharedPref.getPassword()=="") {
            doAuthorization()
        } else {
            Timber.d("sharedPref.getLogin()=${sharedPref.getLogin()}")
            orderPresenter.attachView(this)
            orderPresenter.loadOrders()

            val pb=root.findViewById<ProgressBar>(R.id.progressBar)
            pb.visibility= View.INVISIBLE
        }

        return root
    }


    override fun onDestroy() {
        super.onDestroy()
        orderPresenter.onDestroy()
        loginPresenter.onDestroy()
    }

    private fun doAuthorization() {
        Timber.d("doAuthorization")
        // Получим логин и пароль из настроек
        val sharedpref = this.activity?.getSharedPreferences(Const.SharedPrefConst.APP_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedpref!!.contains(Const.SharedPrefConst.LOGIN) && sharedpref.contains(Const.SharedPrefConst.PASSWORD)) {

            val login = sharedPref.getLogin()
            val password = sharedPref.getPassword()

            Log.d(SPS,"===$login $password")

            loginPresenter.attachView(this)
            loginPresenter.authorization(login, password) // Проверим есть ли авторизация
        } else {
            Timber.d("логин/пароль=ОТСУТСТВУЮТ")
            Log.d(SPS,"логин/пароль=ОТСУТСТВУЮТ")
            // Запустим активити с настройками
            val intent = Intent(this.activity, LoginActivity::class.java)
            startActivityForResult(intent, Const.RequestCodes.AUTH)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Timber.d( "resultCode OK")
            when (requestCode) {
                Const.RequestCodes.AUTH -> {
                    Timber.d( "Авторизуемся")
                    Log.d(SPS,"Const.RequestCodes.AUTH")
                    if (data != null) {
                        val login = data.getStringExtra("login")
                        val password = data.getStringExtra("password")
                        Log.d(SPS,"$login $password")

                        loginPresenter.attachView(this)
                        loginPresenter.authorization(login, password)

                    }
                }
                else -> {
                    //toaster.showToast(R.string.unknown_requestCode)
                    Log.d(SPS,"not Const.RequestCodes.AUTH")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun showMessageLogin(resID: Int) {
        toaster.showToast(resID)

        orderPresenter.attachView(this)
        //orderPresenter.importData()
        orderPresenter.loadOrders()
    }

    override fun showMessageLogin(msg: String) {
        toaster.showToast(msg)
    }

    override fun saveLoginPasswordToSharedPreference(stLogin: String, stPassword: String) {
        Log.d(SPS, "stLogin=$stLogin $stPassword")
        sharedPref.saveLogin(stLogin)
        sharedPref.savePassword(stPassword)
    }

    override fun saveToken(token: String) {
        sharedPref.saveToken(token)
    }

    override fun showFailureTextView() {
        val tv=textfailure
        tv.visibility=View.VISIBLE
        val pb=progressBar
        pb.visibility=View.INVISIBLE
        alertNotInternet()
    }

    /**
     * Метод, проверяющий доступность БД
     * @return - true/false
     */
    override fun alertRepeatSync() {
        Timber.d("doSync")
        val pb=progressBar
        pb.visibility= View.INVISIBLE

        val dbFile = this.requireContext().getDatabasePath("mydatabase.db")
        lateinit var alertDialog: AlertDialog
        if (dbFile.exists()) {
            val layoutInflater = LayoutInflater.from(this.requireContext())
            val dialogView: View =
                layoutInflater.inflate(R.layout.alert_syncdb, (root.parent as ViewGroup), false)

            if (sharedPref.getDateSyncDB()!="") {
                dialogView.stMsgAlert.text=getString(R.string.syncdb, sharedPref.getDateSyncDB())
            } else {
                dialogView.stMsgAlert.text=getString(R.string.syncdb2)
            }

            val builder = AlertDialog.Builder(this.context)

            dialogView.buttonOK.setOnClickListener{
                Timber.d("dialogView.buttonOK")
                loginPresenter.syncDB()
                alertDialog.dismiss()

            }

            dialogView.buttonNo.setOnClickListener{
                showMessageLogin(R.string.auth_ok)
                orderPresenter.loadOrders() // Грузим данные из локальной БД
                alertDialog.dismiss()
            }

            builder.setView(dialogView)
            builder.setCancelable(true)
            alertDialog=builder.create()
            alertDialog.show()
        } else {
            loginPresenter.syncDB()
        }

    }

    private fun alertNotInternet() {
        Timber.d("alertNotInternet")
        lateinit var alertDialogNotInternet: AlertDialog
        val layoutInflater = LayoutInflater.from(this.requireContext())
        val dialogView: View =
            layoutInflater.inflate(R.layout.alert_not_internet, (root.parent as ViewGroup), false)

        val builder = AlertDialog.Builder(this.context)

        dialogView.buttonInetOK.setOnClickListener{
            Timber.d("dialogView.buttonInetOK")
            //TODO возможно тут потребуется вывести окно авторизации
            showMessageLogin(R.string.auth_ok)
            alertDialogNotInternet.dismiss()

        }

        dialogView.buttonInetNo.setOnClickListener{
            alertDialogNotInternet.dismiss()
        }

        builder.setView(dialogView)
        builder.setCancelable(true)
        alertDialogNotInternet=builder.create()
        alertDialogNotInternet.show()


    }

    override fun saveDateSyncToSharedPreference(date: Date) {
        Timber.d("saveDateSyncToSharedPreference")
        sharedPref.saveDateSyncDB(date)
    }

    override fun saveInfoUserToSharedPreference(user: Models.User) {
        sharedPref.saveUser(user)
    }

    override fun startLocationService() {
        // Стартуем фоновый сервис для отслеживания пользователя
        activity?.startService(Intent(activity, UserLocationService::class.java))
    }

    override fun showOrders(orders: List<Orders>) {

        // инициализируем контейнер SwipeRefreshLayout
        val swipeRefreshLayout = root.findViewById(R.id.srl_container) as SwipeRefreshLayout

        // указываем слушатель свайпов пользователя
        swipeRefreshLayout.setOnRefreshListener {
            loginPresenter.syncDB()
            swipeRefreshLayout.isRefreshing = false
        }

        val ordersRecyclerView = root.findViewById(R.id.orders_recycler_view) as RecyclerView
        ordersRecyclerView.layoutManager = LinearLayoutManager(this.activity)
        val adapter = OrderListAdapter(orders,this, this.requireContext())
        ordersRecyclerView.adapter = adapter
    }

    override fun showMessageOrders(msg: String) {
        //TODO реализую позже
    }

    override fun recyclerViewListClicked(v: View?, position: Int) {
        Timber.d("recyclerViewListClicked")

        currentOrder=(orders_recycler_view.adapter as OrderListAdapter).getOrder(position)

        currentOrder.checked=!currentOrder.checked

        /*Для Заявок пока не нужно менять цвет при клике
        if (currentOrder.checked) {
            val cardView = v?.findViewById<CardView>(R.id.cv)
            cardView?.setCardBackgroundColor(
                ContextCompat.getColor(
                    v.context,
                    R.color.colorCardSelect
                )
            )
        } else {
            val cardView = v?.findViewById<CardView>(R.id.cv)
            cardView?.setCardBackgroundColor(
                ContextCompat.getColor(
                    v.context,
                    R.color.colorCardItem
                )
            )
        }*/

        (activity as MainActivity).currentOrder=this.currentOrder

        //Включаем фрагмент со списком Обследований для конкретной заявки
        val bundle = Bundle()
        bundle.putBoolean("checkUpForOrder", true)
        bundle.putLong("idOrder",currentOrder.id)

        val fragmentCheckupList= CheckupListFragment()
        fragmentCheckupList.arguments=bundle
        val fragmentManager=this.requireActivity().supportFragmentManager

        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragmentCheckupList, "checkup_list_fragment_tag")
            .addToBackStack(null)
            .commit()

        fragmentManager.executePendingTransactions()

        (this.requireActivity() as FragmentsContractActivity).setChecupListOrder(currentOrder)


    }


}