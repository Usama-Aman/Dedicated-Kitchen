package com.codingpixel.dedicatekitchen.fragments.orders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.checkout.CartActivity
import com.codingpixel.dedicatekitchen.adapters.OrdersAdapter
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentPastOrdersBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.ToggleSelectionListener
import com.codingpixel.dedicatekitchen.models.Order
import com.codingpixel.dedicatekitchen.activities.order.OrderDetailHistory
import com.codingpixel.dedicatekitchen.activities.product.CategorySelectionActivity
import com.codingpixel.dedicatekitchen.application.MyApplication
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Cart.Cart
import com.codingpixel.dedicatekitchen.database.Cart.CartExtraOption
import com.codingpixel.dedicatekitchen.fragments.dialogs.MealPrepDeliveryTypePopUp
import com.codingpixel.dedicatekitchen.interfaces.MealPrepPopupListener
import com.codingpixel.dedicatekitchen.models.OrderModel
import com.codingpixel.dedicatekitchen.models.OrderToDbModel
import com.codingpixel.dedicatekitchen.viewmodels.OrdersViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [PastOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PastOrdersFragment : BaseFragment(), ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentPastOrdersBinding

    private lateinit var ordersAdapter: OrdersAdapter
    private var ordersList = ArrayList<Order>()
    private var orderHistory = ArrayList<OrderToDbModel>()
    private var selectedFilter = Constants.All
    private var filteredOrderedList = ArrayList<Order>()
    private lateinit var ordersViewModel: OrdersViewModel
    private var nameList = ArrayList<String>()
    private var orderArray = ArrayList<OrderModel>()
    private var orderType: String = ""
    private var tmp = ArrayList<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_past_orders, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.pullRefresh.setOnRefreshListener(this)
        ordersViewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
        initAdapter()
        getOrders()
        initViewModel()
        initClickListener()
        setScrollingCallBack()
        showView(view = mBinding.pbLoader)
    }

    private fun initAdapter() {
//        filteredOrderedList.clear()
//        filteredOrderedList.addAll(ordersList)
//        ordersAdapter.notifyDataSetChanged()

        ordersAdapter = OrdersAdapter(
            ordersList = filteredOrderedList,
            itemClickListener = this
        )
        mBinding.rvOrders.adapter = ordersAdapter
    }

    private fun initClickListener() {
        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                filteredOrderedList.clear()
                if (s.toString().isNotEmpty()) {
                    if (ordersList.isNotEmpty()) {
                        filteredOrderedList.addAll(ordersList.filter {
                            it.orderInfo.items_data.isNotEmpty() && it.orderInfo.items_data[0].name.toLowerCase(
                                Locale.ROOT
                            ).contains(
                                s.toString().toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        })
                    }
                } else filteredOrderedList.addAll(ordersList)

                ordersAdapter.notifyDataSetChanged()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isEmpty())
                    hideView(mBinding.ivCross)
                else
                    showView(mBinding.ivCross)
            }
        })
        mBinding.ivCross.setOnClickListener {
            mBinding.etSearch.text.clear()
            // hideView(mBinding.tvFoundResults)
            val imm: InputMethodManager =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
            filteredOrderedList = ordersList
            ordersAdapter.notifyDataSetChanged()
        }
    }

    private fun getOrders() {
        isLoading = true
        ordersViewModel.getOrders(
            url = ApiUrls.PAST_ORDERS + page
        )
    }

    private fun initViewModel() {
        ordersViewModel.getDbApiStatus().observe(this) {
            run {
                hideView(view = mBinding.pbLoader)
                isLoading = false
                removeLoadingRows()
                when (it.applicationEnum) {
                    ApplicationEnum.GET_PAST_ORDERS_SUCCESS -> {

                        if (mBinding.pullRefresh.isRefreshing)
                            mBinding.pullRefresh.isRefreshing = false
                        hideView(view = mBinding.pbLoader)
                        val orders = JsonManager.getInstance().getOrders(
                            jsonObject = it.resultObject
                        )
                        orders.forEach {
                            it.orderDetail()
                            it.initDeliveryDateTime()
                        }

                        if (orders.isEmpty())
                            isLastPage = true

                        if (page == 1) {
                            ordersList.clear()
                            filteredOrderedList.clear()
                        }

                        if (!isLastPage) {
                            ordersList.addAll(orders)
                            ordersList.forEach { it.orderType = "past" }
                            filteredOrderedList.addAll(orders)
                            filteredOrderedList.forEach { it.orderType = "past" }
                        }
                        ordersAdapter.notifyDataSetChanged()
                    }

                    ApplicationEnum.GET_PAST_ORDERS_ERROR -> {
                        if (mBinding.pullRefresh.isRefreshing)
                            mBinding.pullRefresh.isRefreshing = false

                        if (it.message == "No record found.") {
                            isLastPage = true
                        }
                        ordersAdapter.notifyDataSetChanged()
                        hideView(view = mBinding.pbLoader)
                        // showShortToast(message = "Past: " + it.message)

                    }

                    else -> {
                        if (mBinding.pullRefresh.isRefreshing)
                            mBinding.pullRefresh.isRefreshing = false
                        hideView(view = mBinding.pbLoader)
                        showView(mBinding.tvNoOrder)
                    }
                }

                toggleEmptyData()
            }
        }
    }


    private fun setScrollingCallBack() {
        mBinding.rvOrders.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) {
                } else if (!recyclerView.canScrollVertically(1)) {
                    if (!isLastPage && !isLoading && mBinding.etSearch.text.toString().trim()
                            .isEmpty()
                    ) {
                        isLoading = true
                        ++page
                        filteredOrderedList.add(Order().apply {
                            id = -1
                        })
                        ordersAdapter.notifyItemInserted(filteredOrderedList.size)
                        getOrders()
                    }
                }

            }
        })
    }

    private fun removeLoadingRows() {
        filteredOrderedList.removeAll { it.id == -1 }
    }

    private fun toggleEmptyData() {
        if (ordersList.isEmpty()) {
            showView(view = mBinding.tvNoOrder)
        } else {
            hideView(view = mBinding.tvNoOrder)
        }
    }

