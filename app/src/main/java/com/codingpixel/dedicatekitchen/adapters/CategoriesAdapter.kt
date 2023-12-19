package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleCategoryItemBinding
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.MenuInterface
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.viewholders.CategoryVH
import java.util.*
import kotlin.collections.ArrayList


class CategoriesAdapter(
    private var categories: ArrayList<Category>,
    private val itemClickListener: MenuInterface? = null
) :
    RecyclerView.Adapter<CategoryVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleCategoryItemBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_category_item,
                parent,
                false
            )
        return CategoryVH(binding = binding, adapter = this@CategoriesAdapter)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        holder.bind(model = categories[position], position = position)
    }

    fun itemTapped(position: Int) {
        itemClickListener?.menuItemTapped(categories[position], position = position)
    }
}