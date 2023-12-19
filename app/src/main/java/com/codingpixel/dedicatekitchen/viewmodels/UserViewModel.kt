package com.codingpixel.dedicatekitchen.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.http.DisposableManager
import com.codingpixel.dedicatekitchen.http.RemoteApiService
import com.codingpixel.dedicatekitchen.http.RemoteRepository
import com.codingpixel.dedicatekitchen.models.*
import com.codingpixel.dedicatekitchen.models.time_zone_models.GetTimeZoneBaseModel
import com.google.gson.Gson
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = UserViewModel::class.java.canonicalName

    private val apiStatus = MutableLiveData<ApiResponse>()

    private val apiDbStatus = MutableLiveData<ApiStatus>()

    private var apiStatusObjectVolante: String? = null

    private var apiStatusVolanteObject: ApiResponse? = null

    private var apiStatusObject: ApiStatus? = null

    //
    private var disposable: Disposable? = null

    private val timeZoneResponse = MutableLiveData<GetTimeZoneBaseModel>()
    val getTimeZoneResponse: MutableLiveData<GetTimeZoneBaseModel>
        get() = timeZoneResponse

    //
    fun getApiStatus(): MutableLiveData<ApiResponse> {
        return apiStatus
    }

    fun getDbApiStatus(): MutableLiveData<ApiStatus> {
        return apiDbStatus
    }

    fun getLoyaltyPoints(type: String = "", url: String) {
        disposable = RemoteRepository.getInstance().getDCLoyaltyRetrofitService()
            .getLoyaltyPoints(url = url)
            .map { jsonObject ->

                val responseString = jsonObject.string() as String?


                val jb = JSONObject()
                jb.apply {
                    put("points", CommonMethods.getTotalPoints(responseString = responseString))
                    put(
                        "totalBalance",
                        CommonMethods.getTotalBalance(responseString = responseString)
                    )
                    put("type", type)
                }
                apiStatus.postValue(
                    ApiResponse().apply {
                        dataObject = jb
                        enum = ApplicationEnum.GET_LOYALTY_POINTS_SUCCESS
                    }
                )


//                if (responseString != null && responseString.contains("PTT=")) {
//                    val ptsArr = responseString.split("PTT=")
//                    if (ptsArr.isNotEmpty()) {
//                        if (ptsArr[1].contains("&")) {
//                            val index = ptsArr[1].indexOfFirst { it == '&' }
//                            if (index != -1) {
//                                val userLoyalty =
//                                    ptsArr[1].substring(startIndex = 0, endIndex = index)
//                                val jb = JSONObject()
//                                jb.apply {
//                                    put("points", userLoyalty)
//                                    put("totalBalance" , "")
//                                    put("type", type)
//                                }
//                                apiStatus.postValue(
//                                    ApiResponse().apply {
//                                        dataObject = jb
//                                        enum = ApplicationEnum.GET_LOYALTY_POINTS_SUCCESS
//                                    }
//                                )
//                            } else {
//                                apiStatus.postValue(
//                                    ApiResponse().apply {
//                                        enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
//                                    }
//                                )
//                            }
//                        } else {
//                            apiStatus.postValue(
//                                ApiResponse().apply {
//                                    enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
//                                }
//                            )
//                        }
//                    } else {
//                        apiStatus.postValue(
//                            ApiResponse().apply {
//                                enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
//                            }
//                        )
//                    }
//                }
//                else {
//                    apiStatus.postValue(
//                        ApiResponse().apply {
//                            enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
//                        }
//                    )
//                }

//                val apiResponse: ApiResponseDb =
//                    JsonManager.getInstance().getApiResponse(jsonObject)
//                val response = if (apiResponse.status == "success") {
//                    ApiStatus(
//                        apiResponse.status,
//                        apiResponse.message,
//                        ApplicationEnum.GET_CATEGORIES_IMAGES_SUCCESS,
//                        jsonObject.getAsJsonObject("result")
//                    )
//
//                } else {
//                    ApiStatus(
//                        apiResponse.status,
//                        apiResponse.message,
//                        ApplicationEnum.GET_CATEGORIES_IMAGES_ERROR
//                    )
//                }

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                        }
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun addLoyaltyPoints(url: String, body: RequestBody) {
        disposable = RemoteRepository.getInstance().getDCLoyaltyRetrofitService()
            .addLoyaltyPoints(url = url, requestbody = body)
            .map { jsonObject ->

                val responseString = jsonObject.string() as String?

                try {
                    val json = JSONObject(responseString ?: "{}")
                    if (json.has("MSG") && json.getString("MSG") == "transaction accepted") {
                        val loyaltyPoints = if (json.has("PTT"))
                            json.getString("PTT")
                        else
                            "0.0"

                        val jb = JSONObject()
                        jb.apply {
                            put("points", loyaltyPoints)
                        }
                        apiStatus.postValue(
                            ApiResponse().apply {
                                dataObject = jb
                                enum = ApplicationEnum.ACCUMULATION_DC_POINTS_SUCCESS
                            }
                        )
                    } else {
                        apiStatus.postValue(
                            ApiResponse().apply {
                                enum = ApplicationEnum.ACCUMULATION_DC_POINTS_ERROR
                            }
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ACCUMULATION_DC_POINTS_ERROR
                        }
                    )
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ACCUMULATION_DC_POINTS_ERROR
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun redeemLoyaltyPoints(url: String) {
        disposable = RemoteRepository.getInstance().getDCLoyaltyRetrofitService()
            .redeemLoyaltyPoints(url = url)
            .map { jsonObject ->

                val responseString = jsonObject.string() as String?

                if (responseString != null && responseString.contains("TCN=")) {
                    val ptsArr = responseString.split("TCN=")
                    if (ptsArr.isNotEmpty()) {
                        if (ptsArr[1].contains("&")) {
                            val index = ptsArr[1].indexOfFirst { it == '&' }
                            if (index != -1) {
                                val userLoyalty =
                                    ptsArr[1].substring(startIndex = 0, endIndex = index)
                                val jb = JSONObject()
                                jb.apply {
                                    put("tcn", userLoyalty)
                                }
                                apiStatus.postValue(
                                    ApiResponse().apply {
                                        dataObject = jb
                                        enum = ApplicationEnum.REDEMPTION_DC_POINTS_SUCCESS
                                    }
                                )
                            } else {
                                apiStatus.postValue(
                                    ApiResponse().apply {
                                        enum = ApplicationEnum.REDEMPTION_DC_POINTS_ERROR
                                    }
                                )
                            }
                        } else {
                            apiStatus.postValue(
                                ApiResponse().apply {
                                    enum = ApplicationEnum.REDEMPTION_DC_POINTS_ERROR
                                }
                            )
                        }
                    } else {
                        apiStatus.postValue(
                            ApiResponse().apply {
                                enum = ApplicationEnum.REDEMPTION_DC_POINTS_ERROR
                            }
                        )
                    }
                } else {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.REDEMPTION_DC_POINTS_ERROR
                        }
                    )
                }

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(
                object : DisposableSingleObserver<Boolean>() {

                    override fun onSuccess(t: Boolean) {
                    }

                    override fun onError(e: Throwable) {
                        apiStatus.postValue(
                            ApiResponse().apply {
                                enum = ApplicationEnum.REDEMPTION_DC_POINTS_ERROR
                            }
                        )
                    }
                })
        DisposableManager.add(disposable!!)
    }

    fun commitRedeemTransaction(url: String) {
        disposable = RemoteRepository.getInstance().getDCLoyaltyRetrofitService()
            .redeemLoyaltyPoints(url = url)
            .map { jsonObject ->

                val responseString = jsonObject.string() as String?

                try {

                    val json = JSONObject(responseString ?: "{}")
                    if (json.has("MSG") && json.getString("MSG") == "transaction accepted") {
                        val tcn = if (json.has("TCN"))
                            json.getString("TCN")
                        else
                            ""

                        val jb = JSONObject()
                        jb.apply {
                            put("tcn", tcn)
                        }
                        apiStatus.postValue(
                            ApiResponse().apply {
                                dataObject = jb
                                enum = ApplicationEnum.COMIT_DC_POINTS_SUCCESS
                            }
                        )
                    } else {
                        apiStatus.postValue(
                            ApiResponse().apply {
                                enum = ApplicationEnum.COMIT_DC_POINTS_ERROR
                            }
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    if ((responseString ?: "").contains("transaction%20accepted")) {
                        apiStatus.postValue(
                            ApiResponse().apply {
                                dataObject = JSONObject()
                                enum = ApplicationEnum.COMIT_DC_POINTS_SUCCESS
                            }
                        )
                    } else {
                        apiStatus.postValue(
                            ApiResponse().apply {
                                enum = ApplicationEnum.COMIT_DC_POINTS_ERROR
                            }
                        )
                    }


                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.COMIT_DC_POINTS_ERROR
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun signUpOnVolante(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .createCustomer(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.VOLANTE_REGISTER_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.VOLANTE_REGISTER_ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.VOLANTE_REGISTER_ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun createLoyaltyCard(url: String) {
        disposable = RemoteRepository.getInstance().getDCAuthRetrofitService()
            .createLoyaltyCard(url = url)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())

                if (response.has("body")) {
                    var bodyJson = response.getAsJsonObject("body")
                    if (bodyJson != null && bodyJson.has("count")) {
                        val count = bodyJson.get("count").asInt
                        if (count > 0 && bodyJson.has("card")) {
                            val cardJson = bodyJson.getAsJsonObject("card")
                            val jb = JSONObject()
                            jb.apply {
                                put("card_id", cardJson.get("card_id").asString)
                                put("active", cardJson.get("active").asString)
                            }
                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.GET_LOYALTY_CARD_ID_SUCCESS
                                dataObject = jb
                            })
                        } else {
                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.GET_LOYALTY_CARD_ID_ERROR
                            })
                        }
                    } else {
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.GET_LOYALTY_CARD_ID_ERROR
                        })
                    }
                } else {
                    apiStatus.postValue(ApiResponse().apply {
                        enum = ApplicationEnum.GET_LOYALTY_CARD_ID_ERROR
                    })
                }

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.GET_LOYALTY_CARD_ID_ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun createDataCandyProfile(url: String, phone: String) {
        disposable = RemoteRepository.getInstance().getDCAuthRetrofitService()
            .createProfileONDataCandy(url = url, phone = phone)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())

                if (response.has("body")) {
                    val bodyJson = response.getAsJsonObject("body")
                    if (bodyJson != null && bodyJson.has("contact_id")) {
                        if (bodyJson.has("profile")) {
                            val profilesJsonArray = bodyJson.getAsJsonArray("profile")
                            if (profilesJsonArray != null && profilesJsonArray.size() > 0) {
                                val profileJson = profilesJsonArray.get(0).asJsonObject
                                if (profileJson != null && profileJson.has("contact_id")) {
                                    val jb = JSONObject()
                                    jb.apply {
                                        put("contact_id", profileJson.get("contact_id").asString)
                                    }
                                    apiStatus.postValue(ApiResponse().apply {
                                        enum = ApplicationEnum.CREATE_DC_PROFILE_SUCCESS
                                        dataObject = jb
                                    })
                                }
                            } else {
                                apiStatus.postValue(ApiResponse().apply {
                                    enum = ApplicationEnum.CREATE_DC_PROFILE_ERROR
                                })
                            }
                        } else {
                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.CREATE_DC_PROFILE_ERROR
                            })
                        }
                    } else {
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.CREATE_DC_PROFILE_ERROR
                        })
                    }
                } else {
                    apiStatus.postValue(ApiResponse().apply {
                        enum = ApplicationEnum.CREATE_DC_PROFILE_ERROR
                    })
                }

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.DATA_CANDY_PROFILE_ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun updateDataCandyProfile(url: String, phone: String) {
        disposable = RemoteRepository.getInstance().getDCAuthRetrofitService()
            .updateDataCandyProfile(url = url, phone = phone)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())

                if (response.has("body")) {
                    val bodyJson = response.getAsJsonObject("body")
                    if (bodyJson != null && bodyJson.has("contact_id")) {
                        if (bodyJson.has("profile")) {
                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.UPDATE_DC_PROFILE_ERROR
                            })
                        } else {
                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.UPDATE_DC_PROFILE_ERROR
                            })
                        }
                    } else {
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.UPDATE_DC_PROFILE_ERROR
                        })
                    }
                } else {
                    apiStatus.postValue(ApiResponse().apply {
                        enum = ApplicationEnum.UPDATE_DC_PROFILE_ERROR
                    })
                }

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.UPDATE_DC_PROFILE_ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun updateDataCandyPassword(url: String) {
        disposable = RemoteRepository.getInstance().getDCAuthRetrofitService()
            .updateDataCandyProfile(url = url)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())

                if (response.has("body")) {
                    val bodyJson = response.getAsJsonObject("body")
                    if (bodyJson != null && bodyJson.has("contact_id")) {
                        if (bodyJson.has("profile")) {

                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.UPDATE_DC_PASSWORD_SUCCESS
                            })

                        } else {
                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR
                            })
                        }
                    } else {
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR
                        })
                    }
                } else {
                    apiStatus.postValue(ApiResponse().apply {
                        enum = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR
                    })
                }

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }


    fun updateDataCandyAddress(url: String) {
        disposable = RemoteRepository.getInstance().getDCAuthRetrofitService()
            .updateDataCandyProfile(url = url)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())

                if (response.has("body")) {
                    val bodyJson = response.getAsJsonObject("body")
                    if (bodyJson != null && bodyJson.has("contact_id")) {
                        if (bodyJson.has("profile")) {

                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.UPDATE_DC_PASSWORD_SUCCESS
                            })

                        } else {
                            apiStatus.postValue(ApiResponse().apply {
                                enum = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR
                            })
                        }
                    } else {
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR
                        })
                    }
                } else {
                    apiStatus.postValue(ApiResponse().apply {
                        enum = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR
                    })
                }

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }


    fun createTable(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .createTable(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.CREATE_TABLE_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun addAccountTransactionToDb(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .addAccountTransaction(
                requestBody = body
            )
            .map { response ->
                //Log.d("XMl Response", response.string())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiJsonObject = JSONObject(jsonString.toString())
                val responseJsonObject = apiJsonObject.getJSONObject("response")

                when (responseJsonObject.get("data")) {

                    is String -> {
                        val apiResponse =
                            Gson().fromJson(jsonString.toString(), VolanteResponse::class.java)
                        Log.d("response code", apiResponse.response.error.code)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                        when (apiResponse.response.error.code) {
                            "0" -> {
                                // Success
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.ADD_ACCOUNT_TRANSACTION_SUCCESS
//                                    dataObject =
//                                        JSONObject(jsonString.toString()).getJSONObject("response")
//                                            .getJSONObject("data")
                                    sessionId = apiResponse.response.sessionId
                                })
                            }
                            "11" -> {
                                // Session Expire
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.SESSION_EXPIRED
                                    dataObject = JSONObject()
                                })
                            }
                            else -> {
                                // Error
                                apiStatus.postValue(ApiResponse().apply {
                                    enum = ApplicationEnum.ADD_ACCOUNT_TRANSACTION_ERROR
                                })
                            }
                        }
                    }

                    is JSONObject -> {
                        val apiResponse =
                            Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                        Log.d("response code", apiResponse.response.error.code)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                        when (apiResponse.response.error.code) {
                            "0" -> {
                                // Success
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.ADD_ACCOUNT_TRANSACTION_SUCCESS
                                    dataObject =
                                        JSONObject(jsonString.toString()).getJSONObject("response")
                                            .getJSONObject("data")
                                })
                            }
                            "11" -> {
                                // Session Expire
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.SESSION_EXPIRED
                                    dataObject = JSONObject()
                                })
                            }
                            else -> {
                                // Error
                                apiStatus.postValue(ApiResponse().apply {
                                    enum = ApplicationEnum.ADD_ACCOUNT_TRANSACTION_ERROR
                                })
                            }
                        }
                    }

                }

