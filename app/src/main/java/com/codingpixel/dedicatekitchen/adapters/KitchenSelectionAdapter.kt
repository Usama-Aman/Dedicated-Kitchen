package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.DedicateKitchen
import com.codingpixel.dedicatekitchen.viewholders.KitchenSelectionVH

class KitchenSelectionAdapter(
    private val kitchensList: ArrayList<DedicateKitchen>,
    private val itemClickListener: ItemClickListener? = null
) : RecyclerView.Adapter<KitchenSelectionVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitchenSelectionVH {

        return KitchenSelectionVH(
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.single_kitchen_selection_item,
                parent, false
            ),
            itemClickListener = itemClickListener
        )
    }

    override fun getItemCount(): Int {
        return kitchensList.size
    }

    override fun onBindViewHolder(holder: KitchenSelectionVH, position: Int) {
        holder.bind(source = kitchensList[position], position = position)
    }
}