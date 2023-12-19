package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentPointsEarnedDialogBinding
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.OrderPlacedListener


class PointsEarnedDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: FragmentPointsEarnedDialogBinding

    private var title: String = ""
    private var message: String = ""

    private var callback: OrderPlacedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title", "")
            message = it.getString("message", "")
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReportDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_points_earned_dialog,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.tvTitle.text = title
        if (message.isNotEmpty())
            mBinding.tvSubTitle.text = message

        mBinding.btnGoToMenu.setOnClickListener {
            callback?.orderPlaced()
            dismiss()
        }
    }

    fun setListener(callback: OrderPlacedListener?) {
        this.callback = callback
    }

    companion object {

        @JvmStatic
        fun newInstance(title: String, message: String = "") =
            PointsEarnedDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("message", message)
                }
            }
    }
}