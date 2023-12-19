package com.codingpixel.dedicatekitchen.activities.checkout

import android.annotation.SuppressLint
import com.codingpixel.dedicatekitchen.fragments.dialogs.SelectKitchenDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.application.MyApplication
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.base.DateTimePickerListener
import com.codingpixel.dedicatekitchen.databinding.ShippingActivityBinding
import com.codingpixel.dedicatekitchen.fragments.dialogs.MealPrepDeliveryTypePopUp
import com.codingpixel.dedicatekitchen.fragments.dialogs.SelectTimeDialog
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.MealPrepPopupListener
import com.codingpixel.dedicatekitchen.interfaces.SelectListener
import com.codingpixel.dedicatekitchen.interfaces.SelectedTimeSlotListener
import com.codingpixel.dedicatekitchen.models.DedicateKitchen
import com.codingpixel.dedicatekitchen.models.OrderModel
import com.codingpixel.dedicatekitchen.models.TimeSlot
import com.codingpixel.dedicatekitchen.models.UserLocation
import com.codingpixel.dedicatekitchen.updates.SelectGroupDialog

class ShippingMethod : BaseActivity(), ItemClickListener, DateTimePickerListener {
    private lateinit var addressList: List<String>
    private lateinit var mBinding: ShippingActivityBinding
    private lateinit var locationsAdapter: LocationsAdapter
    private val locations = ArrayList<UserLocation>()
    private var selectedKitchen: String = ""
    private var selectedKitchenId: String = ""
    private var selectedTime: Int = 0

    //    private var orderType: String = ""
    private var isKitchenSelected: Boolean = false
    private var isTimeSelected: Boolean = false
    private var isDateSelected: Boolean = false

    private var groupName = ""

    private var orderArray = ArrayList<OrderModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.shipping_activity)
        changeStatusBarColor(statusBarColor = getColor(R.color.grayBgColor))
        getIntentData()
        initApiResponseObserver()
        initClickListener()
        initAdapter()

        isKitchenSelected = true
        mBinding.tvSpinnerSelectedKitchen.text =
            AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.name
                ?: "No Kitchen is Selected"
        selectedKitchen = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.name
            ?: "No Kitchen is Selected"
        selectedKitchenId =
            AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
                ?: ""


        mBinding.etFirstName.setText(getLoggedInUser()?.fname ?: "")
        mBinding.etLastName.setText(getLoggedInUser()?.lname ?: "")
    }

    private fun initClickListener() {
        mBinding.tvSelectGroup.setOnClickListener {

            CommonMethods.showSelectGroupDialog(
                supportFragmentManager,
                object : SelectedTimeSlotListener {
                    override fun timeSlot(timeSlot: TimeSlot) {
                        mBinding.tvSelectGroup.text = timeSlot.slot
                        groupName = timeSlot.slot
                    }
                }
            )
        }

        mBinding.tvEdit.setOnClickListener {

            CommonMethods.showMealPrepDeliveryPopUp(
                fragmentManager = supportFragmentManager,
                type = "pre_filled",
                mealPrepListener = object : MealPrepPopupListener {
                    override fun deliverySelected(date: String, time: String, location: String) {
                        mBinding.tvDeliveryAddress.text =
                            AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION)
                        mBinding.tvDateAndTime.text = CommonMethods.dateFormatter2(
                            AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
                        ) + " " + AppPreferenceManager.getValue(
                            IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW
                        )
                    }

                    override fun takeOutSelected(date: String, time: String) {
                        mBinding.tvDeliveryAddress.text =
                            AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION)
                        mBinding.tvDateAndTime.text = CommonMethods.dateFormatter2(
                            AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
                        ) + " " + AppPreferenceManager.getValue(
                            IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW
                        )
                    }
                }
            )


