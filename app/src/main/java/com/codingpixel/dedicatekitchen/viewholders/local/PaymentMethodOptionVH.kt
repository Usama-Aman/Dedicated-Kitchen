package com.codingpixel.dedicatekitchen.viewholders.local

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.local.PaymentMethodOptionsAdapter
import com.codingpixel.dedicatekitchen.databinding.SinglePaymentMethodOptionItemBinding
import com.codingpixel.dedicatekitchen.models.PaymentMethod

class PaymentMethodOptionVH(
    binding: SinglePaymentMethodOptionItemBinding,
    adapter: PaymentMethodOptionsAdapter
) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SinglePaymentMethodOptionItemBinding = binding
    private val mAdapter: PaymentMethodOptionsAdapter = adapter

    fun bind(source: PaymentMethod, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter

        mBinding.ivMethodIcon.setImageResource(source.cardImage)
    }
}