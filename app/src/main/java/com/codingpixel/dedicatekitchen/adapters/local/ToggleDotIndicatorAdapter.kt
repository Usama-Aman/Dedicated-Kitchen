package com.codingpixel.dedicatekitchen.adapters.local

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleToggleIndicatorItemBinding
import com.codingpixel.dedicatekitchen.models.local.ToggleIndicator
import com.codingpixel.dedicatekitchen.viewholders.local.ToggleDotVH

class ToggleDotIndicatorAdapter (private val toggles: ArrayList<ToggleIndicator>,
private val showGreen : Boolean = false) :
    RecyclerView.Adapter<ToggleDotVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToggleDotVH {
        val inflator = LayoutInflater.from(parent.context)
        val binding: SingleToggleIndicatorItemBinding =
            DataBindingUtil.inflate(inflator, R.layout.single_toggle_indicator_item, parent, false)
        return ToggleDotVH(binding = binding)
    }

    override fun getItemCount(): Int {
        return toggles.size
    }

    override fun onBindViewHolder(holder: ToggleDotVH, position: Int) {
        holder.bind(source = toggles[position], showGreen = showGreen)
    }
}