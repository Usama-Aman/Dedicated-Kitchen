package com.codingpixel.dedicatekitchen.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable

//@Root(name = "data", strict = false)
class ApiData : Serializable{

//    @get:Element(name = "session", required = false)
//    @set:Element(name = "session", required = false)
    lateinit var session: Session

//    @get:Element(name = "customer", required = false)
//    @set:Element(name = "customer", required = false)
    lateinit var customer: User

//    @get:ElementList(name = "groups", required = false)
//    @set:ElementList(name = "groups", required = false)
//    lateinit var groups: ArrayList<KitchenGroup>

}