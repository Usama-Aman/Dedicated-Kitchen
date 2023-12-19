package com.codingpixel.dedicatekitchen.fragments.account

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
//import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.FragmentAccountInformationDialogBinding


/**
 * A simple [Fragment] subclass.
 * Use the [AccountInformationDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountInformationDialogFragment : BottomSheetDialogFragment() {

    private lateinit var mBinding : FragmentAccountInformationDialogBinding
    private lateinit var mBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AccountsBottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        //return super.onCreateDialog(savedInstanceState)
//
//        val bottomSheetDialog =
//            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        bottomSheetDialog.setOnShowListener { dialog: DialogInterface ->
//            val dialogc = dialog as BottomSheetDialog
//            //setupFullHeight(bottomSheetDialog = dialogc)
//            // When using AndroidX the resource can be found at com.google.android.material.R.id.design_bottom_sheet
//            val bottomSheet =
//                dialogc.findViewById<FrameLayout>( com.google.android.material.R.id.design_bottom_sheet)
//            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
//            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
//            BottomSheetBehavior.from(bottomSheet).setHideable(true);
////            val bottomSheetBehavior: BottomSheetBehavior<*> =
////                BottomSheetBehavior.from(bottomSheet)
////            bottomSheetBehavior.peekHeight = resources.getDisplayMetrics().heightPixels
////            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//        }
//        return bottomSheetDialog
//    }

//    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
//        val bottomSheet =
//            bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet) as FrameLayout?
//        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
//        val layoutParams = bottomSheet!!.layoutParams
//        val windowHeight = getWindowHeight()
//        if (layoutParams != null) {
//            layoutParams.height = windowHeight
//        }
//        bottomSheet.layoutParams = layoutParams
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
//    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay
            .getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDialog()?.getWindow()?.getAttributes()?.windowAnimations = R.style.BottomDialogAnimation;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_information_dialog, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AccountInformationDialogFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}