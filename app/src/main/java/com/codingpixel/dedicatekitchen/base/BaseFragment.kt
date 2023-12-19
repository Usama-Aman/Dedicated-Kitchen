package com.codingpixel.dedicatekitchen.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.models.User
import com.jeevandeshmukh.glidetoastlib.GlideToast


/**
 * A simple [Fragment] subclass.
 * Use the [BaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class BaseFragment : Fragment() {

    var isLoading : Boolean = true
    var page: Int = 1
    var isLastPage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    fun getLoggedInUser(): User? {
        return AppPreferenceManager.getUserFromSharedPreferences()
    }

    fun isUserLoggedIn(): Boolean {
        return !AppPreferenceManager.getUserFromSharedPreferences()?.email.isNullOrEmpty()
    }

    fun showShortToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("InflateParams")
    open fun successPopup(
        message: String?,
        context: Context? = null,
        flag: Boolean
    ) {
        val inflatedView: View =
            LayoutInflater.from(context).inflate(R.layout.time_set_successfully_popup, null, false)
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val successPopup: PopupWindow
        successPopup = PopupWindow(inflatedView, width, height, false)
        successPopup.isOutsideTouchable = false
        activity?.runOnUiThread {
            successPopup.showAtLocation(
                inflatedView,
                Gravity.CENTER,
                0,
                0
            )
        }
        val text = inflatedView.findViewById<TextView>(R.id.tv_message)
        text.text = message
        object : Thread() {
            override fun run() {
                super.run()
                try {
                    sleep(2000)
                } catch (e: InterruptedException) {
                    successPopup.dismiss()
                    e.printStackTrace()
                } finally {
                    activity?.runOnUiThread {
                        successPopup.dismiss()
                    }
                }
            }
        }.start()
    }

    fun showView(view: View) {
        view.visibility = View.VISIBLE
    }

    fun hideView(view: View) {
        view.visibility = View.GONE
    }
    @SuppressLint("InflateParams")
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

    fun showWarningToast(
        message: String
    ) {
        GlideToast.makeToast(
            activity!!,
            message,
            GlideToast.LENGTHLONG,
            GlideToast.WARNINGTOAST,
            GlideToast.TOP
        ).show()
    }

    fun showInfoToast(
        message: String
    ) {
        GlideToast.makeToast(
            activity!!,
            message,
            GlideToast.LENGTHLONG,
            GlideToast.INFOTOAST,
            GlideToast.TOP
        ).show()
    }

    fun isPasswordValid(password: EditText): Boolean {
        val pwd = password.text.toString()
        return pwd.length > 5
    }

    fun isPasswordMatch(confirmPassword: EditText, password: EditText): Boolean {
        return password.text.toString() == confirmPassword.text.toString()
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

}