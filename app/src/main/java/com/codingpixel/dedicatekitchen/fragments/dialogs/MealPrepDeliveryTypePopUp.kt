package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.base.DateTimePickerListener
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.databinding.MealPrepPopupBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.KitchenTimeListener
import com.codingpixel.dedicatekitchen.interfaces.MealPrepPopupListener
import com.codingpixel.dedicatekitchen.interfaces.SelectedTimeSlotListener
import com.codingpixel.dedicatekitchen.models.TimeSlot
import com.codingpixel.dedicatekitchen.models.UserLocation
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class MealPrepDeliveryTypePopUp : BaseDialogFragment(), ItemClickListener, DateTimePickerListener {

    private lateinit var mBinding: MealPrepPopupBinding
    private var twoButtonsDialogListener: MealPrepPopupListener? = null
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var viewModel: UserViewModel
    private var isDateSelected: Boolean = false
    private var isTimeSelected: Boolean = false
    private var flag: String = ""
    private var key: String = ""
    private var takeoutButtonState: Boolean = true
    private var deliveryButtonState: Boolean = true
    private var isLocationSelected: Boolean = false
    private var timeSlotArray = ArrayList<TimeSlot>()
    private var deliveryTimeSlotArray = ArrayList<TimeSlot>()
    private val locations = ArrayList<UserLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString("key").toString()
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
            R.layout.meal_prep_popup,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        //  AppPreferenceManager.setValue(IntentParams.MEAL_PREP_ORDER_TYPE, IntentParams.DELIVERY)
        setViews()
        initAdapter()
        initTimeSlots()
        initClickListener()
        getLocations()
        initApiResponseObserver()
    }

    private fun cartCheck(param: String) {
        val db = context?.let { it1 -> AppDatabase(it1) }
        GlobalScope.launch {
            val cartItemsExist =
                db?.cartDao()?.exists(type = ApplicationEnum.MEAL_PREP.toString())
            if (cartItemsExist!!) {
                if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_ORDER_TYPE) == IntentParams.TAKEOUT) {
                    if (param == "delivery") {

                        activity?.runOnUiThread {
                            mBinding.cbDelivery.isChecked = false
                            mBinding.cbTakeout.isChecked = true
                            mBinding.cvTakeoutContinue.isEnabled = false
                            mBinding.cvTakeoutContinue.isClickable = false
                            deliveryButtonState = false
                            hideView(mBinding.layoutDelivery)
                            showView(mBinding.layoutTakeout)
                            context?.let {
                                showShortToast(
                                    "Please checkout Take Out order first."
                                )
                            }
                        }
                    } else {
                        activity?.runOnUiThread {
                            mBinding.cbDelivery.isChecked = false
                            mBinding.cbTakeout.isChecked = true
                            mBinding.layoutDelivery.visibility = View.GONE
                            mBinding.layoutTakeout.visibility = View.VISIBLE
                        }
                    }

                } else {
//                    val cartItems = db.cartDao().getAllWithOUtTerminal()
                    val cartItems = db.cartDao().getAll()
                    if (cartItems.isNotEmpty()) {
                        if (param == "takeout") {
                            activity?.runOnUiThread {
                                mBinding.cbTakeout.isChecked = false
                                mBinding.cbDelivery.isChecked = true
                                mBinding.cvDeliveryContinue.isEnabled = false
                                mBinding.cvDeliveryContinue.isClickable = false
                                takeoutButtonState = false
                                showView(mBinding.layoutDelivery)
                                hideView(mBinding.layoutTakeout)
                                context?.let { it1 ->
                                    showShortToast(
                                        "Please checkout Delivery order first."
                                    )
                                }
                            }
                        } else {
                            activity?.runOnUiThread {
                                mBinding.cbDelivery.isChecked = true
                                mBinding.cbTakeout.isChecked = false
                                mBinding.layoutDelivery.visibility = View.VISIBLE
                                mBinding.layoutTakeout.visibility = View.GONE
                            }
                        }
                    }
                }
            } else {
                if (param == "takeout") {
                    activity?.runOnUiThread {
                        hideView(mBinding.layoutDelivery)
                        showView(mBinding.layoutTakeout)
                    }
                } else {
                    activity?.runOnUiThread {
                        showView(mBinding.layoutDelivery)
                        hideView(mBinding.layoutTakeout)
                    }
                }
            }
        }
    }

    private fun initTimeSlots() {
        timeSlotArray.add(TimeSlot().apply {
            slot = "10:30 AM"
            time_ = "10:00:00"
//            time_ = "10:30 AM"

        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "10:45 AM"
            time_ = "10:40:00"
//            time_ = "10:45:00"
//            time_ = "10:45 AM"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "11:00 AM"
            time_ = "11:00:00"
//            time_ = "11:00 AM"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "11:15 AM"
            time_ = "11:15:00"
//            time_ = "11:15 AM"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "11:30 AM"
            time_ = "11:30:00"
//            time_ = "11:30 AM"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "11:45 AM"
            time_ = "11:45:00"
//            time_ = "11:45 AM"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "12:00 PM"
            time_ = "12:00:00"
//            time_ = "12:00 PM"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "12:15 PM"
            time_ = "12:15:00"
//            time_ = "12:15 PM"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "12:30 PM"
            time_ = "12:30:00"
//            time_ = "12:30:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "12:45 PM"
            time_ = "12:45:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "01:00 PM"
//            time_ = "12:15:00"
            time_ = "13:00:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "01:15 PM"
//            time_ = "12:15:00"
            time_ = "13:15:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "01:30 PM"
//            time_ = "12:15:00"
            time_ = "13:30:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "01:45 PM"
//            time_ = "12:15:00"
//            time_ = "01:45 PM"
            time_ = "13:45:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "02:00 PM"
//            time_ = "12:15:00"
            time_ = "14:00:00"

        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "02:15 PM"
//            time_ = "12:15:00"
            time_ = "14:15:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "02:30 PM"
//            time_ = "12:15:00"
            time_ = "14:30:00"


        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "02:45 PM"
//            time_ = "12:15:00"
            time_ = "14:45:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "03:00 PM"
//            time_ = "12:15:00"
            time_ = "15:00:00"

        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "03:15 PM"
//            time_ = "12:15:00"
            time_ = "15:15:00"

        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "03:30 PM"
//            time_ = "12:15:00"
            time_ = "15:30:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "03:45 PM"
//            time_ = "12:15:00"
            time_ = "15:45:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "04:00 PM"
//            time_ = "12:15:00"
            time_ = "16:00:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "04:15 PM"
//            time_ = "12:15:00"
            time_ = "16:15:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "04:30 PM"
//            time_ = "12:15:00"
            time_ = "16:30:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "04:45 PM"
//            time_ = "12:15:00"
            time_ = "16:45:00"

        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "05:00 PM"
//            time_ = "12:15:00"
            time_ = "17:00:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "05:15 PM"
//            time_ = "12:15:00"
            time_ = "17:15:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "05:30 PM"
//            time_ = "12:15:00"
            time_ = "17:30:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "05:45 PM"
//            time_ = "12:15:00"
            time_ = "17:45:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "06:00 PM"
//            time_ = "12:15:00"
            time_ = "18:00:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "06:15 PM"
//            time_ = "12:15:00"
            time_ = "18:15:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "06:30 PM"
//            time_ = "12:15:00"
            time_ = "18:30:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "06:45 PM"
//            time_ = "12:15:00"
            time_ = "18:45:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "07:00 PM"
//            time_ = "12:15:00"
            time_ = "19:00:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "07:15 PM"
//            time_ = "12:15:00"
            time_ = "19:15:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "07:30 PM"
//            time_ = "12:15:00"
            time_ = "19:30:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "07:45 PM"
//            time_ = "12:15:00"
            time_ = "19:45:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "08:00 PM"
//            time_ = "12:15:00"
            time_ = "20:00:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "08:15 PM"
//            time_ = "12:15:00"
            time_ = "20:15:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "08:30 PM"
//            time_ = "12:15:00"
            time_ = "20:30:00"
        })
        timeSlotArray.add(TimeSlot().apply {
            slot = "08:45 PM"
//            time_ = "12:15:00"
            time_ = "20:45:00"
        })

        //init mp-delivery slots
//        deliveryTimeSlotArray.add(TimeSlot().apply {
//            slot = "10am - 12pm"
////            time_ = "12:00:00"
//            time_ = "12:00:00"
//        })
//        deliveryTimeSlotArray.add(TimeSlot().apply {
//            slot = "12pm - 02pm"
//            time_ = "14:00:00"
////            time_ = "02:00:00"
//        })
//        deliveryTimeSlotArray.add(TimeSlot().apply {
//            slot = "02pm - 04pm"
//            time_ = "16:00:00"
////            time_ = "04:00:00"
//        })
//        deliveryTimeSlotArray.add(TimeSlot().apply {
//            slot = "04pm - 06pm"
//            time_ = "18:00:00"
////            time_ = "06:00:00"
//        })
//        deliveryTimeSlotArray.add(TimeSlot().apply {
//            slot = "06pm - 08pm"
//            time_ = "20:00:00"
////            time_ = "08:00:00"
//        })
//        deliveryTimeSlotArray.add(TimeSlot().apply {
//            slot = "08pm - 10pm"
//            time_ = "22:00:00"
////            time_ = "10:00:00"
//        })

/*Usama edit*/
        deliveryTimeSlotArray.add(TimeSlot().apply {
            slot = "10am - 02pm"
            time_ = "12:00:00"
//            time_ = "10:00:00"
        })
        deliveryTimeSlotArray.add(TimeSlot().apply {
            slot = "02pm - 06pm"
            time_ = "04:00:00"
//            time_ = "10:00:00"
        })
    }

    private fun initAdapter() {
        locationsAdapter = LocationsAdapter(
            locations = locations,
            itemClickListener = this,
            viewType = Constants.VIEW_TYPE_TOGGLE,
            itemType = Constants.VIEW_TPYE_SMALL
        )
        mBinding.rvLocations.adapter = locationsAdapter
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.GET_LOCATIONS_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    mBinding.progressBar.visibility = View.GONE
                    val location = JsonManager.getInstance().getLocationsList(it.resultObject)
                    locations.addAll(location)
                    if (locations.size == 0) {
                        showView(mBinding.tvNoLocationFound)
                        hideView(mBinding.progressBar)
                        hideView(mBinding.rvLocations)
                    } else {
                        hideView(mBinding.tvNoLocationFound)
                        hideView(mBinding.progressBar)
                        showView(mBinding.rvLocations)
                    }

                    locations.forEach { it.isChecked = false }
                    locationsAdapter.notifyDataSetChanged()
                }
                ApplicationEnum.GET_LOCATIONS_ERROR -> {
                    AppProgressDialog.dismissProgressDialog()
                    showView(mBinding.tvNoLocationFound)
                    hideView(mBinding.progressBar)
                    hideView(mBinding.rvLocations)
                }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                }
            }
        }
    }

    private fun setViews() {


        mBinding.cbDelivery.isChecked = true
//        if (key == "pre_filled") {
//            if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_ORDER_TYPE) == IntentParams.TAKEOUT) {
//                mBinding.cbDelivery.isChecked = false
//                mBinding.cbTakeout.isChecked = true
//                mBinding.cbDelivery.isEnabled = false
//                mBinding.layoutDelivery.visibility = View.GONE
//                mBinding.layoutTakeout.visibility = View.VISIBLE
//            } else {
//                mBinding.cbDelivery.isChecked = true
//                mBinding.cbTakeout.isChecked = false
//                mBinding.cbTakeout.isEnabled = false
//                mBinding.layoutDelivery.visibility = View.VISIBLE
//                mBinding.layoutTakeout.visibility = View.GONE
//            }
//            setPreFilledData()
//
//        } else {
//            if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_ORDER_TYPE).isNotEmpty()) {
//                if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_ORDER_TYPE) == IntentParams.TAKEOUT) {
//                    mBinding.cbDelivery.isChecked = false
//                    mBinding.cbTakeout.isChecked = true
//                    mBinding.layoutDelivery.visibility = View.GONE
//                    mBinding.layoutTakeout.visibility = View.VISIBLE
//                } else {
//                    mBinding.cbDelivery.isChecked = true
//                    mBinding.cbTakeout.isChecked = false
//                    mBinding.layoutDelivery.visibility = View.VISIBLE
//                    mBinding.layoutTakeout.visibility = View.GONE
//                }
//                setPreFilledData()
//            } else {
//                mBinding.cbDelivery.isChecked = true
//                mBinding.cbTakeout.isChecked = false
//                mBinding.layoutDelivery.visibility = View.VISIBLE
//                mBinding.layoutTakeout.visibility = View.GONE
//            }
//        }

    }

    private fun setPreFilledData() {
        if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE).isNotEmpty()) {
            isDateSelected = true
            mBinding.tvSelectDate.text = AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
            mBinding.tvSelectTakeoutDate.text =
                AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
        }

        if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW).isNotEmpty()) {
            isTimeSelected = true
            mBinding.tvSpinnerSelectedTakeoutTime.text =
                AppPreferenceManager.getValue(IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW)
            mBinding.tvSpinnerSelectedItem.text =
                AppPreferenceManager.getValue(IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW)
        }
    }

    private fun getLocations() {
//        context?.let { AppProgressDialog.showProgressDialog(it) }
        viewModel.getAddress()
    }

    private fun checkIfPastNinePM(): Boolean {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val min = cal.get(Calendar.MINUTE)
        val isCurrentNight = cal.get(Calendar.AM_PM)
        return if (cal.get(Calendar.AM_PM) == Calendar.PM) {
            hour >= 9
        } else {
            false
        }
    }

    private fun checkIfPastEightFortyFivePM(): Boolean {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val min = cal.get(Calendar.MINUTE)
        val isCurrentNight = cal.get(Calendar.AM)
        return if (cal.get(Calendar.AM_PM) == Calendar.PM) {
            hour >= 8 && min >= 45
        } else {
            false
        }
    }

    private fun initClickListener() {
        mBinding.cvDeliveryContinue.setOnClickListener {
            if (checkDataForDelivery() && deliveryButtonState) {

                var selectedDateInt = 0
                val selectedDateStr = AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
                if (selectedDateStr.isNotEmpty() && selectedDateStr.contains("-")) {
                    val arr = selectedDateStr.split("-")
                    if (arr.size >= 3) {
                        selectedDateInt = selectedDateStr.split("-")[2].toInt()
                    }
                }
                if (selectedDateInt > 0) {
                    val cal = Calendar.getInstance()
                    val currentDate = cal.get(Calendar.DATE)
                    if (selectedDateInt == currentDate + 1) {
                        AppProgressDialog.showProgressDialog(context = context!!)
                        (activity as BaseActivity?)?.isPastNineOrNot(
                            kitchenTimeListener = object : KitchenTimeListener {
                                override fun isKitchenOpen(
                                    isKitchenOpen: Boolean,
                                    canadianTime: String
                                ) {
                                }

                                override fun isPastNine(isPastNine: Boolean, canadianTime: String) {
                                    Log.d("Canadian Time", canadianTime)
                                    AppProgressDialog.dismissProgressDialog()
                                    if (isPastNine) {
                                        showWarningToast(message = "Please choose later date!")
                                    } else {
                                        AppPreferenceManager.setValue(
                                            IntentParams.MEAL_PREP_ORDER_TYPE,
                                            flag
                                        )
                                        AppPreferenceManager.setValue(
                                            IntentParams.ORDERTYPE,
                                            ApplicationEnum.MEAL_PREP.toString()
                                        )
//                                        AppPreferenceManager.setValue(
//                                            IntentParams.ORDERTYPE,
//                                            "MealPrep Delivery"
//                                        )
                                        dismiss()
                                        twoButtonsDialogListener?.deliverySelected(
                                            "delivery date",
                                            "time",
                                            locations.find { it.isChecked }?.id.toString()
                                        )
                                    }
                                }

                                override fun isError() {
                                    AppProgressDialog.dismissProgressDialog()
                                    showWarningToast(message = Constants.INTERNET_ERROR_MESSAGE)
                                }
                            }
                        )

//                        if (checkIfPastNinePM()) {
//                            showWarningToast(message = "Please choose later date!")
//                        } else {
//                            AppPreferenceManager.setValue(IntentParams.MEAL_PREP_ORDER_TYPE, flag)
//                            AppPreferenceManager.setValue(
//                                IntentParams.ORDERTYPE,
//                                ApplicationEnum.MEAL_PREP.toString()
//                            )
//                            dismiss()
//                            twoButtonsDialogListener?.deliverySelected(
//                                "delivery date",
//                                "time",
//                                "location"
//                            )
//                        }

                    } else {
                        AppPreferenceManager.setValue(IntentParams.MEAL_PREP_ORDER_TYPE, flag)
                        AppPreferenceManager.setValue(
                            IntentParams.ORDERTYPE,
                            ApplicationEnum.MEAL_PREP.toString()
                        )
//                        AppPreferenceManager.setValue(
//                                            IntentParams.ORDERTYPE,
//                                            "MealPrep Delivery"
//                                        )
                        dismiss()
                        twoButtonsDialogListener?.deliverySelected(
                            "delivery date",
                            "time",
                            locations.find { it.isChecked }?.id.toString()

                        )
                    }
                } else {
                    AppPreferenceManager.setValue(IntentParams.MEAL_PREP_ORDER_TYPE, flag)
                    AppPreferenceManager.setValue(
                        IntentParams.ORDERTYPE,
                        ApplicationEnum.MEAL_PREP.toString()
                    )
                    dismiss()
                    twoButtonsDialogListener?.deliverySelected(
                        "delivery date", "time", locations.find { it.isChecked }?.id.toString()
                    )
                }

            }
        }

        mBinding.cvTakeoutContinue.setOnClickListener {
            if (checkDataForTakeAway() && takeoutButtonState) {

//                if (checkIfPastEightFortyFivePM()) {
//                    showWarningToast(message = "Please choose later date!")
//                } else {
                AppPreferenceManager.setValue(IntentParams.MEAL_PREP_ORDER_TYPE, flag)
                AppPreferenceManager.setValue(
                    IntentParams.ORDERTYPE,
                    ApplicationEnum.MEAL_PREP.toString()
                )
                dismiss()
                twoButtonsDialogListener?.takeOutSelected("takeout date", "time")
//                }
            }
        }

        mBinding.rlSpinnerLayout.setOnClickListener {
            CommonMethods.showOptionsDialog(
                fragmentManager = childFragmentManager,
                list = deliveryTimeSlotArray,
                optionMenuDialogListener = object : SelectedTimeSlotListener {
                    override fun timeSlot(timeSlot: TimeSlot) {
                        mBinding.tvSpinnerSelectedItem.text = timeSlot.slot
                        isTimeSelected = true
                        AppPreferenceManager.setValue(
                            IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW,
                            timeSlot.slot
                        )
                        AppPreferenceManager.setValue(
                            IntentParams.MEAL_PREP_TIME_SLOT_TO_SEND,
                            timeSlot.time_
                        )
//                            isAmountSelected = true
//                            amount = timeSlot.slot
                    }
                })
        }

        mBinding.rlSpinnerLayoutTakeout.setOnClickListener {
            CommonMethods.showOptionsDialog(
                fragmentManager = childFragmentManager,
                list = timeSlotArray,
                optionMenuDialogListener = object : SelectedTimeSlotListener {
                    override fun timeSlot(timeSlot: TimeSlot) {
                        mBinding.tvSpinnerSelectedTakeoutTime.text = timeSlot.slot
                        isTimeSelected = true
                        AppPreferenceManager.setValue(
                            IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW,
                            timeSlot.slot
                        )
                        AppPreferenceManager.setValue(
                            IntentParams.MEAL_PREP_TIME_SLOT_TO_SEND,
                            timeSlot.time_
                        )
                    }
                })
        }

        mBinding.cbTakeout.setOnClickListener {
            flag = "takeout"
            mBinding.cbDelivery.isChecked = false
//            hideView(mBinding.layoutDelivery)
//            showView(mBinding.layoutTakeout)
            // AppPreferenceManager.setValue(IntentParams.MEAL_PREP_ORDER_TYPE, IntentParams.TAKEOUT)

//            mBinding.tvSelectDate.text = "Takeout Date"
//            mBinding.tvSpinnerSelectedItem.text = "Takeout Time"
            cartCheck("takeout")

        }
        mBinding.cbDelivery.setOnClickListener {
            flag = "delivery"
            mBinding.cbTakeout.isChecked = false
            hideView(mBinding.layoutTakeout)
            showView(mBinding.layoutDelivery)
            //  AppPreferenceManager.setValue(IntentParams.MEAL_PREP_ORDER_TYPE, IntentParams.DELIVERY)
//            mBinding.tvSelectDate.text = "Delivery Date"
//            mBinding.tvSpinnerSelectedItem.text = "Delivery Time"

            cartCheck("delivery")
        }
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.tvSelectDate.setOnClickListener {
            context?.let { it1 -> openDatePickerDialog(it1, this) }

        }
        mBinding.tvSelectTakeoutDate.setOnClickListener {
            context?.let { it1 -> openDatePickerDialog(it1, this) }
        }
    }

    private fun checkDataForTakeAway(): Boolean {
        if (!isDateSelected) {
            showWarningToast("Select Date")
            return false
        }
        return if (!isTimeSelected) {
            showWarningToast("Select Time")
            false
        } else {
            true
        }
    }

    private fun checkDataForDelivery(): Boolean {
        if (!isDateSelected) {
            showWarningToast("Select Date")
            return false
        }
        if (!isTimeSelected) {
            showWarningToast("Select Time")
            return false
        }
        return if (!isLocationSelected) {
            showWarningToast("Select Location")
            false
        } else {
            true
        }
    }

    private fun openDatePickerDialog(
        context: Context,
        dateTimePickerListener: DateTimePickerListener?
    ) {
        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val dpd = DatePickerDialog(
            context,
//            R.style.MyDatePickerDialogTheme,
//                R.style.DialogTheme,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                dateTimePickerListener?.pickedDateTime(
                    year = year,
                    month = month + 1,
                    minute = 0,
                    hour = 0,
                    second = 0,
                    date = dayOfMonth
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        //not select previous Data
        dpd.datePicker.minDate = calendar.timeInMillis + 1000 * 60 * 60 * 24 * 1
        dpd.datePicker.maxDate = calendar.timeInMillis + 1000 * 60 * 60 * 24 * 7
        dpd.setOnCancelListener {
            DialogInterface.OnCancelListener {
                dateTimePickerListener?.pickedDateTime(
                    year = 0, month = 0, minute = 0, hour = 0, second = 0, date = 0
                )
            }
        }
        dpd.show()
    }

    fun setListener(twoButtonsDialogListener: MealPrepPopupListener?) {
        this.twoButtonsDialogListener = twoButtonsDialogListener
    }

    companion object {

        @JvmStatic
        fun newInstance(key: String) =
            MealPrepDeliveryTypePopUp().apply {
                arguments = Bundle().apply {
                    putString("key", key)
                }
            }
    }

    override fun itemClicked(position: Int) {
        showShortToast("item clicked")
    }

    override fun brandTapped(position: Int, title: String) {
        showShortToast("brand Tapped")
    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {
        showShortToast("makeDefaultLocation")
    }

    override fun deleteLocation(position: Int, addressId: Int) {
        isLocationSelected = true
        locations.forEach { it.isChecked = false }
        locations[position].isChecked = true
        AppPreferenceManager.setValue(
            IntentParams.MEAL_PREP_LOCATION_ID,
            locations[position].id.toString()
        )
        AppPreferenceManager.setValue(
            IntentParams.MEAL_PREP_LOCATION,
            locations[position].address
        )
        locationsAdapter.notifyDataSetChanged()
    }

    override fun pickedDateTime(
        year: Int,
        month: Int,
        date: Int,
        hour: Int,
        minute: Int,
        second: Int
    ) {
        val date_ = "$year-$month-$date"
        val formattedDate = CommonMethods.dateFormatter4(date_)
        if (formattedDate != null) {
            AppPreferenceManager.setValue(IntentParams.MEAL_PREP_DATE, formattedDate)
        }
        isDateSelected = true
        mBinding.tvSelectDate.text = formattedDate
        mBinding.tvSelectTakeoutDate.text = formattedDate
    }

    override fun orderCompleted(position: Int) {

    }
}