package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SinglePackageTileBinding
import com.codingpixel.dedicatekitchen.databinding.SinglePricingPlanBinding
import com.codingpixel.dedicatekitchen.interfaces.SubscriptionInterface
import com.codingpixel.dedicatekitchen.models.PackageModel
import com.codingpixel.dedicatekitchen.viewholders.PackageVH
import com.codingpixel.dedicatekitchen.viewholders.PricingPlanVH

class PricingAdapter(
    private val packages: ArrayList<PackageModel>,
    private val cartItemsListener: SubscriptionInterface? = null
) :
    RecyclerView.Adapter<PackageVH>() {

    // PricingPlanVH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SinglePackageTileBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_package_tile,
                parent,
                false
            )

        return PackageVH(binding = binding, listener = cartItemsListener)
    }

    override fun getItemCount(): Int {
        return packages.size
    }

    override fun onBindViewHolder(holder: PackageVH, position: Int) {
        holder.bind(source = packages[position], position = position)
    }

    fun itemTapped(position: Int) {
        cartItemsListener?.packageSelected(packageDetail = packages[position])
    }

    fun amountTapped(position: Int) {
        cartItemsListener?.amountSelected(position, packages[position])
    }
}