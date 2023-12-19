package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.CategoriesAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleCategoryItemBinding
import com.codingpixel.dedicatekitchen.models.Category

class CategoryVH(binding: SingleCategoryItemBinding, adapter: CategoriesAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleCategoryItemBinding = binding
    private val mAdapter: CategoriesAdapter = adapter

    fun bind(model: Category, position: Int) {
        mBinding.position = position
        mBinding.adapter = mAdapter
        mBinding.model = model

        val url = model.image_base_url + model.image_url
        if (url.isNotEmpty()) {
            Glide.with(mBinding.root.context)
                .load(url)
                .thumbnail(0.5f)
                .placeholder(R.drawable.img_dk_placeholder)
                .error(R.drawable.img_dk_placeholder).into(mBinding.ivCategoryImage)

        } else {
            Glide.with(mBinding.root.context).load(R.drawable.gray_rounded_rectangle)
                .placeholder(R.drawable.img_dk_placeholder)
                .error(R.drawable.img_dk_placeholder).into(mBinding.ivCategoryImage)
        }

//        Glide.with(mBinding.root.context).load(model.image).into(mBinding.ivCategoryImage)
    }
}