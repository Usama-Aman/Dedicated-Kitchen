package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.databinding.SinglePurchaeHistoryItemBinding
import com.codingpixel.dedicatekitchen.models.PurchaseHistory

class PurchaseHistoryVH(private val mBinding: SinglePurchaeHistoryItemBinding) :
    RecyclerView.ViewHolder(mBinding.root) {


    fun bind(source: PurchaseHistory, position: Int) {
        mBinding.source = source
        mBinding.position = position
    }

}