package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.adapters.ToggleKitchenAdapter
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentChangeLocationPointsDialogBinding
import com.codingpixel.dedicatekitchen.helpers.CommonMethods
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.SelectedTimeSlotListener
import com.codingpixel.dedicatekitchen.models.Kitchen
import com.codingpixel.dedicatekitchen.models.TimeSlot
import com.codingpixel.dedicatekitchen.models.UserLocation


/**
 * A simple [Fragment] subclass.
 * Use the [ChangeLocationPointsDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangeLocationPointsDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: FragmentChangeLocationPointsDialogBinding


    private lateinit var kitchenAdapter: ToggleKitchenAdapter
    private val kitchensList = ArrayList<Kitchen>()

    private lateinit var locationsAdapter: LocationsAdapter
    private val locations = ArrayList<UserLocation>()

    private var chosenTimeSlot: TimeSlot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
        setStyle(STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_change_location_points_dialog,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        setDeliveryView()
    }

    private fun initClickListener() {

        mBinding.dialogParent.setOnClickListener {
            dismiss()
        }

        mBinding.tvDelivery.setOnClickListener {
            setDeliveryView()
        }

//        mBinding.tvTime.setOnClickListener {
//            CommonMethods.showTimeSlotsDialog(fragmentManager = childFragmentManager,
//                timeSlotListener = object : SelectedTimeSlotListener {
//                    override fun timeSlot(timeSlot: TimeSlot) {
//                        chosenTimeSlot = timeSlot
//                        mBinding.tvTime.setText(timeSlot.slot)
//                    }
//                }, alreadySelectedTimeSlot = chosenTimeSlot)
//        }

        mBinding.tvPickUp.setOnClickListener {
            setPickUpView()
        }

        mBinding.tvCancel.setOnClickListener {
            dismiss()
        }

        mBinding.btnChangeNow.setOnClickListener {
            dismiss()
        }
    }

    private fun initKitchensAdapter() {
        kitchensList.clear()
        kitchensList.addAll(CommonMethods.getDummyKitchens())
        kitchenAdapter = ToggleKitchenAdapter(kitchens = kitchensList, itemClickListener = object :
            ItemClickListener {

            override fun orderCompleted(position: Int) {

            }

            override fun itemClicked(position: Int) {
                val alreadySelectedIndex = kitchensList.indexOfFirst { it.isSelcted }
                if (alreadySelectedIndex != -1) {
                    kitchensList[alreadySelectedIndex].isSelcted = false
                    kitchenAdapter.notifyItemChanged(alreadySelectedIndex)
                }
                kitchensList[position].isSelcted = true
                kitchenAdapter.notifyItemChanged(position)
            }

            override fun brandTapped(position: Int, title: String) {

            }

            override fun makeDefaultLocation(position: Int, addressId: Int) {

            }

            override fun deleteLocation(position: Int, addressId: Int) {
            }
        })
        mBinding.rvContent.adapter = kitchenAdapter
    }

    private fun initLocationsAdapter() {
        locations.clear()
        for (i in 0 until 2) {
            locations.add(UserLocation().apply {
                is_default = 0
            })
        }
        locationsAdapter = LocationsAdapter(
            locations = locations,
            itemClickListener = object : ItemClickListener {

                override fun orderCompleted(position: Int) {

                }

                override fun itemClicked(position: Int) {
//                    val alreadySelectedIndex = locations.indexOfFirst { it.isSelcted }
//                    if (alreadySelectedIndex != -1) {
//                        locations[alreadySelectedIndex].isSelcted = false
//                        locationsAdapter.notifyItemChanged(alreadySelectedIndex)
//                    }
//                    locations[position].isSelcted = true
//                    locationsAdapter.notifyItemChanged(position)
                }

                override fun brandTapped(position: Int, title: String) {

                }

                override fun makeDefaultLocation(position: Int, addressId: Int) {

                }

                override fun deleteLocation(position: Int, addressId: Int) {
                    TODO("Not yet implemented")
                }
            },
            viewType = Constants.VIEW_TYPE_TOGGLE,
            itemType = Constants.VIEW_TPYE_SMALL
        )
        mBinding.rvContent.adapter = locationsAdapter
    }

    private fun setDeliveryView() {
        mBinding.tvTime.text = getString(R.string.delivery_time)
        mBinding.tvDelivery.setBackgroundResource(R.drawable.dark_gray_left_rounded_rect)
        mBinding.tvDelivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        mBinding.tvPickUp.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.unSelectedTabColor
            )
        )

        mBinding.tvTitle.setText(getString(R.string.change_delivery_location))
        mBinding.tvPickUp.setBackgroundResource(0)
        showView(view = mBinding.tvAddAnother)
        initLocationsAdapter()
    }

    private fun setPickUpView() {
        mBinding.tvTime.text = getString(R.string.pick_up_time)
        mBinding.tvPickUp.setBackgroundResource(R.drawable.dark_gray_right_rounded_rect)
        mBinding.tvPickUp.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        mBinding.tvDelivery.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.unSelectedTabColor
            )
        )
        mBinding.tvTitle.text = getString(R.string.choose_our_kitchen)
        mBinding.tvDelivery.setBackgroundResource(0)
        hideView(view = mBinding.tvAddAnother)
        initKitchensAdapter()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ChangeLocationPointsDialogFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}