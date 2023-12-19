package com.codingpixel.dedicatekitchen.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.codingpixel.dedicatekitchen.helpers.ApiUrls
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.JsonManager
import com.codingpixel.dedicatekitchen.http.DisposableManager
import com.codingpixel.dedicatekitchen.http.RemoteRepository
import com.codingpixel.dedicatekitchen.models.ApiResponse
import com.codingpixel.dedicatekitchen.models.ApiResponseDb
import com.codingpixel.dedicatekitchen.models.ApiStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = OrdersViewModel::class.java.canonicalName

    private val apiDbStatus = MutableLiveData<ApiStatus>()
    private var apiStatusObject: ApiStatus? = null


    //
    private var disposable: Disposable? = null


    fun getDbApiStatus(): MutableLiveData<ApiStatus> {
        return apiDbStatus
    }

    fun getOrders(url: String = ApiUrls.ALL_ORDERS) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .getAllOrders(
                url = url
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        if (url.contains("all"))
                            ApplicationEnum.GET_ALL_ORDERS_SUCCESS
                        else
                            ApplicationEnum.GET_PAST_ORDERS_SUCCESS,
                        if (jsonObject.has("result"))
                            jsonObject.getAsJsonObject("result")
                        else
                            null
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        if (url.contains("all"))
                            ApplicationEnum.GET_ALL_ORDERS_ERROR
                        else
                            ApplicationEnum.GET_PAST_ORDERS_ERROR
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
                            if (url.contains("all"))
                                ApplicationEnum.GET_ALL_ORDERS_ERROR
                            else
                                ApplicationEnum.GET_PAST_ORDERS_ERROR
                        )
                    )
                }
            })

        DisposableManager.add(disposable!!)
    }


    fun cancelOrders(orderId: Int) {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .cancelOrder(
                order_id = orderId
            )
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CANCEL_ORDER_SUCCESS
                        //jsonObject.getAsJsonObject("data")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.CANCEL_ORDER_ERROR
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

    override fun onCleared() {
        if (disposable != null)
            DisposableManager.remove(disposable!!)
        super.onCleared()
    }
}