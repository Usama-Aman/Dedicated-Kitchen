package com.codingpixel.dedicatekitchen.viewholders

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.ProductsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleProductItemBinding
import com.codingpixel.dedicatekitchen.models.MenuItemModel

class ProductVH(binding: SingleProductItemBinding, adapter: ProductsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleProductItemBinding = binding
    private val mAdapter: ProductsAdapter = adapter

    fun bind(source: MenuItemModel, position: Int, showPlusIcon: Boolean = false) {
        mBinding.source = source
        mBinding.adapter = mAdapter
        mBinding.position = position

        if (showPlusIcon)
            mBinding.ivPlus.visibility = View.VISIBLE
        else
            mBinding.ivPlus.visibility = View.GONE

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