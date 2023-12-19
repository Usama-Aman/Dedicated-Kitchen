package com.codingpixel.dedicatekitchen.activities.checkout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivitySelectAddressBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.UserLocation

class SelectAddressActivity : BaseActivity(), ItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: ActivitySelectAddressBinding

    private lateinit var locationsAdapter: LocationsAdapter
    private val locations = ArrayList<UserLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_address)
        changeStatusBarColor(statusBarColor = getColor(R.color.grayBgColor))
        initClickListener()
        initAdapter()
    }


    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }

//        mBinding.tvAddAnother.setOnClickListener {
//            startActivity(Intent(this@SelectAddressActivity, LocationsActivity::class.java))
//        }

        mBinding.cvContinueCheckOut.setOnClickListener {
            startActivity(
                Intent(
                    this@SelectAddressActivity,
                    SelectPaymentMethodActivity::class.java
                )
            )

        }
        mBinding.pullRefresh.setOnRefreshListener(this)
    }

    private fun initAdapter() {
        for (i in 0 until 4) {
            locations.add(UserLocation().apply {
                is_default = 0
            })
        }
        locationsAdapter = LocationsAdapter(
            locations = locations,
            itemClickListener = this,
            viewType = Constants.VIEW_TYPE_TOGGLE,
            itemType = Constants.VIEW_TPYE_LARGE
        )
        mBinding.rvAddress.adapter = locationsAdapter
    }

    override fun itemClicked(position: Int) {
        val alreadySelectedIndex = locations.indexOfFirst { it.isChecked }
        if (alreadySelectedIndex != -1) {
            locations[alreadySelectedIndex].isChecked = false
            locationsAdapter.notifyItemChanged(alreadySelectedIndex)
        }
        if (alreadySelectedIndex != position) {
            locations[position].isChecked = true
            locationsAdapter.notifyItemChanged(position)
        }
    }

    override fun brandTapped(position: Int, title: String) {
        TODO("Not yet implemented")
    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {

    }

    override fun deleteLocation(position: Int, addressId: Int) {

    }

    override fun onRefresh() {


    }

    override fun orderCompleted(position: Int) {

    }
}