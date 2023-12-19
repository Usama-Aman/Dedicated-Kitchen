package com.codingpixel.dedicatekitchen.adapters.local

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleAccountSectionItemBinding
import com.codingpixel.dedicatekitchen.databinding.SinglePaymentMethodOptionItemBinding
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.PaymentMethod
import com.codingpixel.dedicatekitchen.viewholders.local.AccountSectionVH
import com.codingpixel.dedicatekitchen.viewholders.local.PaymentMethodOptionVH

class PaymentMethodOptionsAdapter(
    private val paymentOptions: ArrayList<PaymentMethod>,
    private val itemClickListener: ItemClickListener? = null
) : RecyclerView.Adapter<PaymentMethodOptionVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodOptionVH {
        val inflator = LayoutInflater.from(parent.context)
        val binding: SinglePaymentMethodOptionItemBinding =
            DataBindingUtil.inflate(inflator, R.layout.single_payment_method_option_item, parent, false)
        return PaymentMethodOptionVH(binding = binding, adapter = this@PaymentMethodOptionsAdapter)
    }

    override fun getItemCount(): Int {
        return paymentOptions.size
    }

    override fun onBindViewHolder(holder: PaymentMethodOptionVH, position: Int) {
        holder.bind(source = paymentOptions[position], position = position)
    }

    fun optionTapped(position : Int){
        itemClickListener?.brandTapped(position = position , title = paymentOptions[position].title)
    }
}