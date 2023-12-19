package com.codingpixel.dedicatekitchen.adapters.mealprep

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleMealPrepSectionItemBinding
import com.codingpixel.dedicatekitchen.interfaces.MealPrepListener
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.viewholders.MealPrepSectionVH

class MealPrepSectionAdapter(
    private val mealPrepSections: ArrayList<ExtraOptionsModel>,
    private val mealPrepListener: MealPrepListener? = null
) :
    RecyclerView.Adapter<MealPrepSectionVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPrepSectionVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleMealPrepSectionItemBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_meal_prep_section_item,
                parent,
                false
            )
        return MealPrepSectionVH(binding = binding, adapter = this@MealPrepSectionAdapter)
    }

    override fun getItemCount(): Int {
        return mealPrepSections.size
    }

    override fun onBindViewHolder(holder: MealPrepSectionVH, position: Int) {
        holder.bind(source = mealPrepSections[position], position = position)
    }

    fun itemTapped(position: Int, itemId: String, categoryHeaderId: Int) {
        val itemCheckedCount =
            mealPrepSections[categoryHeaderId].menuItems.sumBy { it.quantity.toInt() }
        if (mealPrepSections[categoryHeaderId].forceMaxSelection.toInt() == 1 && !mealPrepSections[categoryHeaderId].menuItems[position].isChecked) {
            mealPrepSections[categoryHeaderId].menuItems.forEach {
                it.isChecked = false
                it.quantity = "0"
            }
            mealPrepListener?.itemTapped(
                position = position,
                itemId = itemId,
                categoryHeaderId = categoryHeaderId,
                itemDetail = mealPrepSections[categoryHeaderId].menuItems[position]
            )
        } else if (!mealPrepSections[categoryHeaderId].menuItems[position].isChecked && itemCheckedCount > 0 && mealPrepSections[categoryHeaderId].maxSelection.toInt() == itemCheckedCount) {
//            mealPrepListener?.showToast("You can't select more")
            if (mealPrepSections[categoryHeaderId].maxSelection.toInt() == 1) {
                mealPrepListener?.cantSelectMore(
                    sectionIndex = categoryHeaderId,
                    subSectionIndex = position
                )
            } else {
                mealPrepListener?.showToast("You have already selected maximum options!")
            }
        } else {
//            if (mealPrepSections[categoryHeaderId].forceMaxSelection.toInt() != 1) {
                mealPrepListener?.itemTapped(
                    position = position,
                    itemId = itemId,
                    categoryHeaderId = categoryHeaderId,
                    itemDetail = mealPrepSections[categoryHeaderId].menuItems[position]
                )
//            }
        }
    }

    fun plusTapped(sectionIndex: Int, subSectionIndex: Int) {
        val itemCheckedCount = mealPrepSections[sectionIndex].menuItems.filter { it.isChecked }
            .sumBy { it.quantity.toInt() }
        if (mealPrepSections[sectionIndex].maxSelection.toInt() <= itemCheckedCount) {
            mealPrepListener?.showToast("You have already selected maximum options!")
        } else {
            mealPrepListener?.plusTapped(
                sectionIndex = sectionIndex,
                subSectionIndex = subSectionIndex
            )
        }
    }

    fun headerTap(position: Int) {
        mealPrepListener?.headerTapped(position = position)
    }

    fun minusTapped(sectionIndex: Int, subSectionIndex: Int) {
        mealPrepListener?.minusTapped(
            sectionIndex = sectionIndex,
            subSectionIndex = subSectionIndex
        )
    }
}