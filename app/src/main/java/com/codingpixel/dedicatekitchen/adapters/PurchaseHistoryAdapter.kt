package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.models.PurchaseHistory
import com.codingpixel.dedicatekitchen.viewholders.PurchaseHistoryVH

class PurchaseHistoryAdapter(private val list: ArrayList<PurchaseHistory>) :
    RecyclerView.Adapter<PurchaseHistoryVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHistoryVH {
        return PurchaseHistoryVH(
            mBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.single_purchae_history_item, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PurchaseHistoryVH, position: Int) {
        holder.bind(source = list[position], position = position)
    }

}