package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.PaymentMethodsAdapter
import com.codingpixel.dedicatekitchen.databinding.ChooseCardPopupBinding
import com.codingpixel.dedicatekitchen.helpers.AppProgressDialog
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.helpers.JsonManager
import com.codingpixel.dedicatekitchen.interfaces.PaymentMethodListener
import com.codingpixel.dedicatekitchen.interfaces.SelectCardInterface
import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectCardDialog : BottomSheetDialogFragment(), SelectCardInterface, PaymentMethodListener {
    private var twoButtonsDialogListener: SelectCardInterface? = null
    private lateinit var mBinding: ChooseCardPopupBinding
    private lateinit var adapter: PaymentMethodsAdapter
    private lateinit var viewModel: UserViewModel
    private val paymentMethods = ArrayList<CardModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReportDialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.choose_card_popup,
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
        initAdapter()
//        mBinding.tvCancel.setOnClickListener {
//            dismiss()
//        }
        initApiResponseObserver()
        getCards()
    }

    private fun getCards() {
        AppProgressDialog.showProgressDialog(context = context!!)
        viewModel.getCards()
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            AppProgressDialog.dismissProgressDialog()
            when (it.applicationEnum) {
                ApplicationEnum.GET_CARD_SUCCESS -> {

                    paymentMethods.clear()
                    val cards = JsonManager.getInstance().getCardList(it.resultObject)
                    paymentMethods.addAll(cards)
                    if (paymentMethods.size == 0) {
                        dismiss()
                        context?.let { it1 ->
                            errorDialogue(
                                "You do not have a card, Please go to Account Settings to Add a card.",
                                it1
                            )

                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                else -> {
                    dismiss()
                    context?.let { it1 ->
                        errorDialogue(
                            "You do not have a card, Please go to Account Settings to Add a card.",
                            it1
                        )
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = PaymentMethodsAdapter(
            paymentMethods = paymentMethods,
            paymentMethodListener = this,
            viewType = Constants.VIEW_TYPE_TOGGLE
        )
        mBinding.rvCards.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    fun setListener(twoButtonsDialogListener: SelectCardInterface?) {
        this.twoButtonsDialogListener = twoButtonsDialogListener
    }

    companion object {
        @JvmStatic
        fun newInstance(
        ) =
            SelectCardDialog().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun makeCardDefault(position: Int) {

    }

    override fun itemTapped(position: Int) {
        dismiss()
        twoButtonsDialogListener?.selectedCard(paymentMethods, position)
    }

    override fun deleteTapped(position: Int, cardId: Int) {
        TODO("Not yet implemented")
    }

    override fun selectedCard(cardListing: ArrayList<CardModel>, position: Int) {
        dismiss()
        twoButtonsDialogListener?.selectedCard(paymentMethods, position)
    }

    fun errorDialogue(message: String?, context: Context) {
        val layoutInflater =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val inflatedView: View =
            layoutInflater.inflate(R.layout.error_dialogue, null, false)
        val displayMetrics = context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        // set height depends on the device size
        val removePopup = PopupWindow(inflatedView, width, height, false)
        // set a background drawable with rounders corners
        removePopup.height = WindowManager.LayoutParams.MATCH_PARENT

        // removePopup.setAnimationStyle(R.style.PopupAnimation);
        // make it focusable to show the keyboard to enter in `EditText`
        removePopup.isFocusable = true
        // make it outside touchable to dismiss the popup window
        removePopup.isOutsideTouchable = true

        // show the popup at bottom of the screen and set some margin at bottom ie,
        removePopup.showAtLocation(inflatedView, Gravity.CENTER, 0, 0)
        val errorMsg = inflatedView.findViewById<TextView>(R.id.tv_message)
        errorMsg.text = message
        val close =
            inflatedView.findViewById<Button>(R.id.btn_right)
        close.setOnClickListener { removePopup.dismiss() }
        removePopup.contentView.isFocusableInTouchMode = true
        removePopup.setOnDismissListener { removePopup.dismiss() }
    }
}