package com.codingpixel.dedicatekitchen.adapters.local

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleAccountSubSectionItemBinding
import com.codingpixel.dedicatekitchen.models.local.AccountSubSectionItem
import com.codingpixel.dedicatekitchen.viewholders.local.AccountSubSectionVH

class AccountSubSectionsAdapter(
    private val accountSubSections: ArrayList<AccountSubSectionItem>,
    private val adapter: AccountSectionsAdapter
) :
    RecyclerView.Adapter<AccountSubSectionVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountSubSectionVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleAccountSubSectionItemBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.single_account_sub_section_item,
                parent,
                false
            )
        return AccountSubSectionVH(binding = binding, adapter = this@AccountSubSectionsAdapter)
    }

    override fun getItemCount(): Int {
        return accountSubSections.size
    }

    override fun onBindViewHolder(holder: AccountSubSectionVH, position: Int) {
        holder.bind(
            source = accountSubSections[position],
            position = position,
            totalSize = accountSubSections.size
        )
    }

    fun itemTapped(sectionIndex : Int,  position: Int) {
        adapter.itemTapped(sectionIndex = sectionIndex, position = position)
    }

//    fun switchChanged(position: Int, state: Boolean) {
//
//    }
}