package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.google.gson.JsonObject

class ApiStatus {
    private var status: String = ""
    internal var message: String = ""
    internal var applicationEnum: ApplicationEnum
    internal var resultObject: JsonObject? = null


    constructor(status: String, message: String, applicationEnum: ApplicationEnum) {
        this.status = status
        this.message = message
        this.applicationEnum = applicationEnum
    }

    constructor(
        status: String,
        message: String,
        applicationEnum: ApplicationEnum,
        resultObject: JsonObject?
    ) {
        this.status = status
        this.message = message
        this.resultObject = resultObject
        this.applicationEnum = applicationEnum
    }
}