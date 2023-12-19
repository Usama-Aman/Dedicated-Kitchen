package com.codingpixel.dedicatekitchen.viewholders.local

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleToggleIndicatorItemBinding
import com.codingpixel.dedicatekitchen.models.local.ToggleIndicator

class ToggleDotVH(binding: SingleToggleIndicatorItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleToggleIndicatorItemBinding = binding

    fun bind(source: ToggleIndicator, showGreen: Boolean = false) {
        mBinding.source = source
        mBinding.showGreen = showGreen
        if (source.isSelected) {
            if (showGreen) {
                mBinding.dot.setBackgroundResource(R.drawable.green_selected_dot)
            } else {
                mBinding.dot.setBackgroundResource(R.drawable.selected_dot)
            }
        } else {
            if (showGreen) {
                mBinding.dot.setBackgroundResource(R.drawable.un_selected_plan_dot)
            } else {
                mBinding.dot.setBackgroundResource(R.drawable.un_selected_dot)
            }
        }
    }

}