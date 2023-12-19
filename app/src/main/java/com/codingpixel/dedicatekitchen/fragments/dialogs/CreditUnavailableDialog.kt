package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.databinding.CreditUnavailablePopupBinding
import com.codingpixel.dedicatekitchen.interfaces.CreditUnavailableInterface
import com.codingpixel.dedicatekitchen.interfaces.MealPrepPopupListener
import com.codingpixel.dedicatekitchen.models.UserLocation
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import kotlin.collections.ArrayList


class CreditUnavailableDialog : BaseDialogFragment(), CreditUnavailableInterface {

    private lateinit var mBinding: CreditUnavailablePopupBinding
    private var threeButtonsDialogListener: CreditUnavailableInterface? = null
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var viewModel: UserViewModel
    private var isDateSelected: Boolean = false
    private var isTimeSelected: Boolean = true
    private var isLocationSelected: Boolean = false

    private val locations = ArrayList<UserLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.credit_unavailable_popup,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        initClickListener()
    }

    private fun initClickListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.cbPurchaseAPackage.setOnClickListener {
            dismiss()
            threeButtonsDialogListener?.purchasePackage()
        }
        mBinding.cbAddAnotherItem.setOnClickListener {
            dismiss()
            threeButtonsDialogListener?.addAnotherItem()
        }
        mBinding.cbTopup.setOnClickListener {
            dismiss()
            threeButtonsDialogListener?.topUp()
        }

    }


    fun setListener(ButtonsDialogListener: CreditUnavailableInterface?) {
        this.threeButtonsDialogListener = ButtonsDialogListener
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            CreditUnavailableDialog().apply {
                arguments = Bundle().apply {
                }
            }
    }


    override fun purchasePackage() {
        threeButtonsDialogListener?.purchasePackage()
    }

    override fun addAnotherItem() {
        threeButtonsDialogListener?.addAnotherItem()
    }

    override fun topUp() {
        threeButtonsDialogListener?.topUp()
    }
}