package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleCartProductItemBinding
import com.codingpixel.dedicatekitchen.interfaces.CartItemsListener
import com.codingpixel.dedicatekitchen.models.OrderModel
import com.codingpixel.dedicatekitchen.viewholders.CartItemVH

class CartItemsAdapter(
    private val products: ArrayList<OrderModel>,
    private val cartItemsListener: CartItemsListener? = null
) :
    RecyclerView.Adapter<CartItemVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleCartProductItemBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_cart_product_item,
                parent,
                false
            )
        return CartItemVH(binding = binding, adapter = this@CartItemsAdapter)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: CartItemVH, position: Int) {
        holder.bind(source = products[position], position = position)
    }

    fun itemTapped(position: Int) {
        cartItemsListener?.itemTapped(position = position)
    }

    fun plusTapped(position: Int) {
        cartItemsListener?.plusTapped(position = position, pKey = products[position].pKey)
    }

    fun minusTapped(position: Int) {
        cartItemsListener?.minusTapped(position = position, pKey = products[position].pKey)
    }

    fun editTapped(position: Int) {
        cartItemsListener?.editTapped(position = position , productDetail = products[position])
    }

}