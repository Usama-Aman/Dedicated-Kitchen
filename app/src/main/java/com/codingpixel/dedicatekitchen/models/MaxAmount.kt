package com.codingpixel.dedicatekitchen.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

//@Root(name = "maxAmount", strict = false)
class MaxAmount : Serializable {

//    @get:Attribute(name = "value")
//    @set:Attribute(name = "value")
    var value : String = ""
}