//
//                val apiResponse: ApiResponse =
//                    JsonManager.getInstance().getApiResponse(jsonObject)
//                if (apiResponse.status == Constants.API_SUCCESS) {
//                    apiStatusObject = ApiStatus(
//                        apiResponse.status,
//                        apiResponse.message,
//                        ApplicationEnum.SIGN_UP_SUCCESS,
//                        jsonObject.getAsJsonObject("result")
//                    )
//
//                } else {
//                    apiStatusObject = ApiStatus(
//                        apiResponse.status,
//                        apiResponse.message,
//                        ApplicationEnum.SIGN_UP_ERROR
//                    )
//                }
                //apiStatus.postValue(ApiResponse())
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            error = ApiError().apply {
                                code = "-11"
                                localizedDescription = "Error"
                            }
                            enum = ApplicationEnum.ADD_ACCOUNT_TRANSACTION_ERROR
                        }
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun updateUserInfoVolante(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .addAccountTransaction(
                requestBody = body
            )
            .map { response ->
                //Log.d("XMl Response", response.string())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiJsonObject = JSONObject(jsonString.toString())
                val responseJsonObject = apiJsonObject.getJSONObject("response")

                when (responseJsonObject.get("data")) {

                    is String -> {
                        val apiResponse =
                            Gson().fromJson(jsonString.toString(), VolanteResponse::class.java)
                        Log.d("response code", apiResponse.response.error.code)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                        when (apiResponse.response.error.code) {
                            "0" -> {
                                // Success
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.UPDATE_USER_INFO_SUCCESS
//                                    dataObject =
//                                        JSONObject(jsonString.toString()).getJSONObject("response")
//                                            .getJSONObject("data")
                                    sessionId = apiResponse.response.sessionId
                                })
                            }
                            "11" -> {
                                // Session Expire
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.SESSION_EXPIRED
                                    dataObject = JSONObject()
                                })
                            }
                            else -> {
                                // Error
                                apiStatus.postValue(ApiResponse().apply {
                                    enum = ApplicationEnum.ERROR
                                })
                            }
                        }
                    }

                    is JSONObject -> {
                        val apiResponse =
                            Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                        Log.d("response code", apiResponse.response.error.code)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                        when (apiResponse.response.error.code) {
                            "0" -> {
                                // Success
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.UPDATE_USER_INFO_SUCCESS
                                    dataObject =
                                        JSONObject(jsonString.toString()).getJSONObject("response")
                                            .getJSONObject("data")
                                })
                            }
                            "11" -> {
                                // Session Expire
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.SESSION_EXPIRED
                                    dataObject = JSONObject()
                                })
                            }
                            else -> {
                                // Error
                                apiStatus.postValue(ApiResponse().apply {
                                    enum = ApplicationEnum.ERROR
                                })
                            }
                        }
                    }

                }

