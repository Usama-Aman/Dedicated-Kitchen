package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.common.WebViewActivity
import com.codingpixel.dedicatekitchen.databinding.InfoLayoutBinding
import com.codingpixel.dedicatekitchen.helpers.ApiUrls
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.helpers.IntentParams
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoDialog : BottomSheetDialogFragment() {
    private lateinit var mBinding: InfoLayoutBinding

    private var infoString = "        In case you want to cancel order, please call on this number 780-790-0065 or go to the store for refund. We can only give refunds in credit since we do not accept cash on the app.\n"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ReportDialogTheme)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.info_layout,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInfoText()

//        mBinding.tvVideoFromGallery.setOnClickListener {
//            dismiss()
//            fourButtonsDialogListener?.videosFromGalleryTapped()
//        }

//        mBinding.mainLayout.setOnClickListener {
////            dismiss()
////        }
////        mBinding.buttonCancel.setOnClickListener{
////            dismiss()
////        }
    }


    private fun setInfoText() {
        val regularTypeface: Typeface? = ResourcesCompat.getFont(context!!, R.font.sf_pro_display_medium)
        ResourcesCompat.getFont(context!!, R.font.sf_pro_display_bold)

        val bySigningIn = Constants.ORDER_CANCEL_INFO_1ST_HALF
        val termsCondition = Constants.ORDER_CANCEL_INFO_CELL
        val and = Constants.ORDER_CANCEL_INFO_2ND_HALF
        val message = "${bySigningIn}${termsCondition}${and}"

        val spannableString = SpannableString(message)

        // By Signing In
        spannableString.setSpan(
            regularTypeface,
            0,
            message.substring(0, bySigningIn.length).length,
            0
        )
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.black)),
            0,
            message.substring(0, bySigningIn.length).length,
            0
        )

        // Terms Conditions
        val termsConditionsClickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {

                try {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://" +Constants.ORDER_CANCEL_INFO_CELL))
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

//                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", Constants.ORDER_CANCEL_INFO_CELL, null))
//                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(
            termsConditionsClickableSpan, message.substring(0, bySigningIn.length).length,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            RelativeSizeSpan(1.0f),
            message.substring(0, bySigningIn.length).length,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.StyleSpan(Typeface.BOLD),
            message.substring(0, bySigningIn.length).length,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!,R.color.darkGrayTitleColor)),
            message.substring(0, bySigningIn.length).length,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )


        // And
        spannableString.setSpan(
            regularTypeface,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            message.length,
            0
        )
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.black)),
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            message.length,
            0
        )

        mBinding.tvDetail.setText(
            SpannableStringBuilder().append(spannableString),
            TextView.BufferType.SPANNABLE
        )
        mBinding.tvDetail.movementMethod = LinkMovementMethod.getInstance()
    }


    companion object {
        @JvmStatic
        fun newInstance(
        ) =
            InfoDialog().apply {
                arguments = Bundle().apply {
                }
            }
    }
}