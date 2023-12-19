package com.codingpixel.dedicatekitchen.activities.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.PricingAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.PricingPlanLayoutBinding
import com.codingpixel.dedicatekitchen.fragments.dialogs.SelectCardDialog
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.SelectCardInterface
import com.codingpixel.dedicatekitchen.interfaces.SelectedTimeSlotListener
import com.codingpixel.dedicatekitchen.interfaces.SubscriptionInterface
import com.codingpixel.dedicatekitchen.interfaces.TwoButtonsDialogListener
import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.models.PackageModel
import com.codingpixel.dedicatekitchen.models.TimeSlot
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.stripe.Stripe
import com.stripe.model.Charge
import com.stripe.model.Refund
import com.stripe.param.ChargeCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PricingPlans : BaseActivity(), SubscriptionInterface {
    private lateinit var mBinding: PricingPlanLayoutBinding
    private lateinit var adapter: PricingAdapter
    private lateinit var viewModel: UserViewModel
    private var amount: String = ""
    private var offerAmount: Double = 0.0
    private var stripe_card_id: String = ""
    private var stripeChargeId: String = ""
    private var package_pos_id: String = ""
    private var package_id: Int = 0
    private var boughtPackagePoints = ""

    private var card_id: Int = 0
    private var buyPackageAtCheckout: Boolean = false
    private var isAmountSelected: Boolean = false

    private var pricingData = ArrayList<PackageModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.pricing_plan_layout)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        changeStatusBarColor(statusBarColor = getColor(R.color.gray))
        buyPackageAtCheckout = intent.getBooleanExtra(IntentParams.buyPackageAtCheckout, false)
        initListeners()
        getPackagesApi()
        initAdapter()
        initApiResponseObserver()
    }

    private fun initListeners() {
        mBinding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun getUserPackagesApi() {
        viewModel.getUserPackages(getLoggedInUser()!!.id)
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) { it ->
            when (it.applicationEnum) {
                ApplicationEnum.GET_PACKAGE_SUCCESS -> {
                    // showShortToast(it.message)
                    val packages = JsonManager.getInstance().getPackages(it.resultObject)
                    for (i in packages.indices) {
//                        val tmp = ArrayList<TimeSlot>()
//                        //replace offer with price.
//                        var amount = packages[i].price.toFloat()
//                        while (amount < packages[i].end_price.toFloat()) {
//                            amount += 50f
//                            tmp.add(TimeSlot().apply {
//                                slot = amount.toString()
//                            })
//                        }
//                        packages[i].amountArray.addAll(tmp)
                        packages[i].text = packages[i].price
                    }

                    pricingData.addAll(packages)
                    pricingData.sortBy { it.price.toInt() }
                    pricingData.forEach {
                        it.initPackagePosId()
                    }
                    getUserPackagesApi()
                    adapter.notifyDataSetChanged()
                }
                ApplicationEnum.ADD_USER_PACKAGE_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    val inv = "${
                        System.currentTimeMillis() + (AppPreferenceManager.getUserFromSharedPreferences()?.id
                            ?: 0)
                    }"

                    viewModel.addLoyaltyPoints(
                        url = RequestBodyUtil.getAddDataCandyPointsURl(
                            cardId = getLoggedInUser()?.dc_card_id
                                ?: Constants.DEFAULT_CARD_ID,
                            amount = boughtPackagePoints,
                            inv = inv
                        ),
                        body = RequestBodyUtil.getAddDataCandyPointsRequestBody(
                            packageId = package_id,
                            packagePosId = package_pos_id,
                            amount = boughtPackagePoints
                        )
                    )
                    if (buyPackageAtCheckout) {
                        successPopup("You have successfully added a package.", this, false)
                        val resultIntent = Intent()
                        resultIntent.putExtra("added", "added")
                        setResult(Activity.RESULT_OK, resultIntent)
                        Handler(Looper.getMainLooper()).postDelayed({
                            // Your Code
                            finish()
                        }, 2000)

                    } else {
                        successPopup("Account Updated Successfully.", this, false)
                        Handler(Looper.getMainLooper()).postDelayed({
                            // Your Code
                            finish()
                        }, 2000)
                    }

                }

                ApplicationEnum.GET_USER_PACKAGE_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    val userPackages = JsonManager.getInstance().getUserPackage(it.resultObject)
                    val index = pricingData.indexOfFirst { it.id == userPackages!!.package_id }
                    if (index != -1) {
                        pricingData[index].amount = userPackages!!.amount
                        pricingData[index].selectedPackage = true
                        adapter.notifyDataSetChanged()
                    }
                }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                }
            }
        }

        viewModel.getApiStatus().observe(this) {
            when (it.enum) {
                ApplicationEnum.ADD_ACCOUNT_TRANSACTION_SUCCESS -> {
                    updateCustomerInfo(package_pos_id)
                }
                ApplicationEnum.UPDATE_USER_INFO_SUCCESS -> {
                    addUserPackage(package_id, card_id, amount, stripe_card_id)
                }
                else -> {
                    showShortToast("error")
                }
            }
        }
    }

    private fun addUserPackage(
        packageId: Int,
        cardId: Int,
        totalAmount: String,
        stripeCardId: String
    ) {
        viewModel.addUserPackage(packageId, cardId, totalAmount, stripeCardId)
    }

    private fun updateCustomerInfo(packageId: String) {
        viewModel.updateUserInfoVolante(body = RequestBodyUtil.updateUserInfo(packageId))
    }

    private fun getPackagesApi() {
        AppProgressDialog.showProgressDialog(this)
        viewModel.getPackages()
    }

    private fun initAdapter() {
        adapter = PricingAdapter(packages = pricingData, cartItemsListener = this)
        mBinding.rvAccountSections.adapter = adapter

        mBinding.rvAccountSections.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

//                togglesDots.forEach {
//                    it.isSelected = false
//                }
//                togglesDots[position].isSelected = true
//                toggleDotsAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun packageSelected(packageDetail: PackageModel) {
        // offerAmount = (packageDetail.offer.toDouble() / 100) * packageDetail.price.toDouble()
        //  totalAmount = packageDetail.price.toDouble() + offerAmount
        // totalAmount = packageDetail.price.toDouble()
        amount = packageDetail.text
        boughtPackagePoints = if (packageDetail.detail.contains("Receive")) {
            if (packageDetail.detail.split("Receive")[1].contains("points")) {
                packageDetail.detail.split("Receive")[1].split("points")[0].trim()
            } else {
                "0.0"
            }
        } else {
            "0.0"
        }
        package_pos_id = packageDetail.pos_package_id.toString()
        package_id = packageDetail.id
        val cardPopup = SelectCardDialog.newInstance()
        cardPopup.setListener(object : SelectCardInterface {
            override fun selectedCard(cardListing: ArrayList<CardModel>, position: Int) {
                stripe_card_id = cardListing[position].stripe_card_id ?: ""
                card_id = cardListing[position].id
                val alreadySelectedIndex = cardListing.indexOfFirst { it.isSelected }
                if (alreadySelectedIndex != -1) {
                    cardListing[alreadySelectedIndex].isSelected = false
                    adapter.notifyItemChanged(alreadySelectedIndex)
                }
                if (alreadySelectedIndex != position) {
                    cardListing[position].isSelected = true
                    adapter.notifyItemChanged(position)
                }
                CommonMethods.showTwoButtonsAlertDialog(fragmentManager = supportFragmentManager,
                    title = "Alert", message = "Are you sure you want to buy this plan?",
                    leftButtonText = "Cancel", rightButtonText = "Yes",
                    twoButtonsDialogListener = object : TwoButtonsDialogListener {
                        override fun leftButtonTapped() {
                        }

                        override fun rightButtonTapped() {
                            chargeStripe()
                        }
                    })
            }
        })
        cardPopup.show(supportFragmentManager, SelectCardDialog::class.java.canonicalName)
    }

    private fun chargeStripe() {
        AppProgressDialog.showProgressDialog(this)
        GlobalScope.launch(Dispatchers.IO) {
            chargeOnStripeApi()
        }
    }

    private suspend fun chargeOnStripeApi() {
        AppProgressDialog.showProgressDialog(this)
        Stripe.apiKey = StripeParams.STRIPE_SK
        val params: ChargeCreateParams = ChargeCreateParams.builder()
            .setAmount((amount.toFloat() * 100).toLong())
            .setCurrency(Constants.APP_CURRENCY)
            .setDescription("Buy Package")
            .setStatementDescriptor("Buy Package")
            .setCustomer(getLoggedInUser()?.stripe_id ?: "")
            .setSource(stripe_card_id)
            .build()
        val charge = Charge.create(params)

        withContext(Dispatchers.Main) {
            // Dismiss Progress Dialog

            if ((charge.status ?: "") == "succeeded") {
                stripeChargeId = charge.id
                if (charge.id != null && charge.id!!.isNotEmpty()) {
                    //   showShortToast(message = charge.id)
                    addAccountTransactiontoDbVolante()

                } else {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(message = charge.failureMessage)
                    hitRefund()
                }
            } else {
                runOnUiThread {
                    AppProgressDialog.dismissProgressDialog()
                    showWarningToast(message = charge.status ?: "Delivery Fee Charge Error")
                }
            }


        }
    }

    private fun addAccountTransactiontoDbVolante() {
        viewModel.addAccountTransactionToDb(body = RequestBodyUtil.addAccountTransaction(amount_ = amount))
    }

    private fun hitRefund() {
        GlobalScope.launch(Dispatchers.IO) {
            AppProgressDialog.showProgressDialog(this@PricingPlans)
            refundViaStripe()
        }
    }

    private suspend fun refundViaStripe() {
        Stripe.apiKey = StripeParams.STRIPE_SK
        val params: MutableMap<String, Any> = HashMap()
        params["charge"] = stripeChargeId
        val refund = Refund.create(params)
        withContext(Dispatchers.Main) {
            // Dismiss Progress Dialog
            stripeChargeId = ""
            showShortToast(message = refund.status ?: "Error")
        }
    }

    override fun amountSelected(position: Int, packageDetail: PackageModel) {
        val array: List<String> =
            packageDetail.payment_chunks.replace("[", "").replace("]", "").split(",")
                .map { it.trim() }

        for (element in array) {
            packageDetail.amountArray.add(TimeSlot().apply {
                slot = element
            })
        }

        CommonMethods.showOptionsDialog(
            fragmentManager = supportFragmentManager,
            list = packageDetail.amountArray,
            optionMenuDialogListener = object : SelectedTimeSlotListener {
                override fun timeSlot(timeSlot: TimeSlot) {
                    //  isAmountSelected = true
                    amount = timeSlot.slot
                    pricingData[position].text = timeSlot.slot
                    adapter.notifyDataSetChanged()
                }
            })
    }
}