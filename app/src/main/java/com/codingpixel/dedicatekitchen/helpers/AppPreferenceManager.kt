package com.codingpixel.dedicatekitchen.helpers

import android.content.SharedPreferences
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.application.MyApplication
import com.codingpixel.dedicatekitchen.models.DedicateKitchen
import com.codingpixel.dedicatekitchen.models.User
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class AppPreferenceManager {

    companion object {

        private lateinit var prefs: SharedPreferences

        fun getValue(key: String): String {

            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            return prefs.getString(key, "")!!

        }

        fun getIntValue(key: String): Int {
            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            return prefs.getInt(key, 0)

        }


        fun setValue(key: String, value: String) {
            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            prefs.edit().putString(key, value).apply()

        }

        fun setIntValue(key: String, value: Int) {
            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            prefs.edit().putInt(key, value).apply()

        }

        fun setBoolean(key: String, value: Boolean) {

            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            prefs.edit().putBoolean(key, value).apply()

        }

        fun getBoolean(key: String): Boolean {

            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            return prefs.getBoolean(key, false)

        }

        fun getBooleanDefaultTrue(key: String): Boolean {

            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            return prefs.getBoolean(key, true)

        }

        fun addSelectedKitchenToSharedPreferences(selectedKitchen: DedicateKitchen) {
            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            val json = Gson().toJson(selectedKitchen) // selectedKitchen - instance of D-Kitchen
            prefs.edit().putString(PrefFlags.SELECTED_KITCHEN, json).apply()
        }


        fun addUserToSharedPreferences(user: User) {
            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            val json = Gson().toJson(user) // userObject - instance of User
            prefs.edit().putString(PrefFlags.USER_OBJECT, json).apply()
        }


        fun getUserFromSharedPreferences(): User? {
            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            val json = prefs.getString(PrefFlags.USER_OBJECT, "")
            var user: User? = null
            try {
                user = Gson().fromJson(json, User::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
            return user
        }

        fun getSelectedKitchenFromSharedPreferences(): DedicateKitchen? {
            prefs =
                MyApplication.applicationContext()
                    .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            val json = prefs.getString(PrefFlags.SELECTED_KITCHEN, "")
            return if ((json ?: "").isNotEmpty()) {
                var kitchen: DedicateKitchen? = null
                try {
                    kitchen = Gson().fromJson(json, DedicateKitchen::class.java)
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    kitchen = CommonMethods.getKitchensList()[0]
                    addSelectedKitchenToSharedPreferences(selectedKitchen = kitchen)
                }
                kitchen ?: CommonMethods.getKitchensList()[0]
            } else {
                try {

                    /*Edited by Usama*/
//                    addSelectedKitchenToSharedPreferences(selectedKitchen = CommonMethods.getKitchensList()[0])
//                    CommonMethods.getKitchensList()[0]

                    addSelectedKitchenToSharedPreferences(selectedKitchen = MainBottomBarActivity.allKitchenList[0])
                    MainBottomBarActivity.allKitchenList[0]
                } catch (e: Exception) {
                    e.printStackTrace()
                    DedicateKitchen()
                }
            }
        }

        //
//        fun removeUserFromSharedPreferences() {
//            prefs = MyApplication.applicationContext()
//                .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
//            prefs.edit().remove(PrefFlags.USER_OBJECT).apply()
//            prefs.edit().remove(PrefFlags.ACCESS_TOKEN).apply()
//        }
//
        fun clearPreferences() {
            removeUserFromSharedPreferences()
            prefs = MyApplication.applicationContext()
                .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            prefs.edit().clear().apply()
            setBoolean(PrefFlags.FIRST_INSTALL, false)
        }

        private fun removeUserFromSharedPreferences() {
            prefs = MyApplication.applicationContext()
                .getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
            prefs.edit().remove(PrefFlags.USER_OBJECT).apply()
            prefs.edit().remove(PrefFlags.ACCESS_TOKEN).apply()
        }

    }

}