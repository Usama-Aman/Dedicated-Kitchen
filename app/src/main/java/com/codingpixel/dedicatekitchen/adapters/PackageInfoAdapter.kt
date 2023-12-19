package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.viewholders.PackageInfoVH

class PackageInfoAdapter(private val items: List<String>) :
    RecyclerView.Adapter<PackageInfoVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageInfoVH {
        return PackageInfoVH(
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.single_plan_feature_item, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PackageInfoVH, position: Int) {
        holder.bind(source = items[position])
    }
}