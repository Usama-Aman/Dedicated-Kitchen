package com.codingpixel.dedicatekitchen.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.PackageInfoAdapter
import com.codingpixel.dedicatekitchen.databinding.SinglePackageTileBinding
import com.codingpixel.dedicatekitchen.interfaces.SubscriptionInterface
import com.codingpixel.dedicatekitchen.models.PackageModel

class PackageVH(
    private val binding: SinglePackageTileBinding,
    private val listener: SubscriptionInterface? = null
) :
    RecyclerView.ViewHolder(binding.root) {

    private lateinit var adapter: PackageInfoAdapter

    fun bind(source: PackageModel, position: Int) {
        binding.model = source
        binding.listener = listener

        adapter = PackageInfoAdapter(items = source.detail.split(","))
        binding.rvItems.adapter = adapter

        when {
            source.name.contains("Bronze") -> {
                binding.tvFooterLabel.visibility = View.GONE
                binding.tvFooterLabel.text = ""
            }

            source.name.contains("Gold") -> {
                binding.tvFooterLabel.visibility = View.VISIBLE
                binding.tvFooterLabel.text = "$60 of REWARDS"
            }

            source.name.contains("Silver") -> {
                binding.tvFooterLabel.visibility = View.VISIBLE
                binding.tvFooterLabel.text = "$15 of REWARDS"
            }

            source.name.contains("Platinum") -> {
                binding.tvFooterLabel.visibility = View.VISIBLE
                binding.tvFooterLabel.text = "$150 of REWARDS"
            }

            else -> {
                binding.tvFooterLabel.visibility = View.GONE
                binding.tvFooterLabel.text = ""

            }
        }

        binding.tvPlanPrice.setOnClickListener {
            listener?.amountSelected(position, source)
        }

        binding.btnBuyNow.setOnClickListener {
            listener?.packageSelected(packageDetail = source)
        }
    }
}