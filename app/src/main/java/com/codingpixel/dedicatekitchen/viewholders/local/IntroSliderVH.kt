package com.codingpixel.dedicatekitchen.viewholders.local

import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageLoader
import com.codingpixel.dedicatekitchen.databinding.SingleIntroSliderItemBinding
import com.codingpixel.dedicatekitchen.models.local.IntroSlider

class IntroSliderVH(binding: SingleIntroSliderItemBinding) : RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleIntroSliderItemBinding = binding

    fun bind(source: IntroSlider) {
        mBinding.source = source
        mBinding.ivBackgroundImage.setImageResource(source.cardImage)
    }
}