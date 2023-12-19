package com.codingpixel.dedicatekitchen.base

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.models.User
import com.jeevandeshmukh.glidetoastlib.GlideToast
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [BaseDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class BaseDialogFragment : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }


    fun getLoggedInUser(): User? {
        return AppPreferenceManager.getUserFromSharedPreferences()
    }

    fun isUserLoggedIn(): Boolean {
        return !AppPreferenceManager.getUserFromSharedPreferences()?.email.isNullOrEmpty()
    }

    fun showSuccessToast(
        message: String
    ) {
        GlideToast.makeToast(
            context as Activity?,
            message,
            GlideToast.LENGTHLONG,
            GlideToast.SUCCESSTOAST,
            GlideToast.TOP
        ).show()

    }

    fun isEmail(text: EditText): Boolean {
        val email: CharSequence = text.text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    fun isEmpty(text: EditText): Boolean {
        val str: CharSequence = text.text.toString().trim()
        return TextUtils.isEmpty(str)
    }


    fun errorDialogue(message: String?, context: Context) {
        val layoutInflater =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val inflatedView: View =
            layoutInflater.inflate(R.layout.error_dialogue, null, false)
        val displayMetrics = context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        // set height depends on the device size
        val removePopup = PopupWindow(inflatedView, width, height, false)
        // set a background drawable with rounders corners
        removePopup.height = WindowManager.LayoutParams.MATCH_PARENT

        // removePopup.setAnimationStyle(R.style.PopupAnimation);
        // make it focusable to show the keyboard to enter in `EditText`
        removePopup.isFocusable = true
        // make it outside touchable to dismiss the popup window
        removePopup.isOutsideTouchable = true

        // show the popup at bottom of the screen and set some margin at bottom ie,
        removePopup.showAtLocation(inflatedView, Gravity.CENTER, 0, 0)
        val errorMsg = inflatedView.findViewById<TextView>(R.id.tv_message)
        errorMsg.text = message
        val close =
            inflatedView.findViewById<Button>(R.id.btn_right)
        close.setOnClickListener { removePopup.dismiss() }
        removePopup.contentView.isFocusableInTouchMode = true
        removePopup.setOnDismissListener { removePopup.dismiss() }
    }

    fun showWarningToast(
        message: String
    ) {
        try {
            GlideToast.makeToast(
                context as Activity?,
                message,
                GlideToast.LENGTHLONG,
                GlideToast.WARNINGTOAST,
                GlideToast.TOP
            ).show()
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    fun showShortToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    fun showView(view: View) {
        view.visibility = View.VISIBLE
    }

    fun hideView(view: View) {
        view.visibility = View.GONE
    }

    fun invisibleView(view: View) {
        view.visibility = View.INVISIBLE
    }

    fun isViewVisiblle(view: View): Boolean {
        return view.isShown
    }

    fun setTextViewColor(tv: TextView, color: Int) {
        tv.setTextColor(color)
    }

    fun setTextViewTypeface(tv: TextView, typeface: Typeface?) {
        if (typeface != null)
            tv.typeface = typeface
    }
}