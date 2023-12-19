package com.codingpixel.dedicatekitchen.activities.onboarding

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.CommonMethods
import com.codingpixel.dedicatekitchen.helpers.IntentParams
import com.codingpixel.dedicatekitchen.helpers.PrefFlags
import com.codingpixel.dedicatekitchen.interfaces.SessionListener
import com.codingpixel.dedicatekitchen.models.DedicateKitchen
import com.google.android.gms.location.*
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException
import java.util.*


class SplashActivity : BaseActivity() {

    private var sessionCall: Int = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var locationBasedSelectedKitchen: DedicateKitchen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setFullScreen()
//        initGetLocation()


        /*Edited by Usama*/
//        fetchSession()
        Handler(Looper.getMainLooper()).postDelayed({
            goOnHome()
        }, 1500)

    }

    private fun initGetLocation() {
//        try {
//            fusedLocationClient =
//                LocationServices.getFusedLocationProviderClient(this@SplashActivity)
//            requestLocationAccessPermission(callback = object : MultiplePermissionsListener {
//                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
//                    if (p0!!.areAllPermissionsGranted()) {
//                        if (isLocationEnabled(this@SplashActivity))
//                            getLocation(callIndex = 0)
//                        else
//                            fetchSession()
//                    } else {
//                        fetchSession()
//                    }
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    p0: MutableList<PermissionRequest>?,
//                    p1: PermissionToken?
//                ) {
//                    fetchSession()
//                }
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("LocationServices", e.message ?: "Error")
//            fetchSession()
//        }
    }

    private fun fetchSession() {
        initSessionViewModel(sessionListener = object : SessionListener {
            override fun sessionRestored() {
                goOnHome()
            }

            override fun sessionRestorationFailed() {
                // Try to Fetch Session only 3 Times, If First Install then Fetch Session is Must
                if (!AppPreferenceManager.getBooleanDefaultTrue(PrefFlags.FIRST_INSTALL) && sessionCall > 3) {
                    runOnUiThread {
                        showShortToast(message = "Please check your internet connection!")
                    }
                    if (sessionCall > 5)
                        goOnHome()
                    else {
                        ++sessionCall
                        fetchSession()
                    }
                } else {
                    AppPreferenceManager.setBoolean(PrefFlags.FIRST_INSTALL, false)
                    ++sessionCall
                    fetchSession()
                }

            }
        })
    }

    private fun goOnHome() {
        if (AppPreferenceManager.getBooleanDefaultTrue(PrefFlags.FIRST_INSTALL)) {
            startActivity(Intent(this@SplashActivity, IntroSlidesActivity::class.java).apply {
                putExtra(IntentParams.KITCHEN, locationBasedSelectedKitchen)
            })
        } else {
            if (isUserLoggedIn())
                startActivity(Intent(this@SplashActivity, MainBottomBarActivity::class.java))
            else
                startActivity(Intent(this@SplashActivity, IntermediateActivity::class.java))
        }
        finishAffinity()
    }

//    private fun getLocation(callIndex: Int) {
//
//        if (callIndex > 3)
//            fetchSession()
//        else {
//            val locationRequest =
//                LocationRequest.create().apply {
//                    interval = 0
//                    fastestInterval = 0
//                    numUpdates = 1
//                    expirationTime = 10000
//                }
////            locationRequest.setExpirationDuration(10000)
//            val locationCallback = object : LocationCallback() {
//                @SuppressLint("MissingPermission", "ResourceType")
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    if (locationResult.locations.isNotEmpty()) {
//                        val geoCoder = Geocoder(this@SplashActivity, Locale.getDefault())
//                        try {
//                            val addresses: MutableList<Address>? =
//                                geoCoder.getFromLocation(
//                                    locationResult.locations.first().latitude,
//                                    locationResult.locations.first().longitude,
//                                    1
//                                )
//                            if (addresses != null && addresses.size > 0) {
//                                val cityName = addresses[0].locality ?: ""
//                                if (cityName.isNotEmpty()) {
//                                    when {
//
//                                        cityName.toLowerCase().contains("mcmurray") -> {
//                                            locationBasedSelectedKitchen =
//                                                CommonMethods.getKitchensList()[0]
//                                            AppPreferenceManager.addSelectedKitchenToSharedPreferences(
//                                                locationBasedSelectedKitchen!!
//                                            )
//                                        }
//
//                                        cityName.toLowerCase().contains("calgary") -> {
//                                            locationBasedSelectedKitchen =
//                                                CommonMethods.getKitchensList()[1]
//                                            AppPreferenceManager.addSelectedKitchenToSharedPreferences(
//                                                locationBasedSelectedKitchen!!
//                                            )
//                                        }
//
//                                        else -> {
//                                            locationBasedSelectedKitchen =
//                                                CommonMethods.getKitchensList()[0]
//                                            AppPreferenceManager.addSelectedKitchenToSharedPreferences(
//                                                locationBasedSelectedKitchen!!
//                                            )
//                                        }
//                                    }
//                                    fetchSession()
//                                } else
//                                    fetchSession()
//                            } else
//                                fetchSession()
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                            fetchSession()
//                        }
//                    } else
//                        getLocation(callIndex = callIndex + 1)
//                }
//            }
//
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                try {
//                    fusedLocationClient.requestLocationUpdates(
//                        locationRequest,
//                        locationCallback,
//                        Looper.myLooper()!!
//                    )
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    fetchSession()
//                }
//            } else {
//                fetchSession()
//            }
//
//        }
//    }

}