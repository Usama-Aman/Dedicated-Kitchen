package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseDialogFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentTwoButtonsAlertDialogBinding
import com.codingpixel.dedicatekitchen.helpers.IntentParams
import com.codingpixel.dedicatekitchen.interfaces.TwoButtonsDialogListener


/**
 * A simple [Fragment] subclass.
 * Use the [TwoButtonsAlertDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TwoButtonsAlertDialogFragment : BaseDialogFragment() {


    private lateinit var mBinding: FragmentTwoButtonsAlertDialogBinding

    private var title: String = ""
    private var message: String = ""
    private var leftButtonText: String = ""
    private var rightButtontext: String = ""
    private var showBackGroundImage: Boolean = false

    private var twoButtonsDialogListener: TwoButtonsDialogListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(IntentParams.TITLE)!!
            message = it.getString(IntentParams.MESSAGE)!!
            leftButtonText = it.getString(IntentParams.LEFT_BUTTON_TEXT)!!
            rightButtontext = it.getString(IntentParams.RIGHT_BUTTON_TEXT)!!
            showBackGroundImage = it.getBoolean("showBackGroundImage", false)
        }
        setStyle(STYLE_NO_FRAME, R.style.ReportDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_two_buttons_alert_dialog,
            container,
            false
        )
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.tvTitle.text = title
        mBinding.tvMessage.text = message
        mBinding.btnLeft.text = leftButtonText
        mBinding.btnRight.text = rightButtontext


        if (showBackGroundImage)
            mBinding.ivBackGround.visibility = View.VISIBLE
        else
            mBinding.ivBackGround.visibility = View.GONE

        if (leftButtonText.isEmpty())
            hideView(view = mBinding.btnLeft)
        else
            showView(view = mBinding.btnLeft)

        if (rightButtontext.isEmpty())
            hideView(view = mBinding.btnRight)
        else
            showView(view = mBinding.btnRight)

        mBinding.btnLeft.setOnClickListener {
            dismiss()
            twoButtonsDialogListener?.leftButtonTapped()
        }

        mBinding.btnRight.setOnClickListener {
            dismiss()
            twoButtonsDialogListener?.rightButtonTapped()
        }

        mBinding.dialogParent.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(twoButtonsDialogListener: TwoButtonsDialogListener?) {
        this.twoButtonsDialogListener = twoButtonsDialogListener
    }

    companion object {

        @JvmStatic
        fun newInstance(
            title: String,
            message: String,
            leftButtonTitle: String,
            rightButtonTitle: String,
            showBackGroundImage: Boolean = false
        ) =
            TwoButtonsAlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(IntentParams.TITLE, title)
                    putString(IntentParams.MESSAGE, message)
                    putString(IntentParams.LEFT_BUTTON_TEXT, leftButtonTitle)
                    putString(IntentParams.RIGHT_BUTTON_TEXT, rightButtonTitle)
                    putBoolean("showBackGroundImage", showBackGroundImage)
                }
            }
    }
}