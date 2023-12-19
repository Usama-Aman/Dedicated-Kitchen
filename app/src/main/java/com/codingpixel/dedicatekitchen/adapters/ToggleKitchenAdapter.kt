package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleKitchenToggleItemBinding
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.Kitchen
import com.codingpixel.dedicatekitchen.viewholders.ToggleKitchenVH

class ToggleKitchenAdapter(
    private val kitchens: ArrayList<Kitchen>,
    private val itemClickListener: ItemClickListener? = null
) : RecyclerView.Adapter<ToggleKitchenVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToggleKitchenVH {
        val inflator = LayoutInflater.from(parent.context)

        val binding: SingleKitchenToggleItemBinding =
            DataBindingUtil.inflate(
                inflator,
                R.layout.single_kitchen_toggle_item,
                parent,
                false
            )
        return ToggleKitchenVH(binding = binding, adapter = this@ToggleKitchenAdapter)
    }

    override fun getItemCount(): Int {
        return kitchens.size
    }

    override fun onBindViewHolder(holder: ToggleKitchenVH, position: Int) {
        holder.bind(source = kitchens[position], position = position)
    }

    fun itemTapped(position: Int) {
        itemClickListener?.itemClicked(position = position)
    }
}