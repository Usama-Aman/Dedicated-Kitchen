package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.local.PaymentMethodOptionsAdapter
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.base.MyBottomSheetBaseFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentAddPaymentMethodDialogBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.AddedCardInterface
import com.codingpixel.dedicatekitchen.interfaces.DateTImePickerListener
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.maskeditor.MaskTextWatcher
import com.codingpixel.dedicatekitchen.models.PaymentMethod
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.Card
import com.stripe.model.Customer
import com.stripe.model.Token
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AddPaymentMethodDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: FragmentAddPaymentMethodDialogBinding
    private var clickListener: AddedCardInterface? = null
    private var last4Digits: String = ""
    private var expMonth: String = ""
    private var expYear: String = ""
    private var brandName: String = ""
    private var isDefault: Int = 0
    private var dateFlag: Boolean = false
    private var defaultCard: Int = 0

    // private lateinit var paymentMethodsAdapter: PaymentMethodOptionsAdapter
    private lateinit var viewModel: UserViewModel
    private val paymentOptions = ArrayList<PaymentMethod>()

    private var selectedMonth: Int = 0
    private var selectedYear: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReportDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_payment_method_dialog,
            container,
            false
        )
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        initApiResponseObserver()
        isDefault = AppPreferenceManager.getIntValue("card")
        // setFieldsMaxLength()
        // setCardNumberFormatter()


        val textWatcherNumber = MaskTextWatcher(mBinding.etCardNumber, "#### #### #### ####")
        mBinding.etCardNumber.addTextChangedListener(textWatcherNumber)


        initClickListener()
    }

    fun setListener(clickListener: AddedCardInterface?) {
        this.clickListener = clickListener
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.CARD_ADD_SUCCESS -> {
                    // successPopup(it.message, context, false)
                    showShortToast(it.message)
                    val card = JsonManager.getInstance().getAddedCard(it.resultObject)
                    AppProgressDialog.dismissProgressDialog()
                    if (card != null) {
                        clickListener?.addedCardInfo(card)
                        dismiss()
                    }
                }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                    Log.d("card Added successfully", "error")
                }
            }
        }
    }

    private fun initClickListener() {

        mBinding.mainParent.setOnClickListener {
            dismiss()
        }

        mBinding.cbSetDefault.setOnCheckedChangeListener { _, isChecked ->
            isDefault = if (isChecked)
                1
            else
                0
        }

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
                        if (selectedYear == Calendar.getInstance()
                                .get(Calendar.YEAR) && selectedMonth < Calendar.getInstance()
                                .get(Calendar.MONTH)
                        ) {
                            showShortToast("Select a valid month")
                            mBinding.tvMonthYear.text = ""
                            dateFlag = false
                        } else {
                            dateFlag = true
                            selectedMonth = month
                            selectedYear = year
                            expMonth = month.toString()
                            expYear = year.toString()
                            mBinding.tvMonthYear.text =
                                String.format("%02d", month) + " / " + year.toString().substring(2)
                        }
                    }
                }
            )
        }

        mBinding.cvAddCard.setOnClickListener {
            if (checkDataEntered()) {
                if (isUserLoggedIn()) {
                    context?.let { it1 -> AppProgressDialog.showProgressDialog(it1) }
                    getStripeToken(
                        mBinding.etCardNumber.text.toString().replace(" ","").trim(),
                        expMonth,
                        expYear,
                        mBinding.etCvc.text.toString().trim()
                    )
                } else {
                    context?.let { it1 -> AppProgressDialog.showProgressDialog(it1) }
                    getStripeTokenForGuestUser(
                        mBinding.etCardNumber.text.toString().replace(" ","").trim(),
                        expMonth,
                        expYear,
                        mBinding.etCvc.text.toString().trim()
                    )
                }
            }
        }
    }

    private fun getStripeTokenForGuestUser(
        cardNumber: String,
        expMonth: String,
        expYear: String,
        ccvString: String
    ) {
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
                AppProgressDialog.dismissProgressDialog()
            } catch (e: StripeException) {
                showShortToast(e.message!!)
                AppProgressDialog.dismissProgressDialog()
                e.printStackTrace()
            }
        })
        thread.start()
    }

    private fun getStripeToken(
        cardNumber: String,
        expMonth: String,
        expYear: String,
        ccvString: String
    ) {

        if ((getLoggedInUser()?.stripe_id ?: "").isNotEmpty()) {
            Stripe.apiKey = StripeParams.STRIPE_SK
            val thread = Thread(Runnable {
                try {
                    val cardParams: MutableMap<String, Any> =
                        HashMap()
                    cardParams["number"] = cardNumber
                    cardParams["exp_month"] = expMonth
                    cardParams["exp_year"] = expYear
                    cardParams["cvc"] = ccvString
                    val params: MutableMap<String, Any> =
                        HashMap()
                    params["card"] = cardParams
                    val token = Token.create(params)
                    val customer = Customer.retrieve(
                        Objects.requireNonNull(
                            getLoggedInUser()?.stripe_id ?: ""
                        )
                    )
                    val customerCardSourceParams: MutableMap<String, Any> =
                        HashMap()
                    customerCardSourceParams["source"] = token.id
                    val card =
                        customer.sources.create(customerCardSourceParams) as Card
                    brandName = card.brand
                    defaultCard = isDefault
                    addCardAPI(
                        card.id,
                        last4Digits,
                        card.country,
                        defaultCard
                    )
                } catch (e: StripeException) {
                    activity?.runOnUiThread {
                        context?.let { errorDialogue(e.message!!, it) }
                    }
                    AppProgressDialog.dismissProgressDialog()
                    e.printStackTrace()
                    dismiss()
                }
            })
            thread.start()
        } else {
            showWarningToast(message = "Invalid Stripe Customer Id")
        }
    }

    private fun addCardAPI(
        stripeIdd: String?,
        last4Digits: String,
        country: String?,
        defaultCard: Int
    ) {
        viewModel.addCard(
            stripeIdd ?: "",
            mBinding.etCardNumber.text.toString().replace(" ","").trim(),
            expMonth,
            expYear,
            mBinding.etCvc.text.toString().trim(),
            defaultCard,
            brandName
        )
    }

