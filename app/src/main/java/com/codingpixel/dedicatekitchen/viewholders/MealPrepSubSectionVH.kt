package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.mealprep.MealPrepSubSectionAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleMealPrepSubSectionItemBinding
import com.codingpixel.dedicatekitchen.models.MealPrepSubSectionItem
import com.codingpixel.dedicatekitchen.models.MenuItemModel

class MealPrepSubSectionVH(
    binding: SingleMealPrepSubSectionItemBinding,
    adapter: MealPrepSubSectionAdapter
) : RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleMealPrepSubSectionItemBinding = binding
    private val mAdapter: MealPrepSubSectionAdapter = adapter

    fun bind(source: MenuItemModel, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter
    }
}