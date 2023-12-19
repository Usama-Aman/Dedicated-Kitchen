package com.codingpixel.dedicatekitchen.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.fragments.bottomtabs.FavouritesFragment
import com.codingpixel.dedicatekitchen.fragments.bottomtabs.MenuFragment
import com.codingpixel.dedicatekitchen.fragments.bottomtabs.MyAccountFragment
import com.codingpixel.dedicatekitchen.fragments.bottomtabs.PackagesFragment
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.KitchenTimeListener
import com.codingpixel.dedicatekitchen.interfaces.SessionListener
import com.codingpixel.dedicatekitchen.models.User
import com.codingpixel.dedicatekitchen.viewmodels.KitchenViewModel
import com.codingpixel.dedicatekitchen.viewmodels.SessionViewModel
import com.jeevandeshmukh.glidetoastlib.GlideToast
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.*


open class BaseActivity : AppCompatActivity() {


    var emptyDataContainer: ConstraintLayout? = null
    var emptyDataImage: ImageView? = null
    var emptyDataTitle: TextView? = null
    var emptyDataAddLabel: TextView? = null

    lateinit var sessionViewModel: SessionViewModel
    lateinit var kitchenViewModel: KitchenViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable GPS")
            .setMessage("Please enable the GPS of your device to access features of Routing Buddy.")
            .setCancelable(false)
            .setPositiveButton("Enable Now!") { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }


    fun requestLocationAccessPermission(callback: MultiplePermissionsListener) {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(callback).check()
    }

