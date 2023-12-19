package com.codingpixel.dedicatekitchen.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.MenuInterface
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.viewholders.HomeMenuItemVH

class MenuCategoriesAdapter(
    private val categories: ArrayList<Category>,
    private val itemClickListener: MenuInterface? = null
) : RecyclerView.Adapter<HomeMenuItemVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMenuItemVH {
        return HomeMenuItemVH(
            binding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.single_menu_category_item, parent, false
            ), itemClickListener = itemClickListener
        )
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: HomeMenuItemVH, position: Int) {
        holder.bind(source = categories[position], position = position)
    }
}