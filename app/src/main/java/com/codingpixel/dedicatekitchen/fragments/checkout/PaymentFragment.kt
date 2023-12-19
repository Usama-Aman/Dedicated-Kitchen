package com.codingpixel.dedicatekitchen.fragments.checkout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.account.PricingPlans
import com.codingpixel.dedicatekitchen.activities.checkout.CartActivity
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.adapters.PaymentMethodsAdapter
import com.codingpixel.dedicatekitchen.application.MyApplication
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.databinding.ActivitySelectPaymentMethodBinding
import com.codingpixel.dedicatekitchen.databinding.FragmentPaymentBinding
import com.codingpixel.dedicatekitchen.fragments.dialogs.CreditUnavailableDialog
import com.codingpixel.dedicatekitchen.fragments.dialogs.TopUpDialog
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.*
import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.models.GuestUserModel
import com.codingpixel.dedicatekitchen.models.OrderModel
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.Token
import java.util.HashMap


class PaymentFragment : BaseFragment(), ItemClickListener, SessionListener,
    SwipeRefreshLayout.OnRefreshListener, PaymentMethodListener {

    private lateinit var mBinding: FragmentPaymentBinding

    private lateinit var viewModel: UserViewModel
    private var selectedKitchen: String = ""
    private var selectedTime: Int = 0
    private lateinit var addressList: List<String>
    private var totalBill: String = ""
    private var orderNotes: String = ""
    private var tableType: Int = 0
    private var saleType: Int = 0
    private var flag: Boolean = false
    private var isDelivery: Boolean = false
    private var tableTransactionId: String = ""
    private var paymentMethodCode: Int = 0
    private var accountBalance: String = ""
    private var orderArray = ArrayList<OrderModel>()
    private var selectedCardStripeId: String = ""
    private var selectedCardId: Int = 0
    private val secondActivityRequestCode = 0
    private var isCardSelected: Boolean = false
    private var last4Digits: String = ""
    private var userName: String = ""
    private var phoneNumber: String = ""
    private var orderDateTime: String = ""
    private var orderType: String = ""
    private var expMonth: String = ""
    private var expYear: String = ""
    private var selectedMonth: Int = 0
    private var formattedMonth: String = ""
    private var selectedYear: Int = 0
    private var dateFlag: Boolean = false
    private var guestUser = GuestUserModel()
    private var lastChar: String = ""

    private lateinit var adapter: PaymentMethodsAdapter
    private val paymentMethods = ArrayList<CardModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderArray = it.getSerializable(IntentParams.ORDERITEMS) as ArrayList<OrderModel>
            selectedKitchen = it.getString(IntentParams.KITCHEN, "")
            selectedTime = it.getInt(IntentParams.TIME, 0)
            orderNotes = it.getString("notes", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideView(mBinding.ivSelectedTick)
        handleViews()
        getData()
        initClickListener()
        initApiResponseObserver()
        if (isUserLoggedIn())
            getCards()
        initAdapter()
        initWatchers()
        if (isUserLoggedIn()) {
            getAccountBalance()
        } else {
            hideView(mBinding.loadingIcon)
            hideView(mBinding.ivSelectedTick)
        }
    }

    private fun initWatchers() {
        mBinding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s?.toString()?.trim()?.isEmpty() == true) {

                } else {
                    mBinding.etPercentage.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        mBinding.etPercentage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.toString()?.trim()?.isEmpty() == true) {

                } else {
                    mBinding.etAmount.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun authenticateCustomer() {
        viewModel.authenticateCustomer(
            body = RequestBodyUtil.authenticateCustomer(
                sessionId = AppPreferenceManager.getValue(
                    "sessionId"
                ),
                email = getLoggedInUser()?.email ?: "",
                customerCode = getLoggedInUser()?.customer_code ?: ""
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun handleViews() {
        if (!isUserLoggedIn()) {
            paymentMethodCode = 0
            hideView(mBinding.clAvailableCredits)
            hideView(mBinding.tvGiveTip)
            hideView(mBinding.rgTipButtons)
            hideView(mBinding.etPercentage)
            hideView(mBinding.etAmount)
            hideView(mBinding.tvOr)
            hideView(mBinding.tvYourCards)
            hideView(mBinding.pullRefresh)
            hideView(mBinding.tvAddAnother)
//            mBinding.tvMyBag.text = "Personal Info"
            mBinding.tvAddToBag.text = "Finalize your order"
            showView(mBinding.clGuestCheckout)
        } else {
            showView(mBinding.clAvailableCredits)
            showView(mBinding.tvYourCards)
            showView(mBinding.pullRefresh)
            showView(mBinding.tvAddAnother)
            showView(mBinding.tvGiveTip)
            showView(mBinding.rgTipButtons)
//            mBinding.tvMyBag.text = "Payment Methods"
            mBinding.tvAddToBag.text = "Continue"
            hideView(mBinding.clGuestCheckout)

//            showView(mBinding.etPercentage)
//            showView(mBinding.etAmount)
//            showView(mBinding.tvOr)
        }

        mBinding.rbTipYes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showView(mBinding.etPercentage)
                showView(mBinding.etAmount)
                showView(mBinding.tvOr)
            }


        }

        mBinding.rbTipNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hideView(mBinding.etPercentage)
                hideView(mBinding.etAmount)
                hideView(mBinding.tvOr)
            }

        }
    }

    private fun getAccountBalance() {
        showView(mBinding.loadingIcon)
        mBinding.clAvailableCredits.isEnabled = false
        hideView(mBinding.tvAccountBalance)
        AppProgressDialog.showProgressDialog(context!!)
        viewModel.getAccountBalance(body = RequestBodyUtil.geAccountBalanceRequestBody())
    }

    private fun getData() {
        val addressWithoutQuote =
            AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION).replace("'", "")
        addressList = addressWithoutQuote.chunked(50)
//        orderArray = intent.getSerializableExtra(IntentParams.ORDERITEMS) as ArrayList<OrderModel>
//        selectedKitchen = intent.getStringExtra(IntentParams.KITCHEN)!!
//        selectedTime = intent.getIntExtra(IntentParams.TIME, 0)
        if (isUserLoggedIn()) {
            userName = getLoggedInUser()!!.fname + " " + getLoggedInUser()!!.lname
            phoneNumber = getLoggedInUser()!!.phone ?: ""
            orderType = AppPreferenceManager.getValue(IntentParams.ORDERTYPE)
        }
        orderDateTime = if (MyApplication.selectedOrderType != ApplicationEnum.MEAL_PREP) {
            AppPreferenceManager.getValue(IntentParams.instantTakeoutTime)
        } else {
            AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE) + " " + AppPreferenceManager.getValue(
                IntentParams.MEAL_PREP_TIME_SLOT_TO_SEND
            )
        }
