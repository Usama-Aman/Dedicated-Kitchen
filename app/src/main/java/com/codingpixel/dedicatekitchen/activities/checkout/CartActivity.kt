package com.codingpixel.dedicatekitchen.activities.checkout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.activities.product.ProductDetailActivity
import com.codingpixel.dedicatekitchen.adapters.CartItemsAdapter
import com.codingpixel.dedicatekitchen.application.MyApplication
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Cart.Cart
import com.codingpixel.dedicatekitchen.database.Cart.CartExtraOption
import com.codingpixel.dedicatekitchen.databinding.ActivityCartBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.*
import com.codingpixel.dedicatekitchen.models.*
import com.codingpixel.dedicatekitchen.updates.generic_model.CPAttributes
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.google.gson.Gson
import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.Charge
import com.stripe.model.Refund
import com.stripe.param.ChargeCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CartActivity : BaseActivity(), CartItemsListener, SessionListener {

    private lateinit var mBinding: ActivityCartBinding
    private var orderArray = ArrayList<OrderModel>()
    private lateinit var cartAdapter: CartItemsAdapter
    private var tableTransactionId: String = ""
    private lateinit var viewModel: UserViewModel
    private var totalBill: Double = 0.0
    private var discountedTotalBill: Double = 0.0
    private var subTotalBill: String = ""
    private var grandTotalBill: String = ""
    private var tax_: String = ""
    private var paymentMethodCode: Int = 0
    private var stripe_card_id: String = ""
    private var selecedCardId: Int = 0
    private var cardBrand: String = ""
    private var stripeChargeId: String = ""
    private var selectedTime: Int = 0
    private var orderType: Int = 0
    private var guestUserInfo = GuestUserModel()

    private var pointsToApply: Float = 0.0f
    private var discountedAmount: Float = 0.0f

    private var totalPoints: Int = 0
    private var totalDCBalance: Float = 0f
    private var tipGiven: Boolean = false
    private var tipValue: Float = 0f
    private var totalTipToSend: Float = 0f
    private var tipIsPercentage: Boolean = false

    private var dataCandyTCN = ""

    private var showTaxBox = true

    private var globalInv = ""

    private var stripeDeliveryFeeChargeId: String = ""

    private var extraAmount = 0.0
    private var isExtraAdded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        changeStatusBarColor(statusBarColor = getColor(R.color.grayBgColor))
        setViews()
        initClickListener()
        initApiResponseObserver()
        initAdapter()
        getOrderType()
    }


    private fun getOrderType() {
        orderType = if (MyApplication.selectedOrderType == ApplicationEnum.TAKE_OUT) {
            5
        } else {
            if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_ORDER_TYPE) == IntentParams.TAKEOUT) {
                12
            } else {
                13
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initApiResponseObserver() {


        viewModel.getApiStatus().observe(this) {
            when (it.enum) {

                ApplicationEnum.ADD_ACCOUNT_TRANSACTION_SUCCESS -> {
                    stripePaymentFromLoggedInUser()
                }


                ApplicationEnum.ADD_ACCOUNT_TRANSACTION_ERROR -> {
                    errorDialogue("Transaction Error!", this)
                }


                ApplicationEnum.VOLANTE_DISCOUNT_SUCCESS -> {
                    var gotNewBill = false
                    Log.d("VOLANTE_DISCOUNT_SUCCESS", it.dataObject.toString())
                    try {
                        val data =
                            it.dataObject.getJSONObject("tableTransaction").getJSONObject("seats")
                                .getJSONObject("tableSeat").getJSONObject("displayTransaction")
                        if (data.has("grantTotal")) {
                            gotNewBill = true
                            grandTotalBill = data.getString("grantTotal")
                            Log.d("NewGrandTotal", grandTotalBill)
                        }

                        /*Usama Edit*/
//                        if (data.has("numberOfBillPrinted"))
//                            Toast.makeText(
//                                this@CartActivity,
//                                "Volante Discount Success -> ${data.getString("numberOfBillPrinted")}", Toast.LENGTH_LONG
//                            ).show()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (paymentMethodCode == Constants.PAYMENT_METHOD_WALLET) {
                        // Wallet
                        processPayment(
                            newBill = if (gotNewBill) grandTotalBill
                            else ""
                        )
                    } else {
                        // Credit Card
                        if (totalTipToSend > 0f) {
                            addOfflineGiftCard(
                                true,
                                stripeChargeId,
                                if (gotNewBill) String.format(
                                    "%.2f",
                                    (grandTotalBill.toFloat() + totalTipToSend)
                                )
                                else discountedTotalBill.toString(),
                                tableTransactionId,
                                cardBrand,
                                walletPayment = false
                            )
                        } else {
                            addOfflineGiftCard(
                                false,
                                stripeChargeId,
                                if (gotNewBill) grandTotalBill
                                else discountedTotalBill.toString(),
                                tableTransactionId,
                                cardBrand,
                                walletPayment = false
                            )
                        }

                    }
//                    processPayment()
                }
                ApplicationEnum.VOLANTE_DISCOUNT_ERROR -> {
                    hideLoading()
                    errorDialogue(
                        message = "Error in using discount on server.\n${it.error.localizedDescription}",
                        context = this@CartActivity
                    )
//                    if (isUserLoggedIn())
//                        saveOrderToServer()
//                    else
//                        saveOrderToServerForGuestUser()
                }

                ApplicationEnum.ACCUMULATION_DC_POINTS_SUCCESS -> {
                    Log.d("ACCUMULATION_DC_POINTS_SUCCESS", "Success")
//                    clearTable(orderPlaced = true)
                }

                ApplicationEnum.ACCUMULATION_DC_POINTS_ERROR -> {
                    Log.d("ACCUMULATION_DC_POINTS_SUCCESS", "Error")
//                    clearTable(orderPlaced = true)
                }


                ApplicationEnum.GET_ACCOUNT_BALANCE_SUCCESS -> {
                    // mBinding.pullRefresh.isRefreshing = false
                    val accountBalance = it.dataObject.getJSONObject("balance").getString("value")
                    Log.d("TotalBalance", accountBalance)
                    //  showShortToast(accountBalance)
                    totalPoints = if (accountBalance.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        accountBalance.toFloat().toInt()
                    } else {
                        0
                    }
                    mBinding.tvTotalPoints.text = "$totalPoints"
                }

                ApplicationEnum.COMIT_DC_POINTS_SUCCESS -> {
                    Log.d("CommitTransaction", "Successs")
                    if (isUserLoggedIn())
                        saveOrderToServer()
                    else
                        saveOrderToServerForGuestUser()
//                    hitDiscountOnVolante()
//                    AppProgressDialog.dismissProgressDialog()
                }

                ApplicationEnum.COMIT_DC_POINTS_ERROR -> {
                    commitDataCandyTransaction()
                    Log.d("CommitTransaction", "Error")

                }


                ApplicationEnum.REDEMPTION_DC_POINTS_SUCCESS -> {

                    val tcn = if (it.dataObject.has("tcn"))
                        it.dataObject.getString("tcn")
                    else
                        ""
                    dataCandyTCN = tcn
                    if (paymentMethodCode == Constants.PAYMENT_METHOD_WALLET) {
                        // Wallet Flow
                        if (totalTipToSend > 0) {
                            // Tip Given
                            addOfflineGiftCard(
                                true,
                                "",
                                "0",
                                tableTransactionId,
                                "On-Account-Tip"
                            )
                        } else {
                            if (discountedAmount > 0f)
                                hitDiscountOnVolante()
                            else
                                processPayment()
                        }
                    } else {
                        // Credit Card Flow
//                        if (totalTipToSend > 0) {
//                            // Tip Given
//                            addOfflineGiftCard(
//                                true,
//                                stripeChargeId,
//                                "0",
//                                tableTransactionId,
//                                "On-Account-Tip"
//                            )
//                        } else {
//                            if (discountedAmount > 0f)
//                                hitDiscountOnVolante()
//                            else
//                                addOfflineGiftCard(
//                                    false,
//                                    stripeChargeId,
//                                    grandTotalBill,
//                                    tableTransactionId,
//                                    cardBrand
//                                )
//                        }

//                        if (totalTipToSend > 0) {
//                            // Tip Given
//
//                        } else {
                        if (discountedAmount > 0f) {
                            hitDiscountOnVolante()
                        } else {
                            addOfflineGiftCard(
                                false,
                                stripeChargeId,
                                grandTotalBill,
                                tableTransactionId,
                                cardBrand
                            )
                        }
//                        }
                    }
                }

                ApplicationEnum.REDEMPTION_DC_POINTS_ERROR -> {
                    AppProgressDialog.dismissProgressDialog()
                    Log.d("RedemptionPoints", "Error")
                }

                ApplicationEnum.GET_LOYALTY_POINTS_SUCCESS -> {

                    hideLoading()

                    val dataObject = it.dataObject
                    var totalPoints = 0f
                    var totalDCCredit = 0f
                    if (dataObject.has("points")) {
                        totalPoints = dataObject.getString("points").toFloat()
                        mBinding.rbApplyRewardPoints.text = "Use Reward Points ($totalPoints Pts)"
                    }
                    if (dataObject.has("totalBalance")) {
                        totalDCCredit = dataObject.getString("totalBalance").toFloat()
                    }

                    if (dataObject.has("type")) {

                        when (dataObject.getString("type")) {


                            "get" -> {
                                this@CartActivity.totalPoints = totalPoints.toInt()
                                this@CartActivity.totalDCBalance = totalDCCredit
//                                mBinding.tvTotalPoints.text = "${this@CartActivity.totalPoints}"
                                mBinding.tvTotalPoints.text =
                                    "$${String.format("%.2f", this@CartActivity.totalDCBalance)}"

                            }

                            "apply" -> {
                                if (pointsToApply > totalPoints) {
                                    hideView(view = mBinding.tvCoupanDiscount)
                                    hideView(view = mBinding.tvCoupanDiscountTag)
                                    discountedAmount = 0f
                                    showWarningToast(message = "You don't have any enough points")
                                } else {
                                    showView(view = mBinding.tvCoupanDiscount)
                                    showView(view = mBinding.tvCoupanDiscountTag)
//                                    discountedAmount = (pointsToApply * 0.02).toFloat()
                                    mBinding.tvCoupanDiscount.text =
                                        "$${String.format("%.2f", discountedAmount)}"
                                }
                                Log.d("P-Discount", discountedAmount.toString())
                                discountedTotalBill = totalBill - discountedAmount
                                initTotalAmount()
                            }

                        }

                    }


                }

                ApplicationEnum.GET_LOYALTY_POINTS_ERROR -> {
                    AppProgressDialog.dismissProgressDialog()
                    showWarningToast(message = "Could not fetch your Rewarded Points")
                    hideView(view = mBinding.tvCoupanDiscount)
                    hideView(view = mBinding.tvCoupanDiscountTag)
                    discountedAmount = 0f
                    discountedTotalBill = totalBill
                    initTotalAmount()
                }

                ApplicationEnum.SESSION_EXPIRED -> {
                    initSessionViewModel(sessionListener = this)
                }

                ApplicationEnum.ADD_OFFLINE_GIFTCARD_SUCCESS -> {

                    if (paymentMethodCode == Constants.PAYMENT_METHOD_WALLET) {
                        // PAYING VIA WALLET
                        if (totalTipToSend > 0f) {
                            tableTransactionId =
                                when {
                                    it.dataObject.getJSONObject("tableTransaction")
                                        .has("id") -> it.dataObject.getJSONObject("tableTransaction")
                                        .getString("id")
                                    it.dataObject.getJSONObject("tableTransaction")
                                        .has("ID") -> it.dataObject.getJSONObject("tableTransaction")
                                        .getString("ID")
                                    else -> tableTransactionId
                                }
//                            processPayment()
                            if (discountedAmount > 0f) {
                                hitDiscountOnVolante()
                            } else {
                                processPayment()
                            }
                        } else {
                            if (discountedAmount > 0f)
                                commitDataCandyTransaction()
                            else
                                hitDiscountOnVolante()
//                            hitDiscountOnVolante()
                        }
                    } else {
                        // PAYING VIA CREDIT CARD
//                        try {
//                            tableTransactionId =
//                                when {
//                                    it.dataObject.getJSONObject("tableTransaction")
//                                        .has("id") -> it.dataObject.getJSONObject("tableTransaction")
//                                        .getString("id")
//                                    it.dataObject.getJSONObject("tableTransaction")
//                                        .has("ID") -> it.dataObject.getJSONObject("tableTransaction")
//                                        .getString("ID")
//                                    else -> tableTransactionId
//                                }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }

//                        if (totalTipToSend > 0f) {
//                            addOfflineGiftCard(
//                                false,
//                                stripeChargeId,
//                                discountedTotalBill.toString(),
//                                tableTransactionId,
//                                cardBrand
//                            )
//                        }else{
                        if (discountedAmount > 0f)
                            commitDataCandyTransaction()
                        else {
//                            if (totalTipToSend > 0f) {
                            if (isUserLoggedIn())
                                saveOrderToServer()
                            else
                                saveOrderToServerForGuestUser()
//                            } else
//                                hitDiscountOnVolante()
                        }
//                        }


//                        hitDiscountOnVolante()
                    }
                }

                ApplicationEnum.ADD_OFFLINE_GIFTCARD_SUCCESS_TIP -> {

                    if (paymentMethodCode == Constants.PAYMENT_METHOD_WALLET) {
                        // PAYING VIA WALLET
                        if (totalTipToSend > 0f) {
                            tableTransactionId =
                                when {
                                    it.dataObject.getJSONObject("tableTransaction")
                                        .has("id") -> it.dataObject.getJSONObject("tableTransaction")
                                        .getString("id")
                                    it.dataObject.getJSONObject("tableTransaction")
                                        .has("ID") -> it.dataObject.getJSONObject("tableTransaction")
                                        .getString("ID")
                                    else -> tableTransactionId
                                }
//                            processPayment()
                            if (discountedAmount > 0f) {
                                hitDiscountOnVolante()
                            } else {
                                processPayment()
                            }
                        } else {
                            if (discountedAmount > 0f)
                                commitDataCandyTransaction()
                            else
                                hitDiscountOnVolante()
//                            hitDiscountOnVolante()
                        }
                    } else {
                        // PAYING VIA CREDIT CARD
                        try {
                            tableTransactionId =
                                when {
                                    it.dataObject.getJSONObject("tableTransaction")
                                        .has("id") -> it.dataObject.getJSONObject("tableTransaction")
                                        .getString("id")
                                    it.dataObject.getJSONObject("tableTransaction")
                                        .has("ID") -> it.dataObject.getJSONObject("tableTransaction")
                                        .getString("ID")
                                    else -> tableTransactionId
                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (totalTipToSend > 0f) {
                            addOfflineGiftCard(
                                false,
                                stripeChargeId,
                                discountedTotalBill.toString(),
                                tableTransactionId,
                                cardBrand
                            )
                        } else {
                            if (discountedAmount > 0f)
                                commitDataCandyTransaction()
                            else
                                hitDiscountOnVolante()
                        }


//                        hitDiscountOnVolante()
                    }
                }

                ApplicationEnum.ADD_OFFLINE_GIFTCARD_ERROR -> {
                    if (it.dataObject.has("isTipFlow") && !it.dataObject.getBoolean("isTipFlow"))
                        hitRefund()
                    else
                        hideLoading()
                }

                ApplicationEnum.GET_TABLE_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    hideView(mBinding.loader)
                    Log.d("getTableData", it.dataObject.toString())
                    val data =
                        it.dataObject.getJSONObject("tableTransaction").getJSONObject("seats")
                            .getJSONObject("tableSeat").getJSONObject("displayTransaction")

                    //check on taxes, tax is optional. check on tax whether its string or json object.
                    val taxes: Any? = if (data.has("taxes")) {
                        when (data.get("taxes")) {
                            is String -> {
                                // data.getString("taxes")
                                tax_ = "0.0"
                            }
                            is JSONObject -> {
                                data.getJSONObject("taxes")
                            }
                            else -> {
                                null
                            }
                        }
                    } else
                        null

                    tax_ = when (taxes) {

                        null -> {
                            "9"
                        }

                        is String -> {
                            "0"
                        }
                        is JSONObject -> {
                            it.dataObject.getJSONObject("tableTransaction")
                                .getJSONObject("seats")
                                .getJSONObject("tableSeat").getJSONObject("displayTransaction")
                                .getJSONObject("taxes").getJSONObject("tax").getString("amount")
                        }

                        else -> "0"
                    }

                    Log.d("P-Tax", tax_)
                    try {
                        if (tax_.isNotEmpty() && CommonMethods.isNumber(value = tax_))
                            mBinding.tvTaxFeeTagAmount.text =
                                "$${String.format("%.2f", tax_.toFloat())}"
                        else
                            mBinding.tvTaxFeeTagAmount.text = "$$tax_"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mBinding.tvTaxFeeTagAmount.text = "$$tax_"
                    }
                    //convert sub total bill to double first
                    val subTotalString = if (data.has("subtotal"))
                        data.getString("subtotal")
                    else
                        ""
                    grandTotalBill = if (data.has("grantTotal"))
                        data.getString("grantTotal")
                    else
                        ""
                    if (subTotalString.isNotEmpty() && subTotalString.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        subTotalBill = subTotalString
                    }
                    Log.d("P-Subtotal", subTotalBill)
                    val testing = subTotalBill.toDouble()
                    //  mBinding.tvSubTotalAmount.text = "$$subTotalBill"
                    mBinding.tvSubTotalAmount.text = "$${String.format("%.2f", testing)}"
                    if (tipGiven) {
                        showView(view = mBinding.tvTipTag)
                        showView(view = mBinding.tvTipAmount)
                        if (tipIsPercentage) {
                            totalTipToSend = (testing * (tipValue / 100)).toFloat()
                            mBinding.tvTipAmount.text =
                                "$${String.format("%.2f", totalTipToSend)}"
                        } else {
                            totalTipToSend = tipValue
                            mBinding.tvTipAmount.text = "$${String.format("%.2f", tipValue)}"
                        }
                    } else {
                        totalTipToSend = 0f
                        hideView(view = mBinding.tvTipTag)
                        hideView(view = mBinding.tvTipAmount)
                    }
                    Log.d("P-Tip", totalTipToSend.toString())
                    discountedTotalBill = subTotalBill.toDouble()
                    totalBill = totalTipToSend + tax_.toDouble() + subTotalBill.toDouble()
                    Log.d("P-TipTaxSubTotal", totalBill.toString())
                    discountedTotalBill = totalBill
                    initTotalAmount()
                }

                ApplicationEnum.WALLET_PAYMENT_SUCCESS -> {
                    if (it.dataObject.has("accountPayment")) {
                        try {
                            val accountPaymentJsonObject =
                                it.dataObject.getJSONObject("accountPayment")
                            if (accountPaymentJsonObject.has("transId")) {
                                val tableTransIdJsonObject =
                                    accountPaymentJsonObject.getJSONObject("transId")
                                if (tableTransIdJsonObject.has("value")) {
                                    tableTransactionId = tableTransIdJsonObject.getString("value")
                                    Log.d("TableTransactionId", tableTransactionId)
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (discountedAmount > 0f) {
                        commitDataCandyTransaction()
                    } else {
//                        hitDiscountOnVolante()
                        if (isUserLoggedIn())
                            saveOrderToServer()
                        else
                            saveOrderToServerForGuestUser()
                    }
                }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                }
            }
        }

        viewModel.getDbApiStatus().observe(this)
        {
            when (it.applicationEnum) {
                ApplicationEnum.SAVE_ORDER_IN_DB_SUCCESS,
                ApplicationEnum.SAVE_ORDER_IN_DB_GUEST_SUCCESS -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        if (isUserLoggedIn() && paymentMethodCode != 2) {
                            // Accumulate Points only for Logged In User AND
                            // Payment Method is Credti Cart
                            hitPointsAccumulationApi()
                        }
                        clearTable(orderPlaced = true)
                    }
                }

                ApplicationEnum.SAVE_ORDER_IN_DB_ERROR,
                ApplicationEnum.SAVE_ORDER_IN_DB_GUEST_ERROR -> {
                    AppProgressDialog.dismissProgressDialog()
                    showWarningToast(message = it.message)
                }
//            ApplicationEnum.SAVE_ORDER_IN_DB_GUEST_SUCCESS -> {
//                Fst(it.message)
//            }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                }
            }
        }
    }

    private fun commitDataCandyTransaction() {
        viewModel.commitRedeemTransaction(
            url = RequestBodyUtil.getComitDataCandyPointsURl(
                cardId = getLoggedInUser()?.dc_card_id ?: Constants.DEFAULT_CARD_ID,
                tcr = dataCandyTCN
            )
        )
    }

    override fun onBackPressed() {
        if (showTaxBox) {
            finish()
            overridePendingTransition(0, 0)
        } else
            finish()
    }

    override fun onResume() {
        super.onResume()
        if (isUserLoggedIn() && showTaxBox)
            getTotalPoints()
    }

    private fun getTotalPoints() {
        viewModel.getLoyaltyPoints(
            type = "get",
            url = RequestBodyUtil.getDataCandyLoyaltyPointsURl(
                cardId = getLoggedInUser()?.dc_card_id ?: Constants.DEFAULT_CARD_ID
            )
        )
    }

    private fun initTotalAmount() {
        mBinding.tvTotalAmount.text = "$${String.format("%.2f", discountedTotalBill)}"
    }

    private fun hitRedeemPointsApi() {
        if (discountedAmount > 0.0f) {
            // Hit Discount first
            globalInv =
                "${
                    System.currentTimeMillis() + (AppPreferenceManager.getUserFromSharedPreferences()?.id
                        ?: 0)
                }"
            viewModel.redeemLoyaltyPoints(
                url = RequestBodyUtil.getRedeemDataCandyPointsURl(
                    cardId = getLoggedInUser()?.dc_card_id ?: Constants.DEFAULT_CARD_ID,
                    amount = discountedAmount.toString(),
                    inv = globalInv
                )
            )
        } else {

            if (totalTipToSend > 0f) {
                addOfflineGiftCard(
                    true,
                    stripeChargeId,
                    discountedTotalBill.toString(),
                    tableTransactionId,
                    cardBrand
                )
            } else {
                addOfflineGiftCard(
                    false,
                    stripeChargeId,
                    discountedTotalBill.toString(),
                    tableTransactionId,
                    cardBrand
                )
            }

        }
    }


    private fun hitRedeemPointsApiFromWalletPayment() {
        if (discountedAmount > 0.0f) {
            // Hit Discount first
            globalInv =
                "${
                    System.currentTimeMillis() + (AppPreferenceManager.getUserFromSharedPreferences()?.id
                        ?: 0)
                }"
            viewModel.redeemLoyaltyPoints(
                url = RequestBodyUtil.getRedeemDataCandyPointsURl(
                    cardId = getLoggedInUser()?.dc_card_id ?: Constants.DEFAULT_CARD_ID,
                    amount = discountedAmount.toString(),
                    inv = globalInv
                )
            )
        } else {
            if (totalTipToSend > 0f) {
                // Tip Given using Wallet
                addOfflineGiftCard(
                    true,
                    "",
                    "0",
                    tableTransactionId,
                    "On-Account-Tip"
                )
            } else
                processPayment()
        }
    }


    private fun hitPointsAccumulationApi() {
        var totalPointsToAccumulate = 0f
        for (item in orderArray) {
            val itemPrice =
                if (item.price.isNotEmpty() && CommonMethods.isNumber(value = item.price))
                    item.price.toFloat()
                else
                    1f
            val itemQuantity =
                if (item.quantity.isNotEmpty() && CommonMethods.isNumber(value = item.quantity))
                    item.quantity.toFloat()
                else
                    1f
            totalPointsToAccumulate += itemPrice * itemQuantity
        }

        if (globalInv.isEmpty())
            globalInv =
                "${
                    System.currentTimeMillis() + (AppPreferenceManager.getUserFromSharedPreferences()?.id
                        ?: 0)
                }"

        viewModel.addLoyaltyPoints(
            url = RequestBodyUtil.getAddDataCandyPointsURl(
                cardId = getLoggedInUser()?.dc_card_id
                    ?: Constants.DEFAULT_CARD_ID,
                amount = subTotalBill,
                RAM = if (discountedAmount > 0)
                    discountedAmount.toString()
                else
                    "0",
                inv = globalInv
            ),
            body = RequestBodyUtil.getAddDataCandyPointsCheckOutRequestBody(
                orderItems = orderArray
            )
        )
    }

    private suspend fun clearTable(orderPlaced: Boolean = false) {
        if (!orderPlaced)
            AppProgressDialog.showProgressDialog(context = this@CartActivity)
        val db = AppDatabase(this@CartActivity)
        db.cartExtraOptionDao().clearTable()
        db.cartDao().clearTable()
        AppPreferenceManager.setValue(
            PrefFlags.CURRENT_ITEMS_KITCHEN_ID,
            ""
        )

//        if (discountedAmount > 0.0f && orderPlaced) {
//            hitRedeemPointsApi()
//        }
        withContext(Dispatchers.Main) {
            AppProgressDialog.dismissProgressDialog()
            CommonMethods.showOrderPlacedDialog(fragmentManager = supportFragmentManager,
                orderPlacedListener = object : OrderPlacedListener {
                    override fun orderPlaced() {
                        startActivity(
                            Intent(
                                this@CartActivity,
                                MainBottomBarActivity::class.java
                            ).apply {
                                if (isUserLoggedIn())
                                    putExtra(IntentParams.ORDER_PLACED, true)
                            }
                        )
                        finishAffinity()
                    }
                })
        }
    }


    private fun saveOrderToServerForGuestUser() {
        val orderToDb = OrderToDbModel()
        orderToDb.apply {
            table_trans_id = tableTransactionId
            orderArray.forEachIndexed { index, orderModel ->
                items_data.add(ItemData().apply {
                    item_id = orderModel.item_id
                    quantity = orderModel.quantity
                    price = orderModel.price
                    note = orderModel.itemNote
                    //here we need notes
                    //  category_id = orderModel.category_id // This Category Id is Empty
//                    category_id = "909098"// This Category Id is Empty
                    category_id = orderModel.category_id
                    total_amount = orderModel.total_amount
                    name = orderModel.name
                    options.clear()
                    options.addAll(orderModel.options)
                    img = orderModel.img
                })
            }
            //items_data.addAll(orderArray)
        }

        viewModel.saveOrderToServerForGuestUser(SubmitOrderRequestModel().apply {
            is_group = MyApplication.groupName
            order_type = orderType
            terminal_pickup_time = selectedTime.toString()
//         //   payment_method = paymentMethodCode
//            card_id = if (paymentMethodCode != 2)
//                selecedCardId
//            else
//                0
            stripe_charge_id = stripeChargeId
            payment_status = "complete"
            pos_table_trans_id = tableTransactionId
            pos_table_trans_api_params = Gson().toJson(orderToDb)
            //pos_table_trans_api_params = orderToDb
            pos_order_table_trans_id = tableTransactionId
            pos_order_payment_auth_id = tableTransactionId
            subtotal = subTotalBill

            tax = tax_
            tip = if (tipGiven) totalTipToSend else 0f
            points_discount = discountedAmount
            terminal_name =
                AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId ?: ""
            order_id = AppPreferenceManager.getIntValue(IntentParams.ORDERID)
            name = guestUserInfo.name
            phoneno = guestUserInfo.phoneno
            email = guestUserInfo.email
            card_number = guestUserInfo.card_number
            card_month = guestUserInfo.card_month
            card_year = guestUserInfo.card_year
            cvc = guestUserInfo.cvc
            delivery_address = AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION)
            pickup_date_time = if (order_type == 5) {
                AppPreferenceManager.getValue(IntentParams.instantTakeoutTime)
            } else {
                AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE) + " " + AppPreferenceManager.getValue(
                    IntentParams.MEAL_PREP_TIME_SLOT_TO_SEND
                )
            }
            total_amount = discountedTotalBill.toString()
        })
    }

    private fun saveOrderToServer() {
        val orderToDb = OrderToDbModel()
        orderToDb.apply {
            table_trans_id = tableTransactionId
            orderArray.forEachIndexed { index, orderModel ->
                items_data.add(ItemData().apply {
                    item_id = orderModel.item_id
                    quantity = orderModel.quantity
                    price = orderModel.price
                    //  category_id = orderModel.category_id // This Category Id is Empty
//                    category_id = "909098" // This Category Id is Empty
                    category_id = orderModel.category_id // This Category Id is Empty
                    total_amount = orderModel.total_amount
                    name = orderModel.name
                    options.clear()
                    options.addAll(orderModel.options)
                    img = orderModel.img
                })
            }
            //items_data.addAll(orderArray)
        }

        viewModel.saveOrderToServer(SubmitOrderRequestModel().apply {
            is_group = MyApplication.groupName
            order_type = orderType
            terminal_pickup_time = selectedTime.toString()
            payment_method = paymentMethodCode
            card_id = if (paymentMethodCode != 2)
                selecedCardId
            else
                0
            stripe_charge_id = stripeChargeId
            payment_status = "complete"
            pos_table_trans_id = tableTransactionId
            terminal_name =
                AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId ?: ""
            pos_table_trans_api_params = Gson().toJson(orderToDb)
            //pos_table_trans_api_params = orderToDb
            pos_order_table_trans_id = tableTransactionId
            pos_order_payment_auth_id = tableTransactionId
            subtotal = subTotalBill
            tax = tax_
            tip = if (tipGiven) totalTipToSend else 0f
            points_discount = discountedAmount
            total_amount = discountedTotalBill.toString()
            order_id = AppPreferenceManager.getIntValue(IntentParams.ORDERID)
            delivery_address = AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION)
            pickup_date_time = if (order_type == 5) {
                AppPreferenceManager.getValue(IntentParams.instantTakeoutTime)
            } else {
                AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE) + " " + AppPreferenceManager.getValue(
                    IntentParams.MEAL_PREP_TIME_SLOT_TO_SEND
                )
            }
        })
    }

    private fun toggleTip() {
        val hasTip = intent?.getBooleanExtra("isTip", false) ?: false
        if (hasTip) {
            tipGiven = true
//
            showView(view = mBinding.tvTipTag)
            showView(view = mBinding.tvTipAmount)

            val tipAbsolute = intent?.getStringExtra("tipAbsolute") ?: ""
            val tipPercentage = intent?.getStringExtra("tipPercentage") ?: ""

            if (tipAbsolute.isNotEmpty()) {
                tipIsPercentage = false
                tipValue = tipAbsolute.toFloat()
            }

            if (tipPercentage.isNotEmpty()) {
                tipIsPercentage = true
                tipValue = tipPercentage.toFloat()
            }

        } else {
            tipGiven = false
            hideView(view = mBinding.tvTipTag)
            hideView(view = mBinding.tvTipAmount)
        }
    }

    private fun toggleTabs(state: Boolean) {
        if (state) {
            showView(view = mBinding.tvPayment)
            showView(view = mBinding.ivPaymentIcon)
            showView(view = mBinding.tvCheckout)
            showView(view = mBinding.ivCheckoutIcon)
            showView(view = mBinding.middleSep)
        } else {
            hideView(view = mBinding.tvPayment)
            hideView(view = mBinding.ivPaymentIcon)
            hideView(view = mBinding.tvCheckout)
            hideView(view = mBinding.ivCheckoutIcon)
            hideView(view = mBinding.middleSep)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViews() {
        showTaxBox =
            if (intent != null) intent.getBooleanExtra(IntentParams.SHOWTAXBOX, false) else true

        if (showTaxBox) {
            hideView(mBinding.ivClearCart)
            toggleTip()
            toggleTabs(state = true)
        } else {
            showView(mBinding.ivClearCart)
            toggleTabs(state = false)
        }

        AppProgressDialog.showProgressDialog(context = this@CartActivity)
        GlobalScope.launch(Dispatchers.IO) {
            getCartItems()
        }
//        orderArray =
//            if (intent.hasExtra(IntentParams.ORDERITEMS)) intent.getSerializableExtra(IntentParams.ORDERITEMS) as ArrayList<OrderModel>
//            else ArrayList()

    }

    @SuppressLint("SetTextI18n")
    private suspend fun getCartItems() {
        val db = AppDatabase(this@CartActivity)
//        val data = db.cartDao().getAllWithOUtTerminal()
        val data = db.cartDao().getAll()

//        data.forEach {
//            println("cartItems-> "+it)
//        }
        data.forEach {
//            if (it.img.isNullOrEmpty())
//                Log.d("wjbckbewjk", "null hai")
//            else
//                Log.d("wjbckbewjk", it.img)

            val extraData = db.cartExtraOptionDao().getAllById(it.pKey)
            val extras = ArrayList<ExtraItemOrderModel>()
            extraData.forEach { option ->
                extras.add(ExtraItemOrderModel().apply {
                    extra_option_id = option.id.toString()
                    name = option.name ?: ""
                    price = option.price ?: ""
                    quantity = option.quantity.toString()
                    category_id = option.categoryId ?: ""
                })
            }

            orderArray.add(OrderModel().apply {
                pKey = it.pKey
                item_id = it.id.toString()
                name = it.name ?: ""
                price = it.price ?: ""
                category_id = it.categoryId ?: ""
                quantity = it.quantity.toString()
                total_amount = it.totalPrice ?: ""
                img = it.img ?: ""
                options = extras
                notes = it.notes.toString()
            })
        }
        if (showTaxBox) {
            orderArray.forEach { it.isEditable = false }
        }
        orderArray.forEach {
            Log.d("CategoryID", it.category_id)

        }


        withContext(Dispatchers.Main) {
            AppProgressDialog.dismissProgressDialog()
            var basePrice = 0f
            var extraOptionsPrice = 0F
            for (i in 0 until orderArray.size) {
                extraOptionsPrice += orderArray[i].total_amount.toFloat()
//                price =
//                    ((orderArray[i].price.toFloat() * orderArray[i].quantity.toFloat()) + extraOptionsPrice)
                subTotalBill = extraOptionsPrice.toString()
            }

            if (orderArray.isEmpty()) {
                hideView(mBinding.cartContainer)
                showView(mBinding.emptyContainer)
                hideView(mBinding.ivClearCart)
            }

            if (showTaxBox) {
                paymentMethodCode =
                    if (intent != null) intent.getIntExtra(IntentParams.PAYMENT_TYPE, 0)
                    else 0
                mBinding.tvAddToBag.text = "Place Order"
                selectedTime = intent.getIntExtra("slot", 0)
                guestUserInfo = intent.getSerializableExtra("guestUser") as GuestUserModel
                tableTransactionId = intent.getStringExtra(IntentParams.TABLE_TRANSACTION_ID)!!
                stripe_card_id = intent.getStringExtra(IntentParams.CARD_STRIPE_ID) ?: ""
                selecedCardId = intent.getIntExtra(IntentParams.CARD_ID, 0)
                showView(mBinding.mainLayout)
                hideView(mBinding.cvContinue)
                getTableApi(tableTransactionId)
                for (i in 0 until orderArray.size) {
                    orderArray[i].viewType = "checkout"
                }
                orderArray.forEach { it.showProductNote = true }
                cartAdapter.notifyDataSetChanged()

            } else {
                // mBinding.cvCheckOut.text = "Continue to Checkout"
                hideView(mBinding.mainLayout)
                showView(mBinding.cvContinue)
                for (i in 0 until orderArray.size) {
                    orderArray[i].viewType = "cart"
                }
                orderArray.forEach { it.showProductNote = false }
                cartAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getTableApi(tableTransactionId: String) {
        AppProgressDialog.showProgressDialog(context = this@CartActivity)
        viewModel.getTable(
            body = RequestBodyUtil.getTable(
                AppPreferenceManager.getValue("sessionId"),
                tableTransactionId = tableTransactionId
            )
        )
    }

    private fun initClickListener() {
        mBinding.rbApplyRewardPoints.isEnabled = isUserLoggedIn()
        mBinding.rbApplyRewardPoints.setOnCheckedChangeListener { _, isChecked ->


            if (isChecked) {
                showView(view = mBinding.etCoupanCode)
                showView(view = mBinding.etCoupanCode)
                showView(view = mBinding.tvApply)

                showView(view = mBinding.tvCoupanDiscountTag)
                showView(view = mBinding.tvCoupanDiscount)

                showView(view = mBinding.tvTotalPoints)


            } else {
                hideView(view = mBinding.etCoupanCode)
                hideView(view = mBinding.etCoupanCode)
                hideView(view = mBinding.tvApply)

                hideView(view = mBinding.tvCoupanDiscountTag)
                hideView(view = mBinding.tvCoupanDiscount)

                mBinding.tvCoupanDiscount.text = "$0.0"

                hideView(view = mBinding.tvTotalPoints)

                if (discountedAmount != 0.0f) {
                    discountedAmount = 0.0f
                    mBinding.tvTaxFeeTagAmount.text = "$${String.format("%.2f", tax_.toFloat())}"
                    discountedTotalBill = totalBill - discountedAmount
                    initTotalAmount()
                }
            }
        }


        mBinding.tvApply.setOnClickListener {
            if (mBinding.etCoupanCode.text.toString().trim().isNotEmpty()) {

                discountedAmount = when {

                    mBinding.etCoupanCode.text.toString().trim().toFloat() > totalDCBalance -> {
                        showWarningToast(message = "You do not have enough balance")
                        0f
                    }
                    subTotalBill.toDouble() < mBinding.etCoupanCode.text.toString().trim().toFloat() -> {
                        showWarningToast(message = "Discount should be smaller than Sub Total")
                        0f
                    }
                    totalBill < mBinding.etCoupanCode.text.toString().trim().toFloat() -> {
                        showWarningToast(message = "Discount should be smaller than Total Bill")
                        0f
                    }
                    else -> {
                        Log.d("P-Discount", discountedAmount.toString())
                        mBinding.etCoupanCode.text.toString().trim().toFloat()
                    }
                }

                mBinding.tvCoupanDiscount.text = "$${String.format("%.2f", discountedAmount)}"
                val tax = (subTotalBill.toDouble() - mBinding.etCoupanCode.text.toString().toFloat()) * 0.05
                mBinding.tvTaxFeeTagAmount.text = "$${String.format("%.2f", tax.toFloat())}"
//                discountedTotalBill = totalBill - discountedAmount
                discountedTotalBill = subTotalBill.toDouble() - discountedAmount +tax

                initTotalAmount()

            } else {
                showWarningToast(message = "Enter points")
            }
        }

        mBinding.ivClearCart.setOnClickListener {
            CommonMethods.showTwoButtonsAlertDialog(fragmentManager = supportFragmentManager,
                title = "Are You Sure ?",
                message = "All items will be removed from bag.",
                leftButtonText = "No",
                rightButtonText = "Yes",
                twoButtonsDialogListener = object : TwoButtonsDialogListener {
                    override fun leftButtonTapped() {}

                    override fun rightButtonTapped() {
                        GlobalScope.launch(Dispatchers.IO) {
                            clearTable(orderPlaced = false)
                        }
                        finish()
                    }
                })
        }

        mBinding.ivGotoHome.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, MainBottomBarActivity::class.java))
        }
        mBinding.ivBackArrow.setOnClickListener {
            if (showTaxBox) {
                finish()
                overridePendingTransition(0, 0)
            } else
                finish()
        }

        mBinding.cvCheckOut.setOnClickListener {

            CommonMethods.showTwoButtonsAlertDialog(
                fragmentManager = supportFragmentManager,
                title = "Place Order",
                message = "Are you sure you want to check out with current items in the cart and send your order to the Dedicate Kitchen?",
                leftButtonText = "No",
                rightButtonText = "Yes, Place Order",
                twoButtonsDialogListener = object : TwoButtonsDialogListener {
                    override fun leftButtonTapped() {}

                    override fun rightButtonTapped() {
                        if (orderType == 5) {
                            // Take Out Order
                            showLoading()
                            checkIfKitchenOpen(kitchenTimeListener = object : KitchenTimeListener {
                                override fun isKitchenOpen(
                                    isKitchenOpen: Boolean,
                                    canadianTime: String
                                ) {
                                    Log.d("Canadian Time", canadianTime)
                                    if (isKitchenOpen)
                                        startOrderProcess()
                                    else {
                                        hideLoading()
                                        errorDialogue(
                                            message = Constants.KITCHEN_CLOSED_MESSAGE,
                                            context = this@CartActivity,
                                            title = "Alert"
                                        )
                                    }
                                }

                                override fun isPastNine(
                                    isPastNine: Boolean,
                                    canadianTime: String
                                ) {
                                }

                                override fun isError() {
                                    hideLoading()
                                    showWarningToast(message = Constants.INTERNET_ERROR_MESSAGE)
                                }
                            })

                        } else {
                            // Meal Prep Order (Pickup or Delivery)
                            startOrderProcess()
                        }
                    }
                }
            )


        }

        mBinding.cvContinue.setOnClickListener {
            if (orderArray.isNotEmpty()) {
                if (AppPreferenceManager.getValue(IntentParams.ORDERTYPE) == ApplicationEnum.TAKE_OUT.toString()) {
                    if (CommonMethods.isKitchenClosed()) {
                        errorDialogue(
                            message = Constants.KITCHEN_CLOSED_MESSAGE,
                            context = this@CartActivity,
                            title = "Alert"
                        )
                    } else
                        goNext()
                } else
                    goNext()
            } else
                showWarningToast(message = "Cart is Empty")
        }
    }

    private fun startOrderProcess() {
        if (paymentMethodCode == Constants.PAYMENT_METHOD_WALLET) {
            // Pay from Wallet
            showLoading()
            hitRedeemPointsApiFromWalletPayment()
        } else {
            // Pay from Credit Card
            if (discountedTotalBill < Constants.MINIMUM_CREDIT_DOLLAR_AMOUNT) {
                extraAmount = Constants.MINIMUM_CREDIT_DOLLAR_AMOUNT - discountedTotalBill

                discountedTotalBill += extraAmount
                isExtraAdded = true

            }
            if (discountedTotalBill < Constants.MINIMUM_CREDIT_DOLLAR_AMOUNT) {
                errorDialogue(
                    message = "Minimum amount for credit card payment is $${Constants.MINIMUM_CREDIT_DOLLAR_AMOUNT}. Please do add add some other items or choose different payment method.",
                    context = this@CartActivity
                )
            } else {
                showLoading()
                if (isUserLoggedIn()) {
                    if (isExtraAdded) {
                        addAccountTransactiontoDbVolante(extraAmount.toString())
                    } else {
                        stripePaymentFromLoggedInUser()
                    }

                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        stripePaymentFromGuestUser()
                    }
                }
            }
        }
    }

    private fun goNext() {
        startActivity(
            Intent(this@CartActivity, ShippingMethod::class.java).putExtra(
                IntentParams.ORDERITEMS,
                orderArray
            )
        )
    }

    private fun hitDiscountOnVolante() {
        if (discountedAmount > 0.0) {
            Log.d("DiscountAmount", discountedAmount.toString())
            Log.d("TotalBill", totalBill.toString())
//            if (discountedAmount >= totalBill.toFloat()) {
//                viewModel.discountOnVolane(
//                    body = RequestBodyUtil.getDiscountRequestBody(
//                        tableTransactionId = tableTransactionId,
//                        absolutePrice = subTotalBill
//                    )
//                )
//            } else {
//                viewModel.discountOnVolane(
//                    body = RequestBodyUtil.getDiscountRequestBody(
//                        tableTransactionId = tableTransactionId,
//                        absolutePrice = discountedAmount.toString()
//                    )
//                )
//            }
            viewModel.discountOnVolane(
                body = RequestBodyUtil.getDiscountRequestBody(
                    tableTransactionId = tableTransactionId,
                    absolutePrice = discountedAmount.toString()
                )
            )
        } else {
            if (isUserLoggedIn())
                saveOrderToServer()
            else
                saveOrderToServerForGuestUser()
        }
    }

    private suspend fun stripePaymentFromGuestUser() {

        Log.d("P-ChargedAmount", discountedTotalBill.toString())

        Stripe.apiKey = StripeParams.STRIPE_SK
        val params = ChargeCreateParams.builder()
            .setAmount((discountedTotalBill * 100).toLong())
            .setCurrency(Constants.APP_CURRENCY)
            .setDescription("Order Charge")
            .setSource(AppPreferenceManager.getValue(IntentParams.CARD_TOKEN)) // Token is the Card ID
            .build()

        try {

            val charge = Charge.create(params)
            showLoading()
            withContext(Dispatchers.Main) {
                // Dismiss Progress Dialog

                if ((charge.status ?: "") == "succeeded") {
                    stripeChargeId = charge.id
                    if (charge.id != null && charge.id!!.isNotEmpty()) {
                        //showShortToast(message = charge.id)
                        cardBrand = charge.paymentMethodDetails.card.brand
                        addOfflineGiftCard(
                            false,
                            charge.id,
                            discountedTotalBill.toString(),
                            tableTransactionId,
                            cardBrand
                        )
                    } else {
                        showShortToast(message = charge.failureMessage)
                        hitRefund()
                    }
                } else {
                    runOnUiThread {
                        hideLoading()
                        showWarningToast(
                            message = charge?.failureMessage
                                ?: "Unable to charge from card, Try again"
                        )
                    }
                }


            }
        } catch (e: StripeException) {
            e.printStackTrace()
            runOnUiThread {
                hideLoading()
                showWarningToast(message = e.message ?: "Error")
            }
        }
    }

    private fun stripePaymentFromLoggedInUser() {
        GlobalScope.launch(Dispatchers.IO) {
            chargeOnStripe()
        }
    }

    private fun hitRefund() {
        // Handler().postDelayed({
        GlobalScope.launch(Dispatchers.IO) {
            showLoading()
            refundViaStripe()
        }
        //}, 2000)
    }

    private suspend fun refundViaStripe() {
        Stripe.apiKey = StripeParams.STRIPE_SK
        val params: MutableMap<String, Any> = HashMap()
        params["charge"] = stripeChargeId
        val refund = Refund.create(params)
        withContext(Dispatchers.Main) {
            // Dismiss Progress Dialog
            stripeChargeId = ""
            hideLoading()
            showShortToast(message = "${refund.status} : Amount Refunded")
        }
    }

    private suspend fun chargeOnStripe() {
//        AppProgressDialog.showProgressDialog(this@CartActivity)
        Stripe.apiKey = StripeParams.STRIPE_SK
        Log.d("P-ChargedAmount", ((totalBill - discountedAmount) + totalTipToSend).toString())
        Log.d("P-ChargedAmount", discountedTotalBill.toString())
        val params: ChargeCreateParams = ChargeCreateParams.builder()
            .setAmount((discountedTotalBill * 100).toLong())
            .setCurrency(Constants.APP_CURRENCY)
            .setDescription("DK Order")
            .setStatementDescriptor("DK Order")
            .setCustomer(getLoggedInUser()?.stripe_id ?: "")
            .setSource(stripe_card_id)
            .build()
        try {
            val charge = Charge.create(params)
            showLoading()
            withContext(Dispatchers.Main) {
                if ((charge.status ?: "") == "succeeded") {
                    stripeChargeId = charge.id
                    if (charge.id != null && charge.id!!.isNotEmpty()) {
                        //   showShortToast(message = charge.id)
                        cardBrand = charge.paymentMethodDetails.card.brand
                        hitRedeemPointsApi()
                    } else {
                        runOnUiThread {
                            showShortToast(message = charge.failureMessage)
                        }
                        hitRefund()
                    }
                } else {
                    runOnUiThread {
                        hideLoading()
                        showWarningToast(
                            message = charge?.failureMessage
                                ?: "Unable to charge from card, Try again"
                        )
                    }
                }


            }
        } catch (e: StripeException) {
            runOnUiThread {
                hideLoading()
                showWarningToast(message = e.message ?: "Error")
            }
        }

    }

    private fun addOfflineGiftCard(
        isTipFlow: Boolean = false,
        id: String,
        amount: String,
        tableTransactionId: String,
        cardBrand: String,
        walletPayment: Boolean = true
    ) {
        viewModel.addOfflineGiftCard(
            isTipFlow = isTipFlow,
            body = RequestBodyUtil.addOfflineGiftCardPayment(
                id, amount, tableTransactionId, cardBrand,
                if (tipGiven) String.format("%.2f", totalTipToSend) else "0.0"
            ),
            walletPayment = walletPayment
        )
    }

    private fun processPayment(newBill: String = "") {
        AppProgressDialog.showProgressDialog(this@CartActivity)
        Log.d("P-DiscountedBill", discountedTotalBill.toString())
        Log.d("P-TotalBill", totalBill.toString())
        viewModel.processPayment(
            body = RequestBodyUtil.processPayment(
                sessionId = AppPreferenceManager.getValue(
                    "sessionId"
                ), tableTransactionId = tableTransactionId,
                bill = if (newBill.isEmpty()) discountedTotalBill.toString()
                else newBill,
                tip = if (tipGiven) totalTipToSend.toString() else "0"
            )
        )
    }

    private fun toggleEmptyState() {
        if (orderArray.isEmpty()) {
            hideView(mBinding.cartContainer)
            showView(mBinding.emptyContainer)
            hideView(mBinding.ivClearCart)
        } else {
            showView(mBinding.cartContainer)
            hideView(mBinding.emptyContainer)
            showView(mBinding.ivClearCart)
        }
    }

    private fun initAdapter() {
        cartAdapter = CartItemsAdapter(products = orderArray, cartItemsListener = this)
        mBinding.rvCartItems.adapter = cartAdapter
    }

    override fun itemTapped(position: Int) {

    }

    override fun plusTapped(position: Int, pKey: Int) {
        val quantityInInt: Int = orderArray[position].quantity.toInt() + 1
        orderArray[position].quantity = quantityInInt.toString()
        cartAdapter.notifyItemChanged(position)
        updateQuantity(pKey, quantityInInt)
    }

    private suspend fun removeItemFromCart(position: Int, itemId: Int) {
        val db = AppDatabase(this@CartActivity)
        val cartItem = db.cartDao().get(itemId)
        val cartItemOptions = db.cartExtraOptionDao()
            .getAllById(if (cartItem != null) cartItem.pKey else 0)
        cartItemOptions.forEach { option ->
            db.cartExtraOptionDao().delete(
                CartExtraOption(
                    option.pKey,
                    option.id,
                    option.name,
                    option.price,
                    option.quantity,
                    option.categoryId,
                    option.cartOptionId
                )
            )
        }
        db.cartDao().delete(
            Cart(
                cartItem.pKey,
                cartItem.id,
                cartItem.name,
                cartItem.price,
                cartItem.categoryId,
                cartItem.quantity,
                cartItem.img,
                cartItem.totalPrice,
                cartItem.orderType,
                cartItem.notes
            )
        )

        withContext(Dispatchers.Main) {
            AppProgressDialog.dismissProgressDialog()
            orderArray.removeAt(position)
            cartAdapter.notifyDataSetChanged()
            toggleEmptyState()
        }
    }

    private suspend fun removeCartItem(position: Int, itemId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            AppProgressDialog.showProgressDialog(this@CartActivity)
            removeItemFromCart(position, itemId)
        }
    }

    private fun updateQuantity(pKay: Int, quantity: Int) {
        val db = AppDatabase(this@CartActivity)
        GlobalScope.launch(Dispatchers.IO) {
            val cartItem = db.cartDao().getItem(pKay)
            if (cartItem != null) {
                db.cartDao().update(
                    Cart(
                        cartItem.pKey,
                        cartItem.id,
                        cartItem.name,
                        cartItem.price,
                        cartItem.categoryId,
                        quantity,
                        cartItem.img,
                        cartItem.totalPrice,
                        cartItem.orderType,
                        cartItem.notes
                    )
                )
            }

        }
        makeVibrate()
    }

    override fun minusTapped(position: Int, pKey: Int) {
        if (orderArray[position].quantity > "1") {
            val quantityInInt: Int = orderArray[position].quantity.toInt() - 1
            // orderArray[position].quantity = orderArray[position].quantity - 1
            orderArray[position].quantity = quantityInInt.toString()
            cartAdapter.notifyItemChanged(position)
            updateQuantity(pKey, quantityInInt)
        } else {
            //delete from db
            GlobalScope.launch(Dispatchers.IO) {
                removeCartItem(position = position, itemId = orderArray[position].item_id.toInt())
            }
        }
    }

    override fun editTapped(position: Int, productDetail: OrderModel) {
//
//        val product = MenuItemModel()
//        val cpAttributes = CPAttributes()
//
//        product.apply {
//
//            image = image_base_url + productDetail.img
//            image_url = productDetail.img
//            ID = productDetail.item_id
//            terminalId = productDetail.item_id
//            attributes = cpAttributes.apply {
//                this.menuItemName = productDetail.
//
//            }
//            description = m.attributes.itemDescription
//            itemDescription = m.attributes.itemDescription
//            kitchen_id = m.kitchen_id
//            menuItemName = m.attributes.name
//            menu_category_id = m.menu_category_id
//            menu_id = m.menu_id
//            price = m.attributes.price
//            category_name = category.name
//            extraOptions = productDetail.allExtraOptions
//
//        }
//
//
//

        startActivity(
            Intent(
                this@CartActivity,
                ProductDetailActivity::class.java
            )
                .putExtra(IntentParams.PRODUCT_ID, productDetail.item_id)
                .putExtra(IntentParams.CATEGORY_ID, productDetail.category_id)
                .putExtra(IntentParams.CART_ITEM_P_KEY, productDetail.pKey)
                .putExtra(IntentParams.TAG, IntentParams.EDITORDER)
        )
        finish()
    }

    override fun sessionRestored() {
        getTableApi(tableTransactionId)
    }

    override fun sessionRestorationFailed() {}


    private suspend fun deductDeliveryFeeFromCard(defaultCardId: String) {
        Stripe.apiKey = StripeParams.STRIPE_SK
        val params: ChargeCreateParams = ChargeCreateParams.builder()
            .setAmount((7.00 * 100).toLong())
            .setCurrency(Constants.APP_CURRENCY)
            .setDescription("Delivery charge")
            .setStatementDescriptor("Delivery charge")
            .setCustomer(getLoggedInUser()?.stripe_id ?: "")
            .setSource(defaultCardId)
            .build()
        try {
            val charge = Charge.create(params)
            withContext(Dispatchers.Main) {
                // Dismiss Progress Dialog
                // The status of the payment is either succeeded, pending, or failed.
                if ((charge.status ?: "") == "succeeded") {
                    stripeDeliveryFeeChargeId = charge.id ?: ""
                    if ((charge.id ?: "").isNotEmpty()) {
                    } else {
                        showShortToast(message = charge.failureMessage)
                        hitDeliveryFeeRefund()
                    }
                } else {
                    runOnUiThread {
                        hideLoading()
                        showWarningToast(message = charge.status ?: "Delivery Fee Charge Error")
                    }
                }

            }
        } catch (e: StripeException) {
            runOnUiThread {
                hideLoading()
                showWarningToast(message = e.message ?: "Error")
            }
        }

    }


    private fun hitDeliveryFeeRefund() {
        if (stripeDeliveryFeeChargeId.isNotEmpty()) {
            GlobalScope.launch(Dispatchers.IO) {
                showLoading()
                refundDeliveryFeeViaStripe()
            }
        }
    }

    private suspend fun refundDeliveryFeeViaStripe() {
        Stripe.apiKey = StripeParams.STRIPE_SK
        val params: MutableMap<String, Any> = HashMap()
        params["charge"] = stripeDeliveryFeeChargeId
        val refund = Refund.create(params)
        withContext(Dispatchers.Main) {
            // Dismiss Progress Dialog
            stripeDeliveryFeeChargeId = ""
            hideLoading()
            showShortToast(message = "${refund.status} : Delivery Charges Refunded")
        }
    }


    private fun addAccountTransactiontoDbVolante(amount: String) {
        viewModel.addAccountTransactionToDb(body = RequestBodyUtil.addAccountTransaction(amount_ = amount))
    }
}
