package com.codingpixel.dedicatekitchen.activities.product

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.checkout.CartActivity
import com.codingpixel.dedicatekitchen.adapters.mealprep.MealPrepSectionAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityMealPrepBinding
import com.codingpixel.dedicatekitchen.helpers.CommonMethods
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.interfaces.MealPrepListener
import com.codingpixel.dedicatekitchen.models.MealPrepSectionItem

class MealPrepActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMealPrepBinding


    private lateinit var mealPrepSectionsAdapter: MealPrepSectionAdapter
    private var mealPrepSectionsList = ArrayList<MealPrepSectionItem>()

    private var totalAmount = 0.00f
    private var totalQuantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_meal_prep)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = Color.TRANSPARENT
        initClickListener()
        iniAdapter()
        updatePrice()
    }

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }

        mBinding.cvAddToBag.setOnClickListener {
            if (totalAmount > 0.0f) {
                startActivity(Intent(this@MealPrepActivity, CartActivity::class.java))
            } else
                showShortToast(message = "Add at least one item")
        }

        mBinding.ivPlus.setOnClickListener {
            ++totalQuantity
            updateQuantity()
        }


        mBinding.ivMinus.setOnClickListener {
            if (totalQuantity > 1) {
                --totalQuantity
                updateQuantity()
            }
        }

        mBinding.cvAddToBag.setOnClickListener {
            startActivity(Intent(this@MealPrepActivity, CartActivity::class.java))
        }
    }

    private fun updateQuantity() {
        mBinding.tvQuantity.text = "${totalQuantity}"
        mBinding.tvAddToBag.text =
            "${getString(R.string.add_to_bag)}  ${Constants.DOLLAR_SIGN}${totalAmount * totalQuantity}"
    }

    private fun updatePrice() {
        var totalPrice = 0.0f
        for (i in 0 until mealPrepSectionsList.size) {
            for (j in 0 until mealPrepSectionsList[i].subSections.size) {
                if (mealPrepSectionsList[i].subSections[j].checked) {
                    totalPrice += if (mealPrepSectionsList[i].subSections[j].hasQuanity) {
                        mealPrepSectionsList[i].subSections[j].quantity * mealPrepSectionsList[i].subSections[j].startPrice
                    } else {
                        mealPrepSectionsList[i].subSections[j].startPrice
                    }
                }
            }
        }
        totalAmount = totalPrice
        updateQuantity()
    }

    private fun iniAdapter() {
//        mealPrepSectionsList = CommonMethods.getDummyMealPrep()
//        mealPrepSectionsAdapter =
//            MealPrepSectionAdapter(mealPrepSections = mealPrepSectionsList, mealPrepListener = this)
//        mBinding.rvMealPrepSections.adapter = mealPrepSectionsAdapter
    }


//    override fun itemTapped(position: Int, itemId: String) {
////        val alreadySelectedIndex = mealPrepSectionsList[sectionIndex].subSections.indexOfFirst { it.checked }
////        if (alreadySelectedIndex != -1) {
////            mealPrepSectionsList[sectionIndex].subSections[alreadySelectedIndex].checked = false
////            mealPrepSectionsAdapter.notifyItemChanged(alreadySelectedIndex)
////        }
////        mealPrepSectionsList[sectionIndex].subSections[subSectionIndex].checked =
////            !mealPrepSectionsList[sectionIndex].subSections[subSectionIndex].checked
////        mealPrepSectionsAdapter.notifyItemChanged(sectionIndex)
////        updatePrice()
//    }
//
//    override fun plusTapped(sectionIndex: Int, subSectionIndex: Int) {
//        mealPrepSectionsList[sectionIndex].subSections[subSectionIndex].quantity =
//            ++mealPrepSectionsList[sectionIndex].subSections[subSectionIndex].quantity
//        mealPrepSectionsAdapter.notifyItemChanged(sectionIndex)
//        updatePrice()
//    }
//
//    override fun minusTapped(sectionIndex: Int, subSectionIndex: Int) {
//        if (mealPrepSectionsList[sectionIndex].subSections[subSectionIndex].quantity > 1) {
//            mealPrepSectionsList[sectionIndex].subSections[subSectionIndex].quantity =
//                --mealPrepSectionsList[sectionIndex].subSections[subSectionIndex].quantity
//            mealPrepSectionsAdapter.notifyItemChanged(sectionIndex)
//            updatePrice()
//        }
//    }
}