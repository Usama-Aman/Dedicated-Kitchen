package com.codingpixel.dedicatekitchen.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.JsonManager
import com.codingpixel.dedicatekitchen.http.DisposableManager
import com.codingpixel.dedicatekitchen.http.RemoteRepository
import com.codingpixel.dedicatekitchen.models.ApiResponseDb
import com.codingpixel.dedicatekitchen.models.ApiStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class KitchenViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = OrdersViewModel::class.java.canonicalName

    private val apiDbStatus = MutableLiveData<ApiStatus>()
    private var apiStatusObject: ApiStatus? = null


    //
    private var disposable: Disposable? = null


    fun getDbApiStatus(): MutableLiveData<ApiStatus> {
        return apiDbStatus
    }

    fun isKitchenOpen() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .isKitchenOpen()
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.IS_KITCHEN_OPEN_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.IS_KITCHEN_OPEN_ERROR,
                        jsonObject.getAsJsonObject("result")

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

    fun isPastNine() {
        disposable = RemoteRepository.getInstance().getRetrofitService()
            .isPastNine()
            .map { jsonObject ->
                val apiResponse: ApiResponseDb =
                    JsonManager.getInstance().getApiResponse(jsonObject)
                apiStatusObject = if (apiResponse.status == "success") {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.IS_PAST_NINE_SUCCESS,
                        jsonObject.getAsJsonObject("result")
                    )

                } else {
                    ApiStatus(
                        apiResponse.status,
                        apiResponse.message,
                        ApplicationEnum.IS_PAST_NINE_ERROR,
                        jsonObject.getAsJsonObject("result")

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