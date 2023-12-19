package com.codingpixel.dedicatekitchen.http

import com.codingpixel.dedicatekitchen.BuildConfig
import com.codingpixel.dedicatekitchen.helpers.*
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.converter.jaxb.JaxbConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import com.google.gson.GsonBuilder

import com.google.gson.Gson

//import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class ApiClient {

    private val REQUEST_TIMEOUT = 90
    private val REQUEST_READOUT = 90
    private val REQUEST_WRITEOUT = 90
    private var sRetrofit: Retrofit? = null
    private var mRetrofit: Retrofit? = null
    private var vRetrofit: Retrofit? = null
    private var dc_profile_Retrofit: Retrofit? = null
    private var dc_loyalty_Retrofit: Retrofit? = null
    private var googleMapRetrofit: Retrofit? = null

    fun getRetrofitClient(): Retrofit? {

        createAPIClient()
        return sRetrofit
    }


    fun getDataCandyProfileRetrofitClient(): Retrofit? {

        createDataCandyProfileAPIClient()
        return dc_profile_Retrofit
    }

    fun getDataCandyLoyaltyRetrofitClient(): Retrofit? {

        createDataCandyLoyaltyAPIClient()
        return dc_loyalty_Retrofit
    }

    fun getGoogleClient(): Retrofit? {
        createGoogleClient()
        return googleMapRetrofit
    }

    fun getVolanteRetrofitClient(): Retrofit? {
        createVolanteAPIClient()
//        if (sRetrofit == null) {
//
//        }
//        else{
//            val jwt = AppPreferenceManager.getUserFromSharedPreferences()?.token.toString()
//            if(!AppPreferenceManager.getBoolean(Constants.JWT_UPDATED)){
//                if (jwt.isNotEmpty()) {
//                    createAPIClient()
//                    AppPreferenceManager.setBoolean(Constants.JWT_UPDATED, true)
//                }
//            }
//        }
        return vRetrofit
    }

    private fun createVolanteAPIClient() {
        //-----OkHttp 3 client and intercept for logs and header authentication
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        val credentials: String =
            Credentials.basic(Constants.USER_NAME_VOLANTE, Constants.PASSWORD_VOLANTE)

        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(REQUEST_READOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_WRITEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)

        okHttpClientBuilder.addInterceptor { chain ->
//            var jwt = AppPreferenceManager.getValue(PrefFlags.ACCESS_TOKEN)
//            //var jwt = AppPreferenceManager.getUserFromSharedPreferences()?.token.toString()
//            if (jwt.isEmpty()) {
//                //TODO Add static jwt if required
//                jwt = ""
//            }
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                //.addHeader("Content-Type", "application/x-www-form-urlencoded")
                //.addHeader("Content-Type", "application/xml")
                .addHeader("Authorization", credentials)
                .addHeader("Content-Type", "text/xml")
            //.addHeader("Authorization", "Bearer ")
            // Adding Authorization token (API Key)
            // Requests will be denied without API key
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpClientBuilder.build()
        //----------Retrofit Client
        vRetrofit = Retrofit.Builder()
            .baseUrl(
                ApiUrls.VOLANTE_BASE_URL
            )
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private fun createAPIClient() {
        //-----OkHttp 3 client and intercept for logs and header authentication
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(REQUEST_READOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_WRITEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)

        okHttpClientBuilder.addInterceptor { chain ->
            var jwt = AppPreferenceManager.getValue(PrefFlags.ACCESS_TOKEN)
            //var jwt = AppPreferenceManager.getUserFromSharedPreferences()?.token.toString()
            if (jwt.isEmpty()) {
                //TODO Add static jwt if required
                jwt = ""
            }
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                //.addHeader("Content-Type", "application/xml")
                .addHeader("Authorization", "Bearer $jwt")
            //.addHeader("Content-Type", "text/xml")
            //.addHeader("Authorization", "Bearer ")
            // Adding Authorization token (API Key)
            // Requests will be denied without API key
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpClientBuilder.build()
        //----------Retrofit Client
        sRetrofit = Retrofit.Builder()
            .baseUrl(ApiUrls.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    fun getPaymentRetrofitClient(): Retrofit? {
        if (mRetrofit == null)
            createPaymentAPIClient()
        return mRetrofit
    }

    private fun createPaymentAPIClient() {
        //-----OkHttp 3 client and intercept for logs and header authentication
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(REQUEST_READOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_WRITEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)
        okHttpClientBuilder.addInterceptor { chain ->
            val jwt = StripeParams.STRIPE_PK
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer $jwt")
            // Adding Authorization token (API Key)
            // Requests will be denied without API key
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpClientBuilder.build()
        //----------Retrofit Client
        mRetrofit = Retrofit.Builder()
            .baseUrl(ApiUrls.STRIPE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private fun createDataCandyProfileAPIClient() {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        val credentials: String =
            Credentials.basic(Constants.USER_NAME_DATA_CANDY, Constants.PASSWORD_DATA_CANDY)

        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(REQUEST_READOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_WRITEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)

        okHttpClientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                // Requests will be denied without Credentials
                .addHeader("Authorization", credentials)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpClientBuilder.build()
        //----------Retrofit Client
        dc_profile_Retrofit = Retrofit.Builder()
            .baseUrl(ApiUrls.DATA_CANDY_AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }


    fun createDataCandyLoyaltyAPIClient() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(REQUEST_READOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_WRITEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)

        okHttpClientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Content-Type", "text/xml")
                .addHeader("Accept", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpClientBuilder.build()
        //----------Retrofit Client
        dc_loyalty_Retrofit = Retrofit.Builder()
            .baseUrl(ApiUrls.DATA_CANDY_LOYALTY_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }


    private fun createGoogleClient() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(REQUEST_READOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_WRITEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)

        okHttpClientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Content-Type", "text/xml")
                .addHeader("Accept", "application/json")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpClientBuilder.build()
        //----------Retrofit Client
        googleMapRetrofit = Retrofit.Builder()
            .baseUrl(ApiUrls.GOOGLE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }


    /*Edited by Usama*/

    fun createCPApiClient(): Retrofit? {
        //-----OkHttp 3 client and intercept for logs and header authentication
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(REQUEST_READOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_WRITEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)

        var jwt = AppPreferenceManager.getValue(PrefFlags.ACCESS_TOKEN)
        //var jwt = AppPreferenceManager.getUserFromSharedPreferences()?.token.toString()
        if (jwt.isEmpty()) {
            jwt = ""
        }

        okHttpClientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer $jwt")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val okHttpClient = okHttpClientBuilder.build()
        return Retrofit.Builder()
            .baseUrl(ApiUrls.CP_BASE_URL_STAGING)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

}