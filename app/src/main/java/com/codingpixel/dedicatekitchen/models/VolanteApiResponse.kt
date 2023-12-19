package com.codingpixel.dedicatekitchen.models

import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import org.json.JSONObject
import java.io.Serializable

class VolanteApiResponse : Serializable {
    var error: ApiError = ApiError()
    var sessionId: String = ""

    //    @get:Element(name = "data")
//    @set:Element(name = "data")
    lateinit var data: String
    lateinit var dataObject: JSONObject
    var enum: ApplicationEnum? = null
}