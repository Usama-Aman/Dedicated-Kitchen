package com.codingpixel.dedicatekitchen.viewholders

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.CartItemsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleCartProductItemBinding
import com.codingpixel.dedicatekitchen.models.OrderModel

class CartItemVH(binding: SingleCartProductItemBinding, adapter: CartItemsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleCartProductItemBinding = binding
    private val mAdapter: CartItemsAdapter = adapter

    @SuppressLint("SetTextI18n")
    fun bind(source: OrderModel, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter

//        var quantity = source.options.forEach { it.price }.apply {,
//
//        }
        var extraOptionsPrice = 0F
        for (i in 0 until source.options.size) {
            extraOptionsPrice += source.options[i].price.toFloat()
        }
        val price = ((source.price.toFloat() + extraOptionsPrice) * source.quantity.toFloat())
        println("extraOptionsPriceextraOptionsPrice -> $extraOptionsPrice -> price $price -> ${source.options.size}")
        mBinding.tvProductPrice.text = "$${String.format("%.2f", price)}"
        // source.total_amount = price.toString()

//        if (source.img.isEmpty())
//            Glide.with(itemView).load(R.drawable.food_plate_bg).into(mBinding.ivProductImage)
//        else
//            Glide.with(itemView).load(source.img).into(mBinding.ivProductImage)

        Glide.with(mBinding.root.context)
            .load(source.img)
            .placeholder(R.drawable.img_dk_placeholder).error(R.drawable.img_dk_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding.ivProductImage)
//        if (source.viewType == "cart") {
//            mBinding.ivPlus.visibility = View.VISIBLE
//            mBinding.ivMinus.visibility = View.VISIBLE
//            mBinding.tvQuantity.visibility = View.VISIBLE
//            mBinding.tvEdit.visibility = View.VISIBLE
//
//        } else if (source.viewType == "checkout") {
//            mBinding.ivPlus.visibility = View.GONE
//            mBinding.ivMinus.visibility = View.GONE
//            mBinding.tvQuantity.visibility = View.GONE
//            mBinding.tvEdit.visibility = View.GONE
//        }
//        val userSubTags = ArrayList<String>()
//        source.options.forEach {
//            userSubTags.add(it.name)
//        }
//        mBinding.tvProductDescription.text = TextUtils.join("," , userSubTags)
        mBinding.tvProductDescription.text = source.options.joinToString { it.name }
        //put condition here.
        if (source.showProductNote) {
            if (source.notes.isEmpty())
                mBinding.tvProductNote.visibility = View.GONE
            else {
                mBinding.tvProductNote.text = "Note: " + source.notes
                mBinding.tvProductNote.visibility = View.VISIBLE
            }
        } else {
            mBinding.tvProductNote.visibility = View.GONE
        }


        if (!source.isEditable) {
            mBinding.ivPlus.visibility = View.GONE
            mBinding.ivMinus.visibility = View.GONE
            mBinding.tvEdit.visibility = View.GONE
        }
    }
}