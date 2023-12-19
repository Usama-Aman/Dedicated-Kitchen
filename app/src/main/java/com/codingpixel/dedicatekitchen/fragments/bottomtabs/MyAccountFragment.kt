package com.codingpixel.dedicatekitchen.fragments.bottomtabs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.account.*
import com.codingpixel.dedicatekitchen.activities.dashboard.AppWebViewClient
import com.codingpixel.dedicatekitchen.activities.dashboard.FavoriteProducts
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.activities.onboarding.IntermediateActivity
import com.codingpixel.dedicatekitchen.adapters.local.AccountSectionsAdapter
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.databinding.FragmentMyAccountBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.AccountSectionListener
import com.codingpixel.dedicatekitchen.interfaces.SessionListener
import com.codingpixel.dedicatekitchen.interfaces.TwoButtonsDialogListener
import com.codingpixel.dedicatekitchen.models.local.AccountSectionItem
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import com.codingpixel.dedicatekitchen.viewmodels.SessionViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyAccountFragment : BaseFragment(), AccountSectionListener, SessionListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentMyAccountBinding
    private lateinit var viewModel: MenuViewModel
    private lateinit var sessionViewModel: SessionViewModel

    private lateinit var sectionsAdapter: AccountSectionsAdapter
    private var accountSectionsList = ArrayList<AccountSectionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false)
        return mBinding.root
    }

    fun refreshUserPoints(points: String) {
        mBinding.tvUserPoints.text = "${String.format("%.2f", points.toFloat())}"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        // mBinding.pullRefresh.setOnRefreshListener(this)
        initApiResponseObserver()
        initSectionsAdapter()
        initClickListener()

    }

    private fun initApiResponseObserver() {
        viewModel.getApiStatus().observe(this) {
            when (it.enum) {
                ApplicationEnum.SESSION_EXPIRED -> {
                    initSessionViewModel(sessionListener = this)
                }

                ApplicationEnum.GET_ACCOUNT_BALANCE_SUCCESS -> {
                    // mBinding.pullRefresh.isRefreshing = false
                    showView(mBinding.tvSubSectionSubTitle)
                    hideView(mBinding.loading)
                    val accountBalance = it.dataObject.getJSONObject("balance").getString("value")
                    //  showShortToast(accountBalance)
                    mBinding.tvSubSectionSubTitle.text = accountBalance
                }
                else -> {

                }
            }
        }
    }

    private fun getAccountBalance() {
        hideView(mBinding.tvSubSectionSubTitle)
        showView(mBinding.loading)
        viewModel.getAccountBalance(body = RequestBodyUtil.geAccountBalanceRequestBody())
    }

    override fun onResume() {
        super.onResume()
        setData()
        if (isUserLoggedIn()) {
            (activity as MainBottomBarActivity?)?.refreshLoyaltyPoints(caller = "FromAccount")
            getAccountBalance()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        mBinding.tvUserName.text = getLoggedInUser()?.fname + " " + getLoggedInUser()!!.lname
        mBinding.tvUserEmail.text = getLoggedInUser()?.email ?: "No Email Found!"
    }

    private fun initClickListener() {
        mBinding.tvAccountDetails.setOnClickListener {
//            CommonMethods.showAccountInfoDialog(
//                fragmentManager = childFragmentManager
//            )
        }

        mBinding.tvAddCredit.setOnClickListener {

            (activity as MainBottomBarActivity?)?.initPackagesFragment()

//            startActivity(
//                Intent(
//                    context!!,
//                    PricingPlans::class.java
//                ).putExtra(IntentParams.buyPackageAtCheckout, false)
//            )

        }
        mBinding.tvLogOut.setOnClickListener {
            CommonMethods.showTwoButtonsAlertDialog(fragmentManager = childFragmentManager,
                title = "Are You Sure?", message = "Are you sure you want to log out of the Dedicate Kitchen App?",
                leftButtonText = "Later", rightButtonText = "Yes",
                twoButtonsDialogListener = object : TwoButtonsDialogListener {
                    override fun leftButtonTapped() {
                    }

                    override fun rightButtonTapped() {
                        val db = AppDatabase(context!!)
                        GlobalScope.launch {
                            db.cartDao().clearTable()
                        }
                        performLogOut()
                    }
                })
        }
    }

    private fun performLogOut() {
        AppPreferenceManager.clearPreferences()
        startActivity(Intent(context!!, IntermediateActivity::class.java))
        activity?.finishAffinity()
    }

    private fun initSectionsAdapter() {
        accountSectionsList.addAll(CommonMethods.getAccountSectionItem())
        sectionsAdapter = AccountSectionsAdapter(
            accountSections = accountSectionsList,
            accountSectionListener = this
        )
        mBinding.rvAccountSections.adapter = sectionsAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MyAccountFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun itemTapped(sectionIndex: Int, subSectionIndex: Int) {

        when (accountSectionsList[sectionIndex].subSectionsList[subSectionIndex].title) {


            "Account Information" -> {
                startActivity(Intent(context, AccountInformationActivity::class.java))
            }

            "Password" -> {
                startActivity(
                    Intent(context, ChangePasswordActivity::class.java).putExtra(
                        IntentParams.TYPE,
                        "changePassword"
                    ).putExtra(IntentParams.EMAIL, getLoggedInUser()!!.email)
                        .putExtra(IntentParams.CODE, 0)
                )
            }

            "Payment Methods" -> {
                startActivity(Intent(context, PaymentMethodsActivity::class.java))
            }
            "Available Credits" -> {
                startActivity(
                    Intent(
                        context,
                        PricingPlans::class.java
                    ).putExtra(IntentParams.buyPackageAtCheckout, false)
                )
            }

            "Delivery Locations" -> {
                startActivity(Intent(context, LocationsActivity::class.java))
            }
            "Your Dedicate Favourites" -> {
                startActivity(Intent(context, FavoriteProducts::class.java))
            }
            "Locations" -> {
                startActivity(
                    Intent(context, AppWebViewClient::class.java).putExtra(
                        "connectURL",
                        "http://dev.codingpixel.com/dedicatekitchen-backend/locations"
                    ).putExtra("title", "Locations")
                )
            }
            "Loyalty" -> {
                startActivity(
                    Intent(context, AppWebViewClient::class.java).putExtra(
                        "connectURL",
                        "http://dev.codingpixel.com/dedicatekitchen-backend/loyalty"
                    ).putExtra("title", "Loyalty")
                )
            }
            "Nutritional Info" -> {
                startActivity(
                    Intent(context, AppWebViewClient::class.java).putExtra(
                        "connectURL",
                        "http://dev.codingpixel.com/dedicatekitchen-backend/nutritional"
                    ).putExtra("title", "Nutritional Info")
                )
            }

            "Invite your Firends" -> {
                startActivity(Intent(context, PromoCodeActivity::class.java))
            }

            "Notifications", "Promotional Notifications" -> {
                accountSectionsList[sectionIndex].subSectionsList[subSectionIndex].isChecked =
                    !accountSectionsList[sectionIndex].subSectionsList[subSectionIndex].isChecked
                sectionsAdapter.notifyItemChanged(sectionIndex)
            }
        }

    }

    private fun initSessionViewModel(sessionListener: SessionListener? = null) {
        sessionViewModel = ViewModelProvider(this).get(SessionViewModel::class.java)
        sessionViewModel.getApiStatus().observe(viewLifecycleOwner, Observer {
            if (it.enum == ApplicationEnum.SESSION_RESTORED)
                sessionListener?.sessionRestored()
            else
                sessionListener?.sessionRestorationFailed()
        })
        sessionViewModel.getSessionId(body = RequestBodyUtil.getSessionIdRequestBody())
    }

    override fun sessionRestored() {
        getAccountBalance()
    }

    override fun sessionRestorationFailed() {

    }

    override fun onRefresh() {
        getAccountBalance()
    }

}