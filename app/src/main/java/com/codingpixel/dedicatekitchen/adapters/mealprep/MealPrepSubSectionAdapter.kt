package com.codingpixel.dedicatekitchen.adapters.mealprep

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleMealPrepSubSectionItemBinding
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.viewholders.MealPrepSubSectionVH

class MealPrepSubSectionAdapter(
    private val mealPrepSubSections: ArrayList<MenuItemModel>,
    private val mealPrepSectionAdapter: MealPrepSectionAdapter? = null,
    private var sectionIndex: Int
) :
    RecyclerView.Adapter<MealPrepSubSectionVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPrepSubSectionVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleMealPrepSubSectionItemBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_meal_prep_sub_section_item,
                parent,
                false
            )
        return MealPrepSubSectionVH(binding = binding, adapter = this@MealPrepSubSectionAdapter)
    }

    override fun getItemCount(): Int {
        return mealPrepSubSections.size
    }

    override fun onBindViewHolder(holder: MealPrepSubSectionVH, position: Int) {
        holder.bind(source = mealPrepSubSections[position], position = position)
    }

    fun itemTapped(position: Int) {
        mealPrepSectionAdapter?.itemTapped(
            position = position,
            itemId = mealPrepSubSections[position].ID,
            categoryHeaderId = sectionIndex
        )
    }

    fun plusTapped(subSectionIndex: Int) {
        mealPrepSectionAdapter?.plusTapped(
            sectionIndex = sectionIndex,
            subSectionIndex = subSectionIndex
        )
    }

    fun minusTapped(subSectionIndex: Int) {
        mealPrepSectionAdapter?.minusTapped(
            sectionIndex = sectionIndex,
            subSectionIndex = subSectionIndex
        )
    }
}