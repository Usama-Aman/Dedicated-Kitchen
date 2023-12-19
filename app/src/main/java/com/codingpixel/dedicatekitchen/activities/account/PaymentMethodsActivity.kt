package com.codingpixel.dedicatekitchen.activities.account

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.PaymentMethodsAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityPaymentMethodsBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.AddedCardInterface
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.PaymentMethodListener
import com.codingpixel.dedicatekitchen.interfaces.TwoButtonsDialogListener
import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel

class PaymentMethodsActivity : BaseActivity(), ItemClickListener, PaymentMethodListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: ActivityPaymentMethodsBinding
    private lateinit var viewModel: UserViewModel
    private var isDefault: Int = 0
    private lateinit var adapter: PaymentMethodsAdapter
    private var deletedCardId: Int = 0
    private val paymentMethods = ArrayList<CardModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment_methods)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mBinding.pullRefresh.setOnRefreshListener(this)
        initApiResponseObserver()
        changeStatusBarColor(statusBarColor = getColor(R.color.white))
        initAdapter()
        initClickListeners()
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.GET_CARD_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    paymentMethods.clear()
                    mBinding.pullRefresh.isRefreshing = false
                    val cards = JsonManager.getInstance().getCardList(it.resultObject)
                    if (!cards.isNullOrEmpty()) {
                        if (cards.size == 1)
                            isDefault = 0
                    } else
                        isDefault = 1

//                    if (pageNo == 1)
//                        paymentMethodList.clear()
                    paymentMethods.addAll(cards)
                    if (paymentMethods.size == 0) {
                        hideView(mBinding.cardsListingLayout)
                        showView(mBinding.emptyLayout)
                    } else {
                        showView(mBinding.cardsListingLayout)
                        hideView(mBinding.emptyLayout)
                    }
//                    if (paymentMethods.size == 0)
//                        showView(mBinding.ivNoRecord)
//                    else
//                        hideView(mBinding.ivNoRecord)
                    adapter.notifyDataSetChanged()
                }
                ApplicationEnum.CARD_DELETED_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    successPopup(it.message, this, false)
                    paymentMethods.removeAt(deletedCardId)
                    adapter.notifyDataSetChanged()

                    if (paymentMethods.size == 0) {
                        hideView(mBinding.cardsListingLayout)
                        showView(mBinding.emptyLayout)
                    } else {
                        showView(mBinding.cardsListingLayout)
                        hideView(mBinding.emptyLayout)
                    }
                }
                else -> {
                    mBinding.pullRefresh.isRefreshing = false
                    AppProgressDialog.dismissProgressDialog()
                    if (paymentMethods.size == 0) {
                        hideView(mBinding.cardsListingLayout)
                        showView(mBinding.emptyLayout)
                    } else {
                        showView(mBinding.cardsListingLayout)
                        hideView(mBinding.emptyLayout)
                    }
                }
            }
        }
    }

    private fun getCards() {
        AppProgressDialog.showProgressDialog(this)
        viewModel.getCards()
    }

    override fun onResume() {
        super.onResume()
        getCards()
    }

    private fun initClickListeners() {
        mBinding.cvAddCard.setOnClickListener {
            AppPreferenceManager.setIntValue("card", isDefault)
            CommonMethods.showAddPaymentMethodDialog(
                fragmentManager = supportFragmentManager,
                clickListener = object :
                    AddedCardInterface {
                    override fun addedCardInfo(card: CardModel) {
                        paymentMethods.add(0, card)
                        showView(mBinding.cardsListingLayout)
                        hideView(mBinding.emptyLayout)
                        adapter.notifyDataSetChanged()
                    }
                })
        }

        mBinding.cvAddFirstCard.setOnClickListener {
            CommonMethods.showAddPaymentMethodDialog(
                fragmentManager = supportFragmentManager,
                clickListener = object :
                    AddedCardInterface {
                    override fun addedCardInfo(card: CardModel) {
                        this@PaymentMethodsActivity.finish()
//                        paymentMethods.add(0, card)
//                        adapter.notifyDataSetChanged()
                    }
                })
        }
    }


    private fun initAdapter() {
//        for (i in 0 until 4) {
//            paymentMethods.add(PaymentMethod().apply {
//                isDefaultCard = if (i == 0) true else false
//                isSelcted = false
//            })
//        }
        adapter = PaymentMethodsAdapter(
            paymentMethods = paymentMethods,
            paymentMethodListener = this,
            viewType = Constants.VIEW_TYPE_DISPLAY,
            fromPaymentMethodsActivity = true
        )
        mBinding.rvCards.adapter = adapter
    }

    override fun itemClicked(position: Int) {

    }

    override fun brandTapped(position: Int, title: String) {

    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {

    }

    override fun deleteLocation(position: Int, addressId: Int) {

    }

    override fun cancelOrder(position: Int, orderId: Int) {

    }

    private fun toggleEmptyView() {
        if (paymentMethods.isEmpty()) {
            hideView(view = mBinding.cardsListingLayout)
            showView(view = mBinding.emptyLayout)
        } else {
            showView(view = mBinding.cardsListingLayout)
            hideView(view = mBinding.emptyLayout)
        }
    }

    override fun itemTapped(position: Int) {
    }

    override fun makeCardDefault(position: Int) {
//        val pm = paymentMethods[position]
//        showLoading()
//        viewModel.addCard(
//            pm.stripe_id ?: "",
//            pm.cardNumber
//            expMonth,
//            expYear,
//            mBinding.etCvc.text.toString().trim(),
//            defaultCard,
//            brandName
//        )
    }

    override fun deleteTapped(position: Int, cardId: Int) {
//        if (paymentMethods[position].is_default == 1)
//            errorDialogue("You cannot delete a default card", this)
//        else
        deleteCardApi(position, cardId)
//        paymentMethods.removeAt(position)
//        adapter.notifyDataSetChanged()
        toggleEmptyView()
    }

    private fun deleteCardApi(position: Int, cardId: Int) {
        CommonMethods.showTwoButtonsAlertDialog(fragmentManager = supportFragmentManager,
            title = "Delete Card", message = "Are you sure you want to delete this card?",
            leftButtonText = "Later", rightButtonText = "Yes",
            twoButtonsDialogListener = object : TwoButtonsDialogListener {
                override fun leftButtonTapped() {
                }

                override fun rightButtonTapped() {
                    deletedCardId = position
                    AppProgressDialog.showProgressDialog(this@PaymentMethodsActivity)
                    viewModel.deleteCardApi("cards/delete-card/$cardId")
                }
            })
    }

    override fun onRefresh() {
        viewModel.getCards()
    }

    override fun orderCompleted(position: Int) {

    }
}