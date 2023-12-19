package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleSelectAddressItemBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.models.UserLocation

class ChooseLocationVH(binding: SingleSelectAddressItemBinding, adapter: LocationsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleSelectAddressItemBinding = binding
    private val mAdapter: LocationsAdapter = adapter

    fun bind(source: UserLocation, position: Int, viewAs : String = Constants.VIEW_TYPE_DISPLAY) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter
    }

}