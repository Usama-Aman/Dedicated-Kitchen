package com.codingpixel.dedicatekitchen.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import java.io.Serializable


//@Root(name = "session" , strict = false)
class Session : Serializable{

//    @get:Attribute(name = "id")
//    @set:Attribute(name = "id")
    var id: String = ""
}