//        orderNotes = intent.getStringExtra("note")!!
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.GET_CARD_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    paymentMethods.clear()
                    mBinding.pullRefresh.isRefreshing = false
                    val cards = JsonManager.getInstance().getCardList(it.resultObject)
//                    if (pageNo == 1)
//                        paymentMethodList.clear()
                    paymentMethods.addAll(cards)
                    if (paymentMethods.size == 0) {
                        showView(mBinding.tvNoCardView)
                        hideView(mBinding.pullRefresh)
                    } else {
                        hideView(mBinding.tvNoCardView)
                        showView(mBinding.pullRefresh)
                    }
//                    if (paymentMethods.size == 0)
//                        showView(mBinding.ivNoRecord)
//                    else
//                        hideView(mBinding.ivNoRecord)
                    adapter.notifyDataSetChanged()
                }
                ApplicationEnum.GET_GUEST_ORDER_NUMBER_SUCCESS, ApplicationEnum.GET_ORDER_NUMBER_SUCCESS -> {
                    val orderNumber =
                        JsonManager.getInstance().getString(it.resultObject, "order_number")
                    val order_id = JsonManager.getInstance().getString(it.resultObject, "order_id")
                    AppPreferenceManager.setIntValue(IntentParams.ORDERID, order_id.toInt())
                    if (MyApplication.selectedOrderType == ApplicationEnum.TAKE_OUT) {
                        tableType = 5
                        saleType = 2
                    } else {
                        if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_ORDER_TYPE) == IntentParams.TAKEOUT) {
                            tableType = 5
                            saleType = 2
                        } else {
                            tableType = 11
                            saleType = 3
                        }
                    }
                    viewModel.createTable(
                        RequestBodyUtil.createTableRequestBody(
                            sessionId = AppPreferenceManager.getValue(
                                "sessionId"
                            ),
                            orderNumber = orderNumber,
                            orderNotes = orderNotes,
                            tableType = tableType,
                            saleType = saleType
                        )
                    )
                }
