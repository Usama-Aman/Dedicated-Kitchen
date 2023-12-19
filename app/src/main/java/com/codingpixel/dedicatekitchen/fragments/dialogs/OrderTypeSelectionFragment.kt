package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.MyBottomSheetBaseFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentOrderTypeSelectionBinding
import com.codingpixel.dedicatekitchen.interfaces.OrderTypeListener

class OrderTypeSelectionFragment : MyBottomSheetBaseFragment() {


    private lateinit var mBinding: FragmentOrderTypeSelectionBinding

    private var callback: OrderTypeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReportDialogTheme)

    }

    fun setListener(callback: OrderTypeListener?) {
        this.callback = callback
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_order_type_selection,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        Glide.with(this).load(R.drawable.take_out).into(mBinding.ivTakeOut)
        Glide.with(this).load(R.drawable.meal_prep).into(mBinding.ivMealPrep)
    }

    private fun initClickListener() {

        mBinding.ivSkip.setOnClickListener {
            callback?.openSkip()
            dismiss()
        }

        mBinding.ivTakeOut.setOnClickListener {
            callback?.startOrder("takeout")
            dismiss()
//            val db = AppDatabase(context!!)
//            GlobalScope.launch {
//                val cartItemsExist =
//                    db.cartDao().exists(type = ApplicationEnum.MEAL_PREP.toString())
//                if (cartItemsExist) {
//                    activity?.runOnUiThread {
//                        context?.let { it1 ->
//                            errorDialogue(
//                                "Please checkout Meal Prep order first.",
//                                it1
//                            )
//                        }
//                    }
//                } else {
//                    AppPreferenceManager.setValue(IntentParams.ORDERTYPE, "Takeout Instant")
//                    MyApplication.selectedOrderType = ApplicationEnum.TAKE_OUT
//                    startActivity(
//                        Intent(context, CategorySelectionActivity::class.java).putExtra(
//                            IntentParams.isMealPrep,
//                            false
//                        )
//                    )
//                    //startActivity(Intent(context, PersonalInfo::class.java))
//                }
//            }

        }

        mBinding.ivMealPrep.setOnClickListener {

            callback?.startOrder("mealprep")
            dismiss()

//            if (isUserLoggedIn()) {
//                val db = context?.let { it1 -> AppDatabase(it1) }
//                GlobalScope.launch {
//                    val cartItemsExist =
//                        db?.cartDao()?.exists(type = ApplicationEnum.TAKE_OUT.toString())
//                    if (cartItemsExist!!) {
//                        activity?.runOnUiThread {
//                            context?.let { it1 ->
//                                errorDialogue(
//                                    "Please checkout Take Out order first.",
//                                    it1
//                                )
//                            }
//                        }
//                    } else {
//                        val mealPrepPopup = MealPrepDeliveryTypePopUp.newInstance("new")
//                        mealPrepPopup.setListener(object : MealPrepPopupListener {
//                            override fun deliverySelected(
//                                date: String,
//                                time: String,
//                                location: String
//                            ) {
////                                startActivity(Intent(context, MealPrepActivity::class.java))
//                                MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
////                                startActivity(
////                                    Intent(
////                                        context,
////                                        MealPrepMenusActivity::class.java
////                                    )
////                                )
//                                startActivity(
//                                    Intent(
//                                        context,
//                                        CategorySelectionActivity::class.java
//                                    ).putExtra(IntentParams.isMealPrep, true)
//                                )
//                                dismiss()
//                            }
//
//                            override fun takeOutSelected(date: String, time: String) {
//                                activity?.runOnUiThread {
//                                    MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
////                                    startActivity(
////                                        Intent(
////                                            context,
////                                            MealPrepMenusActivity::class.java
////                                        )
////                                    )
//                                    startActivity(
//                                        Intent(
//                                            context,
//                                            CategorySelectionActivity::class.java
//                                        ).putExtra(IntentParams.isMealPrep, true)
//                                    )
//                                }
//                            }
//                        })
//                        dismiss()
//                        mealPrepPopup.show(
//                            childFragmentManager,
//                            MealPrepDeliveryTypePopUp::class.java.canonicalName
//                        )
//
//                    }
//                }
//            }
//            // startActivity(Intent(context, MealPrepActivity::class.java))
//            else {
//                CommonMethods.showTwoButtonsAlertDialog(fragmentManager = childFragmentManager,
//                    title = "Alert", message = "Want to order meal prep ?",
//                    leftButtonText = "Later", rightButtonText = "Register Now",
//                    twoButtonsDialogListener = object : TwoButtonsDialogListener {
//                        override fun leftButtonTapped() {
//                        }
//
//                        override fun rightButtonTapped() {
//                            activity?.finish()
//                            startActivity(Intent(context, RegisterActivity::class.java))
//                        }
//                    })
//            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            OrderTypeSelectionFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}