package com.codingpixel.dedicatekitchen.helpers

object Constants {


    /*  AUTH-CREDENTIALS  (START) */

    const val USER_NAME_VOLANTE = "MOBILEAPP"
    const val PASSWORD_VOLANTE = "MOBILEAPP"

    const val CATEGORIES_WIDTH_RATIO = 0.8

    const val APP_CURRENCY = "cad"

//    const val USER_NAME_DATA_CANDY = "codingpixel_CPII" // test
//    const val PASSWORD_DATA_CANDY = "CP$$%?&22_CPII123" // test

    const val USER_NAME_DATA_CANDY = "codingpixel_CPII" // live
    const val PASSWORD_DATA_CANDY = "CP$$%?&2424_CPII" // live

    /*  AUTH-CREDENTIALS  (END) */

    const val KITCHEN_OPENING_HOUR: Int = 10 // 8
    const val KITCHEN_CLOSED_HOUR: Int = 20 // 8
    const val KITCHEN_CLOSED_MINUTE: Int = 45 // 45

    const val DEFAULT_TERMINAL_ID = "20000007"
    const val DEFAULT_API_URL = "CoPixDedicateKitchen-20000002"

    const val KITCHEN_CLOSED_MESSAGE: String =
        "Dedicate kitchen's operational hours starts from 10:00 AM to 08:45 PM . Please continue your Take Out Order tomorrow after 10:00 AM"

    const val DEFAULT_MENU_SET = "CodingPixelOnlineMenu"

    const val MEAL_PREP_CAT_ID = "20000037"
    val MEAL_PREP_CATS = listOf<String>(
        "20000087",
        "20000088",
        "20000089",
        "20000090",
        "20000091",
        "20000092",
        "20000093",
        "20000094",
        "20000095",
        "20000448",
        "20000449",
        "20000689"
    )

    val TAKE_OUT_CATS = listOf<String>(
        "20000233",
        "20000037",
        "20000300",
        "20000301",
        "20000302",
        "20000039",
        "20000233",
        "20000094",
        "20000095",
        "20000448",
        "20000449",
        "20000689"
    )

    const val All = "All"
    const val ORDER_STATUS_PENDING = "Pending"
    const val ORDER_STATUS_IN_PROGRESS = "In Progress"
    const val ORDER_STATUS_COMPLETED = "Completed"
    const val ORDER_STATUS_CANCELLED = "Cancelled"

    //    const val GOOGLE_PLACES_API_KEY = "AIzaSyBhMoC9OLQs1fxPJkPWxdgC9dui6pIKQoA"
    const val GOOGLE_PLACES_API_KEY = "AIzaSyAevv3JwGR_WLjGHhh-PL7AhrFcWZoq-m4"
    const val SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"

    const val MEDIA_TYPE_PARSE = "application/json; charset=utf-8"
    const val SESSION_ID = "sessionId"
    const val SESSION_ID_VALUE = "xxx"
    const val xmlns = "xmlns:xsi"
    const val instance = "http://www.w3.org/2001/XMLSchema-instance"
    const val xsd = "xmlns:xsd"
    const val instance2 = "http://www.w3.org/2001/XMLSchema"

    //    const val ORDER_STATUS_IN_PROGRESS = "In Progress"
    const val ALL_ORDERS = "Pending Orders"
    const val HISTORY = "History"

    const val CONTENT_TYPE = 1
    const val LOADING_TYPE = -1

    const val PRODUCT_VERTICAL_ITEM = "productVerticalTile"
    const val PRODUCT_HORIZONTAL_ITEM = "productHorizontalTile"

    const val DOLLAR_SIGN = "$"
    const val DUMMY_PRICE = 30

    const val MINIMUM_LENGTH = 6

    const val VIEW_TYPE = "viewType"
    const val VIEW_TYPE_TOGGLE = "toggleSelection"
    const val VIEW_TYPE_DISPLAY = "display"

    const val VIEW_TPYE_SMALL = "Small"
    const val VIEW_TPYE_LARGE = "Large"