//                ApplicationEnum.CARD_DELETED_SUCCESS -> {
//                    showShortToast(it.message)
//                    paymentMethods.removeAt(deletedCardId)
//                    adapter.notifyDataSetChanged()
//                }
                else -> {
                    mBinding.pullRefresh.isRefreshing = false
                    AppProgressDialog.dismissProgressDialog()
                    showView(mBinding.tvNoCardView)
                    hideView(mBinding.pullRefresh)
//                    if (paymentMethods.size == 0) {
//                        hideView(mBinding.cardsListingLayout)
//                        showView(mBinding.emptyLayout)
//                    } else {
//                        showView(mBinding.cardsListingLayout)
//                        hideView(mBinding.emptyLayout)
//                    }
                }
            }
        }

        viewModel.getApiStatus().observe(this) {
            when (it.enum) {

                ApplicationEnum.SESSION_EXPIRED -> {
                    if (activity is BaseActivity){
                        (activity as BaseActivity )?.initSessionViewModel(
                            sessionListener = this
                        )
                    }

                }

                ApplicationEnum.GET_ACCOUNT_BALANCE_SUCCESS -> {
                    mBinding.pullRefresh.isRefreshing = false
                    AppProgressDialog.dismissProgressDialog()
                    accountBalance = it.dataObject.getJSONObject("balance").getString("value")
                    mBinding.clAvailableCredits.isEnabled = true
                    hideView(mBinding.loadingIcon)
                    showView(mBinding.tvAccountBalance)
                    mBinding.tvAccountBalance.text = accountBalance
                    if (flag)
                        authenticateCustomer()
                }

                ApplicationEnum.AUTHENTICATE_CUSTOMER_SUCCESS -> {
                    //get customer summary
                    getCustomerSummaryApi()
                }

                ApplicationEnum.GET_CUSTOMER_SUMMARY_SUCCESS -> {
                    //create table api
                    createTableApi()

                }
                ApplicationEnum.CREATE_TABLE_SUCCESS -> {
                    tableTransactionId =
                        it.dataObject.getJSONObject("tableTransaction").getString("id")
                    //showShortToast(tableTransactionId)
                    if (!isUserLoggedIn()) {
                        userName = mBinding.etName.text.toString().trim()
                        phoneNumber = mBinding.etPhone.text.toString().trim()
                        orderType = "Takeout Instant"
                    }
                    addItemsToTansApi(tableTransactionId)
                }
                ApplicationEnum.ADD_ITEMS_TO_TRANS_SUCCESS -> {
                    totalBill =
                        it.dataObject.getJSONObject("tableTransaction").getString("paymentTotal")
                    if (paymentMethodCode == 2)
                        setCustomerId()
                    else {
                        AppProgressDialog.dismissProgressDialog()
                        startActivity(
                            Intent(
                                context!!,
                                CartActivity::class.java
                            ).apply {
                                putExtra(
                                    IntentParams.SHOWTAXBOX,
                                    true
                                )
                                putExtra(IntentParams.ORDERITEMS, orderArray)
                                putExtra(IntentParams.TABLE_TRANSACTION_ID, tableTransactionId)
                                putExtra("slot", selectedTime)
                                putExtra(IntentParams.PAYMENT_TYPE, paymentMethodCode)
                                putExtra(IntentParams.CARD_STRIPE_ID, selectedCardStripeId)
                                putExtra(IntentParams.CARD_ID, selectedCardId)
                                putExtra("guestUser", guestUser)
                                putExtra("notes", orderNotes)
                                putExtra("isTip", mBinding.rbTipYes.isChecked)
                                putExtra("tipAbsolute", mBinding.etAmount.text.toString().trim())
                                putExtra(
                                    "tipPercentage",
                                    mBinding.etPercentage.text.toString().trim()
                                )
                            }
                        )
                    }
                }

                ApplicationEnum.SET_CUSTOMER_ID_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
//                    startActivity(
//                        Intent(this@SelectPaymentMethodActivity, CartActivity::class.java).putExtra(
//                            IntentParams.SHOWTAXBOX,
//                            true
//                        ).putExtra(IntentParams.ORDERITEMS, orderArray)
//                            .putExtra(IntentParams.TABLE_TRANSACTION_ID, tableTransactionId)
//                            .putExtra("slot", selectedTime)
//                            .putExtra(IntentParams.PAYMENT_TYPE, paymentMethodCode)
//                            .putExtra(IntentParams.CARD_STRIPE_ID, selectedCardStripeId)
//                            .putExtra(IntentParams.CARD_ID, selectedCardId)
//                            .putExtra("guestUser", guestUser)
//                    )
                }
                ApplicationEnum.ERROR -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast("error")
                }
                else -> {

                }
            }
        }
    }

    private fun setCustomerId() {
        viewModel.setCustomerId(
            body = RequestBodyUtil.setCustomerId(
                sessionId = AppPreferenceManager.getValue(
                    "sessionId"
                ),
                tableTransaction = tableTransactionId,
                customerId = getLoggedInUser()!!.customer_id,
                transId = tableTransactionId
            )
        )
    }

