package com.codingpixel.dedicatekitchen.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "group", strict = false)
class KitchenGroup() {

    @get:Attribute(name = "ID")
    @set:Attribute(name = "ID")
    var id: String = ""

    @get:Attribute(name = "name")
    @set:Attribute(name = "name")
    var name: String = ""


    @get:Attribute(name = "masterGID")
    @set:Attribute(name = "masterGID")
    var masterGid: String = ""

//    @get:ElementList(name = "categories")
//    @set:ElementList(name = "categories")
    lateinit var categories : ArrayList<Category>


}