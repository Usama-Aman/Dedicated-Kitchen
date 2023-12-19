package com.codingpixel.dedicatekitchen.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.models.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jeevandeshmukh.glidetoastlib.GlideToast
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [MyBottomSheetBaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class MyBottomSheetBaseFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
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

    fun getLoggedInUser(): User? {
        return AppPreferenceManager.getUserFromSharedPreferences()
    }

    fun isUserLoggedIn(): Boolean {
        return !AppPreferenceManager.getUserFromSharedPreferences()?.email.isNullOrEmpty()
    }

    fun showShortToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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

    fun openDatePickerDialog(
        context: Context,
        dateTimePickerListener: DateTimePickerListener?
    ) {
        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val dpd = DatePickerDialog(
            context,
//                R.style.DialogTheme,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                dateTimePickerListener?.pickedDateTime(
                    year = year,
                    month = month + 1,
                    minute = 0,
                    hour = 0,
                    second = 0,
                    date = dayOfMonth
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        //not select previous Data
        dpd.datePicker.minDate = calendar.timeInMillis
        dpd.setOnCancelListener {
            DialogInterface.OnCancelListener {
                dateTimePickerListener?.pickedDateTime(
                    year = 0, month = 0, minute = 0, hour = 0, second = 0, date = 0
                )
            }
        }
        dpd.show()
    }

    fun showWarningToast(
        message: String
    ) {
        GlideToast.makeToast(
            context as Activity?,
            message,
            GlideToast.LENGTHLONG,
            GlideToast.WARNINGTOAST,
            GlideToast.TOP
        ).show()

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

    fun setTextViewTypeface(tv: TextView, typeface: Typeface?) {
        if (typeface != null)
            tv.typeface = typeface
    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_my_bottom_sheet_base, container, false)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MyBottomSheetBaseFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MyBottomSheetBaseFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}