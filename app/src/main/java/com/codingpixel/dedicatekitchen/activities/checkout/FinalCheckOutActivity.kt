package com.codingpixel.dedicatekitchen.activities.checkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity

class FinalCheckOutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_check_out)

        changeStatusBarColor(statusBarColor = getColor(R.color.grayBgColor))

    }
}