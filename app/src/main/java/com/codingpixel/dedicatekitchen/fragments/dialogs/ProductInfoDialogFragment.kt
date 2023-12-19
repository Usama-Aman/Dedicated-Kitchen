package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.Glide
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentProductInfoDialogBinding
import com.codingpixel.dedicatekitchen.interfaces.ProductInfoListener
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.MenuItemModel

class ProductInfoDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: FragmentProductInfoDialogBinding

    private var category: Category? = null
    private var product: MenuItemModel? = null

    private var productInfoListener: ProductInfoListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getSerializable("product") as MenuItemModel?
            category = it.getSerializable("category") as Category?
        }
        setStyle(STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_product_info_dialog,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
    }

    fun setListener(callback: ProductInfoListener?) {
        this.productInfoListener = callback
    }

    private fun initData() {

        /*Edited by Usama*/
        try {
            Glide.with(context!!).load(product?.image_base_url + product?.image_url ?: "")
                .placeholder(R.drawable.img_dk_placeholder).into(mBinding.ivProductImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mBinding.tvCategory.text = category?.name ?: ""
        mBinding.tvProductName.text = product?.attributes?.name ?: ""
        mBinding.tvProductDescription.text = product?.attributes?.itemDescription ?: ""

        if (product?.isFavourite == true) {
            mBinding.ivHeartToggle.setImageResource(R.drawable.ic_favourite)
        } else {
            mBinding.ivHeartToggle.setImageResource(R.drawable.ic_un_favourite)
        }
    }

    private fun initListener() {
        mBinding.ivHeartToggle.setOnClickListener {
            productInfoListener?.toggle(
                catId = category?.ID ?: "", productId = product?.ID ?: "",
                toggle = if (product?.isFavourite == true)
                    0 else 1
            )
            product?.isFavourite = !(product?.isFavourite ?: false)
            if (product?.isFavourite == true) {
                mBinding.ivHeartToggle.setImageResource(R.drawable.ic_favourite)
            } else {
                mBinding.ivHeartToggle.setImageResource(R.drawable.ic_un_favourite)
            }

        }

        mBinding.parent.setOnClickListener {
            dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(category: Category?, product: MenuItemModel?) =
            ProductInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("product", product)
                    putSerializable("category", category)
                }
            }
    }
}