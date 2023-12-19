package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import org.json.JSONObject
import java.io.Serializable


//@Root(name = "response", strict = false)
class ApiResponse : Serializable {
    //    @get:Element(name = "error")
//    @set:Element(name = "error")
    var error: ApiError = ApiError()
    var sessionId: String = ""

    //    @get:Element(name = "data")
//    @set:Element(name = "data")
    lateinit var data: ApiData
    lateinit var dataObject: JSONObject
    var enum: ApplicationEnum? = null
}