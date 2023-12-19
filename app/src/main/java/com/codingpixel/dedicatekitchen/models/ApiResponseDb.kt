package com.codingpixel.dedicatekitchen.models

class ApiResponseDb {
    var status: String = ""
    var message: String = ""
    var result: Any? = null


    constructor(
        status: String = "",
        result: Boolean = false,
        message: String = ""
    ) {
        this.result = result
        this.status = status
        this.message = message
    }
}