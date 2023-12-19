package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.databinding.SingleKitchenSelectionItemBinding
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.DedicateKitchen

class KitchenSelectionVH(
    private val binding: SingleKitchenSelectionItemBinding,
    private val itemClickListener: ItemClickListener? = null
) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(source: DedicateKitchen, position: Int) {
        binding.source = source
        binding.position = position
        binding.listener = itemClickListener
    }
}