//    private fun updateSelectedFilter() {
//        mBinding.tvSortBy.text = "${getString(R.string.sort_by)} ${selectedFilter}"
//    }


    private fun showOrdersFilter() {
        val toggleOptions = CommonMethods.getOrderFilterOptionsListForPastOrder()
        toggleOptions.forEach { it.isChecked = false }
        val seletedFilterIndex = toggleOptions.indexOfFirst { it.optionName == selectedFilter }
        if (seletedFilterIndex != -1)
            toggleOptions[seletedFilterIndex].isChecked = true
        CommonMethods.showBottomToggleSheetDialog(
            fragmentManager = childFragmentManager,
            showTitle = true,
            title = getString(R.string.sort_by),
            toggleList = toggleOptions,
            toggleSelectionListener = object : ToggleSelectionListener {
                override fun selectedItem(selectedOption: String) {
                    selectedFilter = selectedOption
                    // updateSelectedFilter()
                }
            }
        )
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            PastOrdersFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun orderDetails(position: Int, orderDetail: Order) {
        startActivity(
            Intent(
                context,
                OrderDetailHistory::class.java
            ).putExtra(IntentParams.ORDER_DETAIL_HISTORY, orderDetail)
                .putExtra(IntentParams.TAG, "past")
        )
    }

    override fun brandTapped(position: Int, title: String) {

    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {

    }

    override fun deleteLocation(position: Int, addressId: Int) {
        TODO("Not yet implemented")
    }

    override fun onRefresh() {
        page = 1
        isLastPage = false
        getOrders()
    }

    override fun reorder(position: Int, orderDetail: Order) {

        if ((orderDetail.terminal_name
                ?: "") == AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
        ) {
            val db = context?.let { it1 -> AppDatabase(it1) }
            if (orderDetail.order_type == "mp_delivery" || orderDetail.order_type == "mp_takeout") {
                GlobalScope.launch {
                    val cartItemsExist =
                        db?.cartDao()?.exists(type = ApplicationEnum.TAKE_OUT.toString())
                    if (cartItemsExist == true) {
                        activity?.runOnUiThread {
                            context?.let { it1 ->
                                errorDialogue(
                                    "Please complete your current order.",
                                    it1
                                )
                            }
                        }
                    } else {
                        reOrderMealPrep(orderDetail = orderDetail)
                    }
                }
            } else {
                GlobalScope.launch {
                    val isMealPrepItemExist =
                        db?.cartDao()?.exists(type = ApplicationEnum.MEAL_PREP.toString())
                    if (isMealPrepItemExist == true) {
                        activity?.runOnUiThread {
                            context?.let { it1 ->
                                errorDialogue(
                                    "Please complete your current order.",
                                    it1
                                )
                            }
                        }
                    } else {
                        saveOrderToDb(orderDetail)
                    }
                }
            }
        } else {
            val kitchenName = CommonMethods.getKitchensList()
                .find { it.terminalId == orderDetail.terminal_name }?.name ?: "N/A"
            errorDialogue(
                message = "Please change the selected kitchen to $kitchenName",
                context = context!!
            )
        }


    }


    private fun reOrderMealPrep(orderDetail: Order) {

        CommonMethods.showMealPrepDeliveryPopUp(
            fragmentManager = childFragmentManager,
            type = "new",
            mealPrepListener = object : MealPrepPopupListener {
                override fun deliverySelected(date: String, time: String, location: String) {
                    MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
                    saveOrderToDb(orderDetail)
                }

                override fun takeOutSelected(date: String, time: String) {
                    activity?.runOnUiThread {
                        MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
                        saveOrderToDb(orderDetail)
                    }
                }
            }
        )

//        val mealPrepPopup = MealPrepDeliveryTypePopUp.newInstance("new")
//        mealPrepPopup.setListener(object : MealPrepPopupListener {
//            override fun deliverySelected(
//                date: String,
//                time: String,
//                location: String
//            ) {
//                MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
//                saveOrderToDb(orderDetail)
//            }
//
//            override fun takeOutSelected(date: String, time: String) {
//                activity?.runOnUiThread {
//                    MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
//                    saveOrderToDb(orderDetail)
//                }
//            }
//        })
//
//        activity?.runOnUiThread {
//            mealPrepPopup.show(
//                childFragmentManager,
//                MealPrepDeliveryTypePopUp::class.java.canonicalName
//            )
//        }

    }

    private fun saveOrderToDb(orderDetail: Order) {
        val db = context?.let { AppDatabase(it) }
        if (db != null) {
            GlobalScope.launch {
                val isMealPrepItemExist =
                    db.cartDao().exists(type = ApplicationEnum.MEAL_PREP.toString())
                val isTakeoutItemExist =
                    db.cartDao().exists(type = ApplicationEnum.TAKE_OUT.toString())
                if (orderDetail.order_type == "takeout" && isMealPrepItemExist) {
                    activity?.runOnUiThread {
                        context?.let {
                            errorDialogue(
                                "Please checkout Meal Prep order first.",
                                it
                            )
                        }
                    }
                } else if (orderDetail.order_type != "takeout" && isTakeoutItemExist) {
                    activity?.runOnUiThread {
                        context?.let { errorDialogue("Please checkout Take Out order first.", it) }
                    }
                } else {
                    orderDetail.orderInfo.items_data.forEach {
                        val rowId = db.cartDao().insert(
                            Cart(
                                0,
                                it.item_id.toInt(),
                                it.name,
                                it.price,
                                it.category_id,
                                it.quantity.toInt(),
                                if (it.img.isNullOrEmpty()) ApiUrls.DEFAULT_PRODUCT_IMAGE_URL else it.img,
                                ((it.price.toFloat() + it.options.map { it.price.toFloat() }
                                    .sum()) * it.quantity.toInt()).toString(),
                                if (orderDetail.order_type == "takeout") ApplicationEnum.TAKE_OUT.toString() else ApplicationEnum.MEAL_PREP.toString(),
                                if (it.note.isNullOrEmpty()) "" else it.note
                            )
                        )
                        it.options.forEach { option ->
                            db.cartExtraOptionDao().insert(
                                CartExtraOption(
                                    0,
                                    option.extra_option_id.toInt(),
                                    option.name,
                                    option.price,
                                    option.quantity.toInt(),
                                    option.category_id,
                                    rowId.toInt()
                                )
                            )
                        }
                    }

                    MyApplication.selectedOrderType =
                        if (orderDetail.order_type == "takeout") ApplicationEnum.TAKE_OUT else ApplicationEnum.MEAL_PREP
                    startActivity(
                        Intent(context, CartActivity::class.java).apply {
                            putExtra(
                                IntentParams.SHOWTAXBOX,
                                false
                            )
                            putExtra(IntentParams.TABLE_TRANSACTION_ID, "")
                        }
                    )
                }
            }
        }
    }

    override fun orderCompleted(position: Int) {

    }
}