//            val mealPrepPopup = MealPrepDeliveryTypePopUp.newInstance("pre_filled")
//            mealPrepPopup.setListener(object : MealPrepPopupListener {
//                @SuppressLint("SetTextI18n")
//                override fun deliverySelected(
//                    date: String,
//                    time: String,
//                    location: String
//                ) {
//                    mBinding.tvDeliveryAddress.text =
//                        AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION)
//                    mBinding.tvDateAndTime.text = CommonMethods.dateFormatter2(
//                        AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
//                    ) + " " + AppPreferenceManager.getValue(
//                        IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW
//                    )
//                }
//
//                @SuppressLint("SetTextI18n")
//                override fun takeOutSelected(date: String, time: String) {
//                    mBinding.tvDeliveryAddress.text =
//                        AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION)
//                    mBinding.tvDateAndTime.text = CommonMethods.dateFormatter2(
//                        AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
//                    ) + " " + AppPreferenceManager.getValue(
//                        IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW
//                    )
//                }
//            })
//            mealPrepPopup.show(
//                supportFragmentManager,
//                MealPrepDeliveryTypePopUp::class.java.canonicalName
//            )
        }
        mBinding.tvPickupDate.setOnClickListener {
            openDatePickerDialog(this, this)
        }
        mBinding.ivGotoHome.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, MainBottomBarActivity::class.java))
        }

        mBinding.spinnerSelectKitchen.setOnClickListener {


//            val eventPopup = SelectKitchenDialog.newInstance()
//            eventPopup.selectListener(object : SelectListener {
//                override fun selectKitchen(kitchen: String, id: String) {
//                    isKitchenSelected = true
//                    mBinding.tvSpinnerSelectedKitchen.text = kitchen
//                    selectedKitchen = kitchen
//                    selectedKitchenId = id
//                }
//
//
//                override fun changeKitchen(selectedKitchen: DedicateKitchen) {
//                    isKitchenSelected = true
//                    mBinding.tvSpinnerSelectedKitchen.text = selectedKitchen.name
//                    this@ShippingMethod.selectedKitchen = selectedKitchen.name
//                    selectedKitchenId = selectedKitchen.terminalId
//                }
//
//                override fun selectTime(time: Int, timeValue: String) {
//
//                }
//            })
//            eventPopup.show(
//                supportFragmentManager,
//                SelectKitchenDialog::class.java.canonicalName
//            )
        }

        mBinding.spinnerSelectPickupTime.setOnClickListener {
            val eventPopup = SelectTimeDialog.newInstance(CommonMethods.getTimeSlots())
            eventPopup.selectListener(object : SelectedTimeSlotListener {
                override fun timeSlot(timeSlot: TimeSlot) {
                    mBinding.tvSpinnerSelectedTime.text = timeSlot.slot
                    selectedTime = timeSlot.time
                    AppPreferenceManager.setValue(
                        IntentParams.MEAL_PREP_TIME_SLOT_TO_SEND,
                        timeSlot.time.toString()
                    )
                    //showShortToast(selectedTime.toString())
                    isTimeSelected = true
                }
            })
            eventPopup.show(
                supportFragmentManager,
                SelectTimeDialog::class.java.canonicalName
            )
        }

        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }

        mBinding.tvDelivery.setOnClickListener {
            hideView(mBinding.clTakeOut)
            showView(mBinding.clDelivery)
            mBinding.tvDelivery.background.setColorFilter(
                resources.getColor(R.color.darkLightGrayLabelColor),
                PorterDuff.Mode.SRC_ATOP
            )
            mBinding.tvDelivery.setTextColor(getColor(R.color.white))
            mBinding.tvPickUp.background.setColorFilter(
                resources.getColor(R.color.unSelectedFilterColor),
                PorterDuff.Mode.SRC_ATOP
            )
            mBinding.tvPickUp.setTextColor(getColor(R.color.darkLightGrayLabelColor))
        }

        mBinding.tvPickUp.setOnClickListener {
            showView(mBinding.clTakeOut)
            hideView(mBinding.clDelivery)
            mBinding.tvDelivery.background.setColorFilter(
                resources.getColor(R.color.unSelectedFilterColor),
                PorterDuff.Mode.SRC_ATOP
            )
            mBinding.tvDelivery.setTextColor(getColor(R.color.darkLightGrayLabelColor))
            mBinding.tvPickUp.background.setColorFilter(
                resources.getColor(R.color.darkLightGrayLabelColor),
                PorterDuff.Mode.SRC_ATOP
            )
            mBinding.tvPickUp.setTextColor(getColor(R.color.white))
        }

        mBinding.tvAddAnother.setOnClickListener {
            hideView(mBinding.rvAddress)
            hideView(mBinding.tvAddAnother)
            hideView(mBinding.tvYourAddressHeading)
        }

        mBinding.cvCheckOut.setOnClickListener {
            if (groupName.isBlank()) {
                showWarningToast("Please select group")
                return@setOnClickListener
            }

            MyApplication.groupName = groupName

            val date = CommonMethods.addMinutesToCurrentDate(selectedTime)
            val date23 = CommonMethods.dateFormatter3(date.toString())
            if (date23 != null) {
                AppPreferenceManager.setValue(IntentParams.instantTakeoutTime, date23)
            }
            if (AppPreferenceManager.getValue(IntentParams.ORDERTYPE) == ApplicationEnum.MEAL_PREP.toString()) {
                if (checkDataSelectedForMealPrep()) {
                    startActivity(
                        Intent(
                            this@ShippingMethod,
                            SelectPaymentMethodActivity::class.java
                        ).putExtra(IntentParams.ORDERITEMS, orderArray)
                            .putExtra(IntentParams.KITCHEN, selectedKitchen)
                            .putExtra(IntentParams.TIME, selectedTime)
                            .putExtra(
                                "note",
                                mBinding.etFirstName.text.toString()
                                    .trim() + " " + mBinding.etLastName.text.toString().trim()
                            )
                    )
                }
            } else {
                if (checkDataSelectedForTakeout()) {
                    startActivity(
                        Intent(
                            this@ShippingMethod,
                            SelectPaymentMethodActivity::class.java
                        ).putExtra(IntentParams.ORDERITEMS, orderArray)
                            .putExtra(IntentParams.KITCHEN, selectedKitchen)
                            .putExtra(IntentParams.TIME, selectedTime)
                            .putExtra(
                                "note",
                                mBinding.etFirstName.text.toString()
                                    .trim() + " " + mBinding.etLastName.text.toString().trim()
                            )
                    )
                }
            }
        }
    }


    private fun getIntentData() {
        orderArray = intent.getSerializableExtra(IntentParams.ORDERITEMS) as ArrayList<OrderModel>
        setViews()
    }

    @SuppressLint("SetTextI18n")
    private fun setViews() {
        // showShortToast(AppPreferenceManager.getValue(IntentParams.ORDERTYPE))
        if (AppPreferenceManager.getValue(IntentParams.ORDERTYPE) == ApplicationEnum.MEAL_PREP.toString()) {
            hideView(mBinding.tvSelectPickupDateHeading)
            hideView(mBinding.tvSelectPickupHeading)
            hideView(mBinding.tvSelectPickupDateHeading)
            hideView(mBinding.spinnerSelectPickupTime)
            hideView(mBinding.tvPickupDate)
            showView(mBinding.clMealPrep)
            if (AppPreferenceManager.getValue(IntentParams.MEAL_PREP_ORDER_TYPE) == IntentParams.TAKEOUT) {
                invisibleView(mBinding.tvDelivery)
                showView(mBinding.tvPickUp)
                hideView(mBinding.tvTitleDeliveryAddress)
                hideView(mBinding.tvDeliveryAddress)
                mBinding.tvTitleDateAndTime.text = "Pickup Date & Time:  "
                mBinding.tvDateAndTime.text = CommonMethods.dateFormatter2(
                    AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
                ) + " " + AppPreferenceManager.getValue(
                    IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW
                )
            } else {
                showView(mBinding.tvDelivery)
                invisibleView(mBinding.tvPickUp)
                showView(mBinding.tvTitleDeliveryAddress)
                showView(mBinding.tvDeliveryAddress)
                mBinding.tvDeliveryAddress.text =
                    AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION)
                addressList =
                    AppPreferenceManager.getValue(IntentParams.MEAL_PREP_LOCATION).chunked(50)
                mBinding.tvTitleDateAndTime.text = "Delivery Date & Time:  "
                mBinding.tvDateAndTime.text =
                    CommonMethods.dateFormatter2(
                        AppPreferenceManager.getValue(IntentParams.MEAL_PREP_DATE)
                    ) + " " + AppPreferenceManager.getValue(
                        IntentParams.MEAL_PREP_TIME_SLOT_TO_SHOW
                    )
            }
        } else {
            invisibleView(mBinding.tvDelivery)
            showView(mBinding.tvPickUp)
            showView(mBinding.tvSelectPickupHeading)
            showView(mBinding.tvSelectPickupDateHeading)
            showView(mBinding.tvSelectPickupDateHeading)
            showView(mBinding.spinnerSelectPickupTime)
            showView(mBinding.tvPickupDate)
            hideView(mBinding.clMealPrep)
        }
    }


    private fun checkDataSelectedForTakeout(): Boolean {
        if (!isKitchenSelected) {
            showWarningToast("Please Select Kitchen")
            return false
        }
        if (!isTimeSelected) {
            showWarningToast("Please Select Time")
            return false
        }
        if (!isDateSelected) {
            showWarningToast("Please Select Pickup Date")
            return false
        }
        if (isEmpty(mBinding.etFirstName)) {
            showWarningToast("Please Enter First Name")
            return false
        }

        return if (isEmpty(mBinding.etLastName)) {
            showWarningToast("Please Enter Last Name")
            false
        } else {
            true
        }
    }

    private fun checkDataSelectedForMealPrep(): Boolean {
        if (!isKitchenSelected) {
            showWarningToast("Please Select Kitchen")
            return false
        }
        if (isEmpty(mBinding.etFirstName)) {
            showWarningToast("Please Enter First Name")
            return false
        }
        return if (isEmpty(mBinding.etLastName)) {
            showWarningToast("Please Enter Last Name")
            false
        } else {
            true
        }
    }

    private fun initApiResponseObserver() {

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

    }

    override fun brandTapped(position: Int, title: String) {
    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {

    }

    override fun deleteLocation(position: Int, addressId: Int) {
    }

    @SuppressLint("SetTextI18n")
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
        mBinding.tvPickupDate.text = formattedDate
    }

    override fun orderCompleted(position: Int) {

    }
}