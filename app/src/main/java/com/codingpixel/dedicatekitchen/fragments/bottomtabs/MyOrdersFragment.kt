package com.codingpixel.dedicatekitchen.fragments.bottomtabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.pager.OrdersPagerAdapter
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentMyOrdersBinding


/**
 * A simple [Fragment] subclass.
 * Use the [MyOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyOrdersFragment : BaseFragment() {

    private lateinit var mBinding: FragmentMyOrdersBinding

    private lateinit var pagerAdapter: OrdersPagerAdapter

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_orders, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPagerAdapter()
    }

    private fun initPagerAdapter() {
        pagerAdapter = OrdersPagerAdapter(
            fragmentManager = childFragmentManager
        )
//        mBinding.viewPager.offscreenPageLimit = 2
        mBinding.viewPager.offscreenPageLimit = 1
        mBinding.viewPager.adapter = pagerAdapter
        mBinding.tabLayoutOrders.setupWithViewPager(mBinding.viewPager)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MyOrdersFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}