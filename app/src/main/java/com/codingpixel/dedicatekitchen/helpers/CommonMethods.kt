package com.codingpixel.dedicatekitchen.helpers

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.FragmentManager
import com.codingpixel.dedicatekitchen.BuildConfig
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.fragments.account.AccountInformationDialogFragment
import com.codingpixel.dedicatekitchen.fragments.dialogs.*
import com.codingpixel.dedicatekitchen.interfaces.*
import com.codingpixel.dedicatekitchen.models.*
import com.codingpixel.dedicatekitchen.models.local.AccountSectionItem
import com.codingpixel.dedicatekitchen.models.local.AccountSubSectionItem
import com.codingpixel.dedicatekitchen.models.local.IntroSlider
import com.codingpixel.dedicatekitchen.models.local.ToggleOption
import com.codingpixel.dedicatekitchen.updates.SelectGroupDialog
import org.joda.time.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class CommonMethods {

    companion object {

        fun getKitchensList(): ArrayList<DedicateKitchen> {
            val listOfKitchens = ArrayList<DedicateKitchen>()
            listOfKitchens.add(DedicateKitchen().apply {
                did = 1
                name = "Dedicate Fort McMurray"
                terminalId = "20000007"
                apiUrl = "CoPixDedicateKitchen-20000002"
//                merchantID = "763719" // test
                merchantID = "407917" // live
//                merchantID = "974482"
//                merchantPassword = "43199358" // test
                merchantPassword = "71474481" // live
//                merchantPassword = "17785788"
//                merchantName = "Web-Merchant"
//                merchantName = "974482" // test
                merchantName = "486730" // live
                menuSetName = "CodingPixelOnlineMenu"

            })
            listOfKitchens.add(DedicateKitchen().apply {
                did = 2
                name = "Dedicate Calgary"
                terminalId = "20000008"
                apiUrl = "CoPixDedicateKitchen-20000005"
                //merchantID = "763719" // test
                merchantID = "407917" // live
//                merchantID = "320883"
//                merchantPassword = "43199358" // test
                merchantPassword = "71474481" // live
//                merchantPassword = "61876817"
//                merchantName = "Merchant_Ont_En"
//                merchantName = "320883" // test
                merchantName = "422308" // live
                menuSetName = "Calgary"
            })
            return listOfKitchens
        }

        fun getKitchenNameByTerminalID(terminalId: String): String {
            val listOfKitchens = getKitchensList()
            val matchedIndex = listOfKitchens.indexOfFirst { it.terminalId == terminalId }
            return if (matchedIndex != -1)
                listOfKitchens[matchedIndex].name
            else
                "N/A"
        }

        fun getTotalPoints(responseString: String?): String {
            return if (responseString != null && responseString.contains("PTT=")) {
                val ptsArr = responseString.split("PTT=")
                if (ptsArr.isNotEmpty()) {
                    if (ptsArr[1].contains("&")) {
                        val index = ptsArr[1].indexOfFirst { it == '&' }
                        if (index != -1) {
                            ptsArr[1].substring(startIndex = 0, endIndex = index)
                        } else
                            "0"
                    } else
                        "0"
                } else
                    "0"
            } else
                "0"
        }

        fun getTotalBalance(responseString: String?): String {
            return if (responseString != null && responseString.contains("BLA=")) {
                val ptsArr = responseString.split("BLA=")
                if (ptsArr.isNotEmpty()) {
                    if (ptsArr[1].contains("&")) {
                        val index = ptsArr[1].indexOfFirst { it == '&' }
                        if (index != -1) {
                            ptsArr[1].substring(startIndex = 0, endIndex = index)
                        } else
                            "0"
                    } else
                        "0"
                } else
                    "0"
            } else
                "0"
        }

        // Dedicate Kitchen operates between 10:00 AM - 08:45 PM (For Take Out Orders)
        fun isKitchenClosed(): Boolean {
            val currentDate = DateTime.now()
            val currentHour = currentDate.hourOfDay
            val currentMinute = currentDate.minuteOfHour
            return when {
                currentHour < Constants.KITCHEN_OPENING_HOUR -> false
                currentHour == Constants.KITCHEN_CLOSED_HOUR ->
                    currentMinute >= Constants.KITCHEN_CLOSED_MINUTE
                else -> currentHour > Constants.KITCHEN_CLOSED_HOUR
            }
        }

        fun isNumber(value: String): Boolean {
            return value.matches("-?\\d+(\\.\\d+)?".toRegex())
        }

        fun getIntroSlidesList(context: Context): ArrayList<IntroSlider> {

            val slides = ArrayList<IntroSlider>()

            slides.add(IntroSlider().apply {
                cardImage = R.drawable.rewards_bg
                title = "Rewards"
                subTitle = "Register through our app and earn\nrewards with every purchase!"
            })

            slides.add(IntroSlider().apply {
                cardImage = R.drawable.new_meal_prep
                title = "Meal Prep"
                subTitle =
                    "Save so much time to do the things you love by ordering the most convenient and delicious meal prep meals in the city . Just heat and eat !"
            })

            slides.add(IntroSlider().apply {
                cardImage = R.drawable.new_delivery
                title = "Delivery"
                subTitle = "Get our meals delivered right to your door."
            })


            return slides
        }

        fun isValidEmail(target: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun getAccountSectionItem(): ArrayList<AccountSectionItem> {
            val accountSectionList = ArrayList<AccountSectionItem>()
            val accountSubSectionsList0 = ArrayList<AccountSubSectionItem>()
//            accountSubSectionsList0.add(AccountSubSectionItem().apply {
//                title = "Available Credits"
//                subTitle = "$98.00"
//                showNextArrow = false
//                showSwitch = false
//                showTv = true
//                sectionIndex = accountSubSectionsList0.size
//                startIcon = R.drawable.ic_account_info
//            })
//
//            accountSectionList.add(AccountSectionItem().apply {
//                sectionTitle = ""
//                subSectionsList = accountSubSectionsList0
//            })

            val accountSubSectionsList = ArrayList<AccountSubSectionItem>()
            accountSubSectionsList.add(AccountSubSectionItem().apply {
                title = "Account Information"
                subTitle = "Change your Account Informations"
                showNextArrow = true
                showSwitch = false
                showTv = false
                sectionIndex = accountSectionList.size
                startIcon = R.drawable.ic_account_info
            })
            accountSubSectionsList.add(AccountSubSectionItem().apply {
                title = "Password"
                subTitle = "Change your Password"
                showNextArrow = true
                showSwitch = false
                showTv = false
                sectionIndex = accountSectionList.size
                startIcon = R.drawable.ic_lock
            })
            accountSubSectionsList.add(AccountSubSectionItem().apply {
                title = "Payment Methods"
                subTitle = "Add your Credit & Debit cards"
                showNextArrow = true
                showSwitch = false
                showTv = false
                sectionIndex = accountSectionList.size
                startIcon = R.drawable.ic_payment_method
            })
            accountSubSectionsList.add(AccountSubSectionItem().apply {
                title = "Delivery Locations"
                subTitle = "Change your Delivery Locations"
                showNextArrow = true
                showSwitch = false
                showTv = false
                sectionIndex = accountSectionList.size
                startIcon = R.drawable.ic_delivery_locations
            })

//            accountSubSectionsList.add(AccountSubSectionItem().apply {
//                title = "Your Dedicate Favourites"
//                subTitle = "Get Your Favorite Products"
//                showNextArrow = true
//                showSwitch = false
//                showTv = false
//                sectionIndex = accountSectionList.size
//                startIcon = R.drawable.ic_favorite
//            })
//            accountSubSectionsList.add(AccountSubSectionItem().apply {
//                title = "Locations"
//                subTitle = "Find Our stores."
//                showNextArrow = true
//                showSwitch = false
//                showTv = false
//                sectionIndex = accountSectionList.size
//                startIcon = R.drawable.ic_delivery_locations
//            })
//            accountSubSectionsList.add(AccountSubSectionItem().apply {
//                title = "Loyalty"
//                subTitle = "Our Loyalty Programs"
//                showNextArrow = true
//                showSwitch = false
//                showTv = false
//                sectionIndex = accountSectionList.size
//                startIcon = R.drawable.ic_loyalty
//            })
//            accountSubSectionsList.add(AccountSubSectionItem().apply {
//                title = "Nutritional Info"
//                subTitle = "Get to know all NUTRITIONAL FACTS"
//                showNextArrow = true
//                showSwitch = false
//                showTv = false
//                sectionIndex = accountSectionList.size
//                startIcon = R.drawable.ic_nutritional
//            })

            accountSectionList.add(AccountSectionItem().apply {
                sectionTitle = "General"
                subSectionsList = accountSubSectionsList
            })

//            val accountSubSectionsList1 = ArrayList<AccountSubSectionItem>()
//            accountSubSectionsList1.add(AccountSubSectionItem().apply {
//                title = "Notifications"
//                subTitle = "Your will receive daily updates"
//                showNextArrow = false
//                showSwitch = true
//                showTv = false
//                sectionIndex = accountSectionList.size
//                startIcon = R.drawable.ic_bell
//            })
//            accountSubSectionsList1.add(AccountSubSectionItem().apply {
//                title = "Promotional Notifications"
//                subTitle = "Get notified about promotions"
//                showNextArrow = false
//                showSwitch = true
//                showTv = false
//                sectionIndex = accountSectionList.size
//                startIcon = R.drawable.ic_bell
//            })

//            accountSectionList.add(AccountSectionItem().apply {
//                sectionTitle = "Notifications"
//                subSectionsList = accountSubSectionsList1
//            })

//            val accountSubSectionsList2 = ArrayList<AccountSubSectionItem>()
//            accountSubSectionsList2.add(AccountSubSectionItem().apply {
//                title = "Rate Us"
//                subTitle = "You will receive daily updates"
//                showNextArrow = true
//                showTv = false
//                showSwitch = false
//                sectionIndex = accountSectionList.size
//                startIcon = R.drawable.ic_star
//            })
//            accountSubSectionsList2.add(AccountSubSectionItem().apply {
//                title = "FAQ"
//                subTitle = "Frequently Asked Questions"
//                showNextArrow = true
//                showSwitch = false
//                showTv = false
//                sectionIndex = accountSectionList.size
//                startIcon = R.drawable.ic_faq
//            })
//
//            accountSectionList.add(AccountSectionItem().apply {
//                sectionTitle = "More"
//                subSectionsList = accountSubSectionsList2
//            })
            return accountSectionList
        }

        fun addMinutesToCurrentDate(minutes: Int): Date {
            val oneMinuteInMiles: Long = 60000 //milli-seconds
            val date = Calendar.getInstance()
            val t = date.timeInMillis
            return Date(t + (minutes * oneMinuteInMiles))
        }

        fun showOptionsDialog(
            fragmentManager: FragmentManager,
            list: ArrayList<TimeSlot>,
            optionMenuDialogListener: SelectedTimeSlotListener? = null
        ) {

            val frag =
                fragmentManager.findFragmentByTag(SelectTimeDialog::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = SelectTimeDialog.newInstance(list = list)
            bottomSheetDialog.setCallBack(callBack = optionMenuDialogListener)
            bottomSheetDialog.show(
                fragmentManager,
                SelectTimeDialog::class.java.canonicalName
            )
        }

        fun showSelectGroupDialog(
            fragmentManager: FragmentManager,
            selectDialogInterface: SelectedTimeSlotListener? = null
        ) {

            val frag =
                fragmentManager.findFragmentByTag(SelectTimeDialog::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = SelectGroupDialog.newInstance()
            bottomSheetDialog.setCallBack(callBack = selectDialogInterface)
            bottomSheetDialog.show(
                fragmentManager,
                SelectTimeDialog::class.java.canonicalName
            )
        }

        fun showProductInfoDialog(
            fragmentManager: FragmentManager,
            category: Category? = null,
            product: MenuItemModel? = null,
            listener: ProductInfoListener? = null

        ) {

            val frag =
                fragmentManager.findFragmentByTag(ProductInfoDialogFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = ProductInfoDialogFragment.newInstance(
                category = category,
                product = product
            )
            bottomSheetDialog.setListener(callback = listener)
            bottomSheetDialog.show(
                fragmentManager,
                ProductInfoDialogFragment::class.java.canonicalName
            )
        }

        fun dateFormatter2(date: String?): String? {
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("EEE , dd MMM yyyy")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
                Log.wtf("fbnsdmbnmdvbmnv", formattedDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun dateFormatter4(date: String?): String? {
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
                Log.wtf("fbnsdmbnmdvbmnv", formattedDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun getDummyActiveOrders(): ArrayList<Order> {
            val dummyActiveOrders = ArrayList<Order>()

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_PENDING
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_IN_PROGRESS
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_PENDING
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_IN_PROGRESS
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_PENDING
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_IN_PROGRESS
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_PENDING
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_IN_PROGRESS
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_PENDING
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_IN_PROGRESS
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_PENDING
            })

            return dummyActiveOrders
        }

        fun getDummyPastOrders(): ArrayList<Order> {
            val dummyActiveOrders = ArrayList<Order>()

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_COMPLETED
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_CANCELLED
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_COMPLETED
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_CANCELLED
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_COMPLETED
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_CANCELLED
            })

            dummyActiveOrders.add(Order().apply {
                orderStatus = Constants.ORDER_STATUS_COMPLETED
            })

            return dummyActiveOrders
        }

        fun getOrderFilterOptionsListForActiveOrder(): ArrayList<ToggleOption> {

            val reportOptions = ArrayList<ToggleOption>()

            reportOptions.add(ToggleOption().apply {
                optionName = Constants.All
            })
            reportOptions.add(ToggleOption().apply {
                optionName = Constants.ORDER_STATUS_PENDING
            })
            reportOptions.add(ToggleOption().apply {
                optionName = Constants.ORDER_STATUS_IN_PROGRESS
            })
//            reportOptions.add(ToggleOption().apply {
//                optionName = Constants.ORDER_STATUS_COMPLETED
//            })
//            reportOptions.add(ToggleOption().apply {
//                optionName = Constants.ORDER_STATUS_CANCELLED
//            })

            return reportOptions
        }

        fun getOrderFilterOptionsListForPastOrder(): ArrayList<ToggleOption> {

            val reportOptions = ArrayList<ToggleOption>()

            reportOptions.add(ToggleOption().apply {
                optionName = Constants.All
            })
//            reportOptions.add(ToggleOption().apply {
//                optionName = Constants.ORDER_STATUS_PENDING
//            })
//            reportOptions.add(ToggleOption().apply {
//                optionName = Constants.ORDER_STATUS_IN_PROGRESS
//            })
            reportOptions.add(ToggleOption().apply {
                optionName = Constants.ORDER_STATUS_COMPLETED
            })
            reportOptions.add(ToggleOption().apply {
                optionName = Constants.ORDER_STATUS_CANCELLED
            })

            return reportOptions
        }


        fun getDummyIngredientsList(): ArrayList<ToggleOption> {

            val reportOptions = ArrayList<ToggleOption>()
            reportOptions.add(ToggleOption().apply {
                optionName = "Rear Steak"
            })
            reportOptions.add(ToggleOption().apply {
                optionName = "Medium-Rare Steak"
            })
            reportOptions.add(ToggleOption().apply {
                optionName = "Medium Steak"
            })
            reportOptions.add(ToggleOption().apply {
                optionName = "Medium-Well Steak"
            })
            reportOptions.add(ToggleOption().apply {
                optionName = "Well-Done Steak"
            })
            return reportOptions
        }


        fun showBottomToggleSheetDialog(
            fragmentManager: FragmentManager,
            title: String = "",
            showTitle: Boolean = false,
            toggleList: ArrayList<ToggleOption>,
            toggleSelectionListener: ToggleSelectionListener? = null
        ) {

            val frag =
                fragmentManager.findFragmentByTag(BottomToggleOptionsListFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = BottomToggleOptionsListFragment.newInstance(
                title = title, showTitle = showTitle, toggleOptions = toggleList
            )
            bottomSheetDialog.setCallBack(callBack = toggleSelectionListener)
            bottomSheetDialog.show(
                fragmentManager,
                BottomToggleOptionsListFragment::class.java.canonicalName
            )
        }

        fun showAccountInfoDialog(
            fragmentManager: FragmentManager
        ) {

            val frag =
                fragmentManager.findFragmentByTag(AccountInformationDialogFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = AccountInformationDialogFragment.newInstance(
            )
//            bottomSheetDialog.setCallBack(callBack = toggleSelectionListener)
            bottomSheetDialog.show(
                fragmentManager,
                AccountInformationDialogFragment::class.java.canonicalName
            )
        }

        fun showChangePickUpAndDeliveryDialog(
            fragmentManager: FragmentManager
        ) {

            val frag =
                fragmentManager.findFragmentByTag(ChangeLocationPointsDialogFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = ChangeLocationPointsDialogFragment.newInstance(
            )
//            bottomSheetDialog.setCallBack(callBack = toggleSelectionListener)
            bottomSheetDialog.show(
                fragmentManager,
                ChangeLocationPointsDialogFragment::class.java.canonicalName
            )
        }

//        fun showTimeSlotsDialog(
//            fragmentManager: FragmentManager,
//            timeSlotListener: SelectedTimeSlotListener? = null,
//            alreadySelectedTimeSlot: TimeSlot? = null
//        ) {
//
//            val frag =
//                fragmentManager.findFragmentByTag(PickTimeSlotDialogFragment::class.java.canonicalName)
//            if (frag != null) {
//                fragmentManager.beginTransaction().remove(frag).commit()
//            }
//            val bottomSheetDialog =
//                PickTimeSlotDialogFragment.newInstance(timeSlot = alreadySelectedTimeSlot)
//            bottomSheetDialog.setCallBack(callBack = timeSlotListener)
//            bottomSheetDialog.show(
//                fragmentManager,
//                PickTimeSlotDialogFragment::class.java.canonicalName
//            )
//        }

        fun showOrderPlacedDialog(
            fragmentManager: FragmentManager,
            orderPlacedListener: OrderPlacedListener? = null
        ) {
            val frag =
                fragmentManager.findFragmentByTag(OrderPlacedDialogFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = OrderPlacedDialogFragment.newInstance(
            )
            bottomSheetDialog.setCallBack(orderPlacedListener = orderPlacedListener)
            bottomSheetDialog.isCancelable = false
            //bottomSheetDialog.setCallBack(callBack = toggleSelectionListener)
            bottomSheetDialog.show(
                fragmentManager,
                OrderPlacedDialogFragment::class.java.canonicalName
            )
        }

        fun showEarnedPointsDialog(
            fragmentManager: FragmentManager,
            title: String = "",
            message: String = "",
            orderPlacedListener: OrderPlacedListener? = null
        ) {
            val frag =
                fragmentManager.findFragmentByTag(PointsEarnedDialogFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = PointsEarnedDialogFragment.newInstance(
                title, message
            )
            bottomSheetDialog.setListener(callback = orderPlacedListener)
            bottomSheetDialog.isCancelable = false
            bottomSheetDialog.show(
                fragmentManager,
                PointsEarnedDialogFragment::class.java.canonicalName
            )
        }

        fun getDummyProducts(): ArrayList<Product> {

            val productsList = ArrayList<Product>()

            for (i in 0 until 10) {
                if (i % 2 == 0)
                    productsList.add(Product().apply {
                        isFavourite = false
                    })
                else
                    productsList.add(Product().apply {
                        isFavourite = true
                    })
            }

            return productsList
        }

        fun getDummyTopProducts(): ArrayList<Product> {

            val productsList = ArrayList<Product>()

            for (i in 0 until 4) {
                if (i % 2 == 0)
                    productsList.add(Product().apply {
                        isFavourite = false
                    })
                else
                    productsList.add(Product().apply {
                        isFavourite = true
                    })
            }

            return productsList
        }

        fun getDummyMealPrep(): ArrayList<MealPrepSectionItem> {

            val mealPrepSections = ArrayList<MealPrepSectionItem>()

            var mealPrepSectionObject = MealPrepSectionItem()
            mealPrepSectionObject.title = "Pick Your Protein"
            var mealPrepSectionSubSectionsList = ArrayList<MealPrepSubSectionItem>()
            mealPrepSectionSubSectionsList.add(MealPrepSubSectionItem().apply {
                title = "Steak"
                startPrice = 9.50f
            })
            mealPrepSectionSubSectionsList.add(MealPrepSubSectionItem().apply {
                title = "Chicken"
                startPrice = 5.50f
            })
            mealPrepSectionSubSectionsList.add(MealPrepSubSectionItem().apply {
                title = "Salmon"
                startPrice = 4.50f
            })
            mealPrepSectionSubSectionsList.add(MealPrepSubSectionItem().apply {
                title = "Turkey"
                startPrice = 7.50f
            })
            mealPrepSectionSubSectionsList.add(MealPrepSubSectionItem().apply {
                title = "Bison"
                startPrice = 3.50f
            })
            mealPrepSectionSubSectionsList.forEach { it.sectionIndex = mealPrepSections.size }
            mealPrepSectionObject.subSections = mealPrepSectionSubSectionsList
            mealPrepSections.add(mealPrepSectionObject)


            var mealPrepSectionObject1 = MealPrepSectionItem()
            mealPrepSectionObject1.title = "Pick Your Veggies"
            var mealPrepSectionSubSectionsList1 = ArrayList<MealPrepSubSectionItem>()
            mealPrepSectionSubSectionsList1.add(MealPrepSubSectionItem().apply {
                title = "Seasonal Veggies"
                startPrice = 3.25f
                endPrice = 4.75f
            })
            mealPrepSectionSubSectionsList1.add(MealPrepSubSectionItem().apply {
                title = "Mixed Veggies"
                startPrice = 2.50f
                endPrice = 4.50f
                info = "(Broccoli, Cauliflower, Bell Peppers)"
            })
            mealPrepSectionSubSectionsList1.add(MealPrepSubSectionItem().apply {
                title = "Carrots & Peas"
                startPrice = 3.00f
                endPrice = 4.50f
            })
            mealPrepSectionSubSectionsList1.forEach { it.sectionIndex = mealPrepSections.size }
            mealPrepSectionObject1.subSections = mealPrepSectionSubSectionsList1
            mealPrepSections.add(mealPrepSectionObject1)


            val mealPrepSectionObject2 = MealPrepSectionItem()
            mealPrepSectionObject2.title = "Add a Salad"
            val mealPrepSectionSubSectionsList2 = ArrayList<MealPrepSubSectionItem>()
            mealPrepSectionSubSectionsList2.add(MealPrepSubSectionItem().apply {
                title = "Strawberry Spinach Salad"
                startPrice = 3.25f
                endPrice = 4.75f
            })
            mealPrepSectionSubSectionsList2.add(MealPrepSubSectionItem().apply {
                title = "Cranberry Pecan Salad"
                startPrice = 2.50f
                endPrice = 4.50f
            })
            mealPrepSectionSubSectionsList2.forEach { it.sectionIndex = mealPrepSections.size }
            mealPrepSectionObject2.subSections = mealPrepSectionSubSectionsList2
            mealPrepSections.add(mealPrepSectionObject2)


            val mealPrepSectionObject3 = MealPrepSectionItem()
            mealPrepSectionObject3.title = "Add Sauces"
            val mealPrepSectionSubSectionsList3 = ArrayList<MealPrepSubSectionItem>()
            mealPrepSectionSubSectionsList3.add(MealPrepSubSectionItem().apply {
                title = "Sweet Chili Thai Sauce"
                startPrice = 0.50f
                hasQuanity = true
            })
            mealPrepSectionSubSectionsList3.add(MealPrepSubSectionItem().apply {
                title = "Pesto (Pasta Sauce)"
                startPrice = 0.50f
                hasQuanity = true
            })
            mealPrepSectionSubSectionsList3.add(MealPrepSubSectionItem().apply {
                title = "Barbeque Sauce"
                startPrice = 0.50f
                hasQuanity = true
            })
            mealPrepSectionSubSectionsList3.add(MealPrepSubSectionItem().apply {
                title = "Balsamic (Salad Dressing)"
                startPrice = 0.50f
                hasQuanity = true
            })
            mealPrepSectionSubSectionsList3.add(MealPrepSubSectionItem().apply {
                title = "Tomato Basil (Pasta Sauce)"
                startPrice = 0.50f
                hasQuanity = true
            })
            mealPrepSectionSubSectionsList3.add(MealPrepSubSectionItem().apply {
                title = "Balsamic (Salad Dressing)"
                startPrice = 0.50f
                hasQuanity = true
            })
            mealPrepSectionSubSectionsList3.add(MealPrepSubSectionItem().apply {
                title = "Barbeque Sauce"
                startPrice = 0.50f
                hasQuanity = true
            })
            mealPrepSectionSubSectionsList3.forEach { it.sectionIndex = mealPrepSections.size }
            mealPrepSectionObject3.subSections = mealPrepSectionSubSectionsList3
            mealPrepSections.add(mealPrepSectionObject3)


            return mealPrepSections


        }

        fun getPaymentMethodOptionsList(): ArrayList<PaymentMethod> {
            val paymentMethodOptions = ArrayList<PaymentMethod>()

            paymentMethodOptions.add(PaymentMethod().apply {
                title = "Visa"
                subTitle = "Enter information on the card"
                cardImage = R.drawable.ic_visa

            })

            paymentMethodOptions.add(PaymentMethod().apply {
                title = "MasterCard"
                subTitle = "Enter information on the card"
                cardImage = R.drawable.ic_master_card
            })

//            paymentMethodOptions.add(PaymentMethod().apply {
//                title = "PayPal"
//                subTitle = "Enter email PayPal"
//                cardImage = R.drawable.ic_paypal
//            })

            return paymentMethodOptions


        }

        fun showAddPaymentMethodDialog(
            fragmentManager: FragmentManager,
            clickListener: AddedCardInterface? = null
        ) {

            val frag =
                fragmentManager.findFragmentByTag(AddPaymentMethodDialogFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = AddPaymentMethodDialogFragment.newInstance()
            bottomSheetDialog.setListener(clickListener = clickListener)
//            bottomSheetDialog.setCallBack(callBack = toggleSelectionListener)
            bottomSheetDialog.show(
                fragmentManager,
                AddPaymentMethodDialogFragment::class.java.canonicalName
            )
        }

        fun getDummyKitchens(): ArrayList<Kitchen> {

            val kitchens = ArrayList<Kitchen>()

            kitchens.add(Kitchen().apply {
                isSelcted = false
                name = "Eagle Ridge Blvd"
            })

            kitchens.add(Kitchen().apply {
                isSelcted = false
                name = "Fraser Avenue"
            })

            return kitchens

        }

        fun getDummyTimeSlots(): ArrayList<TimeSlot> {

            val timeSlots = ArrayList<TimeSlot>()

            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "20 mins"
            })

            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "25 mins"
            })

            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "1 hour"
            })


            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "Later"
            })

            return timeSlots

        }

        fun getTimeSlots(): ArrayList<TimeSlot> {

            val timeSlots = ArrayList<TimeSlot>()

            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "20 mins"
                time = 20
            })

            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "25 mins"
                time = 25
            })
            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "30 mins"
                time = 30
            })

            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "45 mins"
                time = 45
            })

            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "1 hour"
                time = 60
            })

            timeSlots.add(TimeSlot().apply {
                isSelected = false
                slot = "2 hours"
                time = 120
            })
            return timeSlots

        }

        fun showCardExpiryDatePickerDialog(
            fragmentManager: FragmentManager,
            selectedMonth: Int = 1, selectedYear: Int = 0,
            dateTImePickerListener: DateTImePickerListener?
        ) {

            val frag =
                fragmentManager.findFragmentByTag(CardExpiryDatePickerDialogFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = CardExpiryDatePickerDialogFragment.newInstance(
                selectedMonth = selectedMonth,
                selectedYear = selectedYear
            )
            bottomSheetDialog.setListener(listener = dateTImePickerListener)
            bottomSheetDialog.show(
                fragmentManager,
                CardExpiryDatePickerDialogFragment::class.java.canonicalName
            )
        }

        fun getFormattedCardNumber(cardNumber: String): String {
            if (cardNumber.isNotEmpty()) {
                var formattedCardNumber = ""
                var indexCount = 0
                for (i in 0 until Constants.CARD_NUMBER_FORMAT.length) {
                    if (Constants.CARD_NUMBER_FORMAT[i] == 'X') {
                        if (indexCount < cardNumber.length)
                            formattedCardNumber += cardNumber[indexCount++]
                        else
                            return formattedCardNumber + Constants.CARD_NUMBER_FORMAT.substring(
                                formattedCardNumber.length
                            )
                    } else
                        formattedCardNumber += " "
                }
                return formattedCardNumber
            } else
                return Constants.CARD_NUMBER_FORMAT
        }

        fun showTwoButtonsAlertDialog(
            fragmentManager: FragmentManager,
            title: String, message: String,
            leftButtonText: String = Constants.NO, rightButtonText: String = Constants.YES,
            twoButtonsDialogListener: TwoButtonsDialogListener? = null,
            showBackGroundImage: Boolean = false
        ) {

            val frag =
                fragmentManager.findFragmentByTag(TwoButtonsAlertDialogFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = TwoButtonsAlertDialogFragment.newInstance(
                title = title, message = message,
                leftButtonTitle = leftButtonText, rightButtonTitle = rightButtonText,
                showBackGroundImage = showBackGroundImage
            )
            bottomSheetDialog.setListener(twoButtonsDialogListener = twoButtonsDialogListener)
            bottomSheetDialog.show(
                fragmentManager,
                TwoButtonsAlertDialogFragment::class.java.canonicalName
            )
        }

        fun showOrderSelectionDialog(
            fragmentManager: FragmentManager,
            callback: OrderTypeListener? = null
        ) {

            val frag =
                fragmentManager.findFragmentByTag(OrderTypeSelectionFragment::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val bottomSheetDialog = OrderTypeSelectionFragment.newInstance(

            )
            bottomSheetDialog.setListener(callback = callback)
            bottomSheetDialog.show(
                fragmentManager,
                OrderTypeSelectionFragment::class.java.canonicalName
            )
        }

        fun convertUtcTimeToLocalForComment(timeFromServer: String): String {
            var filteredTime = "N/A"
            try {
                val formatter = SimpleDateFormat(Constants.SERVER_DATE_FORMAT)
                formatter.timeZone = TimeZone.getTimeZone("UTC")

                val pasTime = formatter.parse(timeFromServer)
                val remoteDate = DateTime(pasTime)
                //remoteDate = remoteDate.plusHours(1)
                // Getting the current local date time
                val localDate = DateTime()
                // Calculating days between curernt time and remote time
                val days = Days.daysBetween(remoteDate, localDate).days
                // Calculating minutes between curernt time and remote time
                val minutes = Minutes.minutesBetween(remoteDate, localDate).minutes
                // Calculating hours between curernt time and remote time
                val hours = Hours.hoursBetween(remoteDate, localDate).hours

                // Calculating seconds between curernt time and remote time
                val seconds = Seconds.secondsBetween(remoteDate, localDate).seconds

                filteredTime = when {
                    days > 0 && days <= 28 -> {
                        val raminder = days % 7
                        val quotient = days / 7
                        if (raminder == 0) {
                            "${days / 7}w"
                        } else {
                            if (quotient > 0) {
                                "${quotient}w"
                            } else {
                                "${raminder}d"
                            }
                        }
//                        if (days == 1)
//                            "$days day ago"
//                        else
//                            "$days days ago"

                    }

                    days > 28 -> {
                        val quotient = days / 30
                        val remainder = days % 30
                        if (remainder == 0) {
                            //if(quotient == 1)
                            "${quotient}mon"
//                            else
//                                "${quotient}mons"
                        } else {
                            "${quotient + 1}mons"
                        }
                    }
                    hours in 1..24 -> {
                        "${hours}h"
//                        if (hours == 1)
//                            "$hours hour ago"
//                        else
//                            "$hours hours ago"
                    }
                    minutes in 1..60 -> {
                        "${minutes}min"
//                        if (minutes == 1)
//                            "$minutes min ago"
//                        else
//                            "$minutes mins ago"
                    }
                    //minutes == 0 -> "just now"
                    seconds > 0 -> {
                        "${seconds}s"
                    }
                    seconds == 0 -> "just now"
                    else -> "N/A"
                }

            } catch (e: Exception) {
                Log.d("", "getLastActiveTime: " + e.message)
            }

            return filteredTime

        }

        fun covertTimeToText(dataDate: String?): String? {
            var convTime: String? = null
            val prefix = ""
            val suffix = "ago"
            try {
                val simpleDateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = simpleDateFormat.parse(dataDate)
                simpleDateFormat.timeZone = TimeZone.getDefault()
                val formattedDate = simpleDateFormat.format(date)
                val df =
                    SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS")
                df.timeZone = TimeZone.getTimeZone("UTC")
                val pasTime = simpleDateFormat.parse(formattedDate)
                val nowTime = Date()
                val currentDate = simpleDateFormat.format(nowTime)
                simpleDateFormat.timeZone = TimeZone.getDefault()
                val currentdatef = simpleDateFormat.format(date)
                val dateDiff = nowTime.time - pasTime.time
                val second =
                    TimeUnit.MILLISECONDS.toSeconds(dateDiff)
                val minute =
                    TimeUnit.MILLISECONDS.toMinutes(dateDiff)
                val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
                val day = TimeUnit.MILLISECONDS.toDays(dateDiff)
                when {
                    second < 60 -> {
                        convTime = "$second seconds $suffix"
                    }
                    minute < 60 -> {
                        convTime = "$minute minutes $suffix"
                    }
                    hour < 24 -> {
                        convTime = "$hour hours $suffix"
                    }
                    day >= 7 -> {
                        convTime = when {
                            day > 360 -> {
                                (day / 360).toString() + " years " + suffix
                            }
                            day > 30 -> {
                                (day / 30).toString() + " months " + suffix
                            }
                            else -> {
                                (day / 7).toString() + " weeks " + suffix
                            }
                        }
                    }
                    day < 7 -> {
                        convTime = "$day days $suffix"
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return convTime
        }

        fun dateFormatter(date: String?): String? {
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd")
            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("dd MMM yyyy")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }


        fun orderDateFormatter(date: String?): String? {

            // 2021-04-05 18:45:06
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("dd MMM yyyy ${Constants.DOT} hh:mm a")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun orderDateOnlyFormatter(date: String?): String? {

            // 2021-04-05 18:45:06
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("dd MMM yyyy")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun orderTimeFormatter(date: String?): String? {

            // 2021-04-05 18:45:06
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("hh:mm a")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun showMealPrepDeliveryPopUp(
            fragmentManager: FragmentManager,
            type: String = "new",
            mealPrepListener: MealPrepPopupListener? = null
        ) {
            val frag =
                fragmentManager.findFragmentByTag(MealPrepDeliveryTypePopUp::class.java.canonicalName)
            if (frag != null) {
                fragmentManager.beginTransaction().remove(frag).commit()
            }
            val mealPrepPopUp = MealPrepDeliveryTypePopUp.newInstance(
                key = type
            )
            mealPrepPopUp.setListener(mealPrepListener)
            mealPrepPopUp.show(
                fragmentManager,
                MealPrepDeliveryTypePopUp::class.java.canonicalName
            )
        }


        fun orderTimeFormatterMealPrepPickup(date: String?): String? {

            // 2021-04-05 18:45:06
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd hh:mm a")
//            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("hh:mm a")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun orderTimeFormatter2(date: String?): String? {

            // 2021-04-05 18:45:06
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {

                d = dateFormatprev.parse(date)
                d.minutes = d.minutes - 120
                val dateFormat =
                    SimpleDateFormat("hh a")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun orderTimeFormatter3(date: String?): String? {

            // 2021-04-05 18:45:06
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("hh a")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun dateFormatter3(date: String?): String? {
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun timeFormatter(date: String?): String? {
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("h:mm a")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun historyTimeFormatter(date: String?): String? {
            var formattedDate: String? = null
            val dateFormatprev =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            dateFormatprev.timeZone = TimeZone.getTimeZone("UTC")
            var d: Date? = null
            val f: Date? = null
            try {
                d = dateFormatprev.parse(date)
                val dateFormat =
                    SimpleDateFormat("dd MMM, yyyy")
                if (BuildConfig.DEBUG && d == null) {
                    error("Assertion failed")
                }
                formattedDate = dateFormat.format(d)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return formattedDate
        }

        fun timeDifference(dataDate: String): Long {
            try {
                val simpleDateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = simpleDateFormat.parse(dataDate)
                simpleDateFormat.timeZone = TimeZone.getDefault()
                val formattedDate = simpleDateFormat.format(date)
                val df =
                    SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS")
                df.timeZone = TimeZone.getTimeZone("UTC")
                val pasTime = simpleDateFormat.parse(formattedDate)
                val nowTime = Date()
                simpleDateFormat.timeZone = TimeZone.getDefault()
                val dateDiff = nowTime.time - pasTime.time
                val second =
                    TimeUnit.MILLISECONDS.toSeconds(dateDiff)
                return second
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0
        }

        fun validatePassword(text: String): Boolean {
            val pattern: Pattern
            val matcher: Matcher

            val PASSWORD_PATTERN =
                "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$"

            pattern = Pattern.compile(PASSWORD_PATTERN)
            matcher = pattern.matcher(text)

            return matcher.matches()
        }
    }
}