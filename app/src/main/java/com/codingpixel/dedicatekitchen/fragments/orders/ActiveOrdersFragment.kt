package com.codingpixel.dedicatekitchen.fragments.orders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.order.OrderDetailHistory
import com.codingpixel.dedicatekitchen.adapters.OrdersAdapter
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.databinding.FragmentActiveOrdersBinding
import com.codingpixel.dedicatekitchen.fragments.dialogs.InfoDialog
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.ToggleSelectionListener
import com.codingpixel.dedicatekitchen.interfaces.TwoButtonsDialogListener
import com.codingpixel.dedicatekitchen.models.Order
import com.codingpixel.dedicatekitchen.viewmodels.OrdersViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ActiveOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActiveOrdersFragment : BaseFragment(), ItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentActiveOrdersBinding

    private lateinit var ordersAdapter: OrdersAdapter
    private var ordersList = ArrayList<Order>()

    private var selectedFilter = Constants.All
    private lateinit var ordersViewModel: OrdersViewModel

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_active_orders, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateSelectedFilter()
        initListener()
        initViewModel()
        mBinding.pullRefresh.setOnRefreshListener(this)
        //ordersList.addAll(CommonMethods.getDummyActiveOrders())
        ordersAdapter = OrdersAdapter(ordersList = ordersList, itemClickListener = this)
        mBinding.rvOrders.adapter = ordersAdapter
        showView(view = mBinding.pbLoader)
        getOrders()
    }

    private fun getOrders() {
//        context?.let { AppProgressDialog.showProgressDialog(it) }
        ordersViewModel.getOrders(
            url = ApiUrls.ALL_ORDERS + page
        )
    }

    private fun initViewModel() {
        ordersViewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
        ordersViewModel.getDbApiStatus().observe(this) {
            run {

                hideView(view = mBinding.pbLoader)

                when (it.applicationEnum) {
                    ApplicationEnum.GET_ALL_ORDERS_SUCCESS -> {
                        AppProgressDialog.dismissProgressDialog()
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
                        if (page == 1)
                            ordersList.clear()
                        ordersList.addAll(orders)
                        ordersList.forEach { it.orderType = "active" }
                        isLastPage = ordersList.size == JsonManager.getInstance().getIntValue(
                            jsonObject = it.resultObject,
                            keyName = ""
                        )
                        // showShortToast(message = "Active: " + ordersList.size.toString())
                        ordersAdapter.notifyDataSetChanged()
                    }

                    ApplicationEnum.GET_ALL_ORDERS_ERROR -> {
                        AppProgressDialog.dismissProgressDialog()
                        ordersList.clear()
                        ordersAdapter.notifyDataSetChanged()
                        if (mBinding.pullRefresh.isRefreshing)
                            mBinding.pullRefresh.isRefreshing = false
                        hideView(view = mBinding.pbLoader)
                        //  showShortToast(message = "Active: " + it.message)

                    }

                    ApplicationEnum.CANCEL_ORDER_SUCCESS -> {
                        AppProgressDialog.dismissProgressDialog()
                        successPopup(it.message, context, false)
                        getOrders()
                    }
                    else -> {
                        AppProgressDialog.dismissProgressDialog()
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

    private fun toggleEmptyData() {
        if (ordersList.isEmpty()) {
            showView(view = mBinding.tvNoOrder)
        } else {
            hideView(view = mBinding.tvNoOrder)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedFilter() {
        mBinding.tvSortBy.text = "${getString(R.string.sort_by)} ${selectedFilter}"
    }

    private fun initListener() {

        mBinding.tvSortBy.setOnClickListener {
            showOrdersFilter()
        }
    }

    private fun showOrdersFilter() {
        val toggleOptions = CommonMethods.getOrderFilterOptionsListForActiveOrder()
        toggleOptions.forEach { it.isChecked = false }
        val selectedFilterIndex = toggleOptions.indexOfFirst { it.optionName == selectedFilter }
        if (selectedFilterIndex != -1)
            toggleOptions[selectedFilterIndex].isChecked = true
        CommonMethods.showBottomToggleSheetDialog(
            fragmentManager = childFragmentManager,
            showTitle = true,
            title = getString(R.string.sort_by),
            toggleList = toggleOptions,
            toggleSelectionListener = object : ToggleSelectionListener {
                override fun selectedItem(selectedOption: String) {
                    selectedFilter = selectedOption
                    updateSelectedFilter()
                }
            }
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ActiveOrdersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ActiveOrdersFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun itemClicked(position: Int) {
        startActivity(
            Intent(
                context,
                OrderDetailHistory::class.java
            ).putExtra(IntentParams.ORDER_DETAIL_HISTORY, ordersList)
                .putExtra(IntentParams.TAG, "active")
        )

    }

    override fun brandTapped(position: Int, title: String) {
        TODO("Not yet implemented")
    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {

    }

    override fun deleteLocation(position: Int, addressId: Int) {
        TODO("Not yet implemented")
    }

    override fun cancelOrder(position: Int, orderId: Int) {
        CommonMethods.showTwoButtonsAlertDialog(fragmentManager = childFragmentManager,
            title = "Cancel Order", message = "Are you sure you want to cancel your order?",
            leftButtonText = "Later", rightButtonText = "Yes",
            twoButtonsDialogListener = object : TwoButtonsDialogListener {
                override fun leftButtonTapped() {
                }

                override fun rightButtonTapped() {
                    context?.let { AppProgressDialog.showProgressDialog(it) }
                    ordersViewModel.cancelOrders(orderId = orderId)
                }
            })
    }

    override fun info() {
        val sheet = InfoDialog.newInstance()
        sheet.show(childFragmentManager, InfoDialog::class.java.canonicalName)
    }

    override fun orderDetails(position: Int, orderDetail: Order) {
        startActivity(
            Intent(
                context,
                OrderDetailHistory::class.java
            ).putExtra(IntentParams.ORDER_DETAIL_HISTORY, orderDetail)
                .putExtra(IntentParams.TAG, "active")
        )
    }

    override fun orderCompleted(position: Int) {
//        ordersList.removeAt(position)
//        ordersAdapter.notifyDataSetChanged()
        ordersAdapter.notifyItemChanged(position)
    }

    override fun refreshRow(position: Int) {
        ordersAdapter.notifyItemChanged(position)
    }

    override fun onRefresh() {
        page = 1
        isLastPage = false
        getOrders()
    }
}