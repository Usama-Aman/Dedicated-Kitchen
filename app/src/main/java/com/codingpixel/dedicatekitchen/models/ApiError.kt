package com.codingpixel.dedicatekitchen.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import java.io.Serializable

//@Root(name = "error", strict = false)
class ApiError : Serializable{

//    @get:Attribute(name = "code")
//    @set:Attribute(name = "code")
    var code: String = "0" // 0 Success , Other than 0 is Error

//    @get:Attribute(name = "localizedDescription")
//    @set:Attribute(name = "localizedDescription")
    var localizedDescription: String = ""

//    @get:Attribute(name = "defaultDescription")
//    @set:Attribute(name = "defaultDescription")
    var defaultDescription: String = ""
}