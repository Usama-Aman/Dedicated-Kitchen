//package com.codingpixel.dedicatekitchen.fragments.dialogs
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.DialogFragment
//import com.codingpixel.dedicatekitchen.R
//import com.codingpixel.dedicatekitchen.adapters.TimeSlotsAdapter
//import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
//import com.codingpixel.dedicatekitchen.databinding.FragmentPickTimeSlotDialogBinding
//import com.codingpixel.dedicatekitchen.helpers.CommonMethods
//import com.codingpixel.dedicatekitchen.helpers.IntentParams
//import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
//import com.codingpixel.dedicatekitchen.interfaces.SelectedTimeSlotListener
//import com.codingpixel.dedicatekitchen.models.TimeSlot
//
//
///**
// * A simple [Fragment] subclass.
// * Use the [PickTimeSlotDialogFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
//class PickTimeSlotDialogFragment : BaseDialogFragment() {
//
//    private lateinit var mBinding: FragmentPickTimeSlotDialogBinding
//
//    private lateinit var timeslotsAdapter: TimeSlotsAdapter
//    private val timeSlots = ArrayList<TimeSlot>()
//
//    private var timeSlotListener: SelectedTimeSlotListener? = null
//
//    private var chosenTimeSlot: TimeSlot? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            chosenTimeSlot = it.getSerializable(IntentParams.TIME_SLOT) as TimeSlot?
////            param1 = it.getString(ARG_PARAM1)
////            param2 = it.getString(ARG_PARAM2)
//        }
//        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        mBinding = DataBindingUtil.inflate(
//            inflater,
//            R.layout.fragment_pick_time_slot_dialog,
//            container,
//            false
//        )
//        return mBinding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initClickListener()
//        initTimeSlotsAdapter()
//    }
//
//    private fun initClickListener() {
//
//        mBinding.dialogParent.setOnClickListener {
//            dismiss()
//        }
//
//        mBinding.btnDone.setOnClickListener {
//            if (timeSlots.indexOfFirst { it.isSelected } == -1) {
//                showShortToast(message = "Please select a time slot")
//            } else {
//                dismiss()
//                timeSlotListener?.timeSlot(timeSlot = timeSlots.find { it.isSelected }!!)
//            }
//
//        }
//
//        mBinding.tvCancel.setOnClickListener {
//            dismiss()
//        }
//
//    }
//
//    private fun initTimeSlotsAdapter() {
//        timeSlots.clear()
//        timeSlots.addAll(CommonMethods.getDummyTimeSlots())
//        if (chosenTimeSlot != null) {
//            val alreadySelectedIndex = timeSlots.indexOfFirst { it.slot == chosenTimeSlot!!.slot }
//            if (alreadySelectedIndex != -1)
//                timeSlots[alreadySelectedIndex].isSelected = true
//        }
//        timeslotsAdapter = TimeSlotsAdapter(timeSlots = timeSlots, itemClickListener = object :
//            ItemClickListener {
//            override fun itemClicked(position: Int) {
//                val alreadySelectedIndex = timeSlots.indexOfFirst { it.isSelected }
//                if (alreadySelectedIndex != -1) {
//                    timeSlots[alreadySelectedIndex].isSelected = false
//                    timeslotsAdapter.notifyItemChanged(alreadySelectedIndex)
//                }
//                timeSlots[position].isSelected = true
//                timeslotsAdapter.notifyItemChanged(position)
//            }
//
//            override fun brandTapped(position: Int, title: String) {
//
//            }
//
//            override fun makeDefaultLocation(position: Int, addressId: Int) {
//
//            }
//
//            override fun deleteLocation(position: Int, addressId: Int) {
//                TODO("Not yet implemented")
//            }
//        })
//        mBinding.rvTimeSlots.adapter = timeslotsAdapter
//    }
//
//    fun setCallBack(callBack: SelectedTimeSlotListener?) {
//        this.timeSlotListener = callBack
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(timeSlot: TimeSlot?) =
//            PickTimeSlotDialogFragment().apply {
//                arguments = Bundle().apply {
//                    putSerializable(IntentParams.TIME_SLOT, timeSlot)
////                    putString(ARG_PARAM1, param1)
////                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}