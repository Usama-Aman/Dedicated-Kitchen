package com.codingpixel.dedicatekitchen.viewholders

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.mealprep.MealPrepSectionAdapter
import com.codingpixel.dedicatekitchen.adapters.mealprep.MealPrepSubSectionAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleMealPrepSectionItemBinding
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel

class MealPrepSectionVH(
    binding: SingleMealPrepSectionItemBinding,
    adapter: MealPrepSectionAdapter
) : RecyclerView.ViewHolder(binding.root) {


    private val mBinding: SingleMealPrepSectionItemBinding = binding
    private val mAdapter: MealPrepSectionAdapter = adapter

    private lateinit var mealPrepSubSectionsAdapter: MealPrepSubSectionAdapter

    @SuppressLint("SetTextI18n")
    fun bind(source: ExtraOptionsModel, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter

        mBinding.rvIngredients.itemAnimator = null
        if (source.maxSelection == "1" && source.forceMaxSelection == "1")
        {
            mBinding.tvSelectOne.text = source.forceMaxSelection + " item must be selected."
            mBinding.steric.visibility = View.VISIBLE
        }

        else if (source.maxSelection == "1"  && source.forceMaxSelection == "0")
            mBinding.tvSelectOne.text = "Maximum " + source.maxSelection + " item to be selected"
        else if (source.maxSelection > "1")
            mBinding.tvSelectOne.text = " Select Maximum " + source.maxSelection + " items"
//        if (source.forceMaxSelection > "0") {
//            if (source.forceMaxSelection == "1")
//                mBinding.tvSelectOne.text = source.forceMaxSelection + " item must be selected."
//            else
//                mBinding.tvSelectOne.text = source.forceMaxSelection + " items must be selected."
//        } else {
//            if (source.maxSelection == "1")
//                mBinding.tvSelectOne.text = " Select Maximum " + source.maxSelection + " item"
//            else
//                mBinding.tvSelectOne.text = "Select Maximum " + source.maxSelection + " items"
//        }


        mealPrepSubSectionsAdapter =
            MealPrepSubSectionAdapter(
                mealPrepSubSections = source.menuItems,
                mealPrepSectionAdapter = mAdapter,
                sectionIndex = position
            )
        mBinding.rvIngredients.adapter = mealPrepSubSectionsAdapter
    }
}