//
//                val apiResponse: ApiResponse =
//                    JsonManager.getInstance().getApiResponse(jsonObject)
//                if (apiResponse.status == Constants.API_SUCCESS) {
//                    apiStatusObject = ApiStatus(
//                        apiResponse.status,
//                        apiResponse.message,
//                        ApplicationEnum.SIGN_UP_SUCCESS,
//                        jsonObject.getAsJsonObject("result")
//                    )
//
//                } else {
//                    apiStatusObject = ApiStatus(
//                        apiResponse.status,
//                        apiResponse.message,
//                        ApplicationEnum.SIGN_UP_ERROR
//                    )
//                }
                //apiStatus.postValue(ApiResponse())
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            error = ApiError().apply {
                                code = "-11"
                                localizedDescription = "Error"
                            }
                            enum = ApplicationEnum.ERROR
                        }
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun addOfflineGiftCard(
        body: RequestBody, isTipFlow: Boolean = false,
        walletPayment: Boolean = true
    ) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .addOfflineGiftCard(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
//                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
//                        if (isTipFlow){
//                            apiStatus.postValue(ApiResponse().apply {
//                                error = apiResponse.response.error
//                                enum = ApplicationEnum.ADD_OFFLINE_GIFTCARD_SUCCESS_TIP
//                                dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
//                                    .getJSONObject("data")
//                                sessionId = apiResponse.response.sessionId
//                            })
//                        }else{
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.ADD_OFFLINE_GIFTCARD_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
//                        }

                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        val dummyJsonObj = JSONObject()
                        dummyJsonObj.put("isTipFlow", isTipFlow)
                        apiStatus.postValue(ApiResponse().apply {
                            dataObject = dummyJsonObj
                            enum = ApplicationEnum.ADD_OFFLINE_GIFTCARD_ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    val dummyJsonObj = JSONObject()
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ADD_OFFLINE_GIFTCARD_ERROR
                            dataObject = dummyJsonObj
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }


    fun discountOnVolane(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .discountOnVolante(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.VOLANTE_DISCOUNT_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.VOLANTE_DISCOUNT_ERROR
                            error = apiResponse.response.error
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.VOLANTE_DISCOUNT_ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun processPayment(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .processPayment(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
//                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.WALLET_PAYMENT_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    "81001" -> {
                        // Success
                        // case of -> Account partially charged (from Volante)
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.WALLET_PAYMENT_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }


    fun getTable(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getTable(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.GET_TABLE_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }


    fun addItemsToTrans(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .addItemsToTrans(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.ADD_ITEMS_TO_TRANS_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun getCustomerSummary(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getCustomerSummary(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.GET_CUSTOMER_SUMMARY_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }


    fun setCustomerId(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .setCustomerId(
                requestBody = body
            )
            .map { response ->
                //Log.d("XMl Response", response.string())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiJsonObject = JSONObject(jsonString.toString())
                val responseJsonObject = apiJsonObject.getJSONObject("response")

                when (responseJsonObject.get("data")) {

                    is String -> {
                        val apiResponse =
                            Gson().fromJson(jsonString.toString(), VolanteResponse::class.java)
                        Log.d("response code", apiResponse.response.error.code)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                        when (apiResponse.response.error.code) {
                            "0" -> {
                                // Success
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.SET_CUSTOMER_ID_SUCCESS
                                    sessionId = apiResponse.response.sessionId
                                })
                            }
                            "11" -> {
                                // Session Expire
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.SESSION_EXPIRED
                                    dataObject = JSONObject()
                                })
                            }
                            else -> {
                                // Error
                                apiStatus.postValue(ApiResponse().apply {
                                    enum = ApplicationEnum.ERROR
                                })
                            }
                        }
                    }

//                    is JSONObject -> {
//                        val apiResponse =
//                            Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
//                        Log.d("response code", apiResponse.response.error.code)
//
////                response.enum = ApplicationEnum.GET_MENU_SUCCESS
////                Log.d("Response", response.apiError.code.toString())
//
//                        when (apiResponse.response.error.code) {
//                            "0" -> {
//                                // Success
//                                apiStatus.postValue(ApiResponse().apply {
//                                    error = apiResponse.response.error
//                                    enum = ApplicationEnum.GET_ACCOUNT_BALANCE_SUCCESS
//                                    dataObject =
//                                        JSONObject(jsonString.toString()).getJSONObject("response")
//                                            .getJSONObject("data")
//                                })
//                            }
//                            "11" -> {
//                                // Session Expire
//                                apiStatus.postValue(ApiResponse().apply {
//                                    error = apiResponse.response.error
//                                    enum = ApplicationEnum.SESSION_EXPIRED
//                                    dataObject = JSONObject()
//                                })
//                            }
//                            else -> {
//                                // Error
//                                apiStatus.postValue(ApiResponse().apply {
//                                    enum = ApplicationEnum.ERROR
//                                })
//                            }
//                        }
//                    }

                }

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            error = ApiError().apply {
                                code = "-11"
                                localizedDescription = "Error"
                            }
                            enum = ApplicationEnum.ERROR
                        }
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun getUserDataFromVolante(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getUserData(url = Constants.DEFAULT_API_URL, requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.GET_USER_DATA_FROM_VOLANTE_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.GET_USER_DATA_FROM_VOLANTE_ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.GET_USER_DATA_FROM_VOLANTE_ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }


    fun authenticateCustomer(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getUserData(requestBody = body)
            .map { response ->
                //  Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                apiStatus.postValue(ApiResponse())
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.AUTHENTICATE_CUSTOMER_SUCCESS
                            dataObject = JSONObject(jsonString.toString()).getJSONObject("response")
                                .getJSONObject("data")
                            sessionId = apiResponse.response.sessionId
                        })
                    }
                    "11" -> {
                        // Session Expire
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_EXPIRED
                            dataObject = apiResponse.response.dataObject
                        })
                    }
                    else -> {
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                        })
                    }
                }
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.ERROR
                            error = ApiError().apply {
                                code = "0"
                                localizedDescription = "Error"
                                defaultDescription = "Error"
                            }
                        }
                    )
                }
            })
        DisposableManager.add(disposable!!)
    }

    fun getAccountBalance(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getAccountBalance(
                requestBody = body
            )
            .map { response ->
                //Log.d("XMl Response", response.string())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiJsonObject = JSONObject(jsonString.toString())
                val responseJsonObject = apiJsonObject.getJSONObject("response")

                when (responseJsonObject.get("data")) {

                    is String -> {
                        val apiResponse =
                            Gson().fromJson(jsonString.toString(), VolanteResponse::class.java)
                        Log.d("response code", apiResponse.response.error.code)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                        when (apiResponse.response.error.code) {
                            "0" -> {
                                // Success
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.GET_ACCOUNT_BALANCE_SUCCESS
                                    dataObject =
                                        JSONObject(jsonString.toString()).getJSONObject("response")
                                            .getJSONObject("data")
                                    sessionId = apiResponse.response.sessionId
                                })
                            }
                            "11" -> {
                                // Session Expire
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.SESSION_EXPIRED
                                    dataObject = JSONObject()
                                })
                            }
                            else -> {
                                // Error
                                apiStatus.postValue(ApiResponse().apply {
                                    enum = ApplicationEnum.ERROR
                                })
                            }
                        }
                    }

                    is JSONObject -> {
                        val apiResponse =
                            Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                        Log.d("response code", apiResponse.response.error.code)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                        when (apiResponse.response.error.code) {
                            "0" -> {
                                // Success
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.GET_ACCOUNT_BALANCE_SUCCESS
                                    dataObject =
                                        JSONObject(jsonString.toString()).getJSONObject("response")
                                            .getJSONObject("data")
                                })
                            }
                            "11" -> {
                                // Session Expire
                                apiStatus.postValue(ApiResponse().apply {
                                    error = apiResponse.response.error
                                    enum = ApplicationEnum.SESSION_EXPIRED
                                    dataObject = JSONObject()
                                })
                            }
                            else -> {
                                // Error
                                apiStatus.postValue(ApiResponse().apply {
                                    enum = ApplicationEnum.ERROR
                                })
                            }
                        }
                    }

                }

