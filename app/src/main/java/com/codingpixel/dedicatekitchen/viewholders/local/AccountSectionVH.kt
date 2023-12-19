package com.codingpixel.dedicatekitchen.viewholders.local

import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.adapters.local.AccountSectionsAdapter
import com.codingpixel.dedicatekitchen.adapters.local.AccountSubSectionsAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleAccountSectionItemBinding
import com.codingpixel.dedicatekitchen.models.local.AccountSectionItem

class AccountSectionVH(binding: SingleAccountSectionItemBinding, adapter: AccountSectionsAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleAccountSectionItemBinding = binding
    private val mAdapter: AccountSectionsAdapter = adapter

    private lateinit var subSectionsAdapter: AccountSubSectionsAdapter

    fun bind(source: AccountSectionItem, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter
        subSectionsAdapter = AccountSubSectionsAdapter(
            accountSubSections = source.subSectionsList,
            adapter = mAdapter
        )
        mBinding.rvSubSections.adapter = subSectionsAdapter

    }
}