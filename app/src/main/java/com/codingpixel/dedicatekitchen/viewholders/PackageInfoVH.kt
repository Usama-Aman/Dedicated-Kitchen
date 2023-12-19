package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.databinding.SinglePlanFeatureItemBinding

class PackageInfoVH(private val binding: SinglePlanFeatureItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(source: String) {
        binding.source = source
    }
}