package com.codingpixel.dedicatekitchen.application

import android.app.Application
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.helpers.StripeParams
import com.stripe.android.PaymentConfiguration

class MyApplication : Application() {


    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null
        var selectedOrderType = ApplicationEnum.TAKE_OUT
        var groupName: String = ""


        fun applicationContext(): MyApplication {
            return instance as MyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
//        Stetho.initializeWithDefaults(this)

//        OneSignal.startInit(this)
//            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
//            .unsubscribeWhenNotificationsAreDisabled(true)
//            .setNotificationReceivedHandler(NotificationReceivedHandler())
//            .setNotificationOpenedHandler(NotificationOpenedHandler())
//            .init();

        PaymentConfiguration.init(
            applicationContext,
            StripeParams.STRIPE_PK
        )
    }
}