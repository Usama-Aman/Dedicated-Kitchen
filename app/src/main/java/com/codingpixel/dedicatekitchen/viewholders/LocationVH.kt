package com.codingpixel.dedicatekitchen.viewholders

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleLocationItemBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.models.UserLocation

class LocationVH(binding: SingleLocationItemBinding, adapter: LocationsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleLocationItemBinding = binding
    private val mAdapter: LocationsAdapter = adapter

    @SuppressLint("SetTextI18n")
    fun bind(source: UserLocation, position: Int, viewAs: String = Constants.VIEW_TYPE_DISPLAY) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter
        if (source.isChecked) {
            mBinding.rbDefault.text = "Default"
        } else {
            mBinding.rbDefault.text = "Not Default"
        }




        when (viewAs) {

            Constants.VIEW_TYPE_DISPLAY -> {
                mBinding.showToggle = false
                mBinding.showDelete = true

                if (source.isChecked) {
                    mBinding.ivDelete.visibility = View.GONE
                } else {
                    mBinding.ivDelete.visibility = View.VISIBLE
                }

            }

            Constants.VIEW_TYPE_TOGGLE -> {
                mBinding.showToggle = true
                mBinding.showDelete = false
            }

            else -> {
                mBinding.showToggle = false
                mBinding.showDelete = false
            }


        }


    }

}