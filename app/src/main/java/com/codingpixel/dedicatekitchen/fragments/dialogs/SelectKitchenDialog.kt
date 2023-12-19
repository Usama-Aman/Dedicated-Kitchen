package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.adapters.KitchenSelectionAdapter
import com.codingpixel.dedicatekitchen.databinding.SelectPopupBinding
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.SelectListener
import com.codingpixel.dedicatekitchen.models.DedicateKitchen
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectKitchenDialog : BottomSheetDialogFragment(), ItemClickListener {

    private var twoButtonsDialogListener: SelectListener? = null
    private lateinit var mBinding: SelectPopupBinding

    private lateinit var kitchensAdapter: KitchenSelectionAdapter
    private var kitchensList = ArrayList<DedicateKitchen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReportDialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.select_popup,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kitchensList.clear()
        kitchensList.addAll(MainBottomBarActivity.allKitchenList)
        initAdapter()
        mBinding.buttonCancel.setOnClickListener {
            dismiss()
        }


        mBinding.rlDedicateEagleRidgeLayout.setOnClickListener {
            dismiss()
            twoButtonsDialogListener?.selectKitchen(
                mBinding.tvDedicateEagleRidge.text.toString(),
                "20000007"
            )
        }
        mBinding.rlDedicateMardaKitchenLayout.setOnClickListener {
            dismiss()
            twoButtonsDialogListener?.selectKitchen(
                mBinding.tvDedicateMardaLoop.text.toString(),
                "132000060"
            )
        }
    }

    private fun initAdapter() {
        kitchensAdapter =
            KitchenSelectionAdapter(kitchensList = kitchensList, itemClickListener = this)
        mBinding.rvKitchens.adapter = kitchensAdapter
    }

    fun selectListener(selectListener: SelectListener?) {
        this.twoButtonsDialogListener = selectListener
    }

    companion object {
        @JvmStatic
        fun newInstance(
        ) =
            SelectKitchenDialog().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun brandTapped(position: Int, title: String) {

    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {

    }

    override fun deleteLocation(position: Int, addressId: Int) {

    }

    override fun orderCompleted(position: Int) {

    }

    override fun itemClicked(position: Int) {
//        twoButtonsDialogListener?.selectKitchen(
//            kitchensList[position].name,
//            kitchensList[position].terminalId
//        )
        dismiss()
        twoButtonsDialogListener?.changeKitchen(
            selectedKitchen = kitchensList[position]
        )
    }
}