package com.codingpixel.dedicatekitchen.helpers
import android.content.Context
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.codingpixel.dedicatekitchen.R

class AppProgressDialog {

    companion object {

        private var progressDialog: android.app.Dialog? = null

        fun showProgressDialog(context: Context) {
            try {
                if (progressDialog != null && progressDialog?.isShowing!!) {
                    return
                }
                progressDialog = android.app.Dialog(
                        context,
                        R.style.TransparentBackgroundDialog
                )

                progressDialog?.setCancelable(false)
                progressDialog?.show()
                progressDialog?.setContentView(R.layout.progress_dialog)
//                val imageView = progressDialog?.findViewById<ImageView>(R.id.iv_central_logo)
//                val animation = AnimationUtils.loadAnimation(context , R.anim.rotation)
                //animation.repeatMode = Animation.INFINITE
                //imageView?.startAnimation(animation)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        fun dismissProgressDialog() {
            try {
                if (progressDialog != null && progressDialog?.isShowing!!) {
                    progressDialog!!.dismiss()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}