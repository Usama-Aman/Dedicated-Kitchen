package com.codingpixel.dedicatekitchen.adapters.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleMealPrepSectionItemBinding
import com.codingpixel.dedicatekitchen.databinding.SingleOrderHistoryItemBinding
import com.codingpixel.dedicatekitchen.databinding.SingleOrderItemBinding
import com.codingpixel.dedicatekitchen.interfaces.MealPrepListener
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.models.ItemData
import com.codingpixel.dedicatekitchen.models.OrderModel
import com.codingpixel.dedicatekitchen.models.OrderToDbModel
import com.codingpixel.dedicatekitchen.viewholders.MealPrepSectionVH
import com.codingpixel.dedicatekitchen.viewholders.order.OrderHistoryItemVH

class OrderHistoryItemAdapter(
    private val mealPrepSections: ArrayList<ItemData>) :
    RecyclerView.Adapter<OrderHistoryItemVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryItemVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleOrderHistoryItemBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_order_history_item,
                parent,
                false
            )
        return OrderHistoryItemVH(binding = binding, adapter = this@OrderHistoryItemAdapter)
    }

    override fun getItemCount(): Int {
        return mealPrepSections.size
    }

    override fun onBindViewHolder(holder: OrderHistoryItemVH, position: Int) {
        holder.bind(source = mealPrepSections[position], position = position)
    }

//    fun itemTapped(position: Int, itemId: String, categoryHeaderId: Int) {
//        val itemCheckedCount = mealPrepSections[categoryHeaderId].menuItems.count { it.isChecked }
//        if (!mealPrepSections[categoryHeaderId].menuItems[position].isChecked && itemCheckedCount > 0 && mealPrepSections[categoryHeaderId].maxSelection.toInt() == itemCheckedCount)
//            mealPrepListener?.showToast("You can't select more")
//        else
//            mealPrepListener?.itemTapped(
//                position = position,
//                itemId = itemId,
//                categoryHeaderId = categoryHeaderId,
//                itemDetail = mealPrepSections[categoryHeaderId].menuItems[position]
//            )
//
//    }
//
//    fun plusTapped(sectionIndex: Int, subSectionIndex: Int) {
//        mealPrepListener?.plusTapped(sectionIndex = sectionIndex, subSectionIndex = subSectionIndex)
//    }
//
//    fun minusTapped(sectionIndex: Int, subSectionIndex: Int) {
//        mealPrepListener?.minusTapped(
//            sectionIndex = sectionIndex,
//            subSectionIndex = subSectionIndex
//        )
//    }
}