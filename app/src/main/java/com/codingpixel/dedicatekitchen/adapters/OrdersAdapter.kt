package com.codingpixel.dedicatekitchen.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleLoaderItemBinding
import com.codingpixel.dedicatekitchen.databinding.SingleOrderItemBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.Order
import com.codingpixel.dedicatekitchen.viewholders.LoaderVH
import com.codingpixel.dedicatekitchen.viewholders.OrderVH

class OrdersAdapter(
    private val ordersList: ArrayList<Order>,
    private val itemClickListener: ItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // OrderVH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == Constants.CONTENT_TYPE) {
            val binding: SingleOrderItemBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.single_order_item,
                    parent,
                    false
                )
            return OrderVH(binding = binding, adapter = this@OrdersAdapter)
        } else {
            val binding: SingleLoaderItemBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.single_loader_item,
                    parent,
                    false
                )
            return LoaderVH(binding = binding)
        }

    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderVH -> holder.bind(source = ordersList[position], position = position)
            is LoaderVH -> holder.bind()
            else -> {
                Log.d("VH Type", "Invalid")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (ordersList[position].id != -1) {
            Constants.CONTENT_TYPE
        } else {
            Constants.LOADING_TYPE
        }
    }

    fun orderDetails(position: Int) {
        itemClickListener?.orderDetails(position = position, orderDetail = ordersList[position])
    }

    fun cancelOrder(position: Int) {
        itemClickListener?.cancelOrder(position, ordersList[position].id)
    }

    fun infoClick() {
        itemClickListener?.info()
    }

    fun reOrderTapped(position: Int) {
        itemClickListener?.reorder(position = position, orderDetail = ordersList[position])
    }

    fun refreshRow(position: Int) {
        itemClickListener?.refreshRow(position = position)
    }

    fun orderCompleted(position: Int) {
        itemClickListener?.orderCompleted(position = position)
    }
}