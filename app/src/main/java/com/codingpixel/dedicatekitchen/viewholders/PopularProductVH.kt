package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.ProductsAdapter
import com.codingpixel.dedicatekitchen.databinding.SinglePopularProductItemBinding
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.models.Product

class PopularProductVH(binding: SinglePopularProductItemBinding, adapter: ProductsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SinglePopularProductItemBinding = binding
    private val mAdapter: ProductsAdapter = adapter

    fun bind(source: MenuItemModel, position: Int) {
        mBinding.source = source
        mBinding.adapter = mAdapter
        mBinding.position = position


        /*Edited By Usama*/
//        val url =  source.image
        val url = source.image_base_url + source.image_url
        if (url.isNotEmpty()) {
            Glide.with(mBinding.root.context)
                .load(url)
                .thumbnail(0.5f)
                .placeholder(R.drawable.img_dk_placeholder)
                .error(R.drawable.gray_rounded_rectangle).into(mBinding.ivProductImage)

        } else {
            Glide.with(mBinding.root.context).load(R.drawable.gray_rounded_rectangle)
                .placeholder(R.drawable.img_dk_placeholder)
                .error(R.drawable.gray_rounded_rectangle).into(mBinding.ivProductImage)
        }
    }
}