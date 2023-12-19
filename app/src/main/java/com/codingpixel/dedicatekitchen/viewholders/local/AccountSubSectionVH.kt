package com.codingpixel.dedicatekitchen.viewholders.local

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.local.AccountSubSectionsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleAccountSubSectionItemBinding
import com.codingpixel.dedicatekitchen.models.local.AccountSubSectionItem

class AccountSubSectionVH(binding: SingleAccountSubSectionItemBinding, adapter: AccountSubSectionsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleAccountSubSectionItemBinding = binding
    private val mAdapter: AccountSubSectionsAdapter = adapter

    fun bind(source: AccountSubSectionItem, position: Int, totalSize: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.totalSize = totalSize
        mBinding.adapter = mAdapter
        mBinding.ivStartIcon.setImageResource(source.startIcon)
    }
}