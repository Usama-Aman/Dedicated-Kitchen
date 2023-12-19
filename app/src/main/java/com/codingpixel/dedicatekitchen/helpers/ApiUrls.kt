package com.codingpixel.dedicatekitchen.helpers

object ApiUrls {

    const val SKIP_URL = "https://www.skipthedishes.com/"

    const val VOLANTE_BASE_URL = "https://switchboard.volantecloud.com/api-v1/"
    const val API_BASE_URL = "CoPixDedicateKitchen-20000002"

    //    const val BASE_URL = "https://dev.codingpixel.com/dedicatekitchen-backend-live/api/" // test
    /*const val BASE_URL = "https://dedicatekitchen.com/api/" // live*/

    const val BASE_URL = "https://dedicate.codingpixels.us/api/" // live

    //    const val IMAGE_BASE_URL = "https://dev.codingpixel.com/dedicatekitchen-backend-live/" // test
    const val IMAGE_BASE_URL = "https://dedicatekitchen.com/" // live
    const val DEFAULT_CATEGORY_IMAGE_URL = IMAGE_BASE_URL + "public/assets/images/menu_set.png"
    const val DEFAULT_PRODUCT_IMAGE_URL =
        IMAGE_BASE_URL + "public/assets/images/product_default.png"

    //    const val DATA_CANDY_AUTH_URL = "https://ms2-codingpixel.dcuat.com" // test
    const val DATA_CANDY_AUTH_URL = "https://app.datacandy.com" // live

    //    const val DATA_CANDY_LOYALTY_URL = "https://codingpixel.dcuat.com" // test
    const val DATA_CANDY_LOYALTY_URL = "http://secure.datacandy.com" // live
    const val GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/" // live

    const val GET_SESSION_ID = "login"
    const val GENERAL_CATEGORY = "general"
    const val CRM_CATEGORY = "crm"
    const val GET_CUSTOMER_SUMMARY = "CRM.getCustomerSummary"
    const val API_CATEGORY = "pos.trans"
    const val SET_UP_INFO_CATEGORY = "setupinfo"
    const val STRIPE_BASE_URL = "https://api.stripe.com/v1/"
    const val ADD_ACCOUNT_TRANSACTION = "CRM.addAccountTransactionToDB"

    const val ADD_OFFLINE_GIFT_PAYMENT = "POSTransaction.addOfflineGiftCardPayment"
    const val PROCESS_PAYMENT = "CRM.processAccountPayment"
    const val GET_MENU = "POSMainManager.getMenuItems"
    const val GET_TABLE = "POSMainManager.getTableTransaction"
    const val GET_OPTIONS = "POSMainManager.getOptions"
    const val GET_ACCOUNT_BALANCE = "CRM.getAccountBalance"
    const val ADD_ITEMS_TO_TRANS = "POSTransaction.addItemToTrans"
    const val TOGGLE_FAVOURTIE = "favorites/favorite-product"
    const val FAVOURITE_LISTING = "favorites/get-favorite-products"
    const val AUTHENTICATECUSTOMER = "CRM.authenticateCustomer"


    const val CREATE_NEW_CUSTOMER = "CRM.createNewCustomerInfo"
    const val CREATE_TABLE = "POSTransaction.createTableTransaction"
    const val SET_CUSTOMER_ID = "POSTransaction.setCustomerId"
    const val GET_CUSTOMER_INFO = "CRM.getCustomerInfo"
    const val checkEmail = "auth/check_email"
    const val login = "auth/signin"
    const val changePassword = "user/change-password"
    const val addCard = "cards/add-card"
    const val signUp = "auth/signup"
    const val verifyEmail = "auth/verify-account"
    const val updateInfo = "user/edit-profile"
    const val getCard = "cards/cards-list"
    const val forgetPassword = "auth/forgot-password"
    const val resetPassword = "auth/reset-password"
    const val addAddress = "addresses/add-address"
    const val addressList = "addresses/addresses-list"
    const val setAddressDefault = "addresses/set_default_address"
    const val saveAuthOrder = "catalog/save-auth-order"
    const val saveGuestOrder = "catalog/save-guest-order"
    const val GET_PAYMENT_ID = "payment_methods"
    const val getPackages = "packages/get_packages"
    const val UPDATE_CUSTOMER_INFO = "CRM.updateCustomerInfo"
    const val DISCOUNT_VOLANTE = "POSTransaction.applyBillPriceMod"
    const val addUserPackage = "packages/add-user-package"
    const val ALL_ORDERS = "catalog/get-orders-data/all/"
    const val PAST_ORDERS = "catalog/get-orders-data/history/"
    const val CANCEL_ORDER = "catalog/cancel-order"
    const val GET_CATEGORIES_IMAGES = "catalog/get-categories-images"
    const val GET_PRODUCT_IMAGES = "catalog/get-products-images/"
    const val getUserPackages = "packages/get-user-package/1"
    const val resendCode = "auth/resend-code"
    const val guestOrderNumber = "catalog/guest-order-number"
    const val orderNumber = "catalog/order-number"
    const val GET_PURCHASE_HISTORY = "packages/get-user-all-package/"
    const val IS_KITCHEN_OPEN = "is_kitchen_open"
    const val IS_PAST_NINE = "is_past_nine"


    /*Edited by Usama */

    const val CP_BASE_URL = "http://dedicatekitchen.com/api/" // live
    const val CP_BASE_URL_STAGING = "https://dedicate.codingpixels.us/api/" // live
    const val CP_GET_ALL_KITCHENS = "all_kitchens"
    const val CP_GET_MENU_CATEGORIES_BY_KITCHEN = "get_menu_categories_by_kitchen"
    const val CP_GET_MENU_ITEMS_BY_KITCHEN_AND_CATEGORY_ID = "get_menu_items_by_category"
    const val CP_GET_MENU_OPTION_BY_PRODUCT_ID = "get_menu_item_options"
    const val CP_GET_FAVORITE_PRODUCTS = "favorites/get-favorite-products"
}