    const val TERM_CONDITIONS_URL =
        "${ApiUrls.IMAGE_BASE_URL}loyalty#terms"
    const val PRIVACY_POLICY_URL =
        "${ApiUrls.IMAGE_BASE_URL}loyalty#policy"

    const val CARD_NUMBER_FORMAT = "XXXX XXXX XXXX XXXX"
    const val CARD_NUMBER_MAX_LENGTH = 16

    const val DOT = " • "

    const val YES = "Yes"
    const val NO = "No"
    const val DONE = "Done"
    const val OKAY = "Okay"
    const val CANCEL = "Cancel"

    const val ORDER_NUMBER_FORMAT = "00000000"


    const val API_VERSION_KEY = "apiVersion"
    const val API_VERSION_KEY_VALUE = "1.00"
    const val API_KEY = "apiKey"
    const val API_KEY_VALUE = "NOT_USED"
    const val CLIENT_ID_KEY = "clientId"
    const val CLIENT_ID_KEY_VALUE = "POS"
    const val API_NAME_KEY = "apiName"
    const val API_NAME_KEY_VALUE = "login"
    const val API_CATEGORY_KEY = "apiCategory"
    const val API_CATEGORY_KEY_VALUE = "general"
    const val LOCAL_KEY = "locale"
    const val LOCAL_KEY_VALUE = "en-US"
    const val REQUEST_TIMESTAMP_KEY = "requestTimestamp"
    const val REQUEST_TIMESTAMP_KEY_VALUE = "xxx"

    const val PREFS_FILE_NAME = "dedicate_kitchen_CP_pf"
    const val DATA = "result"

    const val CLIENT_ID_CRM_KEY_VALUE = "its_me"
    const val API_KEY_CRM_VALUE = "unless_you_know_this_key_you_cant_call_me"
    const val POS = "pos.trans"


    const val CUSTOMER_TYPE_ID = "132000020" // Student


//    const val DC_APP_KEY =
//        "b7aa8e0924444ba3b710f49969e31c9b6c3518e3361826d30b8361995109c90c" // test
    const val DC_APP_KEY =
        "7b2bae647818568ac13ee72a2407eb3c77b20987d68d30a6fac5e05898a2f2de" // live

    //    const val DC_ACCESS_KEY = "1LIJUC" // test
    const val DC_ACCESS_KEY = "P8UUMR" // live

//    const val DC_MID = "763719" // test
    const val DC_MID = "407917" // live
    const val DC_MERCHANT_NAME = "Web-Merchant"
//    const val DC_MPW = "43199358" // test
    const val DC_MPW = "71474481" // live
//    const val CARD_FORMAT_ID = "7049" // test
    const val CARD_FORMAT_ID = "19421" // live
    const val DC_VER = "2010-01-06"
    const val DC_WAN = "1"
    const val DC_WSN = "1"

    const val REWARD_PRICE_MODIFIER = "20000018"

    //    const val DEFAULT_CARD_ID = "6360879999987560893"
    const val DEFAULT_CARD_ID = "0"

    // If you want to cancel this order, please contact the location in which you ordered from. Find contact info on our website, www,dedicatekitchen.com. We can only give refunds in credit since we do not accept cash on the app. If your order has been started or completed, we may not be able to refund the order.

    //    const val ORDER_CANCEL_INFO_CELL = "780-790-0065"
    const val ORDER_CANCEL_INFO_CELL = "www.dedicatekitchen.com"
    const val ORDER_CANCEL_INFO_1ST_HALF =
        "If you want to cancel this order, please contact the location in which you ordered from. Find contact info on our website, "
    const val ORDER_CANCEL_INFO_2ND_HALF =
        " .We can only give refunds in credit since we do not accept cash on the app. If your order has been started or completed, we may not be able to refund the order."

    const val MINIMUM_CREDIT_DOLLAR_AMOUNT = 0.50f
    const val INTERNET_ERROR_MESSAGE = "Please make sure you have an active internet connection!"

    const val PAYMENT_METHOD_WALLET = 2
    const val PAYMENT_METHOD_CREDIT_CARD = 1
}