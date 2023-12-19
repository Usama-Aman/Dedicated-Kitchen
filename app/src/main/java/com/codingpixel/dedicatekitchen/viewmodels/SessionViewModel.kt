package com.codingpixel.dedicatekitchen.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.http.DisposableManager
import com.codingpixel.dedicatekitchen.http.RemoteRepository
import com.codingpixel.dedicatekitchen.models.ApiError
import com.codingpixel.dedicatekitchen.models.ApiResponse
import com.codingpixel.dedicatekitchen.models.NewApiResponse
import com.google.gson.Gson
import fr.arnaudguyon.xmltojsonlib.XmlToJson
//import com.codingpixel.drawsome.helpers.ApplicationEnum
//import com.codingpixel.drawsome.helpers.Constants
//import com.codingpixel.drawsome.helpers.JsonManager
//import com.codingpixel.drawsome.http.DisposableManager
//import com.codingpixel.drawsome.http.RemoteRepository
//import com.codingpixel.drawsome.models.ApiResponse
//import com.codingpixel.drawsome.models.ApiStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = SessionViewModel::class.java.canonicalName
    private val apiStatus = MutableLiveData<ApiResponse>()
    private var apiStatusObject: String? = null

    //
    private var disposable: Disposable? = null

    //
    fun getApiStatus(): MutableLiveData<ApiResponse> {
        return apiStatus
    }

    fun getSessionId(body: RequestBody) {

        var url = ""

        url = try {
            AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            Constants.DEFAULT_API_URL
        }

        disposable = RemoteRepository.getInstance().getVolanteRetrofitService()
            .getSessionId(
                //   url = "PspecOhioHealth-132000011",
                url,
                requestBody = body
            )
            .map { response ->
                // Log.d("Response", response.error.code.toString())
                val jsonString = XmlToJson.Builder(response.string()).build()
                Log.d("Json Response", jsonString.toString())

                val apiResponse = Gson().fromJson(jsonString.toString(), NewApiResponse::class.java)
                when (apiResponse.response.error.code) {
                    "0" -> {
                        // Success
                        Log.d("Session", "Saved")
                        AppPreferenceManager.setValue(
                            "sessionId",
                            apiResponse.response.data.session.id

                        )
                        apiStatus.postValue(ApiResponse().apply {
                            error = apiResponse.response.error
                            enum = ApplicationEnum.SESSION_RESTORED
                            dataObject = JSONObject()
                        })
                    }
                    else -> {
                        Log.d("Session", "Error")
                        // Error
                        apiStatus.postValue(ApiResponse().apply {
                            enum = ApplicationEnum.SESSION_EXPIRED
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
                    Log.d("Session", "Error")
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


    override fun onCleared() {
        if (disposable != null)
            DisposableManager.remove(disposable!!)
        super.onCleared()
    }
}