    fun setFullScreen() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = Color.TRANSPARENT
    }

    fun showLoading() {
        AppProgressDialog.showProgressDialog(this)
    }

    fun hideLoading() {
        AppProgressDialog.dismissProgressDialog()
    }

    fun changeStatusBarColor(statusBarColor: Int) {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor
    }

    fun makeVibrate() {
        try {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
// Vibrate for 500 milliseconds
// Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                v.vibrate(300)
            }
        } catch (e: Exception) {
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getSavedHomeFragment(): MenuFragment? {
        val menuFragment =
            supportFragmentManager.findFragmentByTag(MenuFragment::class.java.canonicalName)
        return if (menuFragment !is MenuFragment?) {
            null
        } else {
            menuFragment
        }
    }

    fun getSavedAccountFragment(): MyAccountFragment? {
        val accountFragment =
            supportFragmentManager.findFragmentByTag(MyAccountFragment::class.java.canonicalName)
        return if (accountFragment !is MyAccountFragment?) {
            null
        } else {
            accountFragment
        }
    }

    fun getSavedFavoritesFragment(): FavouritesFragment? {
        val favsFragment =
            supportFragmentManager.findFragmentByTag(FavouritesFragment::class.java.canonicalName)
        return if (favsFragment !is FavouritesFragment?) {
            null
        } else {
            favsFragment
        }
    }

    fun getSavedPackagesFragment(): PackagesFragment? {
        val packagesFragment =
            supportFragmentManager.findFragmentByTag(PackagesFragment::class.java.canonicalName)
        return if (packagesFragment !is PackagesFragment?) {
            null
        } else {
            packagesFragment
        }
    }

    @SuppressLint("InflateParams")
    fun errorDialogue(message: String?, context: Context, title: String = "Alert") {
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
        val errorTitle = inflatedView.findViewById<TextView>(R.id.tv_title)
        errorTitle.text = title
        val errorMsg = inflatedView.findViewById<TextView>(R.id.tv_message)
        errorMsg.text = message
        val close =
            inflatedView.findViewById<Button>(R.id.btn_right)
        close.setOnClickListener { removePopup.dismiss() }
        removePopup.contentView.isFocusableInTouchMode = true
        removePopup.setOnDismissListener { removePopup.dismiss() }
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
        dpd.datePicker.maxDate = calendar.timeInMillis
        dpd.setOnCancelListener {
            DialogInterface.OnCancelListener {
                dateTimePickerListener?.pickedDateTime(
                    year = 0, month = 0, minute = 0, hour = 0, second = 0, date = 0
                )
            }
        }
        dpd.show()
    }

    fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("InflateParams")
    open fun successPopup(
        message: String?,
        context: Context? = null,
        flag: Boolean
    ) {
        val inflatedView: View =
            LayoutInflater.from(this).inflate(R.layout.time_set_successfully_popup, null, false)
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val successPopup: PopupWindow
        successPopup = PopupWindow(inflatedView, width, height, false)
        successPopup.isOutsideTouchable = false
        runOnUiThread {
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
                    this@BaseActivity.runOnUiThread {
                        successPopup.dismiss()
                        if (flag) finish()
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


    fun isEmail(text: EditText): Boolean {
        val email: CharSequence = text.text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    fun showWarningToast(
        message: String
    ) {
        GlideToast.makeToast(
            this,
            message,
            GlideToast.LENGTHTOOLONG,
            GlideToast.WARNINGTOAST,
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

    fun isEmpty(text: EditText): Boolean {
        val str: CharSequence = text.text.toString().trim()
        return TextUtils.isEmpty(str)
    }

    fun showShortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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

    // : Fragment?
    fun changeFragmentWithoutReCreation(fragment: Fragment, tagFragmentName: String) {

        val mFragmentManager = supportFragmentManager
        val fragmentTransaction = mFragmentManager.beginTransaction()
        //fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        val currentFragment = mFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        var wasSaved = false
        var fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName)
        if (fragmentTemp == null) {
            fragmentTemp = fragment
            fragmentTransaction.add(R.id.fragment_container, fragmentTemp, tagFragmentName)
        } else {
            wasSaved = true
            fragmentTransaction.show(fragmentTemp)
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
        //return if (wasSaved) fragmentTemp else null
    }


    fun showEmptyContainer() {
        emptyDataContainer?.visibility = View.VISIBLE
    }

    fun hideEmptyContainer() {
        emptyDataContainer?.visibility = View.GONE
    }

    fun setEmptyDataContainer(rootView: View, icon: Int, title: String, addTitle: String): View? {
        try {
            emptyDataContainer = rootView.findViewById(R.id.empty_data_parent)
            emptyDataImage = emptyDataContainer?.findViewById(R.id.iv_empty_data_icon)
            emptyDataTitle = emptyDataContainer?.findViewById(R.id.tv_empty_data_title)
            emptyDataAddLabel = emptyDataContainer?.findViewById(R.id.tv_empty_data_add_item_label)

            emptyDataImage?.setImageResource(icon)
            emptyDataTitle?.text = title
            emptyDataAddLabel?.text = addTitle

            return emptyDataAddLabel
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun initSessionViewModel(sessionListener: SessionListener? = null) {
        sessionViewModel = ViewModelProvider(this).get(SessionViewModel::class.java)
        sessionViewModel.getApiStatus().observe(this, Observer {
            if (it.enum == ApplicationEnum.SESSION_RESTORED)
                sessionListener?.sessionRestored()
            else
                sessionListener?.sessionRestorationFailed()
        })

        Log.e("SessionRequestBody", RequestBodyUtil.getSessionIdRequestBody().toString())
        sessionViewModel.getSessionId(body = RequestBodyUtil.getSessionIdRequestBody())
    }

    fun checkIfKitchenOpen(kitchenTimeListener: KitchenTimeListener?) {
        kitchenViewModel = ViewModelProvider(this).get(KitchenViewModel::class.java)
        kitchenViewModel.getDbApiStatus().observe(this, Observer {

            when (it.applicationEnum) {

                ApplicationEnum.IS_KITCHEN_OPEN_SUCCESS -> {
                    kitchenTimeListener?.isKitchenOpen(
                        isKitchenOpen = true,
                        canadianTime = if (it.resultObject?.has(
                                ApiParams.CANADIAN_TIME
                            ) == true
                        ) it.resultObject?.get(ApiParams.CANADIAN_TIME)?.asString ?: ""
                        else
                            ""
                    )
                }

                ApplicationEnum.IS_KITCHEN_OPEN_ERROR -> {
                    kitchenTimeListener?.isKitchenOpen(
                        isKitchenOpen = false,
                        canadianTime = if (it.resultObject?.has(
                                ApiParams.CANADIAN_TIME
                            ) == true
                        ) it.resultObject?.get(ApiParams.CANADIAN_TIME)?.asString ?: ""
                        else
                            ""
                    )
                }
                ApplicationEnum.IS_PAST_NINE_ERROR -> {
                    kitchenTimeListener?.isPastNine(
                        isPastNine = true,
                        canadianTime = if (it.resultObject?.has(
                                ApiParams.CANADIAN_TIME
                            ) == true
                        ) it.resultObject?.get(ApiParams.CANADIAN_TIME)?.asString ?: ""
                        else
                            ""
                    )
                }
                else -> {
                    kitchenTimeListener?.isError()
                }


            }
        })
        kitchenViewModel.isKitchenOpen()
    }

    fun isPastNineOrNot(kitchenTimeListener: KitchenTimeListener?) {
        kitchenViewModel = ViewModelProvider(this).get(KitchenViewModel::class.java)
        kitchenViewModel.getDbApiStatus().observe(this, Observer {

            when (it.applicationEnum) {

                ApplicationEnum.IS_PAST_NINE_SUCCESS -> {
                    kitchenTimeListener?.isPastNine(
                        isPastNine = false,
                        canadianTime = if (it.resultObject?.has(
                                ApiParams.CANADIAN_TIME
                            ) == true
                        ) it.resultObject?.get(ApiParams.CANADIAN_TIME)?.asString ?: ""
                        else
                            ""
                    )
                }

                ApplicationEnum.IS_KITCHEN_OPEN_ERROR -> {
                    kitchenTimeListener?.isPastNine(
                        isPastNine = true,
                        canadianTime = if (it.resultObject?.has(
                                ApiParams.CANADIAN_TIME
                            ) == true
                        ) it.resultObject?.get(ApiParams.CANADIAN_TIME)?.asString ?: ""
                        else
                            ""
                    )
                }

                ApplicationEnum.IS_PAST_NINE_ERROR -> {
                    kitchenTimeListener?.isPastNine(
                        isPastNine = true,
                        canadianTime = if (it.resultObject?.has(
                                ApiParams.CANADIAN_TIME
                            ) == true
                        ) it.resultObject?.get(ApiParams.CANADIAN_TIME)?.asString ?: ""
                        else
                            ""
                    )
                }

                else -> {
                    kitchenTimeListener?.isError()
                }


            }
        })
        kitchenViewModel.isPastNine()
    }

}