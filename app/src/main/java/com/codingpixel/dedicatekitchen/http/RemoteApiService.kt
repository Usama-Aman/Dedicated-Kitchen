package com.codingpixel.dedicatekitchen.http

import com.codingpixel.dedicatekitchen.helpers.ApiParams
import com.codingpixel.dedicatekitchen.helpers.ApiUrls
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.models.SubmitOrderRequestModel
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.*

interface RemoteApiService {

    //("PspecOhioHealth-132000011")
    @POST
    fun getSessionId(
        @Url url: String ,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun createTable(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>


    @POST
    fun addAccountTransaction(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun addOfflineGiftCard(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun discountOnVolante(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun processPayment(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @FormUrlEncoded
    @POST(ApiUrls.GET_PAYMENT_ID)
    fun getPaymentIdForCard(
        @Field(ApiParams.CARD_HOLDER_NAME) cardHolderName: String,
        @Field(ApiParams.CARD_NUMBER) cardNumber: String,
        @Field(ApiParams.CARD_EXPIRY_MONTH) cardExpiryMonth: Int,
        @Field(ApiParams.CARD_EXPIRY_YEAR) cardExpiryYear: Int,
        @Field(ApiParams.CARD_EXPIRY_CVV) cvv: Int,
        @Field(ApiParams.CARD_TYPE) cardType: String
    ): Single<JsonObject>

    @POST
    fun getTable(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun addItemsToTrans(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun getCustomerSummary(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun setCustomerId(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>


    @POST
    fun createCustomer(
        @Url url: String = Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun getUserData(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @FormUrlEncoded
    @POST(ApiUrls.checkEmail)
    fun checkEmail(
        @Field("email") email: String
    ): Single<JsonObject>


    @POST(ApiUrls.guestOrderNumber)
    fun guestOrderNumber(): Single<JsonObject>

    @POST(ApiUrls.orderNumber)
    fun orderNumber(): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.setAddressDefault)
    fun setAddressDefault(
        @Field("address_id") address_id: Int
    ): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.addAddress)
    fun addAddress(
        @Field("address") address: String,
        @Field("country") country: String,
        @Field("city") city: String,
        @Field("state") state: String = "",
        @Field("postal_code") zipCode: String = "",
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Single<JsonObject>


    @FormUrlEncoded
    @POST(ApiUrls.updateInfo)
    fun updateInfo(
        @Field("email") email: String,
        @Field("fname") fname: String,
        @Field("lname") lname: String,
        @Field("phone") phone: String
    ): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.updateInfo)
    fun updateDcInfo(
        @Field("dc_contact_id") dcContactId: String,
        @Field("dc_card_id") dcCardId: String,
        @Field("email") email: String,
        @Field("fname") fname: String,
        @Field("lname") lname: String,
        @Field("phone") phone: String
    ): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.addCard)
    fun addCard(
        @Field("stripe_card_id") stripeId: String,
        @Field("card_number") card_number: String,
        @Field("month") month: String,
        @Field("year") year: String,
        @Field("cvc") cvc: String,
        @Field("is_default") is_default: Int,
        @Field("card_brand") card_brand: String
    ): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.resetPassword)
    fun resetPassword(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirm_password") confirm_password: String,
        @Field("code") code: Int
    ): Single<JsonObject>


    @GET(ApiUrls.getCard)
    fun getCard(): Single<JsonObject>

    @GET(ApiUrls.addressList)
    fun getLocations(): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.forgetPassword)
    fun forgetPassword(
        @Field("email") email: String
    ): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.changePassword)
    fun changePassword(
        @Field("old_password") old_password: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String
    ): Single<JsonObject>


    //    @FormUrlEncoded
    @POST(ApiUrls.saveAuthOrder)
    fun saveOrderToServer(
        @Body model: SubmitOrderRequestModel
    ): Single<JsonObject>


    @POST(ApiUrls.saveGuestOrder)
    fun saveGuestOrder(
        @Body model: SubmitOrderRequestModel
    ): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.verifyEmail)
    fun verifyEmail(
        @Field("email") email: String,
        @Field("code") code: Int
    ): Single<JsonObject>


    @FormUrlEncoded
    @POST(ApiUrls.resendCode)
    fun resendCode(
        @Field("email") email: String
    ): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.signUp)
    fun saveUser(
        @Field("email") email: String,
        @Field("fname") fname: String,
        @Field("lname") lname: String,
        @Field("customer_id") customer_id: String,
        @Field("customer_code") customer_code: String,
        @Field("customer_account_id") customer_account_id: String,
        @Field("password") password: String,
        @Field("confirm_password") confirm_password: String,
        @Field("phone") phone: String,
        @Field("address") address: String
    ): Single<JsonObject>


    @FormUrlEncoded
    @POST(ApiUrls.addUserPackage)
    fun addUserPackage(
        @Field("package_id") package_id: Int,
        @Field("card_id") card_id: Int,
        @Field("stripe_id") stripe_id: String,
        @Field("amount") amount: String
    ): Single<JsonObject>

    @FormUrlEncoded
    @POST(ApiUrls.login)
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Single<JsonObject>

    @POST
    fun getMenu(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun getAccountBalance(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun authenticateCustomer(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>


    @POST
    fun getMenuCategories(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun getOptions(
        @Url url: String = AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.apiUrl
            ?: Constants.DEFAULT_API_URL,
        @Body requestBody: RequestBody
    ): Single<ResponseBody>

    @FormUrlEncoded
    @POST(ApiUrls.CANCEL_ORDER)
    fun cancelOrder(
        @Field("order_id") order_id: Int
    ): Single<JsonObject>

    @GET(ApiUrls.getPackages)
    fun getPackages(): Single<JsonObject>

    @GET
    fun getPurchaseHistory(@Url url: String): Single<JsonObject>

    @GET(ApiUrls.getUserPackages)
    fun getUserPackages(
        @Query("id") id: Int = 0
    ): Single<JsonObject>

    @DELETE
    fun delete(
        @Url url: String
    ): Single<JsonObject>

    @GET(ApiUrls.FAVOURITE_LISTING)
    fun favouritesListing(
        @Query("per_page") perPage: Int = 100,
        @Query("page") pageNumber: Int = 1
    ): Single<JsonObject>


    @FormUrlEncoded
    @POST(ApiUrls.TOGGLE_FAVOURTIE)
    fun toggleFavourite(
        @Field("product_id") productId: String,
        @Field("is_fvt") toggle: Int = 0,
        @Field("cat_parent") catPrent: Int = 333,
        @Field("cat_id") categoryId: String
    ): Single<JsonObject>


    @GET
    fun getAllOrders(
        @Url url: String = ApiUrls.ALL_ORDERS
    ): Single<JsonObject>

    @GET(ApiUrls.FAVOURITE_LISTING)
    fun getPastOrders(
        @Query("per_page") perPage: Int = 100,
        @Query("page") pageNumber: Int = 1
    ): Single<JsonObject>

    @GET(ApiUrls.GET_CATEGORIES_IMAGES)
    fun getCategoriesImages(
    ): Single<JsonObject>

    @POST
    fun getLoyaltyPoints(
        @Url url: String
    ): Single<ResponseBody>


    @POST
    fun addLoyaltyPoints(
        @Url url: String,
        @Body requestbody: RequestBody
    ): Single<ResponseBody>

    @POST
    fun redeemLoyaltyPoints(
        @Url url: String
    ): Single<ResponseBody>

    @GET
    fun getProductsImages(
        @Url url: String
    ): Single<JsonObject>

    @POST
    fun checkProfileONDataCandy(
        @Url url: String,
        @Query("phone", encoded = true) phone: String
    ): Single<JsonObject>

    @POST
    fun checkProfileONDataCandy(
        @Url url: String
    ): Single<JsonObject>


    @POST
    fun createProfileONDataCandy(
        @Url url: String,
        @Query("phone", encoded = true) phone: String
    ): Single<JsonObject>


    @PUT
    fun updateDataCandyProfile(
        @Url url: String,
        @Query("phone", encoded = true) phone: String
    ): Single<JsonObject>

    @PUT
    fun updateDataCandyProfile(
        @Url url: String
    ): Single<JsonObject>

    @POST
    fun createLoyaltyCard(
        @Url url: String
    ): Single<JsonObject>


    @POST("/api2/cardcreation/eloyalty")
    fun createProfile(
        @Body body: RequestBody
    ): Single<JsonObject>


    @GET(ApiUrls.IS_KITCHEN_OPEN)
    fun isKitchenOpen(): Single<JsonObject>

    @GET(ApiUrls.IS_PAST_NINE)
    fun isPastNine(): Single<JsonObject>

//    @POST(ApiUrls.API_BASE_URL)
//    fun getData(
//        @Body requesyBody: RequestBody
//    ): Single<ApiResponse>


//    @GET
//    fun genericGetCall(@Url url: String): Single<JsonObject>
//
//    @FormUrlEncoded
//    @POST(ApiUrls.SIGN_UP)
//    fun signUp(
//        @Field(ApiParams.NAME) name: String,
//        @Field(ApiParams.EMAIL) email: String,
//        @Field(ApiParams.USER_TYPE) userType: String,
//        @Field(ApiParams.USER_GENDER) gender: String,
//        @Field(ApiParams.USER_AGE) age: Int,
//        @Field(ApiParams.PASSWORD) password: String,
//        @Field(ApiParams.CONFIRM_PASSWORD) confirmPassword: String
//    ): Single<JsonObject>
//

    /*Edited By Usama*/

    @GET(ApiUrls.CP_GET_ALL_KITCHENS)
    fun getAllKitchens(): Single<ResponseBody>


    @GET(ApiUrls.CP_GET_MENU_CATEGORIES_BY_KITCHEN)
    fun getMenuCategoriesByKitchenId(
        @Query("kitchen_id") kitchen_id: String
    ): Single<ResponseBody>

    @GET(ApiUrls.CP_GET_MENU_ITEMS_BY_KITCHEN_AND_CATEGORY_ID)
    fun getMenuItemByKitchenAndCategoryID(
        @Query("kitchen_id") kitchen_id: String,
        @Query("menu_category_id") menu_category_id: String
    ): Single<ResponseBody>

    @GET(ApiUrls.CP_GET_MENU_OPTION_BY_PRODUCT_ID)
    fun getMenuOptionsByProductId(
        @Query("menu_item_id") menu_item_id: String
    ): Single<ResponseBody>

    @GET(ApiUrls.CP_GET_FAVORITE_PRODUCTS)
    fun getFavoriteProductsCP(
        @Query("page") page: Int
    ): Single<ResponseBody>


}