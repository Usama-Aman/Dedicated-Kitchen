package com.codingpixel.dedicatekitchen.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

//@Root(name = "customer", strict = false)
class User : Serializable {
    var customerId: String = ""
    var id: Int = 0
    var stripe_id: String = ""
    var fname: String = ""
    var lname: String = ""
    var email: String = ""
    var name: String = ""
    var user_type: String = ""
    var customer_code: String = ""
    var customer_id: String = ""
    var customer_account_id: String = ""
    var phone: String? = ""

    var dc_contact_id: String? = "" // Data Candy Contact ID
    var dc_card_id: String? = "" // Data Candy Card ID


}