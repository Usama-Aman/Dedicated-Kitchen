package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentCardExpiryDatePickerDialogBinding
import com.codingpixel.dedicatekitchen.helpers.IntentParams
import com.codingpixel.dedicatekitchen.interfaces.DateTImePickerListener
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [CardExpiryDatePickerDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardExpiryDatePickerDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: FragmentCardExpiryDatePickerDialogBinding


    private var selectedMonth = 0
    private var selectedYear = 0
    private var dateTImePickerListener: DateTImePickerListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedMonth = it.getInt(IntentParams.SELECTED_MONTH)
            selectedYear = it.getInt(IntentParams.SELECTED_YEAR)
        }
        setStyle(STYLE_NO_FRAME, R.style.ReportDialogTheme)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        getDialog()?.getWindow()?.getAttributes()?.windowAnimations = R.style.ReportDialogTheme;
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_card_expiry_date_picker_dialog,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMonthPicker()
        setYearPicker()
        setClickListener()
    }

    private fun setClickListener() {
        mBinding.btnDone.setOnClickListener {
            dismiss()
            dateTImePickerListener?.pickedDateTime(
                year = mBinding.npYear.value,
                month = mBinding.npMonth.value
            )
        }

        mBinding.parent.setOnClickListener {
            dismiss()
        }
    }

    private fun setMonthPicker() {
        mBinding.npMonth.minValue = 1
        mBinding.npMonth.maxValue = 12
        mBinding.npMonth.value = selectedMonth
    }

    private fun setYearPicker() {
        mBinding.npYear.minValue = Calendar.getInstance().get(Calendar.YEAR)
        mBinding.npYear.maxValue = Calendar.getInstance().get(Calendar.YEAR) + 20
        mBinding.npYear.value = if (selectedYear > 0)
            selectedYear
        else
            Calendar.getInstance().get(Calendar.YEAR)
    }

    fun setListener(listener: DateTImePickerListener?) {
        this.dateTImePickerListener = listener
    }


    companion object {
        @JvmStatic
        fun newInstance(selectedMonth: Int = 1, selectedYear: Int = 0) =
            CardExpiryDatePickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(IntentParams.SELECTED_MONTH, selectedMonth)
                    putInt(IntentParams.SELECTED_YEAR, selectedYear)
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}