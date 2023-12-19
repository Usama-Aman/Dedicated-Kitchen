package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleIngredientToggleItemBinding
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.viewholders.IngredientToggleVH
import kotlin.collections.ArrayList

class ToggleIngredientsAdapter(
    private val ingredientsList: ArrayList<Category>,
    private val itemClickListener: ItemClickListener? = null
) : RecyclerView.Adapter<IngredientToggleVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientToggleVH {
        val inflator = LayoutInflater.from(parent.context)

        val binding: SingleIngredientToggleItemBinding =
            DataBindingUtil.inflate(
                inflator,
                R.layout.single_ingredient_toggle_item,
                parent,
                false
            )
        return IngredientToggleVH(binding = binding, adapter = this@ToggleIngredientsAdapter)
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    override fun onBindViewHolder(holder: IngredientToggleVH, position: Int) {
        holder.bind(source = ingredientsList[position], position = position)
    }

    fun itemTapped(position: Int) {
        itemClickListener?.itemClicked(position = position)
    }
}