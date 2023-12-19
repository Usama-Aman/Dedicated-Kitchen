package com.codingpixel.dedicatekitchen.adapters.local

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleToggleSelectionItemBinding
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.local.ToggleOption
import com.codingpixel.dedicatekitchen.viewholders.local.ToggleOptionVH

class ToggleOptionsAdapter(
    private val toggleOptions: ArrayList<ToggleOption>,
    private val itemClickListener: ItemClickListener? = null
) : RecyclerView.Adapter<ToggleOptionVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToggleOptionVH {
        val inflator = LayoutInflater.from(parent.context)
        val binding: SingleToggleSelectionItemBinding =
            DataBindingUtil.inflate(
                inflator,
                R.layout.single_toggle_selection_item,
                parent,
                false
            )
        return ToggleOptionVH(binding = binding, adapter = this@ToggleOptionsAdapter)
    }

    override fun getItemCount(): Int {
        return toggleOptions.size
    }

    override fun onBindViewHolder(holder: ToggleOptionVH, position: Int) {
        holder.bind(source = toggleOptions[position], position = position)
    }

    fun toggle(position: Int) {
        itemClickListener?.itemClicked(position = position)
    }
}