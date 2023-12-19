package com.codingpixel.dedicatekitchen.activities.account

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.PurchaseHistoryAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityPurchaseHistoryBinding
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.JsonManager
import com.codingpixel.dedicatekitchen.models.PurchaseHistory
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel

class PurchaseHistoryActivity : BaseActivity() {

    private lateinit var mBinding: ActivityPurchaseHistoryBinding
    private lateinit var viewModel: UserViewModel

    private lateinit var adapter: PurchaseHistoryAdapter
    private var historyList = arrayListOf<PurchaseHistory>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_history)
        initClickListener()
        initAdapter()
        initViewModel()
        changeStatusBarColor(statusBarColor = getColor(R.color.white))
        showLoading()
        viewModel.getPurchasHistory()
    }

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }
    }

    private fun initAdapter() {
        adapter = PurchaseHistoryAdapter(list = historyList)
        mBinding.rvHistiry.adapter = adapter
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel.getDbApiStatus().observe(this) {

            run {

                hideLoading()
                when (it.applicationEnum) {


                    ApplicationEnum.GET_PURCHASE_HISTORY_SUCCESS -> {
                        val list = JsonManager.getInstance().getPurchaseHistoryList(it.resultObject)
                        historyList.clear()
                        historyList.addAll(list)
                        historyList.forEach {
                            it.initData()
                        }
                        adapter.notifyDataSetChanged()
                    }

                    else -> {
//                    hideLoading()
                    }
                }

                toggleEmptyView()

            }

        }


        viewModel.getApiStatus().observe(this) {
            when (it.enum) {

                ApplicationEnum.GET_ACCOUNT_BALANCE_SUCCESS -> {
                    // mBinding.pullRefresh.isRefreshing = false
                    val accountBalance = it.dataObject.getJSONObject("balance").getString("value")
//                    Log.d("TotalBalance", accountBalance)
                    //  showShortToast(accountBalance)
                    val totalPoints = if (accountBalance.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                        accountBalance.toFloat().toInt()
                    } else {
                        0
                    }
                    mBinding.tvCurrentPoints.text = "$totalPoints"
                }


                else -> {
//                    hideLoading()
                }
            }
        }

    }

    private fun toggleEmptyView() {
        if (historyList.isEmpty()) {
            showView(view = mBinding.tvNoDataFound)
        } else {
            hideView(view = mBinding.tvNoDataFound)
        }
    }

}