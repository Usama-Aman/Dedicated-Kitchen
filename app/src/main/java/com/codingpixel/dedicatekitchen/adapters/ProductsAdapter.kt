package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SinglePopularProductItemBinding
import com.codingpixel.dedicatekitchen.databinding.SingleProductItemBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.interfaces.ProductItemListener
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.viewholders.PopularProductVH
import com.codingpixel.dedicatekitchen.viewholders.ProductVH

class ProductsAdapter(
    private val showPlusIcon : Boolean = false,
    private val products: ArrayList<MenuItemModel>,
    private val productViewType: String = Constants.PRODUCT_VERTICAL_ITEM,
    private val productItemListener: ProductItemListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when(productViewType){

            Constants.PRODUCT_VERTICAL_ITEM -> {
                val binding: SingleProductItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.single_product_item,
                        parent,
                        false
                    )
                return ProductVH(binding = binding, adapter = this@ProductsAdapter)
            }

            Constants.PRODUCT_HORIZONTAL_ITEM -> {
                val binding: SinglePopularProductItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.single_popular_product_item,
                        parent,
                        false
                    )
                return PopularProductVH(binding = binding, adapter = this@ProductsAdapter)
            }

            else -> {
                val binding: SingleProductItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.single_product_item,
                        parent,
                        false
                    )
                return ProductVH(binding = binding, adapter = this@ProductsAdapter)
            }
        }


    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ProductVH)
            holder.bind(source = products[position], position = position, showPlusIcon = showPlusIcon)
        else if(holder is PopularProductVH)
            holder.bind(source = products[position], position = position)
    }


    fun productTapped(position: Int) {
        productItemListener?.productTapped(position = position , product = products[position])
    }

    fun plusTapped(position: Int) {
        productItemListener?.productTapped(position = position, product = products[position])
    }

    fun minusTapped(position: Int) {
        productItemListener?.minusTapped(position = position)
    }

    fun heartTapped(position: Int) {
        productItemListener?.heartTapped(position = position)
    }
}