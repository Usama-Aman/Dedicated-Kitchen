package com.codingpixel.dedicatekitchen.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.databinding.SingleMenuCategoryItemBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.MenuInterface
import com.codingpixel.dedicatekitchen.models.Category

class HomeMenuItemVH(
    private val binding: SingleMenuCategoryItemBinding,
    private val itemClickListener: MenuInterface?
) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(source: Category, position: Int) {

        binding.source = source
        binding.position = position
        binding.listener = itemClickListener

        binding.ivCatImg.setOnClickListener {
            itemClickListener?.menuItemTapped(products = source, position = position)
        }

        val url = source.image_base_url + source.image_url
        binding.ivCatImg.setImage(url)

        binding.ivCatImg.viewTreeObserver.addOnPreDrawListener {
            binding.ivCatImg.viewTreeObserver.removeOnPreDrawListener { true }
            val imageHeight = binding.ivCatImg.measuredHeight.toString()
            binding.ivCatImg.layoutParams.width =
                (imageHeight.toDouble() * Constants.CATEGORIES_WIDTH_RATIO).toInt()
            true
        }
    }


}