//
//                val apiResponse: ApiResponse =
//                    JsonManager.getInstance().getApiResponse(jsonObject)
//                if (apiResponse.status == Constants.API_SUCCESS) {
//                    apiStatusObject = ApiStatus(
//                        apiResponse.status,
//                        apiResponse.message,
//                        ApplicationEnum.SIGN_UP_SUCCESS,
//                        jsonObject.getAsJsonObject("result")
//                    )
//
//                } else {
//                    apiStatusObject = ApiStatus(
//                        apiResponse.status,
//                        apiResponse.message,
//                        ApplicationEnum.SIGN_UP_ERROR
//                    )
//                }
                //apiStatus.postValue(ApiResponse())
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            error = ApiError().apply {
                                code = "-11"
                                localizedDescription = "Error"
                            }
                            enum = ApplicationEnum.ERROR
                        }
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun checkEmail(requestBody: RequestBody? = null, email: String) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .checkEmail(
                email = email
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CHECK_EMAIL_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CHECK_EMAIL_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun getPurchasHistory() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .getPurchaseHistory(
                url = ApiUrls.GET_PURCHASE_HISTORY + "${AppPreferenceManager.getUserFromSharedPreferences()?.id ?: 0}"
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_PURCHASE_HISTORY_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_PURCHASE_HISTORY_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.GET_PURCHASE_HISTORY_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun getOrderNumberApi() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .orderNumber()
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_ORDER_NUMBER_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_ORDER_NUMBER_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun getGuestOrderNumberApi() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .guestOrderNumber()
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_GUEST_ORDER_NUMBER_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_GUEST_ORDER_NUMBER_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun getPackages() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .getPackages(
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_PACKAGE_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_PACKAGE_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun getUserPackages(id: Int) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .getUserPackages(id = id)
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_USER_PACKAGE_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_USER_PACKAGE_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun deleteCardApi(url_: String) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .delete(
                url = url_
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CARD_DELETED_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CARD_DELETED_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun addUserPackage(
        packageId: Int,
        cardId: Int,
        totalAmount: String,
        stripeCardId: String
    ) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .addUserPackage(
                package_id = packageId,
                card_id = cardId,
                stripe_id = stripeCardId,
                amount = totalAmount
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.ADD_USER_PACKAGE_SUCCESS,
                        jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.ADD_USER_PACKAGE_SUCCESS
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }


                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun deleteLocation(url_: String) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .delete(
                url = url_
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.LOCATION_DELETED_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.LOCATION_DELETED_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.LOCATION_DELETED_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun getAddress() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .getLocations()
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_LOCATIONS_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_LOCATIONS_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun makeDefaultLocation(address_id: Int) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .setAddressDefault(
                address_id = address_id
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.DEFAULT_ADDRESS_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.DEFAULT_ADDRESS_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun addLocation(
        address: String, lat: String, lng: String, city: String, country: String,
        zipCode: String = "", state: String = ""
    ) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .addAddress(
                address = address,
                latitude = lat,
                longitude = lng,
                city = city,
                zipCode = zipCode,
                state = state,
                country = country
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.ADD_LOCATION_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.ADD_LOCATION_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ADD_LOCATION_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun resetPassword(email: String, password: String, confirmPassword: String, code: Int) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .resetPassword(
                email = email,
                password = password,
                confirm_password = confirmPassword,
                code = code
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.RESET_PASSWORD_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.RESET_PASSWORD_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun updateInfo(email: String, fname: String, lname: String, phone: String) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .updateInfo(
                email = email,
                fname = fname,
                lname = lname,
                phone = phone
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.UPDATE_PROFILE_SUCCESSS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.UPDATE_PROFILE_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun updateDCInfo(
        dcContactID: String, dcCardId: String,
        email: String, fname: String, lname: String, phone: String
    ) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .updateDcInfo(
                dcCardId = dcCardId,
                dcContactId = dcContactID,
                email = email,
                fname = fname,
                lname = lname,
                phone = phone
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.UPDATE_PROFILE_SUCCESSS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.UPDATE_PROFILE_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun addCard(
        stripeId: String = "",
        cardNumber: String,
        expMonth: String,
        expYear: String,
        cvc: String,
        isDefault: Int,
        brandName: String
    ) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .addCard(
                stripeId = stripeId,
                card_number = cardNumber,
                month = expMonth,
                year = expYear,
                cvc = cvc,
                is_default = isDefault,
                card_brand = brandName
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CARD_ADD_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CARD_ADD_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun getCards() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .getCard()
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_CARD_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_CARD_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun forgetPassword(email: String) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .forgetPassword(
                email = email
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.FORGET_PASSWORD_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.FORGET_PASSWORD_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .changePassword(
                old_password = oldPassword,
                new_password = newPassword,
                confirm_password = confirmPassword
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CHANGE_PASSWORD_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CHANGE_PASSWORD_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun verifyUser(email: String, code: Int) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .verifyEmail(
                email = email,
                code = code
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.USER_VERIFY_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.USER_VERIFY_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.USER_VERIFY_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun resendCode(email: String) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .resendCode(
                email = email
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.RESEND_CODE_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.RESEND_CODE_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun saveUserOnOurServer(
        email: String,
        fname: String,
        lname: String,
        customer_id: String,
        customer_code: String,
        customer_account_id: String,
        password: String,
        confirm_password: String,
        phone: String,
        address: String
    ) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .saveUser(
                email = email,
                fname = fname,
                lname = lname,
                customer_id = customer_id,
                customer_code = customer_code,
                customer_account_id = customer_account_id,
                password = password,
                confirm_password = confirm_password,
                phone = phone,
                address = address
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.USER_SAVED_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.USER_SAVED_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun login(email: String, password: String) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .login(
                email = email,
                password = password
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.LOG_IN_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )
                } else {
                    /*  To find Case if Account NOT Verified Response  */
                    val resultJson = if (jsonObject.has("result"))
                        jsonObject.getAsJsonObject("result")
                    else
                        null
                    if (resultJson != null) {
                        if (resultJson.has("email") && resultJson.has("code")) {
                            ApiStatus(
                                apiResponse.status,
                                apiResponse.message,
                                ApplicationEnum.LOG_IN_ACCOUNT_NOT_VERIFIED,
                                resultJson.getAsJsonObject("result")
                            )
                        } else {
                            ApiStatus(
                                apiResponse.status,
                                apiResponse.message,
                                ApplicationEnum.LOG_IN_ERROR
                            )
                        }
                    } else {
                        ApiStatus(
                            apiResponse.status,
                            apiResponse.message,
                            ApplicationEnum.LOG_IN_ERROR
                        )
                    }


                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun saveOrderToServer(model: SubmitOrderRequestModel) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .saveOrderToServer(
                model = model
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.SAVE_ORDER_IN_DB_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.SAVE_ORDER_IN_DB_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.SAVE_ORDER_IN_DB_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    /*fun callToGetTimeZone(latLng: String, timZone: Long) {
        disposable = RemoteRepository.getInstance().getGoogleApiService()
            .getTimeZone(latLng, timZone, Constants.GOOGLE_PLACES_API_KEY).map {
                timeZoneResponse.value = it
                true
            }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {


                }

                override fun onError(e: Throwable) {
                    val timeZoneModel = GetTimeZoneBaseModel().apply {
                        this.status = "NO"
                        this.message = e.message.toString()

                    }
                }
            })

        DisposableManager.add(disposable!!)
    }*/


    fun saveOrderToServerForGuestUser(model: SubmitOrderRequestModel) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .saveGuestOrder(
                model = model
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.SAVE_ORDER_IN_DB_GUEST_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.SAVE_ORDER_IN_DB_GUEST_ERROR
                    )
                }
                apiDbStatus.postValue(apiStatusObject)
                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                }

                override fun onError(e: Throwable) {
                    apiDbStatus.postValue(
                        ApiStatus(
                            "error",
                            e.message!!,
                            ApplicationEnum.SAVE_ORDER_IN_DB_GUEST_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


//    fun login(body: RequestBody) {
//        disposable = RemoteRepository.getInstance().getRetrofitService()
//            .login(
//                //   url = "PspecOhioHealth-132000011",
//                requestBody = body
//            )
//            .map { response ->
//                // Log.d("Response", response.error.code.toString())
//                apiStatus.postValue(
//                    if (response.error.code == "11") {
//                        ApiResponse().apply {
//                            enum = ApplicationEnum.SESSION_EXPIRED
//                        }
//                    } else response.apply {
//                        enum = ApplicationEnum.CHECK_EMAIL_SUCCESS
//                    }
//                )
//                true
//            }
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : DisposableSingleObserver<Boolean>() {
//
//                override fun onSuccess(t: Boolean) {
//                }
//
//                override fun onError(e: Throwable) {
//                    apiStatus.postValue(
//                        ApiResponse().apply {
//                            enum = ApplicationEnum.ERROR
//                            error = ApiError().apply {
//                                code = "0"
//                                localizedDescription = "Error"
//                                defaultDescription = "Error"
//                            }
//                        }
//                    )
//                }
//            })
//        DisposableManager.add(disposable!!)
//    }

//    fun signUp(body: RequestBody) {
//        disposable = RemoteRepository.getInstance().getRetrofitService()
//            .getData(   //   url = "PspecOhioHealth-132000011",
//                requesyBody = body)
//            .map { response ->
//               // Log.d("Response", response.error.code.toString())
//                apiStatus.postValue(
//                    if (response.error.code == "11") {
//                        ApiResponse().apply {
//                            enum = ApplicationEnum.SESSION_EXPIRED
//                        }
//                    } else response.apply {
//                        enum = ApplicationEnum.REGISTER
//                    }
//                )
//                true
//            }
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : DisposableSingleObserver<Boolean>() {
//
//                override fun onSuccess(t: Boolean) {
//                }
//
//                override fun onError(e: Throwable) {
//                    apiStatus.postValue(
//                        ApiResponse().apply {
//                            enum = ApplicationEnum.ERROR
//                            error = ApiError().apply {
//                                code = "0"
//                                localizedDescription = "Error"
//                                defaultDescription = "Error"
//                            }
//                        }
//                    )
//                }
//            })
//        DisposableManager.add(disposable!!)
//    }


    override fun onCleared() {
        if (disposable != null)
            DisposableManager.remove(disposable!!)
        super.onCleared()
    }
}