//    private fun initPaymentMethodOptionsAdapter() {
//        paymentOptions.addAll(CommonMethods.getPaymentMethodOptionsList())
//        paymentMethodsAdapter =
//            PaymentMethodOptionsAdapter(paymentOptions = paymentOptions, itemClickListener = this)
//        mBinding.rvPaymentMethodsOptions.adapter = paymentMethodsAdapter
//    }


//    override fun brandTapped(position: Int, title: String) {
//        brandName = title
//        when (position) {
//
//            0, 1 -> {
//                hideView(view = mBinding.rvPaymentMethodsOptions)
//                hideView(view = mBinding.optionsDummyFooter)
//                showView(view = mBinding.cardInfoLayout)
//            }
//
//            2 -> {
//                // PayPal
//            }
//        }
//    }


//    private fun setFieldsMaxLength() {
//        val filtersListCardNumber = arrayOfNulls<InputFilter>(1)
//        filtersListCardNumber[0] = InputFilter.LengthFilter(Constants.CARD_NUMBER_MAX_LENGTH)
//        mBinding.etCardNumber.filters = filtersListCardNumber
//    }


//    private fun setCardNumberFormatter() {
//        mBinding.etCardNumber.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                if (s != null) {
//                    mBinding.etCardNumber.setSelection(s.length)
//                    mBinding.tvCardNumber.text = CommonMethods.getFormattedCardNumber(
//                        cardNumber = s.toString().trim()
//                    )
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//        })
//    }

    private fun checkDataEntered(): Boolean {

//        if (brand.isEmpty()) {
//            showWarningToast("Select Card Brand")
//            return false
//        }
//        if (isEmpty(mBinding.etPersonName)) {
//            showWarningToast("Enter Card Holder Name")
//            return false
//        }
        if (isEmpty(mBinding.etCardNumber)) {
            showWarningToast("Enter Card Number")
            return false
        }
        if (mBinding.etCardNumber.text.toString().replace(" ","").trim().length < 16) {
            showWarningToast("Enter Valid Card Number")
            return false
        } else {
            last4Digits = mBinding.etCardNumber.text.toString().replace(" ","").trim()
                .substring(mBinding.etCardNumber.text.toString().replace(" ","").trim().length - 4)
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

//    private fun checkCardNumber() {
//        if (mBinding.tvCardNumber.text.toString().contains("X")) {
//
//        }
//    }

    companion object {


        fun newInstance(): AddPaymentMethodDialogFragment {
            val args = Bundle()
            val fragment = AddPaymentMethodDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}