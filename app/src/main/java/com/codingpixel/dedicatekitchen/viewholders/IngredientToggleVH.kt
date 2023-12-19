package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.ToggleIngredientsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleIngredientToggleItemBinding
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.local.ToggleOption
import java.util.*

class IngredientToggleVH(binding: SingleIngredientToggleItemBinding, adapter : ToggleIngredientsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding : SingleIngredientToggleItemBinding =  binding
    private val mAdapter : ToggleIngredientsAdapter = adapter

    fun bind(source: Category, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter
    }
}