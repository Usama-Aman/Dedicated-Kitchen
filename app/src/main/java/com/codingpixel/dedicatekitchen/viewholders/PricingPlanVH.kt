package com.codingpixel.dedicatekitchen.viewholders

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.PricingAdapter
import com.codingpixel.dedicatekitchen.databinding.SinglePricingPlanBinding
import com.codingpixel.dedicatekitchen.models.PackageModel

class PricingPlanVH(binding: SinglePricingPlanBinding, adapter: PricingAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SinglePricingPlanBinding = binding
    private val mAdapter: PricingAdapter = adapter

    fun bind(source: PackageModel, position: Int) {
        mBinding.model = source
        mBinding.position = position
        mBinding.adapter = mAdapter



//        if (source.selectedPackage) {
//            mBinding.cvMain.setCardBackgroundColor(itemView.resources.getColor(R.color.colorPrimaryDark))
//            mBinding.cvCheckOut.setCardBackgroundColor(itemView.resources.getColor(R.color.white))
//            mBinding.tvAddToBag.text = "Plan Subscribed"
//            mBinding.tvAddToBag.setTextColor(itemView.resources.getColor(R.color.black))
//            mBinding.tvPlanName.setTextColor(itemView.resources.getColor(R.color.white))
//            mBinding.tvPackageDetail.setTextColor(itemView.resources.getColor(R.color.white))
//            mBinding.tvPlanPrice.setTextColor(itemView.resources.getColor(R.color.white))
//            ImageViewCompat.setImageTintList(mBinding.ivDollar, ColorStateList.valueOf(itemView.resources.getColor(R.color.white)))
//        }

        mBinding.clMain.setBackgroundColor(Color.parseColor(source.bg_color))
        mBinding.tvPlanName.setTextColor(Color.parseColor(source.font_color))
//        mBinding.ivDollar.background.setColorFilter(
//            Color.parseColor(source.font_color),
//            PorterDuff.Mode.SRC_ATOP
//        )
        mBinding.tvPackageDetail.setTextColor(Color.parseColor(source.font_color))
        if (source.selectedPackage)
        {
            mBinding.tvLastBoughtCredit.visibility = View.VISIBLE
            mBinding.tvLastBoughtCredit.text = "Last bought credit : $" + source.amount
            mBinding.tvLastBoughtCredit.setTextColor(itemView.resources.getColor(R.color.black))
            mBinding.cvMain.setBackgroundResource(R.drawable.bg_with_border)
            mBinding.clMain.setBackgroundResource(R.color.white)
            mBinding.cvCheckOut.setBackgroundResource(R.drawable.bg_with_border)
            mBinding.tvAddToBag.setTextColor(itemView.resources.getColor(R.color.black))
            mBinding.tvPackageDetail.setTextColor(itemView.resources.getColor(R.color.black))
            mBinding.tvPlanName.setTextColor(itemView.resources.getColor(R.color.black))
        }

        else
            mBinding.tvLastBoughtCredit.visibility = View.GONE
    }
}