package com.codingpixel.dedicatekitchen.activities.auth

import android.content.Intent
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.common.WebViewActivity
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityRegisterBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.SessionListener
import com.codingpixel.dedicatekitchen.maskeditor.MaskTextWatcher
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : BaseActivity(), SessionListener {


    private lateinit var mBinding: ActivityRegisterBinding
    private var code: String = ""
    private var placesClient: PlacesClient? = null
    private lateinit var viewModel: UserViewModel
    private var customerId: String = ""
    private var geoCoder: Geocoder? = null
    private var address: String = ""
    private var contactID = ""
    private var placesField: List<Place.Field> =
        listOf(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS
        )

    var pickedCountry: String = ""
    var pickedCity: String = ""
    var pickedState: String = ""
    var pickedZip: String = ""
    var getLoyaltyCardId: String = ""
    var otpCode: Int = 0
    var email: String = ""
    var authToken: String = ""

    private var currentLength = 0
    private var lengthBefore = 0

    private var userPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        initViewModel()
        initClickListener()
        initPlaces()
        setBottomLabelTermsAndPrivacyText()
        initSessionViewModel()
        geoCoder = Geocoder(this@RegisterActivity)

//        AppProgressDialog.showProgressDialog(context = this@RegisterActivity)
//        viewModel.createLoyaltyCard(url = RequestBodyUtil.getLoyaltyCardIdUrl())
    }

    private fun initPlaces() {
        Places.initialize(this, Constants.GOOGLE_PLACES_API_KEY)
        placesClient = Places.createClient(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel.getDbApiStatus().observe(this, Observer {
            run {

                when (it.applicationEnum) {

                    ApplicationEnum.SESSION_EXPIRED -> {
                        initSessionViewModel(sessionListener = this)
                    }

                    ApplicationEnum.CHECK_EMAIL_SUCCESS -> {
                        //showShortToast(it.message)
                        //registerOnVolante()
                        viewModel.createLoyaltyCard(url = RequestBodyUtil.getLoyaltyCardIdUrl())
                        // showShortToast(message = it.error.code)
                    }

                    ApplicationEnum.CHECK_EMAIL_ERROR -> {
                        hideLoading()
                        showWarningToast(message = it.message)
                    }

                    ApplicationEnum.UPDATE_PROFILE_SUCCESSS -> {


                        hideLoading()

                        // TODO: FOR TEST PURPOSE , MOVE TO LOGIN
//                        startActivity(Intent(this@RegisterActivity, SignInActivity::class.java))
//                        finish()

                        // TODO : FOLLOWING CODE WILL BE USED AFTER TESTING CODE IS REMOVED
                        startActivity(
                            Intent(
                                this@RegisterActivity,
                                VerificationCodeActivity::class.java
                            ).apply {
                                putExtra(IntentParams.CODE, otpCode)
                                putExtra(IntentParams.EMAIL, email)
                                putExtra(IntentParams.TYPE, "register")
                            }
                        )
                    }

                    ApplicationEnum.USER_SAVED_SUCCESS -> {
                        otpCode = JsonManager.getInstance().getIntValue(it.resultObject, "code")
                        email = JsonManager.getInstance().getString(it.resultObject, "email")
                        authToken = JsonManager.getInstance().getString(it.resultObject, "token")
                        AppPreferenceManager.setValue(PrefFlags.ACCESS_TOKEN, authToken)
                        userPhone = mBinding.etPhone.text.toString().trim()
                        userPhone = userPhone.replace("(", "")
                        userPhone = userPhone.replace(")", "")
                        userPhone = userPhone.replace("-", "")
                        userPhone = userPhone.replace(" ", "")


                        viewModel.updateDCInfo(
                            dcContactID = contactID,
                            dcCardId = getLoyaltyCardId,
                            fname = mBinding.etFirstName.text.toString().trim(),
                            lname = mBinding.etLastName.text.toString().trim(),
                            phone = mBinding.etPhone.text.toString().trim(),
                            email = mBinding.etEmail.text.toString().trim()
                        )

                        // viewModel.createLoyaltyCard(url = RequestBodyUtil.getLoyaltyCardIdUrl())
                    }

                    ApplicationEnum.USER_SAVED_ERROR -> {
                        hideLoading()
                        showWarningToast(message = it.message)
                    }

                    ApplicationEnum.REGISTER -> {
                        //  showShortToast("registered")
                    }
                    else -> {

                    }
                }
            }
        })

        viewModel.getApiStatus().observe(this) {
            when (it.enum) {


                ApplicationEnum.GET_LOYALTY_CARD_ID_SUCCESS -> {
                    userPhone = mBinding.etPhone.text.toString().trim()
                    userPhone = userPhone.replace("(", "")
                    userPhone = userPhone.replace(")", "")
                    userPhone = userPhone.replace("-", "")
                    userPhone = userPhone.replace(" ", "")


                    getLoyaltyCardId = it.dataObject.getString("card_id")
                    viewModel.createDataCandyProfile(
                        url = RequestBodyUtil.getDataCandyCreateProfileUrl(
                            cardId = getLoyaltyCardId,
                            firstName = mBinding.etFirstName.text.toString().trim(),
                            lastName = mBinding.etLastName.text.toString().trim(),
                            phone = userPhone.trim(),
                            email = mBinding.etEmail.text.toString().trim(),
                            password = mBinding.etPassword.text.toString().trim(),
                            address = address,
                            city = pickedCity.replace(" ", ""),
                            zip = pickedZip.replace(" ", ""),
                            state = pickedState
                        ), phone = userPhone.trim()
                    )
                }


                ApplicationEnum.CREATE_DC_PROFILE_SUCCESS -> {

                    contactID = it.dataObject.getString("contact_id")

                    registerOnVolante()

                    /*   viewModel.updateDCInfo(
                           dcContactID = it.dataObject.getString("contact_id"),
                           dcCardId = getLoyaltyCardId,
                           fname = mBinding.etFirstName.text.toString().trim(),
                           lname = mBinding.etLastName.text.toString().trim(),
                           phone = mBinding.etPhone.text.toString().trim(),
                           email = mBinding.etEmail.text.toString().trim()
                       )*/
                }

                ApplicationEnum.CREATE_DC_PROFILE_ERROR -> {
                    hideLoading()
                    showWarningToast(message = it.error.localizedDescription)
                }

                ApplicationEnum.GET_LOYALTY_CARD_ID_ERROR -> {
                    hideLoading()
                    showWarningToast(message = "Card Id Error")
                }

                ApplicationEnum.ERROR -> {
                    hideLoading()
                    showWarningToast(message = "Some thing went wrong")
                }


                ApplicationEnum.VOLANTE_REGISTER_ERROR -> {
                    hideLoading()
                    showWarningToast(message = it.error.defaultDescription)
                }

                ApplicationEnum.VOLANTE_REGISTER_SUCCESS -> {
                    customerId = it.dataObject.getJSONObject("customer").getString("id")
                    getUserInfoFromVolante(customerId)
                }


                ApplicationEnum.DATA_CANDY_PROFILE_ERROR -> {
                    showWarningToast(message = "Phone Number is invalid")
                    hideLoading()
                }


                ApplicationEnum.GET_USER_DATA_FROM_VOLANTE_SUCCESS -> {
                    val data = it.dataObject.getJSONObject("customer")
                    val accountId = data.getString("accountId1")
                    registerUserOnServer(customerId = customerId, customerAccountId = accountId)
                }

                ApplicationEnum.GET_USER_DATA_FROM_VOLANTE_ERROR -> {
                    hideLoading()
                    showWarningToast(message = it.error.localizedDescription)
                }


                else -> {

                }
            }
        }
    }

    private fun getUserInfoFromVolante(customerId: String) {
        viewModel.getUserDataFromVolante(
            body = RequestBodyUtil.getUserInfoRequestBody(
                sessionId = AppPreferenceManager.getValue("sessionId"),
                customerId = customerId,
                customerCode = code
            )
        )
    }

    private fun registerUserOnServer(customerId: String, customerAccountId: String) {
        viewModel.saveUserOnOurServer(
            mBinding.etEmail.text.toString().trim(),
            mBinding.etFirstName.text.toString().trim(),
            mBinding.etLastName.text.toString().trim(),
            customerId,
            code,
            customerAccountId,
            mBinding.etPassword.text.toString().trim(),
            mBinding.etPassword.text.toString().trim(),
            mBinding.etPhone.text.toString().trim(),
            address
        )
    }

    private fun initClickListener() {

        val textWatcherNumber = MaskTextWatcher(mBinding.etPhone, "+# (###) ###-####")
        mBinding.etPhone.addTextChangedListener(textWatcherNumber)

//        mBinding.etPhone.addTextChangedListener(object : TextWatcher {
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                Log.i("asdasd", "$start $before $count")
//                val text = mBinding.etPhone.text.toString()
//                currentLength = text.length
//                if (currentLength > lengthBefore) {
//                    when (currentLength) {
//                        1 -> {
//                            if (!mBinding.etPhone.text.toString().trim().contains("(")) {
//                                mBinding.etPhone.setText("($s")
//                            }
//                            mBinding.etPhone.setSelection(mBinding.etPhone.text.toString().length)
//                        }
//                        4 -> {
//                            if (!mBinding.etPhone.text.toString().trim().contains(")")) {
//                                mBinding.etPhone.append(") ")
//                            }
//                        }
//                        9 -> {
//                            if (!mBinding.etPhone.text.toString().trim().contains("-")) {
//                                mBinding.etPhone.append("-")
//                            }
//
//                        }
//                    }
//
//
//                }
//
//                lengthBefore = currentLength
//
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//
//        })

        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }
        mBinding.tvLocation.setOnClickListener {
            if (Places.isInitialized()) {
                startActivityForResult(
                    Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, placesField
                    ).setCountry("CA")
                        .build(this), 12
                )
            }
        }


        mBinding.tvAlreadHaveAccount.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, SignInActivity::class.java))
            finish()
        }

        mBinding.cvRegister.setOnClickListener {
            if (validateForm())
                checkEmailApi()
        }
    }

    private fun checkEmailApi() {
        val user = JSONObject()
        try {
            user.put("email", mBinding.etEmail.text.toString().trim())
            user.put("phone", mBinding.etPhone.text.toString().trim())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requestBody: RequestBody = user.toString()
            .toRequestBody(Constants.MEDIA_TYPE_PARSE.toMediaTypeOrNull())
        showLoading()
        viewModel.checkEmail(
            requestBody = requestBody,
            email = mBinding.etEmail.text.toString().trim()
        )
    }

    private fun registerOnVolante() {
        code = System.currentTimeMillis().toString()
        // Log.d("Code", code)
        viewModel.signUpOnVolante(
            body = RequestBodyUtil.getSignUpRequestBody(
                sessionId = AppPreferenceManager.getValue("sessionId"),
                firstName = mBinding.etFirstName.text.toString().trim(),
                lastName = mBinding.etLastName.text.toString().trim(),
                customerCode = code,
                customerCardCode = code,
                customerTypeId = Constants.CUSTOMER_TYPE_ID,
                email = mBinding.etEmail.text.toString().trim(),
                password = mBinding.etPassword.text.toString().trim(),
                phone = mBinding.etPhone.text.toString().trim()
            )
        )
    }

    private fun validateForm(): Boolean {
        return if (mBinding.etFirstName.text.toString().trim().isEmpty()) {
            showWarningToast(message = "Enter First Name")
            false
        } else if (mBinding.etLastName.text.toString().trim().isEmpty()) {
            showWarningToast(message = "Enter Last Name")
            false
        } else if (mBinding.etEmail.text.toString().trim().isEmpty()) {
            showWarningToast(message = "Enter Email Address")
            false
        } else if (!CommonMethods.isValidEmail(target = mBinding.etEmail.text.toString().trim())) {
            showWarningToast(message = "Enter Valid Email Address")
            false
        } else if (mBinding.etPassword.text.toString().trim().isEmpty()) {
            showWarningToast(message = "Enter Password")
            false
        } else if (!CommonMethods.validatePassword(
                text = mBinding.etPassword.text.toString().trim()
            )
        ) {
            showWarningToast(message = "Password must be like Qwerty@123 etc")
            false
        } else if (mBinding.etConfirmPassword.text.toString().trim().isEmpty()) {
            showWarningToast(message = "Enter Confirm Password")
            false
        } else if (mBinding.etPassword.text.toString()
                .trim() != mBinding.etConfirmPassword.text.toString().trim()
        ) {
            showWarningToast(message = "Password & Confirm Password mismatch")
            false
        } else if (mBinding.etPhone.text.toString().trim().isEmpty()) {
            showWarningToast(message = "Enter Phone Number")
            false
        } else if (address == "") {
            showWarningToast(message = "Address is Required")
            false
        } else {
            true
        }
    }

    private fun setBottomLabelTermsAndPrivacyText() {
        val regularTypeface: Typeface? = ResourcesCompat.getFont(this, R.font.sf_pro_display_medium)
        ResourcesCompat.getFont(this, R.font.sf_pro_display_bold)

        val bySigningIn = "By signing up you agree to our "
        val termsCondition = "Terms & Conditions"
        val and = " and "
        val privacyPolicy = "Privacy Policy"
        val message = "$bySigningIn$termsCondition$and$privacyPolicy"

        val spannableString = SpannableString(message)

        // By Signing In
        spannableString.setSpan(
            regularTypeface,
            0,
            message.substring(0, bySigningIn.length).length,
            0
        )
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.dontHaveAccountLabelColor)),
            0,
            message.substring(0, bySigningIn.length).length,
            0
        )

        // Terms Conditions
        val termsConditionsClickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                //showShortToast(message = "Terms Condititions")
                startActivity(Intent(this@RegisterActivity, WebViewActivity::class.java).apply {
                    putExtra(IntentParams.TITLE, "Terms & Conditions")
                    putExtra(IntentParams.WEB_URL, Constants.TERM_CONDITIONS_URL)
                })
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
            ForegroundColorSpan(getColor(R.color.white)),
            message.substring(0, bySigningIn.length).length,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )


        // And
        spannableString.setSpan(
            regularTypeface,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            message.substring(
                0,
                bySigningIn.length
            ).length + 1 + termsCondition.length + and.length,
            0
        )
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.dontHaveAccountLabelColor)),
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            message.substring(
                0,
                bySigningIn.length
            ).length + 1 + termsCondition.length + and.length,
            0
        )


        // Privacy Policy
        val privacyPolicyClickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                startActivity(Intent(this@RegisterActivity, WebViewActivity::class.java).apply {
                    putExtra(IntentParams.TITLE, "Privacy Policy")
                    putExtra(IntentParams.WEB_URL, Constants.PRIVACY_POLICY_URL)
                })
            }


            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(
            privacyPolicyClickableSpan,
            message.indexOf(string = privacyPolicy, startIndex = 0, ignoreCase = false),
            message.indexOf(
                string = privacyPolicy,
                startIndex = 0,
                ignoreCase = false
            ) + privacyPolicy.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            RelativeSizeSpan(1.0f),
            message.indexOf(string = privacyPolicy, startIndex = 0, ignoreCase = false),
            message.indexOf(
                string = privacyPolicy,
                startIndex = 0,
                ignoreCase = false
            ) + privacyPolicy.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.StyleSpan(Typeface.BOLD),
            message.indexOf(string = privacyPolicy, startIndex = 0, ignoreCase = false),
            message.indexOf(
                string = privacyPolicy,
                startIndex = 0,
                ignoreCase = false
            ) + privacyPolicy.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.white)),
            message.indexOf(string = privacyPolicy, startIndex = 0, ignoreCase = false),
            message.indexOf(
                string = privacyPolicy,
                startIndex = 0,
                ignoreCase = false
            ) + privacyPolicy.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )



        mBinding.tvPrivacyTerms.setText(
            SpannableStringBuilder().append(spannableString),
            TextView.BufferType.SPANNABLE
        )

        mBinding.tvPrivacyTerms.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun sessionRestored() {
        showShortToast(message = "Connection Restored, Try now")
    }

    override fun sessionRestorationFailed() {
        showShortToast(message = "Connection Error, Try again")
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    mBinding.tvLocation.text = place.address ?: ""
                    address = place.address ?: ""
                    pickedCountry = "CA"
                    if ((place.addressComponents?.asList() ?: listOf()).isNotEmpty()) {
                        for (component in place.addressComponents?.asList() ?: listOf()) {
                            if (component != null) {

                                if (component.types.contains("locality") && component.types.contains(
                                        "political"
                                    )
                                ) {
                                    pickedCity = component.name
                                }

                                if (component.types.contains("administrative_area_level_1") && component.types.contains(
                                        "political"
                                    )
                                ) {
                                    pickedState = component.shortName ?: "CA"
                                }


                                if (component.types.contains("postal_code") || component.types.contains(
                                        "postal_code"
                                    )
                                ) {
                                    if (pickedZip.isEmpty())
                                        pickedZip = component.shortName ?: "CA"
                                    else
                                        pickedZip += " " + (component.shortName ?: "CA")
                                }


                            }
                        }
                    } else {
                        pickedCity = ""
                        pickedCountry = ""
                        pickedState = ""
                        pickedZip = ""
                    }

//                    val lat = place.latLng?.latitude.toString()
//                    val lng = place.latLng?.longitude.toString()
//                    if (geoCoder != null) {
//                        val addresses: List<Address> = geoCoder!!.getFromLocation(
//                            place.latLng!!.latitude,
//                            place.latLng!!.longitude,
//                            1
//                        )
//                        if (addresses.isNotEmpty()) {
//                            pickedCity = addresses[0].locality ?: ""
//                            pickedCountry = addresses[0].countryName ?: ""
//                            pickedZip = addresses[0].postalCode ?: ""
//                            pickedState = addresses[0].adminArea ?: ""
//                        } else {
//                            pickedCity = ""
//                            pickedCountry = ""
//                            pickedState = ""
//                            pickedZip = ""
//                        }
//                    } else {
//                        showWarningToast(message = "Geocoding was failed!!")
//                    }


                    //AppProgressDialog.showProgressDialog(this)
                }
                AutocompleteActivity.RESULT_ERROR -> {

                }
                RESULT_CANCELED -> {
                }
            }
        }
    }
}