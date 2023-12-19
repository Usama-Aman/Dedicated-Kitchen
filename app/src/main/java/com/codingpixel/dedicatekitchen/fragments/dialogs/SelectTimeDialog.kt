package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.TimeSlotsAdapter
import com.codingpixel.dedicatekitchen.databinding.SelectTimePopupBinding
import com.codingpixel.dedicatekitchen.helpers.CommonMethods
import com.codingpixel.dedicatekitchen.interfaces.SelectListener
import com.codingpixel.dedicatekitchen.interfaces.SelectedTimeSlotListener
import com.codingpixel.dedicatekitchen.models.TimeSlot
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectTimeDialog : BottomSheetDialogFragment(), SelectedTimeSlotListener {
    private lateinit var mBinding: SelectTimePopupBinding
    private lateinit var timeSlotsAdapter: TimeSlotsAdapter
    private var timeSlots = ArrayList<TimeSlot>()
    private var timeSlotListener: SelectedTimeSlotListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReportDialogTheme)
        arguments?.let {
            timeSlots = it.getSerializable("list") as ArrayList<TimeSlot>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.select_time_popup,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.buttonCancel.setOnClickListener {
            dismiss()
        }
        initAdapter()
    }

    private fun initAdapter() {
      //  timeSlots.addAll(timeSlots)
        timeSlotsAdapter = TimeSlotsAdapter(timeSlots, this)
        mBinding.rvTimeSlots.adapter = timeSlotsAdapter
    }

    fun setCallBack(callBack: SelectedTimeSlotListener? = null) {
        timeSlotListener = callBack
    }

    fun selectListener(selectListener: SelectedTimeSlotListener?) {
        this.timeSlotListener = selectListener
    }

    companion object {
        @JvmStatic
        fun newInstance(list: ArrayList<TimeSlot>) =
            SelectTimeDialog().apply {
                arguments = Bundle().apply {
                    putSerializable("list", list)
                }
            }
    }

    override fun timeSlot(timeSlot: TimeSlot) {
        dismiss()
        timeSlotListener?.timeSlot(timeSlot)
    }
}