package com.codingpixel.dedicatekitchen.activities.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.checkout.CartActivity
import com.codingpixel.dedicatekitchen.adapters.order.OrderHistoryItemAdapter
import com.codingpixel.dedicatekitchen.application.MyApplication
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Cart.Cart
import com.codingpixel.dedicatekitchen.database.Cart.CartExtraOption
import com.codingpixel.dedicatekitchen.databinding.OrderDetailHistoryBinding
import com.codingpixel.dedicatekitchen.fragments.dialogs.MealPrepDeliveryTypePopUp
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.MealPrepPopupListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


import com.codingpixel.dedicatekitchen.models.*
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel

class OrderDetailHistory : BaseActivity() {

    private var order = Order()
    private var orderHistory = ArrayList<OrderToDbModel>()
    private lateinit var mBinding: OrderDetailHistoryBinding
    private lateinit var adapter: OrderHistoryItemAdapter
    private var orderArray = ArrayList<OrderModel>()
    private var orderType: String = ""
    private var tmp = ArrayList<OrderModel>()
    private lateinit var viewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.order_detail_history)
        changeStatusBarColor(statusBarColor = getColor(R.color.white))
        initViewModel()
        getData()
        initClickListener()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel.getDbApiStatus().observe(this) { it ->
            when (it.applicationEnum) {
                ApplicationEnum.GET_CARD_SUCCESS -> {
                    hideView(view = mBinding.pbCardLoader)
                    showView(view = mBinding.tvCardNumber)
                    AppProgressDialog.dismissProgressDialog()
                    val cards = JsonManager.getInstance().getCardList(it.resultObject)
                    val matchedIndex = cards.indexOfFirst { it.id == order.user_card_id }
                    if (matchedIndex != -1) {
                        mBinding.tvCardNumber.text =
                            "**** **** **** ${cards[matchedIndex].card_number.takeLast(4)}"
                    } else {
                        mBinding.tvCardNumber.text = "Card Not Found"
                    }

                }

                else -> {
                    AppProgressDialog.dismissProgressDialog()
                }
            }
        }

    }

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }
        mBinding.btnReOrder.setOnClickListener {
            reOrder()
        }
    }

    private fun reOrder() {


        if ((order.terminal_name
                ?: "") == AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
        ) {
            val db = AppDatabase(this@OrderDetailHistory)
            if (order.order_type == "mp_delivery" || order.order_type == "mp_takeout") {
                GlobalScope.launch {
                    val cartItemsExist =
                        db?.cartDao()?.exists(type = ApplicationEnum.TAKE_OUT.toString())
                    if (cartItemsExist == true) {
                        runOnUiThread {
                            errorDialogue(
                                "Please complete your current order.",
                                this@OrderDetailHistory
                            )

                        }
                    } else {
                        reOrderMealPrep()
                    }
                }
            } else {
                GlobalScope.launch {
                    val isMealPrepItemExist =
                        db.cartDao().exists(type = ApplicationEnum.MEAL_PREP.toString())
                    if (isMealPrepItemExist == true) {
                        runOnUiThread {
                            errorDialogue(
                                "Please complete your current order.",
                                this@OrderDetailHistory
                            )
                        }
                    } else {
                        saveOrderToDb(orderDetail = order)
                    }
                }
            }
        } else {
            val kitchenName =
                CommonMethods.getKitchensList().find { it.terminalId == order.terminal_name }?.name
                    ?: "N/A"
            errorDialogue(
                message = "Please change the selected kitchen to $kitchenName",
                context = this@OrderDetailHistory
            )
        }


    }

    private fun reOrderMealPrep() {

        CommonMethods.showMealPrepDeliveryPopUp(
            fragmentManager = supportFragmentManager,
            type = "new",
            mealPrepListener = object : MealPrepPopupListener {
                override fun deliverySelected(date: String, time: String, location: String) {
                    MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
                    saveOrderToDb(order)
                }

                override fun takeOutSelected(date: String, time: String) {
                    this@OrderDetailHistory.runOnUiThread {
                        MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
                        saveOrderToDb(order)
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
//                saveOrderToDb(order)
////                    startActivity(
////                        Intent(
////                            context,
////                            CategorySelectionActivity::class.java
////                        ).putExtra(IntentParams.isMealPrep, true)
////                    )
//            }
//
//            override fun takeOutSelected(date: String, time: String) {
//                this@OrderDetailHistory.runOnUiThread {
//                    MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
//                    saveOrderToDb(order)
//                }
//            }
//        })
//        mealPrepPopup.show(
//            supportFragmentManager,
//            MealPrepDeliveryTypePopUp::class.java.canonicalName
//        )
    }

    private fun saveOrderToDb(orderDetail: Order) {
        val db = AppDatabase(this@OrderDetailHistory)
        GlobalScope.launch {
            val isMealPrepItemExist =
                db.cartDao().exists(type = ApplicationEnum.MEAL_PREP.toString())
            val isTakeoutItemExist =
                db.cartDao().exists(type = ApplicationEnum.TAKE_OUT.toString())
            if (orderDetail.order_type == "takeout" && isMealPrepItemExist) {
                this@OrderDetailHistory.runOnUiThread {
                    errorDialogue(
                        "Please checkout Meal Prep order first.",
                        this@OrderDetailHistory
                    )
                }
            } else if (orderDetail.order_type != "takeout" && isTakeoutItemExist) {
                this@OrderDetailHistory.runOnUiThread {
                    errorDialogue("Please checkout Take Out order first.", this@OrderDetailHistory)

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
                    if (orderDetail.order_type == "takeout")
                        ApplicationEnum.TAKE_OUT
                    else
                        ApplicationEnum.MEAL_PREP
                startActivity(
                    Intent(this@OrderDetailHistory, CartActivity::class.java).apply {
                        putExtra(
                            IntentParams.SHOWTAXBOX,
                            false
                        )
                        putExtra(IntentParams.TABLE_TRANSACTION_ID, "")
                    }
                )
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        order =
            intent.getSerializableExtra(IntentParams.ORDER_DETAIL_HISTORY) as Order
        orderType = intent.getStringExtra(IntentParams.TAG)!!
//        for (i in 0 until ordersList.size) {
//            orderHistory.add(ordersList[i].orderInfo)
//        }

        order.orderInfo.items_data.forEach {
            it.subTotal = 0f
            for (opiton in it.options) {
                it.subTotal += if (opiton.price.isNotEmpty() &&
                    opiton.price.matches("-?\\d+(\\.\\d+)?".toRegex())
                    && opiton.quantity.matches("-?\\d+(\\.\\d+)?".toRegex())
                ) {
                    opiton.price.toFloat() * opiton.quantity.toFloat()
                } else {
                    0f
                }
            }
            it.subTotal += it.price.toFloat()
            it.subTotal = it.subTotal * it.quantity.toFloat()
        }

        adapter = OrderHistoryItemAdapter(order.orderInfo.items_data)
        mBinding.rvOrderHistory.adapter = adapter

        mBinding.tvOrderNumber.text = order.getFormattedOrderNumber()

        if ((order.tip ?: 0.0) > 0.0) {
            showView(view = mBinding.tvTipTag)
            showView(view = mBinding.tvTipAmount)
            showView(view = mBinding.tipSep)
        } else {
            hideView(view = mBinding.tvTipTag)
            hideView(view = mBinding.tvTipAmount)
            hideView(view = mBinding.tipSep)
        }

        if ((order.points_discount ?: 0.0) > 0.0) {
            showView(view = mBinding.tvDiscountTag)
            showView(view = mBinding.tvDiscountAmount)
            showView(view = mBinding.discountSep)

            showView(view = mBinding.tvUsedPointsTag)
            showView(view = mBinding.tvUsedPoints)

            mBinding.tvUsedPoints.text = "${((order.points_discount ?: 0.0) * 50).toInt()}"

        } else {
            hideView(view = mBinding.tvDiscountTag)
            hideView(view = mBinding.tvDiscountAmount)
            hideView(view = mBinding.discountSep)

            hideView(view = mBinding.tvUsedPointsTag)
            hideView(view = mBinding.tvUsedPoints)
        }

        if (order.user_card_id ?: 0 > 0) {
            // Used Card
            mBinding.tvPaidBy.text = "Credit Card"
            showView(view = mBinding.pbCardLoader)
            viewModel.getCards()

        } else {
            // Used Credit Card
            mBinding.tvPaidBy.text = "Account Credit"
        }

        mBinding.tvKitchenName.text =
            CommonMethods.getKitchenNameByTerminalID(terminalId = order.terminal_name ?: "")

        mBinding.tvSubTotal.text = "$" + String.format("%.2f", order.subtotal)
        mBinding.tvTipAmount.text = "$" + String.format("%.2f", order.tip ?: 0.0)
        mBinding.tvDiscountAmount.text =
            "$" + String.format("%.2f", order.points_discount ?: 0.0)
        mBinding.tvTax.text = "$" + String.format("%.2f", order.tax)
        mBinding.tvTotal.text = "$" + String.format(
            "%.2f",
            ((order.subtotal + order.tax + (order.tip ?: 0.0)) - (order.points_discount ?: 0.0))
        )
    }
}