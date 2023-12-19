package com.codingpixel.dedicatekitchen.adapters.local

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleIntroSliderItemBinding
import com.codingpixel.dedicatekitchen.models.local.IntroSlider
import com.codingpixel.dedicatekitchen.viewholders.local.IntroSliderVH

class AppIntroSliderRVAdapter(private val mContext: Context,
                              private var introslides: ArrayList<IntroSlider>) : RecyclerView.Adapter<IntroSliderVH>() {




//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view.equals(`object`)
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(`object` as View)
//    }
//
//    override fun getCount(): Int {
//        return introslides.size
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val inflator = LayoutInflater.from(mContext)
//        val mBinding: SingleIntroSliderItemBinding =
//            DataBindingUtil.inflate(inflator, R.layout.single_intro_slider_item, container, false)
//        mBinding.source = introslides[position]
//        mBinding.ivBackgroundImage.setImageResource(introslides[position].cardImage)
//        container.addView(mBinding.root , 0)
//        return mBinding.root
//    }
//
//
//    override fun saveState(): Parcelable? {
//        return null
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderVH {
        val inflator = LayoutInflater.from(mContext)

        return IntroSliderVH(binding = DataBindingUtil.inflate(inflator, R.layout.single_intro_slider_item, parent, false) )
//        val mBinding: SingleIntroSliderItemBinding =
//            DataBindingUtil.inflate(inflator, R.layout.single_intro_slider_item, parent, false)
    }

    override fun getItemCount(): Int {
        return introslides.size
    }

    override fun onBindViewHolder(holder: IntroSliderVH, position: Int) {

        holder.bind(source = introslides[position])

//        mBinding.source = introslides[position]
//        mBinding.ivBackgroundImage.setImageResource(introslides[position].cardImage)
    }
}