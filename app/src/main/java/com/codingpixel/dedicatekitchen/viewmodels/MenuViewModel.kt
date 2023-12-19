package com.codingpixel.dedicatekitchen.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.codingpixel.dedicatekitchen.helpers.ApiUrls
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.JsonManager
import com.codingpixel.dedicatekitchen.http.DisposableManager
import com.codingpixel.dedicatekitchen.http.RemoteRepository
import com.codingpixel.dedicatekitchen.models.*
import com.codingpixel.dedicatekitchen.updates.ApiResponse.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = MenuViewModel::class.java.canonicalName
    private val apiStatus = MutableLiveData<ApiResponse>()
    private var apiStatusObject: String? = null
    private val apiDbStatus = MutableLiveData<ApiStatus>()

    //
    private var disposable: Disposable? = null

    //
    fun getApiStatus(): MutableLiveData<ApiResponse> {
        return apiStatus
    }

    fun getApiDBStatus(): MutableLiveData<ApiStatus> {
        return apiDbStatus
    }

    /*Edited By Usama*/
    private val cpApiStatus = MutableLiveData<Resource<JSONObject>>()
    fun getCPApiStatus(): MutableLiveData<Resource<JSONObject>> {
        return cpApiStatus
    }


    fun getLoyaltyPoints(url: String) {
        disposable = RemoteRepository.getInstance().getDCLoyaltyRetrofitService()
            .getLoyaltyPoints(url = url)
            .map { jsonObject ->

                val responseString = jsonObject.string() as String?

                if (responseString != null && responseString.contains("PTT=")) {
                    val ptsArr = responseString.split("PTT=")
                    if (ptsArr.isNotEmpty()) {
                        if (ptsArr[1].contains("&")) {
                            val index = ptsArr[1].indexOfFirst { it == '&' }
                            if (index != -1) {
                                val userLoyalty =
                                    ptsArr[1].substring(startIndex = 0, endIndex = index)
                                val jb = JSONObject()
                                jb.apply {
                                    put("points", userLoyalty)
                                }
                                apiStatus.postValue(
                                    ApiResponse().apply {
                                        dataObject = jb
                                        enum = ApplicationEnum.GET_LOYALTY_POINTS_SUCCESS
                                    }
                                )
                            } else {
                                apiStatus.postValue(
                                    ApiResponse().apply {
                                        enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                                    }
                                )
                            }
                        } else {
                            apiStatus.postValue(
                                ApiResponse().apply {
                                    enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                                }
                            )
                        }
                    } else {
                        apiStatus.postValue(
                            ApiResponse().apply {
                                enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                            }
                        )
                    }
                } else {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                        }
                    )
                }

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


    fun clearAllTables(url: String) {
        disposable = RemoteRepository.getInstance().getDCLoyaltyRetrofitService()
            .getLoyaltyPoints(url = url)
            .map { jsonObject ->

                val responseString = jsonObject.string() as String?

                if (responseString != null && responseString.contains("PTT=")) {
                    val ptsArr = responseString.split("PTT=")
                    if (ptsArr.isNotEmpty()) {
                        if (ptsArr[1].contains("&")) {
                            val index = ptsArr[1].indexOfFirst { it == '&' }
                            if (index != -1) {
                                val userLoyalty =
                                    ptsArr[1].substring(startIndex = 0, endIndex = index)
                                val jb = JSONObject()
                                jb.apply {
                                    put("points", userLoyalty)
                                }
                                apiStatus.postValue(
                                    ApiResponse().apply {
                                        dataObject = jb
                                        enum = ApplicationEnum.GET_LOYALTY_POINTS_SUCCESS
                                    }
                                )
                            } else {
                                apiStatus.postValue(
                                    ApiResponse().apply {
                                        enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                                    }
                                )
                            }
                        } else {
                            apiStatus.postValue(
                                ApiResponse().apply {
                                    enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                                }
                            )
                        }
                    } else {
                        apiStatus.postValue(
                            ApiResponse().apply {
                                enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                            }
                        )
                    }
                } else {
                    apiStatus.postValue(
                        ApiResponse().apply {
                            enum = ApplicationEnum.GET_LOYALTY_POINTS_ERROR
                        }
                    )
                }

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


//    fun getSessionId(body: RequestBody) {
//        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
//            .getSessionId(
//                //   url = "PspecOhioHealth-132000011",
//                requestBody = body
//            )
//            .map { response ->
//                Log.d("Response", response.error.code.toString())
//                response.enum = ApplicationEnum.GET_SESSION_ID_SUCCESS
//                apiStatus.postValue(response)
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
//                            error = ApiError().apply {
//                                code = "-11"
//                                localizedDescription = "Error"
//                            }
//                        }
//                    )
//                }
//            })
//
//        DisposableManager.add(disposable!!)
//    }


    fun getCategoriesImages() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .getCategoriesImages()
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                val response = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_CATEGORIES_IMAGES_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_CATEGORIES_IMAGES_ERROR
                    )
                }
                apiDbStatus.postValue(response)
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
                            ApplicationEnum.GET_CATEGORIES_IMAGES_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun getProductImages(categoryId: String = "") {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .getProductsImages(url = ApiUrls.GET_PRODUCT_IMAGES + categoryId)
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                val response = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_PRODUCT_IMAGES_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_PRODUCT_IMAGES_ERROR
                    )
                }
                apiDbStatus.postValue(response)
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
                            ApplicationEnum.GET_PRODUCT_IMAGES_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun getMenuCategories(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getMenuCategories(
                requestBody = body
            )
            .map { response ->

                //Log.d("XMl Response", response.string())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.GET_MENU_SUCCESS
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
                            enum = ApplicationEnum.GET_MENU_ERROR
                        })
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
                            enum = ApplicationEnum.GET_MENU_ERROR
                        }
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun getMenu(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getOptions(
                requestBody = body
            )
            .map { response ->

                //Log.d("XMl Response", response.string())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.GET_MENU_SUCCESS
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

    fun getOptions(body: RequestBody) {
        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getMenu(
                requestBody = body
            )
            .map { response ->

                //Log.d("XMl Response", response.string())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)

//                response.enum = ApplicationEnum.GET_MENU_SUCCESS
//                Log.d("Response", response.apiError.code.toString())

                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.GET_OPTIONS_SUCCESS
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
                            enum = ApplicationEnum.GET_OPTIONS_ERROR
                        })
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
                            enum = ApplicationEnum.GET_OPTIONS_ERROR
                        }
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun getFavouritesList(pageSize: Int = 200, pageNumber: Int = 1) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .favouritesListing(
                pageNumber = pageNumber,
                perPage = pageSize
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                val response = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_FAVOURITES_LISTING_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.GET_FAVOURITES_LISTING_ERROR
                    )
                }
                apiDbStatus.postValue(response)
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
                            ApplicationEnum.GET_FAVOURITES_LISTING_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }

    fun toggleFavourite(
        productId: String, categoryId: String, toggle: Int, index: Int,
        changeState: Boolean = true
    ) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .toggleFavourite(
                productId = productId,
                categoryId = categoryId,
                toggle = toggle
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                val response = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.TOGGLE_FAVOURITE_SUCCESS,
                        JsonObject().apply {
                            addProperty("index", index.toString())
                            addProperty("changeState", changeState)
                        }
                    )
                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.TOGGLE_FAVOURITE_ERROR
                    )
                }
                apiDbStatus.postValue(response)
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

    override fun onCleared() {
        if (disposable != null)
            DisposableManager.remove(disposable!!)
        super.onCleared()
    }

    /*Edited By Usama*/

    fun getAllKitchens() {
        disposable = RemoteRepository.getInstance().getCPApiClient()
            .getAllKitchens()
            .map { response ->

                val jsonObject = JSONObject(response.string())
                cpApiStatus.postValue(Resource.success(jsonObject, ApplicationEnum.CP_GET_KITCHEN_SUCCESS))

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                    Log.d("SubscribeOnSuccess", "$t")
                }

                override fun onError(e: Throwable) {
                    cpApiStatus.postValue(Resource.error(e.localizedMessage ?: "", null, ApplicationEnum.CP_GET_KITCHEN_ERROR))
                    Log.d("SubscribeOnError", "${e.localizedMessage}")
                }
            })

        DisposableManager.add(disposable!!)

    }

    fun getMenuCategoriesByKitchenId(kitchenId: String) {
        disposable = RemoteRepository.getInstance().getCPApiClient()
            .getMenuCategoriesByKitchenId(kitchenId)
            .map { response ->

                val jsonObject = JSONObject(response.string())
                cpApiStatus.postValue(Resource.success(jsonObject, ApplicationEnum.CP_GET_MENU_CATEGORIES_BY_KITCHEN_ID_SUCCESS))

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                    Log.d("SubscribeOnSuccess", "$t")
                }

                override fun onError(e: Throwable) {
                    cpApiStatus.postValue(
                        Resource.error(
                            e.localizedMessage ?: "",
                            null,
                            ApplicationEnum.CP_GET_MENU_CATEGORIES_BY_KITCHEN_ID_ERROR
                        )
                    )
                    Log.d("SubscribeOnError", "${e.localizedMessage}")
                }
            })

        DisposableManager.add(disposable!!)

    }

    fun getMenuItemByKitchenAndCategoryID(kitchenId: String, menuCategoryID: String) {
        disposable = RemoteRepository.getInstance().getCPApiClient()
            .getMenuItemByKitchenAndCategoryID(kitchenId, menuCategoryID)
            .map { response ->

                val jsonObject = JSONObject(response.string())
                cpApiStatus.postValue(Resource.success(jsonObject, ApplicationEnum.CP_GET_MENU_ITEMS_BY_KITCHEN_AND_CATEGORY_ID_SUCCESS))

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                    Log.d("SubscribeOnSuccess", "$t")
                }

                override fun onError(e: Throwable) {
                    cpApiStatus.postValue(
                        Resource.error(
                            e.localizedMessage ?: "",
                            null,
                            ApplicationEnum.CP_GET_MENU_ITEMS_BY_KITCHEN_AND_CATEGORY_ID_ERROR
                        )
                    )
                    Log.d("SubscribeOnError", "${e.localizedMessage}")
                }
            })

        DisposableManager.add(disposable!!)

    }

    fun getMenuOptionsByProductId(productId: String) {
        disposable = RemoteRepository.getInstance().getCPApiClient()
            .getMenuOptionsByProductId(productId)
            .map { response ->

                val jsonObject = JSONObject(response.string())

                val status = jsonObject.getString("status")

                if (status == "success")
                    cpApiStatus.postValue(Resource.success(jsonObject, ApplicationEnum.CP_GET_MENU_OPTION_BY_PRODUCT_ID_SUCCESS))
                else
                    cpApiStatus.postValue(
                        Resource.error(
                            jsonObject.getString("message") ?: "",
                            null,
                            ApplicationEnum.CP_GET_MENU_OPTION_BY_PRODUCT_ID_ERROR
                        )
                    )

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                    Log.d("SubscribeOnSuccess", "$t")
                }

                override fun onError(e: Throwable) {
                    cpApiStatus.postValue(
                        Resource.error(
                            e.localizedMessage ?: "",
                            null,
                            ApplicationEnum.CP_GET_MENU_OPTION_BY_PRODUCT_ID_ERROR
                        )
                    )
                    Log.d("SubscribeOnError", "${e.localizedMessage}")
                }
            })

        DisposableManager.add(disposable!!)

    }

    fun getFavoriteProductsCP(page: Int) {
        disposable = RemoteRepository.getInstance().getCPApiClient()
            .getFavoriteProductsCP(page)
            .map { response ->

                val jsonObject = JSONObject(response.string())

                if (jsonObject.getString("status") == "error")
                    cpApiStatus.postValue(
                        Resource.error(
                            jsonObject.getString("message") ?: "",
                            null,
                            ApplicationEnum.CP_GET_FAVORITE_PRODUCTS_ERROR
                        )
                    )
                else
                    cpApiStatus.postValue(Resource.success(jsonObject, ApplicationEnum.CP_GET_FAVORITE_PRODUCTS_SUCCESS))

                true
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {

                override fun onSuccess(t: Boolean) {
                    Log.d("SubscribeOnSuccess", "$t")
                }

                override fun onError(e: Throwable) {
                    cpApiStatus.postValue(
                        Resource.error(
                            e.localizedMessage ?: "",
                            null,
                            ApplicationEnum.CP_GET_FAVORITE_PRODUCTS_ERROR
                        )
                    )
                    Log.d("SubscribeOnError", "${e.localizedMessage}")
                }
            })

        DisposableManager.add(disposable!!)

    }

}