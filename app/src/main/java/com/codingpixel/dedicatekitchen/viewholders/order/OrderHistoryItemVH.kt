package com.codingpixel.dedicatekitchen.viewholders.order

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.order.OrderHistoryItemAdapter
import com.codingpixel.dedicatekitchen.adapters.order.OrderHistoryOptionsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleOrderHistoryItemBinding
import com.codingpixel.dedicatekitchen.models.*

class OrderHistoryItemVH(
    binding: SingleOrderHistoryItemBinding,
    adapter: OrderHistoryItemAdapter
) : RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleOrderHistoryItemBinding = binding
    private val mAdapter: OrderHistoryItemAdapter = adapter
    private var hasOptions: Boolean = false

    private lateinit var mealPrepSubSectionsAdapter: OrderHistoryOptionsAdapter

    @SuppressLint("SetTextI18n")
    fun bind(source: ItemData, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter

        mBinding.rvIngredients.itemAnimator = null

        mealPrepSubSectionsAdapter =
            OrderHistoryOptionsAdapter(
                mealPrepSubSections = source.options,
                mealPrepSectionAdapter = mAdapter,
                sectionIndex = position
            )

        if (source.options.size > 0)
            mBinding.separator2.visibility = View.VISIBLE
        else
            mBinding.separator2.visibility = View.GONE

        mBinding.tvMealPrepSectionSubTitle.text =
            "Price: $${String.format("%.2f", source.price.toFloat())} (x ${source.quantity})"

        mBinding.tvTotalAmount.text =
            "Total: $" + String.format("%.2f", source.subTotal)

        mBinding.rvIngredients.adapter = mealPrepSubSectionsAdapter
//        if (source.total_amount.matches("-?\\d+(\\.\\d+)?".toRegex())) {
//
//            mBinding.tvTotalAmount.text =
//                "Total: $" + String.format("%.2f", source.total_amount.toFloat())
//        } else {
//            mBinding.tvTotalAmount.text = "Total: $ ${source.total_amount}"
//        }


        if ((source.img ?: "").isNotEmpty())
            Glide.with(itemView).load(source.img).into(mBinding.ivItem)
        else
            Glide.with(itemView).load(R.drawable.ic_dummy_category)
                .transform(RoundedCorners(10)).into(mBinding.ivItem)

        mBinding.tvProductNote.text = source.note
    }
}