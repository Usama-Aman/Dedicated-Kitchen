package com.codingpixel.dedicatekitchen.http

class RemoteRepository {

    companion object {

        private val TAG = "RemoteRepository"
        private var ourInstance: RemoteRepository? =
            RemoteRepository()

        fun getInstance(): RemoteRepository {
            if (ourInstance == null)
                ourInstance = RemoteRepository()
            return ourInstance as RemoteRepository
        }
    }

    // Get remote api client
    fun getRetrofitService(): RemoteApiService {
        return ApiClient().getRetrofitClient()!!.create(RemoteApiService::class.java)
    }

    fun getVolanteRetrofitService(): RemoteApiService {
        return ApiClient().getVolanteRetrofitClient()!!.create(RemoteApiService::class.java)
    }

    fun getDCAuthRetrofitService(): RemoteApiService {
        return ApiClient().getDataCandyProfileRetrofitClient()!!
            .create(RemoteApiService::class.java)
    }

    fun getDCLoyaltyRetrofitService(): RemoteApiService {
        return ApiClient().getDataCandyLoyaltyRetrofitClient()!!
            .create(RemoteApiService::class.java)
    }

    // Get Payment api client
    fun getPaymentRetrofitService(): RemoteApiService {
        return ApiClient().getPaymentRetrofitClient()!!.create(RemoteApiService::class.java)
    }


    fun getGoogleApiService(): RemoteApiService {
        return ApiClient().getGoogleClient()!!.create(RemoteApiService::class.java)
    }
    // Get Payment api client
//    fun getPaymentRetrofitService(): RemoteApiService {
//        return ApiClient().getPaymentRetrofitClient()!!.create(RemoteApiService::class.java)
//    }

    /*Edited by Usama*/

    fun getCPApiClient() : RemoteApiService{
        return ApiClient().createCPApiClient()!!.create(RemoteApiService::class.java)
    }
}