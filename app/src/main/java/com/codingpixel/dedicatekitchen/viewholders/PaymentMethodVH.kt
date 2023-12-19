package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.PaymentMethodsAdapter
import com.codingpixel.dedicatekitchen.databinding.SinglePaymentMethodItemBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.models.PaymentMethod

class PaymentMethodVH(binding: SinglePaymentMethodItemBinding, adapter: PaymentMethodsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SinglePaymentMethodItemBinding = binding
    private val mAdapter: PaymentMethodsAdapter = adapter

    fun bind(
        source: CardModel, position: Int, viewAs: String = Constants.VIEW_TYPE_DISPLAY,
        fromPaymentMethodsActivity: Boolean = false
    ) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter
        mBinding.fromPaymentMethodsActivity = fromPaymentMethodsActivity


        if (source.is_default == 1) {
            mBinding.rbSubtitle.text = "Default Card"
            mBinding.rbSubtitle.isChecked = true
        } else {
            mBinding.rbSubtitle.text = if (fromPaymentMethodsActivity)
                "Make it Default"
            else
                "Not Default Card"
            mBinding.rbSubtitle.isChecked = false
        }

        when (source.card_brand) {
            "Visa" -> Glide.with(itemView).load(R.drawable.visa).into(mBinding.ivAddressIcon)
            "MasterCard" -> Glide.with(itemView).load(R.drawable.master_card)
                .into(mBinding.ivAddressIcon)
            else -> Glide.with(itemView).load(R.drawable.visa).into(mBinding.ivAddressIcon)
        }

        when (viewAs) {
            Constants.VIEW_TYPE_DISPLAY -> {
                mBinding.showToggle = false
                mBinding.showDelete = true
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