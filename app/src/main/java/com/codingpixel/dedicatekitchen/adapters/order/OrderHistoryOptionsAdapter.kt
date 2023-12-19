package com.codingpixel.dedicatekitchen.adapters.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleOrderHistoryOptionItemBinding
import com.codingpixel.dedicatekitchen.models.ExtraItemOrderModel
import com.codingpixel.dedicatekitchen.viewholders.order.OrderHistoryOptionVH

class OrderHistoryOptionsAdapter(
    private val mealPrepSubSections: ArrayList<ExtraItemOrderModel>,
    private val mealPrepSectionAdapter: OrderHistoryItemAdapter? = null,
    private var sectionIndex: Int
) :
    RecyclerView.Adapter<OrderHistoryOptionVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryOptionVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleOrderHistoryOptionItemBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_order_history_option_item,
                parent,
                false
            )
        return OrderHistoryOptionVH(binding = binding, adapter = this@OrderHistoryOptionsAdapter)
    }

    override fun getItemCount(): Int {
        return mealPrepSubSections.size
    }

    override fun onBindViewHolder(holder: OrderHistoryOptionVH, position: Int) {
        holder.bind(source = mealPrepSubSections[position], position = position)
    }

//    fun itemTapped(position: Int) {
//        Log.d("isItemTapped","itemTapped")
//        mealPrepSectionAdapter?.itemTapped(
//            position = position,
//            itemId = mealPrepSubSections[position].ID,
//            categoryHeaderId = sectionIndex
//        )
//    }
//
//    fun plusTapped(sectionIndex: Int, subSectionIndex: Int) {
//        mealPrepSectionAdapter?.plusTapped(
//            sectionIndex = sectionIndex,
//            subSectionIndex = subSectionIndex
//        )
//    }
//
//    fun minusTapped(sectionIndex: Int, subSectionIndex: Int) {
//        mealPrepSectionAdapter?.minusTapped(
//            sectionIndex = sectionIndex,
//            subSectionIndex = subSectionIndex
//        )
//    }
}