//    private fun createTable() {
//        viewModel.createTable(
//            RequestBodyUtil.createTableRequestBody(
//                sessionId = AppPreferenceManager.getValue(
//                    "sessionId"
//                )
//            )
//        )
//    }

    private fun getCustomerSummaryApi() {
        viewModel.getCustomerSummary(body = RequestBodyUtil.getCustomerSummary(getLoggedInUser()!!.customer_code))
    }

    private fun addItemsToTansApi(tableTransactionId: String) {
        if (orderType == "MealPrep Delivery")
            isDelivery = true

        viewModel.addItemsToTrans(
            body = RequestBodyUtil.addItemsToTransaction(
                sessionId = AppPreferenceManager.getValue(
                    "sessionId"
                ),
                orderArray = orderArray,
                tableTransactionId = tableTransactionId,
                name = userName,
                phoneNumber = phoneNumber,
                orderDateTime = orderDateTime,
                orderType = orderType,
                addressList = addressList,
                isDelivery = isDelivery
            )
        )
    }

    private fun initClickListener() {
        mBinding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                val digits: Int = mBinding.etPhone.text.toString().length
                if (digits > 1) lastChar = mBinding.etPhone.text.toString().substring(digits - 1)
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val digits: Int = mBinding.etPhone.text.toString().length
                if (lastChar != "-") {
                    if (digits == 3 || digits == 7) {
                        mBinding.etPhone.append("-")
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        mBinding.clAvailableCredits.setOnClickListener {
            if (isUserLoggedIn()) {
                showView(mBinding.ivSelectedTick)
                paymentMethodCode = 2
                paymentMethods.forEach { it.isSelected = false }
                adapter.notifyDataSetChanged()
                AppProgressDialog.showProgressDialog(context = context!!)
                authenticateCustomer()
            } else
                showShortToast("Login To see your Account Balance!")
        }

        mBinding.etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.tvFullName.text = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        mBinding.tvMonthYear.setOnClickListener {
            CommonMethods.showCardExpiryDatePickerDialog(fragmentManager = childFragmentManager,
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                dateTImePickerListener = object : DateTImePickerListener {
                    @SuppressLint("SetTextI18n")
                    override fun pickedDateTime(
                        year: Int,
                        month: Int,
                        date: Int,
                        hour: Int,
                        minute: Int,
                        second: Int
                    ) {
                        dateFlag = true
                        val temp = String.format("%02d", month)
                        val i: Int = Integer.parseInt(temp)
                        selectedMonth = i
                        formattedMonth = temp
                        selectedYear = year
                        expMonth = i.toString()
                        expYear = year.toString()
                        mBinding.tvMonthYear.text =
                            String.format("%02d", month) + " / " + year.toString().substring(2)
                    }
                }
            )
        }

        mBinding.tvAddAnother.setOnClickListener {
            //add listener.
            CommonMethods.showAddPaymentMethodDialog(
                fragmentManager = childFragmentManager,
                clickListener = object :
                    AddedCardInterface {
                    override fun addedCardInfo(card: CardModel) {
                        paymentMethods.add(0, card)
                        adapter.notifyDataSetChanged()
                    }
                })
        }
        mBinding.tvAddToBag.setOnClickListener {
            //create table api
            if (isUserLoggedIn()) {
                if (paymentMethodCode == 0)
                    showShortToast("Please select a payment method")
                else if (paymentMethodCode == 2) {
                    checkAccountBalance(accountBalance)
//                    if (accountBalance == "0.00")
//                        showShortToast("You have no credits in your Wallet")
////                    else if (accountBalance < totalAmount)
////                        showShortToast("You do not have enough credits in your Wallet")
                } else {
                    if (paymentMethodCode == 1) {
                        CommonMethods.showTwoButtonsAlertDialog(fragmentManager = childFragmentManager,
                            title = "Alert!",
                            message = "Want to try purchasing our package? Purchase items through our package and earn points to get exciting rewards and discounts.",
                            leftButtonText = "Later",
                            rightButtonText = "Yes",
                            twoButtonsDialogListener = object : TwoButtonsDialogListener {
                                override fun leftButtonTapped() {
                                    if (isCardSelected) {
                                        AppProgressDialog.showProgressDialog(context!!)
                                        createTableApi()
                                    } else
                                        showShortToast("Please Select Card")
                                }

                                override fun rightButtonTapped() {
                                    flag = true
                                    startActivityForResult(
                                        Intent(
                                            context!!,
                                            PricingPlans::class.java
                                        ).putExtra(
                                            IntentParams.buyPackageAtCheckout, true
                                        ),
                                        secondActivityRequestCode
                                    )
                                }
                            })
                    }
                    //card payment
//                    if (isCardSelected) {
//                        AppProgressDialog.showProgressDialog(this)
//                        createTableApi()
//                    } else
//                        showShortToast("Please Select Card")
                }

            } else {
                if (checkDataEnteredForGuestUser()) {
                    getStripeTokenForGuestUser(
                        mBinding.etCardNumber.text.toString().trim(),
                        expMonth,
                        expYear,
                        mBinding.etCvc.text.toString().trim()
                    )
                }
//                if (paymentMethods.size == 0)
//                    showShortToast("Please Add Card")
//                else {
//                    //select card check.
//                    if (isCardSelected) {
//                        AppProgressDialog.showProgressDialog(this)
//                        viewModel.createTable(
//                            RequestBodyUtil.createTableRequestBody(
//                                sessionId = AppPreferenceManager.getValue(
//                                    "sessionId"
//                                )
//                            )
//                        )
//                    } else
//                        showShortToast("Please Select Card")
//                }
            }
        }

//        mBinding.cvContinueCheckOut.setOnClickListener {
//            CommonMethods.showOrderPlacedDialog(fragmentManager = supportFragmentManager)
//        }

        mBinding.pullRefresh.setOnRefreshListener(this)
    }

    private fun checkAccountBalance(balance: String) {
        if (balance.toFloat() < totalBill.toFloat()) {
            val popup = CreditUnavailableDialog.newInstance()
            popup.setListener(object : CreditUnavailableInterface {
                override fun purchasePackage() {
                    startActivity(
                        Intent(
                            context!!,
                            PricingPlans::class.java
                        ).putExtra(IntentParams.buyPackageAtCheckout, false)
                    )
                }

                override fun addAnotherItem() {
                    startActivity(
                        Intent(
                            context!!,
                            MainBottomBarActivity::class.java
                        ).apply {
                            if (isUserLoggedIn())
                                putExtra(IntentParams.ORDER_PLACED, false)
                        }
                    )
                    finishAffinity(activity!!)
                }

                override fun topUp() {
                    val topUpPopup =
                        TopUpDialog.newInstance(topUpAmount = (totalBill.toFloat() - balance.toFloat()).toString())
                    topUpPopup.show(childFragmentManager, TopUpDialog::class.java.canonicalName)
                }
            })
            popup.show(childFragmentManager, CreditUnavailableDialog::class.java.canonicalName)
        } else {
            startActivity(
                Intent(context!!, CartActivity::class.java).putExtra(
                    IntentParams.SHOWTAXBOX,
                    true
                ).putExtra(IntentParams.ORDERITEMS, orderArray)
                    .putExtra(IntentParams.TABLE_TRANSACTION_ID, tableTransactionId)
                    .putExtra("slot", selectedTime)
                    .putExtra(IntentParams.PAYMENT_TYPE, paymentMethodCode)
                    .putExtra(IntentParams.CARD_STRIPE_ID, selectedCardStripeId)
                    .putExtra(IntentParams.CARD_ID, selectedCardId)
                    .putExtra("guestUser", guestUser)
                    .putExtra("notes", orderNotes)
            )
        }
    }

    private fun getStripeTokenForGuestUser(
        cardNumber: String,
        expMonth: String,
        expYear: String,
        ccvString: String
    ) {
        AppProgressDialog.showProgressDialog(context!!)
        Stripe.apiKey = StripeParams.STRIPE_PK
        val thread = Thread(Runnable {
            try {
                val param2: MutableMap<String, Any> =
                    HashMap()
                param2["number"] = cardNumber
                param2["exp_month"] = expMonth
                param2["exp_year"] = expYear
                param2["cvc"] = ccvString
                val params: MutableMap<String, Any> =
                    HashMap()
                params["card"] = param2
                val token = Token.create(params)
                AppPreferenceManager.setValue(IntentParams.CARD_TOKEN, token.id)
                //AppProgressDialog.dismissProgressDialog()
                guestUser.apply {
                    name = mBinding.etName.text.toString().trim()
                    email = mBinding.etEmail.text.toString().trim()
                    phoneno = mBinding.etPhone.text.toString().trim()
                    card_number = mBinding.etCardNumber.text.toString().trim()
                    card_month = formattedMonth
                    card_year = selectedYear
                    cvc = mBinding.etCvc.text.toString().trim().toInt()
                }
                createTableApi()
            } catch (e: StripeException) {
                activity?.runOnUiThread {
                    e.message?.let { showShortToast(it) }
                }
                AppProgressDialog.dismissProgressDialog()
                e.printStackTrace()
            }
        })
        thread.start()
    }

    private fun createTableApi() {
        //before creating table api, we call order number api which gives table name.
        if (isUserLoggedIn())
            viewModel.getOrderNumberApi()
        else
            viewModel.getGuestOrderNumberApi()
    }

    private fun checkDataEnteredForGuestUser(): Boolean {
        if (isEmpty(mBinding.etName)) {
            showWarningToast("Enter Name")
            return false
        }
        if (isEmpty(mBinding.etEmail)) {
            showWarningToast("Enter Email")
            return false
        }
        if (!isEmpty(mBinding.etEmail)) {
            if (!isEmail(mBinding.etEmail)) {
                showWarningToast("Enter a Valid Email Address.")
                return false
            }
        }
        if (isEmpty(mBinding.etPhone)) {
            showWarningToast("Enter Phone Number")
            return false
        }
        if (isEmpty(mBinding.etCardNumber)) {
            showWarningToast("Enter Card Number")
            return false
        }
        if (mBinding.etCardNumber.text.toString().trim().length < 16) {
            showWarningToast("Enter Valid Card Number")
            return false
        } else {
            last4Digits = mBinding.etCardNumber.text.toString().trim()
                .substring(mBinding.etCardNumber.text.toString().trim().length - 4)
        }

        if (!dateFlag) {
            showWarningToast("Add Card Expiry Date")
            return false
        }
        return if (isEmpty(mBinding.etCvc)) {
            showWarningToast("Enter Card CVV")
            false
        } else {
            true
        }

    }

    private fun getCards() {
        AppProgressDialog.showProgressDialog(context!!)
        viewModel.getCards()
    }

    private fun initAdapter() {
        adapter = PaymentMethodsAdapter(
            paymentMethods = paymentMethods,
            paymentMethodListener = this,
            viewType = Constants.VIEW_TYPE_TOGGLE
        )
        mBinding.rvAddress.adapter = adapter
    }

    override fun itemClicked(position: Int) {
        val alreadySelectedIndex = paymentMethods.indexOfFirst { it.isSelected }
        if (alreadySelectedIndex != -1) {
            paymentMethods[alreadySelectedIndex].isSelected = false
            adapter.notifyItemChanged(alreadySelectedIndex)
        }
        if (alreadySelectedIndex != position) {
            paymentMethods[position].isSelected = true
            adapter.notifyItemChanged(position)
        }
    }

    override fun brandTapped(position: Int, title: String) {
        TODO("Not yet implemented")
    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteLocation(position: Int, addressId: Int) {

    }

    override fun onRefresh() {
        if (isUserLoggedIn()) {
            getAccountBalance()
            getCards()
        }

//        Handler().postDelayed({
//            mBinding.pullRefresh.isRefreshing = false
//        }, 1000)
    }

    override fun makeCardDefault(position: Int) {

    }

    override fun itemTapped(position: Int) {
        hideView(mBinding.ivSelectedTick)
        paymentMethodCode = 1
        isCardSelected = true
        val alreadySelectedIndex = paymentMethods.indexOfFirst { it.isSelected }
        if (alreadySelectedIndex != -1) {
            paymentMethods[alreadySelectedIndex].isSelected = false
            adapter.notifyItemChanged(alreadySelectedIndex)
        }
        if (alreadySelectedIndex != position) {
            paymentMethods[position].isSelected = true
            adapter.notifyItemChanged(position)
            selectedCardStripeId = paymentMethods[position].stripe_card_id ?: ""
            selectedCardId = paymentMethods[position].id
        }

        // showShortToast(paymentMethods[position].card_number)
    }

    override fun deleteTapped(position: Int, cardId: Int) {
        TODO("Not yet implemented")
    }

    override fun sessionRestored() {
        getAccountBalance()
    }

    override fun sessionRestorationFailed() {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        if (isUserLoggedIn())
            getAccountBalance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            flag = true
            showView(mBinding.ivSelectedTick)
            paymentMethodCode = 2
            paymentMethods.forEach { it.isSelected = false }
            adapter.notifyDataSetChanged()
            // getAccountBalance()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(
            orderItems: ArrayList<OrderModel>,
            kitchen: String,
            selectedTime: Int,
            notes: String
        ) =
            PaymentFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(IntentParams.ORDERITEMS, orderItems)
                    putSerializable(IntentParams.KITCHEN, kitchen)
                    putSerializable(IntentParams.TIME, selectedTime)
                    putSerializable("notes", notes)
                }
            }
    }

    override fun orderCompleted(position: Int) {

    }
}