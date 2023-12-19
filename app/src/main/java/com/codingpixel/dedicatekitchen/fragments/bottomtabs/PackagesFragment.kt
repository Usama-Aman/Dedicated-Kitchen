package com.codingpixel.dedicatekitchen.fragments.bottomtabs

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.account.PurchaseHistoryActivity
import com.codingpixel.dedicatekitchen.activities.auth.RegisterActivity
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.adapters.PricingAdapter
import com.codingpixel.dedicatekitchen.adapters.local.ToggleDotIndicatorAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentPackagesBinding
import com.codingpixel.dedicatekitchen.fragments.dialogs.SelectCardDialog
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.*
import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.models.PackageModel
import com.codingpixel.dedicatekitchen.models.TimeSlot
import com.codingpixel.dedicatekitchen.models.local.ToggleIndicator
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.stripe.Stripe
import com.stripe.model.Charge
import com.stripe.model.Refund
import com.stripe.param.ChargeCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PackagesFragment : BaseFragment(), SubscriptionInterface,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentPackagesBinding
    private lateinit var adapter: PricingAdapter
    private lateinit var viewModel: UserViewModel
    private var amount: String = ""
    private var boughtPackagePoints = ""
    private var offerAmount: Double = 0.0
    private var stripe_card_id: String = ""
    private var stripeChargeId: String = ""
    private var package_pos_id: String = ""
    private var chosenMultiplier: Float = 1f
    private var package_id: Int = 0
    private var card_id: Int = 0
    private var buyPackageAtCheckout: Boolean = false
    private var isAmountSelected: Boolean = false

    private var pricingData = ArrayList<PackageModel>()

    private lateinit var toggleDotsAdapter: ToggleDotIndicatorAdapter
    private var togglesDots = ArrayList<ToggleIndicator>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_packages, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.pullRefresh.setOnRefreshListener(this)
        mBinding.pullRefresh.isEnabled = false
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        initListeners()
        showView(view = mBinding.loader)
        getPackagesApi()
        initAdapter()
        initApiResponseObserver()
    }

    private fun initListeners() {
//        mBinding.ivBack.setOnClickListener {
//            finish()
//        }

        mBinding.tvNoData.setOnClickListener {
            AppProgressDialog.showProgressDialog(context = context!!)
            getPackagesApi()
        }

        mBinding.tvViewHistory.setOnClickListener {
            startActivity(Intent(context!!, PurchaseHistoryActivity::class.java))
        }
    }

    private fun getUserPackagesApi() {
        viewModel.getUserPackages(getLoggedInUser()?.id ?: 0)
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) { it ->

            run {

                AppProgressDialog.dismissProgressDialog()

                if (mBinding.pullRefresh.isRefreshing)
                    mBinding.pullRefresh.isRefreshing = false

                hideView(view = mBinding.loader)

                when (it.applicationEnum) {
                    ApplicationEnum.GET_PACKAGE_SUCCESS -> {
                        // showShortToast(it.message)
                        val packages = JsonManager.getInstance().getPackages(it.resultObject)
                        if (packages.isNotEmpty()) {
                            for (i in packages.indices)
                                packages[i].text = packages[i].price
                            pricingData.addAll(packages)
                            pricingData.sortBy { it.price.toInt() }
                            pricingData.forEach {
                                it.initPackagePosId()
                            }
                            togglesDots.clear()
                            for (image in 0 until pricingData.size)
                                togglesDots.add(ToggleIndicator().apply {
                                    isSelected = false
                                })

                            togglesDots[0].isSelected = true
                            toggleDotsAdapter =
                                ToggleDotIndicatorAdapter(toggles = togglesDots, showGreen = true)
                            mBinding.rvDots.adapter = toggleDotsAdapter
                            adapter.notifyDataSetChanged()
                            if (isUserLoggedIn())
                                getUserPackagesApi()
                        } else
                            showInfoToast(message = "No Packages Found!")
                    }
                    ApplicationEnum.ADD_USER_PACKAGE_SUCCESS -> {
//                        AppProgressDialog.dismissProgressDialog()

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

                    }

                    ApplicationEnum.GET_USER_PACKAGE_SUCCESS -> {
                        AppProgressDialog.dismissProgressDialog()
                        val userPackages = JsonManager.getInstance().getUserPackage(it.resultObject)
                        val index = pricingData.indexOfFirst { it.id == userPackages!!.package_id }
                        if (index != -1) {
                            pricingData[index].amount = userPackages!!.amount
                            pricingData[index].selectedPackage = true
                            adapter.notifyDataSetChanged()
                        } else {

                        }
                    }
                    else -> {
                        AppProgressDialog.dismissProgressDialog()
                    }
                }
                toggleEmptyData()

            }

        }

        viewModel.getApiStatus().observe(this) {

            run {
                when (it.enum) {

                    ApplicationEnum.SESSION_EXPIRED -> {
                        (activity as BaseActivity?)?.initSessionViewModel(object : SessionListener {
                            override fun sessionRestored() {

                            }

                            override fun sessionRestorationFailed() {

                            }
                        })
                    }

                    ApplicationEnum.ADD_ACCOUNT_TRANSACTION_SUCCESS -> {
                        updateCustomerInfo(package_pos_id)
                    }

                    ApplicationEnum.ADD_ACCOUNT_TRANSACTION_ERROR -> {
                        hitRefund()
                    }

                    ApplicationEnum.UPDATE_USER_INFO_SUCCESS -> {
                        addUserPackage(package_id, card_id, amount, stripe_card_id)
                    }

                    ApplicationEnum.ACCUMULATION_DC_POINTS_SUCCESS -> {
                        AppProgressDialog.dismissProgressDialog()
                        if (buyPackageAtCheckout) {
                            successPopup("Package Purchased Successfully", context!!, false)
                            val resultIntent = Intent()
                            resultIntent.putExtra("added", "added")
                        } else {
                            successPopup("Package Purchased Successfully", context!!, false)
                        }
                        (activity as MainBottomBarActivity?)?.refreshLoyaltyPoints(
                            caller = "Package Bought"
                        )
                    }

                    ApplicationEnum.ACCUMULATION_DC_POINTS_ERROR -> {
                        AppProgressDialog.dismissProgressDialog()
                        if (buyPackageAtCheckout) {
                            successPopup("Package Purchased Successfully", context!!, false)
                            val resultIntent = Intent()
                            resultIntent.putExtra("added", "added")
                        } else {
                            successPopup("Package Purchased Successfully", context!!, false)
                        }
                        showShortToast(message = "Error accumulating points on Data Candy")
                    }

                    else -> {
                        showShortToast("error")
                    }
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

    private fun toggleEmptyData() {
        if (pricingData.isEmpty())
            showView(view = mBinding.tvNoData)
        else
            hideView(view = mBinding.tvNoData)
    }

    private fun updateCustomerInfo(packageId: String) {
        viewModel.updateUserInfoVolante(body = RequestBodyUtil.updateUserInfo(packageId))
    }

    private fun getPackagesApi() {
//        AppProgressDialog.showProgressDialog(context!!)
        viewModel.getPackages()
    }

    private fun initAdapter() {
        adapter = PricingAdapter(packages = pricingData, cartItemsListener = this)
        mBinding.rvPlans.adapter = adapter

        mBinding.rvPlans.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                togglesDots.forEach {
                    it.isSelected = false
                }
                togglesDots[position].isSelected = true
                toggleDotsAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun packageSelected(packageDetail: PackageModel) {
        // offerAmount = (packageDetail.offer.toDouble() / 100) * packageDetail.price.toDouble()
        //  totalAmount = packageDetail.price.toDouble() + offerAmount
        // totalAmount = packageDetail.price.toDouble()

        if (isUserLoggedIn()) {
            amount = packageDetail.text
            boughtPackagePoints = (amount.toFloat() * packageDetail.points_multiplier).toString()
//            boughtPackagePoints = if (packageDetail.detail.contains("Receive")) {
//                if (packageDetail.detail.split("Receive")[1].contains("points")) {
//                    packageDetail.detail.split("Receive")[1].split("points")[0].trim()
//                } else {
//                    "0.0"
//                }
//            } else {
//                "0.0"
//            }
            package_pos_id = packageDetail.pos_package_id.toString()
            package_id = packageDetail.id
            val cardPopup = SelectCardDialog.newInstance()
            cardPopup.setListener(object : SelectCardInterface {
                override fun selectedCard(cardListing: ArrayList<CardModel>, position: Int) {
                    val alreadySelectedIndex = cardListing.indexOfFirst { it.isSelected }
                    if (alreadySelectedIndex != -1) {
                        cardListing[alreadySelectedIndex].isSelected = false
                        adapter.notifyItemChanged(alreadySelectedIndex)
                    }
                    if ((cardListing[position].stripe_card_id ?: "").isEmpty()) {
                        showWarningToast(message = "Remove this card and try again!")
                    } else {


                        stripe_card_id = cardListing[position].stripe_card_id ?: ""
                        card_id = cardListing[position].id
                        if (alreadySelectedIndex != position) {
                            cardListing[position].isSelected = true
                            adapter.notifyItemChanged(position)
                        }
                        CommonMethods.showTwoButtonsAlertDialog(fragmentManager = childFragmentManager,
                            title = "Alert", message = "Are you sure you want to buy this plan?",
                            leftButtonText = "Cancel", rightButtonText = "Yes",
                            twoButtonsDialogListener = object : TwoButtonsDialogListener {
                                override fun leftButtonTapped() {}

                                override fun rightButtonTapped() {
                                    AppProgressDialog.showProgressDialog(context = context!!)
                                    initSessionBeforeCharge()
                                }
                            })
                    }
                }
            })
            cardPopup.show(childFragmentManager, SelectCardDialog::class.java.canonicalName)
        } else {
            startActivity(Intent(context!!, RegisterActivity::class.java))
        }
    }

    private fun initSessionBeforeCharge() {
        (activity as BaseActivity?)?.initSessionViewModel(sessionListener = object :
            SessionListener {
            override fun sessionRestored() {
                chargeStripe()
            }

            override fun sessionRestorationFailed() {
                initSessionBeforeCharge()
            }
        })
    }

    private fun chargeStripe() {
        AppProgressDialog.showProgressDialog(context!!)
        GlobalScope.launch(Dispatchers.IO) {
            chargeOnStripeApi()
        }
    }

    private suspend fun chargeOnStripeApi() {
        try {
            AppProgressDialog.showProgressDialog(context!!)
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
                        addAccountTransactiontoDbVolante()
                    } else {
                        AppProgressDialog.dismissProgressDialog()
                        showShortToast(message = charge.failureMessage)
                        hitRefund()
                    }

                } else {
                    activity?.runOnUiThread {
                        AppProgressDialog.dismissProgressDialog()
                        showWarningToast(message = charge.status ?: "Delivery Fee Charge Error")
                    }
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
            AppProgressDialog.dismissProgressDialog()

            activity?.runOnUiThread {
                showWarningToast(message = "Error, while charging on stripe")
            }

        }

    }

    private fun addAccountTransactiontoDbVolante() {
        viewModel.addAccountTransactionToDb(body = RequestBodyUtil.addAccountTransaction(amount_ = amount))
    }

    private fun hitRefund() {
        GlobalScope.launch(Dispatchers.IO) {
            AppProgressDialog.showProgressDialog(context!!)
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

        packageDetail.amountArray.clear()
        packageDetail.amountArray = arrayListOf()

        for (element in array) {
            packageDetail.amountArray.add(TimeSlot().apply {
                slot = element
            })
        }

        CommonMethods.showOptionsDialog(
            fragmentManager = childFragmentManager,
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

    companion object {

        @JvmStatic
        fun newInstance() =
            PackagesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onRefresh() {
        getPackagesApi()
    }

    override fun onResume() {
        super.onResume()


        if (isUserLoggedIn())
            showView(view = mBinding.tvViewHistory)
        else
            hideView(view = mBinding.tvViewHistory)
    }
}