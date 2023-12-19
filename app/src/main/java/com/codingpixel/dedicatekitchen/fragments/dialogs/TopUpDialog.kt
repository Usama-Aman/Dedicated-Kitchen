package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.adapters.PaymentMethodsAdapter
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.databinding.TopupDialogPopupBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.MealPrepPopupListener
import com.codingpixel.dedicatekitchen.interfaces.OrderPlacedListener
import com.codingpixel.dedicatekitchen.interfaces.PaymentMethodListener
import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.models.UserLocation
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.stripe.Stripe
import com.stripe.model.Charge
import com.stripe.model.Refund
import com.stripe.param.ChargeCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList


class TopUpDialog : BaseDialogFragment() {
    private lateinit var mBinding: TopupDialogPopupBinding
    private var twoButtonsDialogListener: MealPrepPopupListener? = null
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var viewModel: UserViewModel
    private var isDateSelected: Boolean = false
    private var stripe_card_id: String = ""
    private var stripeChargeId: String = ""
    private lateinit var adapter: PaymentMethodsAdapter
    private val paymentMethods = ArrayList<CardModel>()
    private var isTimeSelected: Boolean = true
    private var isCardSelected: Boolean = false
    private var isLocationSelected: Boolean = false

    private var topUpAmount: String = "4.00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            topUpAmount = it.getString("topUpAmount", "4.00")
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.topup_dialog_popup,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        initAdapter()
        initClickListener()
        getCards()
        initApiResponseObserver()
        mBinding.etTopupCredit.setText(topUpAmount)
    }

    private fun getCards() {
        viewModel.getCards()
    }

    private fun initAdapter() {
        adapter = PaymentMethodsAdapter(paymentMethods, object : PaymentMethodListener {
            override fun itemTapped(position: Int) {
                val alreadySelectedIndex = paymentMethods.indexOfFirst { it.isSelected }
                if (alreadySelectedIndex != -1) {
                    isCardSelected = false
                    paymentMethods[alreadySelectedIndex].isSelected = false
                    adapter.notifyItemChanged(alreadySelectedIndex)
                } else {
                    if ((paymentMethods[position].stripe_card_id ?: "").isEmpty()) {
                        showWarningToast(message = "Remove this card and try again!")
                    } else {
                        paymentMethods[position].isSelected = true
                        stripe_card_id = paymentMethods[position].stripe_card_id ?: ""
                        isCardSelected = true
                        adapter.notifyItemChanged(position)
                    }
                }
            }

            override fun makeCardDefault(position: Int) {

            }

            override fun deleteTapped(position: Int, cardId: Int) {

            }
        }, Constants.VIEW_TYPE_TOGGLE)
        mBinding.rvCards.adapter = adapter
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.GET_CARD_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    paymentMethods.clear()
                    val cards = JsonManager.getInstance().getCardList(it.resultObject)
                    paymentMethods.addAll(cards)
                    if (paymentMethods.size == 0) {
                        hideView(mBinding.rvCards)
                        showView(mBinding.tvNoCard)
                    } else {
                        showView(mBinding.rvCards)
                        hideView(mBinding.tvNoCard)
                    }
                    adapter.notifyDataSetChanged()
                }
                else -> {

                }
            }
        }

        viewModel.getApiStatus().observe(this) {
            when (it.enum) {
                ApplicationEnum.ADD_ACCOUNT_TRANSACTION_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    dismiss()
                    // updateCustomerInfo(package_pos_id)
                }
                else -> {
                    showShortToast("error")
                }
            }
        }
    }

    private fun initClickListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.cvTopup.setOnClickListener {
            if (isCardSelected)
                topUpCreditInUserWallet()
            else
                showWarningToast("Please Choose a card")
        }
    }

    private fun topUpCreditInUserWallet() {
        chargeStripe()
    }

    private fun chargeStripe() {
        context?.let { AppProgressDialog.showProgressDialog(it) }
        GlobalScope.launch(Dispatchers.IO) {
            context?.let { AppProgressDialog.showProgressDialog(it) }
            chargeOnStripeApi()
        }
    }

    private suspend fun chargeOnStripeApi() {
        Stripe.apiKey = StripeParams.STRIPE_SK
        val params: ChargeCreateParams = ChargeCreateParams.builder()
            .setAmount((mBinding.etTopupCredit.text.toString().toDouble() * 100).toLong())
            .setCurrency(Constants.APP_CURRENCY)
            .setDescription("TopUp Charge")
            .setStatementDescriptor("TopUp Charge")
            .setCustomer(AppPreferenceManager.getUserFromSharedPreferences()?.stripe_id ?: "")
            .setSource(stripe_card_id)
            .build()
        val charge = Charge.create(params)

        withContext(Dispatchers.Main) {
            // Dismiss Progress Dialog
            if ((charge.status ?: "") == "succeeded") {
                stripeChargeId = charge.id
                if (charge.id != null && charge.id!!.isNotEmpty()) {
                    AppProgressDialog.dismissProgressDialog()
                    //   showShortToast(message = charge.id)
                    AddAccountTransactiontoDbVolante()

                } else {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(message = charge.failureMessage)
                    hitRefund()
                }
            } else {
                AppProgressDialog.dismissProgressDialog()
                showShortToast(message = charge.failureMessage)
            }
        }
    }

    fun setListener(twoButtonsDialogListener: MealPrepPopupListener?) {
        this.twoButtonsDialogListener = twoButtonsDialogListener
    }

    private fun AddAccountTransactiontoDbVolante() {
        viewModel.addAccountTransactionToDb(body = RequestBodyUtil.addAccountTransaction(amount_ = mBinding.etTopupCredit.text.toString()))
    }

    private fun hitRefund() {
        GlobalScope.launch(Dispatchers.IO) {
            context?.let { AppProgressDialog.showProgressDialog(it) }
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

    companion object {

        @JvmStatic
        fun newInstance(topUpAmount: String = "4.00") =
            TopUpDialog().apply {
                arguments = Bundle().apply {
                    putString("topUpAmount", topUpAmount)
                }
            }
    }
}