package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SinglePaymentMethodItemBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.interfaces.PaymentMethodListener
import com.codingpixel.dedicatekitchen.models.CardModel
import com.codingpixel.dedicatekitchen.viewholders.PaymentMethodVH

class PaymentMethodsAdapter(
    private val paymentMethods: ArrayList<CardModel>,
    private val paymentMethodListener: PaymentMethodListener? = null,
    private val viewType: String = Constants.VIEW_TYPE_DISPLAY,
    val fromPaymentMethodsActivity: Boolean = false
) : RecyclerView.Adapter<PaymentMethodVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodVH {
        val inflator = LayoutInflater.from(parent.context)
        val binding: SinglePaymentMethodItemBinding =
            DataBindingUtil.inflate(
                inflator,
                R.layout.single_payment_method_item,
                parent,
                false
            )
        return PaymentMethodVH(binding = binding, adapter = this@PaymentMethodsAdapter)
    }

    override fun getItemCount(): Int {
        return paymentMethods.size
    }

    override fun onBindViewHolder(holder: PaymentMethodVH, position: Int) {
        holder.bind(
            source = paymentMethods[position], position = position, viewAs = viewType,
            fromPaymentMethodsActivity = fromPaymentMethodsActivity
        )
    }

    fun itemTapped(position: Int) {
        paymentMethodListener?.itemTapped(position = position)
    }

    fun deleteTapped(position: Int) {
        paymentMethodListener?.deleteTapped(
            position = position,
            cardId = paymentMethods[position].id
        )
    }

    fun makeCardDefault(position: Int) {
        paymentMethodListener?.makeCardDefault(position)
    }
}