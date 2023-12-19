package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentOrderPlacedDialogBinding
import com.codingpixel.dedicatekitchen.interfaces.OrderPlacedListener


/**
 * A simple [Fragment] subclass.
 * Use the [OrderPlacedDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderPlacedDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: FragmentOrderPlacedDialogBinding

    private var orderPlacedListener: OrderPlacedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }


//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return object : Dialog(activity!!, theme) {
//            override fun onBackPressed() {
//                //do your stuff
//                orderPlacedListener?.orderPlaced()
//            }
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_order_placed_dialog,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.btnDone.setOnClickListener {
            dismiss()
            orderPlacedListener?.orderPlaced()
        }

//        mBinding.parent.setOnClickListener {
//            dismiss()
//        }
    }

    fun setCallBack(orderPlacedListener: OrderPlacedListener? = null){
        this.orderPlacedListener = orderPlacedListener
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            OrderPlacedDialogFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}