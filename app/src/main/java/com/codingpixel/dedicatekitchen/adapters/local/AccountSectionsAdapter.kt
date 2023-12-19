package com.codingpixel.dedicatekitchen.adapters.local

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleAccountSectionItemBinding
import com.codingpixel.dedicatekitchen.interfaces.AccountSectionListener
import com.codingpixel.dedicatekitchen.models.local.AccountSectionItem
import com.codingpixel.dedicatekitchen.viewholders.local.AccountSectionVH

class AccountSectionsAdapter(
    private val accountSections: ArrayList<AccountSectionItem>,
    private val accountSectionListener: AccountSectionListener? = null
) :
    RecyclerView.Adapter<AccountSectionVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountSectionVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: SingleAccountSectionItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.single_account_section_item, parent, false)
        return AccountSectionVH(binding = binding, adapter = this@AccountSectionsAdapter)
    }

    override fun getItemCount(): Int {
        return accountSections.size
    }

    override fun onBindViewHolder(holder: AccountSectionVH, position: Int) {
        holder.bind(source = accountSections[position], position = position)
    }


    fun itemTapped(sectionIndex : Int,  position: Int) {
        accountSectionListener?.itemTapped(sectionIndex = sectionIndex, subSectionIndex = position)
    }

//    fun switchChanged(position: Int, state: Boolean) {
//
//    }
}