package com.codingpixel.dedicatekitchen.helpers

import android.util.Log
import com.codingpixel.dedicatekitchen.models.OrderModel
import com.codingpixel.dedicatekitchen.models.local.RequestKeyValue
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.math.ln

class RequestBodyUtil {

    companion object {

        private fun getRequestBody(body: String): RequestBody {
            return body.toRequestBody("text/xml".toMediaTypeOrNull())
        }


        private fun getInnerParameters(params: ArrayList<RequestKeyValue>): String {
            var innerParams = ""
            for (i in 0 until params.size)
                innerParams =
                    innerParams + " " + "<${params[i].key} ${if (params[i].key == "session") "id" else if (params[i].key == "menuSet") "name" else "value"}=\"${params[i].keyValue}\" />"

            return innerParams
        }

        private fun getAuthParams(): String {
            return getInnerParameters(params =
            ArrayList<RequestKeyValue>().apply {
                add(RequestKeyValue().apply {
                    key = "user"
                    keyValue = Constants.USER_NAME_VOLANTE
                })
                add(RequestKeyValue().apply {
                    key = "password"
                    keyValue = Constants.PASSWORD_VOLANTE
                })
            })
        }

        private fun getUserInfoParam(customerCode: String, customerId: String): String {
            return getInnerParameters(params =
            ArrayList<RequestKeyValue>().apply {
                add(RequestKeyValue().apply {
                    key = "customerId"
                    keyValue = customerId
                })
                add(RequestKeyValue().apply {
                    key = "customerCode"
                    keyValue = customerCode
                })
            })
        }

        fun getSessionIdRequestBody(
        ): RequestBody {
            val innerParams = getInnerParameters(params =
            ArrayList<RequestKeyValue>().apply {
                add(RequestKeyValue().apply {
                    key = "user"
                    keyValue = Constants.USER_NAME_VOLANTE
                })
                add(RequestKeyValue().apply {
                    key = "password"
                    keyValue = Constants.PASSWORD_VOLANTE
                })
            })
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.GET_SESSION_ID}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.GENERAL_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${innerParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        private fun getCustomerParams(
            customerCode: String, customerCardCode: String,
            firstName: String, lastName: String, customerTypeId: String,
            email: String, password: String, phone: String
        ): String {
            return "<customer\n" +
                    "            customerCode='$customerCode'\n" +
                    "            customerCardCode='$customerCardCode'\n" +
                    "            alias='$firstName $lastName'\n" +
                    "            password='$password'\n" +
                    "            firstName='$firstName'\n" +
                    "            middleName=''\n" +
                    "            lastName='$lastName'\n" +
                    "            sex='m'\n" +
                    "            customerStatus='0'\n" +
                    "            address1=''\n" +
                    "            address2=''\n" +
                    "            city=''\n" +
                    "            prov=''\n" +
                    "            zipCode=''\n" +
                    "            country=''\n" +
                    "            dateOfBirth='2006-01-01 00:00:00'\n" +
                    "            homePhone=''\n" +
                    "            businessPhone=''\n" +
                    "            cellPhone='$phone'\n" +
                    "            fax=''\n" +
                    "            email='$email'\n" +
                    "            creditCardInformation=''\n" +
                    "            notes=''\n" +
                    "            dateCreated='2006-01-01 00:00:00'\n" +
                    "            startDate='2006-01-01 00:00:00'\n" +
                    "            expiryDate='2030-01-01 00:00:00'\n" +
                    "            lastModified='2006-01-01 00:00:00'\n" +
                    "            customerLevel='0'\n" +
                    "            customerGroups=''\n" +
                    "            customerTypeId='-20'\n" +
                    "            parentCustomerId='0'\n" +
                    "            custValue1=''\n" +
                    "            custValue2=''\n" +
                    "            custValue3=''\n" +
                    "            custValue4=''\n" +
                    "            custValue5=''\n" +
                    "            custValue6=''\n" +
                    "            custValue7=''\n" +
                    "            custValue8=''\n" +
                    "            custValue9=''\n" +
                    "            custValue10=''\n" +
                    "            custValueText1=''\n" +
                    "            customerSystemType='1'>\n" +
//                    "                <picturePath value=\"1000000\"/>\n" +
                    "                <useOverrideCreditLimit value=\"0\"/>\n" +
                    "                <overrideCreditLimitAmount value=\"0\"/>\n" +
                    "        </customer>"

//            return "<customer \npassword=\'${password}\' \nemail=\'${email}\' \ncustomerCode=\'${customerCode}\' \ncustomerCardCode=\'${customerCardCode}\' firstName=\'${firstName}\' lastName=\'${lastName}\' customerTypeId=\'${customerTypeId}\'  dateCreated='2020-12-12 00:00:00'\n" +
//                    "startDate='2020-12-12 00:00:00'\n" +
//                    "expiryDate='3000-01-01 00:00:00'\n" +
//                    "lastModified='2020-12-12 00:00:00'\nparentCustomerId='0'\n" +
//                    "custValue1=''\n" +
//                    "custValue2=''\n" +
//                    "custValue3=''\n" +
//                    "custValue4=''\n" +
//                    "custValue5=''\n" +
//                    "custValue6=''\n" +
//                    "custValue7=''\n" +
//                    "custValue8=''\n" +
//                    "custValue9=''\n" +
//                    "customerLevel='0'\n" +
//                    "customerGroups=''\n" +
//                    "custValue10=''\n" +
//                    "custValueText1=''\n" +
//                    "customerSystemType='1'>" +
//                    "<picturePath value=\'1000000\'/>\n" +
//                    "<useOverrideCreditLimit value=\'0\'/>\n" +
//                    "<overrideCreditLimitAmount value=\'0\'/>" +
//                    "</customer>"
        }

        fun getSignInRequestBody(customerCode: String): RequestBody {
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.GET_CUSTOMER_INFO}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">" +
                        "<parameters>\n" +
                        "<session id=\"$customerCode\"/>\n" +
                        "<user value=\"${Constants.USER_NAME_VOLANTE}\"/> <password value=\"${Constants.PASSWORD_VOLANTE}\"/>\n" +
                        "<customerCode value=\"${customerCode}\"/>\n" +
                        "</parameters>\n" +
                        "</request>"
            )
        }

        fun getSignUpRequestBody(
            sessionId: String, customerCode: String, customerCardCode: String,
            firstName: String, lastName: String, customerTypeId: String,
            email: String, password: String, phone: String
        ): RequestBody {

            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val authParams = getAuthParams()
            val customerParams = getCustomerParams(
                customerCode,
                customerCardCode,
                firstName,
                lastName,
                customerTypeId,
                email,
                password,
                phone
            )
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.CREATE_NEW_CUSTOMER}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + authParams.trim() + '\n' + customerParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }


        fun getUserInfoRequestBody(
            customerCode: String, customerId: String, sessionId: String
        ): RequestBody {

            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val customerParams = getUserInfoParam(customerCode, customerId)
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.GET_CUSTOMER_INFO}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + customerParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

//        private fun getUserDataParams(
//            customerCode: String, customerId: String
//        ): String {
//            return "<parameters\n" +
//                    "customerCode='$customerCode'\n" +
//                    "customerId='$customerId'\n" +
//                    "</parameters>"
//        }

        fun getMenuRequestBody(): RequestBody {
            val innerParams = getInnerParameters(params =
            ArrayList<RequestKeyValue>().apply {
                add(com.codingpixel.dedicatekitchen.models.local.RequestKeyValue().apply {
                    key = "user"
                    keyValue = Constants.USER_NAME_VOLANTE
                })
                add(com.codingpixel.dedicatekitchen.models.local.RequestKeyValue().apply {
                    key = "password"
                    keyValue = Constants.PASSWORD_VOLANTE
                })
                add(RequestKeyValue().apply {
                    key = "menuSet"
                    keyValue =
                        AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.menuSetName
                            ?: Constants.DEFAULT_MENU_SET
                })
                add(RequestKeyValue().apply {
                    key = "session"
                    keyValue = AppPreferenceManager.getValue(key = "sessionId")
                })
//                add(RequestKeyValue().apply {
//                    key = "downloadImage"
//                    keyValue = "true"
//                })
            })
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.GET_MENU}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.SET_UP_INFO_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
//                        "[<termIP value=\"130.000.0.060\"/>]\n"+
                        "<parameters>\n${innerParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }


        fun getOptionsRequestBody(): RequestBody {
            val innerParams = getInnerParameters(params =
            ArrayList<RequestKeyValue>().apply {
                add(com.codingpixel.dedicatekitchen.models.local.RequestKeyValue().apply {
                    key = "user"
                    keyValue = Constants.USER_NAME_VOLANTE
                })
                add(com.codingpixel.dedicatekitchen.models.local.RequestKeyValue().apply {
                    key = "password"
                    keyValue = Constants.PASSWORD_VOLANTE
                })
//                add(RequestKeyValue().apply {
//                    key = "downloadImage"
//                    keyValue = "true"
//                })
            })
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.GET_OPTIONS}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.SET_UP_INFO_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
//                        "[<termIP value=\"130.000.0.060\"/>]\n"+
                        "<parameters>\n${innerParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        fun geAccountBalanceRequestBody(): RequestBody {
            val innerParams = getInnerParameters(params =
            ArrayList<RequestKeyValue>().apply {
                add(RequestKeyValue().apply {
                    key = "session"
                    keyValue = AppPreferenceManager.getValue("sessionId")
                    // keyValue = "MOBILEAPP1605695276955"
                })
                add(RequestKeyValue().apply {
                    key = "accountReceivableId"
                    keyValue =
                        AppPreferenceManager.getUserFromSharedPreferences()!!.customer_account_id
                })
//                add(RequestKeyValue().apply {
//                    key = "downloadImage"
//                    keyValue = "true"
//                })
            })

            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.GET_ACCOUNT_BALANCE}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\" ${Constants.SESSION_ID}=\" ${Constants.SESSION_ID_VALUE}\">\n" +
//                        "[<termIP value=\"130.000.0.060\"/>]\n"+
                        "<parameters>\n${innerParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        fun getCustomerSummary(customerCode: String): RequestBody {
            val innerParams = getInnerParameters(params =
            ArrayList<RequestKeyValue>().apply {
                add(RequestKeyValue().apply {
                    key = "session"
                    keyValue = AppPreferenceManager.getValue("sessionId")
                })
                add(RequestKeyValue().apply {
                    key = "scanStr"
                    keyValue = customerCode
                })
            })

            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.GET_CUSTOMER_SUMMARY}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\" ${Constants.SESSION_ID}=\" ${Constants.SESSION_ID_VALUE}\">\n" +
                        "<parameters>\n${innerParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }


        fun authenticateCustomer(
            sessionId: String,
            email: String,
            customerCode: String
        ): RequestBody {
            val innerParams = getInnerParameters(params =
            ArrayList<RequestKeyValue>().apply {
                add(RequestKeyValue().apply {
                    key = "session"
                    keyValue = sessionId
                })
                add(RequestKeyValue().apply {
                    key = "emailAddress"
                    keyValue = email
                })
                add(RequestKeyValue().apply {
                    key = "customerCode"
                    keyValue = customerCode
                })
            })

            return getRequestBody(
                body = "<request ${Constants.xmlns}=\"${Constants.instance}\" ${Constants.xsd}=\"${Constants.instance2}\" ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.AUTHENTICATECUSTOMER}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\">\n" +
                        "<parameters>\n${innerParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        fun createTableRequestBody(
            sessionId: String,
            orderNumber: String,
            orderNotes: String,
            tableType: Int,
            saleType: Int
        ): RequestBody {
            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val tableParams = getCreateTableParams(
                tableType.toString(),
                "order$orderNumber",
                "true",
                "1",
                saleType.toString(),
                "false",
                AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
                    ?: Constants.DEFAULT_TERMINAL_ID,
                orderNotes
            )
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.CREATE_TABLE}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.API_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + tableParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        fun setCustomerId(
            sessionId: String,
            tableTransaction: String,
            customerId: String,
            transId: String
        ): RequestBody {
            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val tableParams = setCustomerIdParams(
                tableTransaction, customerId, transId
            )
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.SET_CUSTOMER_ID}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.API_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + tableParams.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        private fun getCreateTableParams(
            tableType: String,
            tableName: String,
            tempTable: String,
            headCount: String,
            saleType: String,
            printToKitchen: String,
            registerToTerm: String,
            notes: String
        ): String {
            return "<createTableTransaction\n" +
                    "            tableType='$tableType'\n" +
                    "            tableName='$tableName'\n" +
                    "            tempTable='$tempTable'\n" +
                    "            headCount='$headCount'\n" +
                    "            saleType='$saleType'\n" +
                    "            printToKitchen='$printToKitchen'\n" +
                    "            registerToTerm='$registerToTerm'\n" +
                    "            notes='$notes'\n" +
                    "        />"
        }

        private fun setCustomerIdParams(
            tableTransaction: String,
            customerId: String,
            transId: String
        ): String {
            return "<tableTransaction id='$tableTransaction'\n" +
                    "            customerId='$customerId'\n" +
                    "            transId='$transId'\n" +
                    "        />"
        }

        fun addItemsToTransaction(
            sessionId: String,
            orderArray: ArrayList<OrderModel>,
            tableTransactionId: String,
            name: String,
            phoneNumber: String,
            orderDateTime: String,
            orderType: String,
            addressList: List<String>,
            isDelivery: Boolean
        ): RequestBody {
            val seatNumber = "1"
            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val seat = "<seat seatNumber=\"${seatNumber}\">"

            val tableTrans = getTableTrans(tableTransactionId)
            val menuItemsParams = addItemsToTransactionMenuItems(orderArray)
            val userInfo = addUserAndOrderInfo(name, phoneNumber, orderDateTime, orderType)
            val addressInfo = addUserAndOrderInfoWithAddress(addressList)
            val endingTags = endingTags()
            return if (isDelivery) {
                getRequestBody(
                    body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.ADD_ITEMS_TO_TRANS}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.API_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                            "<parameters>\n${sessionIdParam.trim() + '\n' + tableTrans.trim() + '\n' + seat.trim() + '\n' + menuItemsParams.trim() + '\n' + userInfo.trim() + '\n' + addressInfo.trim() + '\n' + endingTags.trim()}\n</parameters>\n" +
                            "</request>"
                )
            } else {
                getRequestBody(
                    body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.ADD_ITEMS_TO_TRANS}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.API_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                            "<parameters>\n${sessionIdParam.trim() + '\n' + tableTrans.trim() + '\n' + seat.trim() + '\n' + menuItemsParams.trim() + '\n' + userInfo.trim() + '\n' + endingTags.trim()}\n</parameters>\n" +
                            "</request>"
                )
            }

        }

        private fun addUserAndOrderInfo(
            name: String,
            phoneNumber: String,
            orderDateTime: String,
            orderType: String
        ): String {
            val quantity = "1"
            val nameId = "20000727"
            val phoneId = "20000729"
            val timeId = "20000730"
            val orderTypeId = "20000731"
            return "<item\n" +
                    "            menuItemId='$nameId'\n" +
                    "            quantity= '$quantity'\n" +
                    "            note='$name'\n" +
                    "></item>\n" +
                    "<item\n" +
                    "            menuItemId='$phoneId'\n" +
                    "            quantity= '$quantity'\n" +
                    "            note='$phoneNumber'\n" +
                    "></item>\n" +
                    "<item\n" +
                    "            menuItemId='$timeId'\n" +
                    "            quantity= '$quantity'\n" +
                    "            note='$orderDateTime'\n" +
                    "></item>\n" +
                    "<item\n" +
                    "            menuItemId='$orderTypeId'\n" +
                    "            quantity= '$quantity'\n" +
                    "            note='$orderType'\n" +
                    "></item>\n"
        }

        private fun addUserAndOrderInfoWithAddress(
            addressList: List<String>
        ): String {
            var result = ""
            val quantity = "1"
            val addressId = "20000728"
            for (i in addressList.indices) {
                val address: String = addressList[i]
                result += "<item  " + "  menuItemId='$addressId'" + "  quantity= '$quantity'" +
                        "            note='$address'\n" +
                        "></item>\n"
            }
            return result
        }

        private fun endingTags(): String {
            return "</seat>\n" +
                    "</tableTrans>"
        }

        private fun getTableTrans(tableTransactionId: String): String {
//            val printAsTermId = "20000007"
            return "<tableTrans\n" +
                    "            tableTransId='$tableTransactionId'\n" +
                    "            printAsTermId='${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId ?: Constants.DEFAULT_TERMINAL_ID}'\n" +
                    "        >\n"
        }

        private fun addItemsToTransactionMenuItems(
            orderArray: ArrayList<OrderModel>
        ): String {
            var menuItems = ""
            var extraOptions = ""
            for (i in 0 until orderArray.size) {
                extraOptions = ""
                val menuItemId = orderArray[i].item_id
                val quantity = orderArray[i].quantity
                //below we add note of each item
                val note = orderArray[i].notes

                orderArray[i].options.forEachIndexed { _, extraItemOrderModel ->
                    extraOptions += "<option\n" +
                            "            menuItemId='${extraItemOrderModel.extra_option_id}'\n" +
                            "            quantity='${extraItemOrderModel.quantity}'\n" +
                            "        />\n"
                }

//                for (j in 0 until orderArray[i].options.size) {
//                    val extraItemId = orderArray[i].options[j].extra_option_id
//                    val extraItemQuantity = "1"
//                    extraOptions += "<option\n" +
//                            "            menuItemId='$extraItemId'\n" +
//                            "            quantity='$extraItemQuantity'\n" +
//                            "        />\n"
//
//                }
                menuItems += "<item\n" +
                        "            menuItemId='$menuItemId'\n" +
                        "            quantity='$quantity'\n" +
                        "            note='$note'\n" +
                        ">" +
                        "<options>\n${extraOptions.trim()}\n</options>\n" +
                        "</item>\n"
            }
            return menuItems
        }

        fun getTable(
            sessionId: String, tableTransactionId: String
        ): RequestBody {

            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val tableTransaction = "<tableTransaction value=\"${tableTransactionId}\"/>"
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.GET_TABLE}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.API_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + tableTransaction.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        fun processPayment(
            sessionId: String, tableTransactionId: String, bill: String,
            tip: String = "0",
            closeTable: Boolean = false
        ): RequestBody {
//            val paymentTerId = "132000060"
//            val shouldCloseTable = if (closeTable)
//                "closeTable/>"
//            else
//                ""
            val shouldCloseTable = "<closeTable/>"
            val paymentTerId =
                AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId
                    ?: Constants.DEFAULT_TERMINAL_ID
            Log.d("PaymentTerminalID", paymentTerId)
            val customerCode_ =
                AppPreferenceManager.getUserFromSharedPreferences()?.customer_code ?: ""
            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val tableTransaction = "<tableTransId value=\"${tableTransactionId}\"/>"
            val customerCode = "<customerCode value=\"${customerCode_}\"/>"
            val transId = "<transId value=\"${tableTransactionId}\"/>"
            val amount = "<amount value=\"${bill}\"/>"

            val tip = "<tip value=\"${tip}\" />"
            val paymentTerminalId =
                "<paymentTerminalId value=\"${paymentTerId}\"/>\n<disallowPartialPayment />\n$shouldCloseTable"
            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.PROCESS_PAYMENT}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${
                            sessionIdParam.trim() + '\n' + customerCode.trim() + '\n' + tableTransaction.trim() + transId.trim() + '\n'
                                    + amount.trim() + '\n' +
                                    tip.trim() + '\n' +
                                    paymentTerminalId.trim()
                        }\n</parameters>\n" +
                        "</request>"
            )
        }

        private fun payment(
            amount: String,
            cardBrand: String,
            id: String,
            tip: String = "0"
        ): String {
            val paymentProcessor = "MobileWallet"
            var shouldCloseTable: Boolean = true
            var paymentType = ""
            var closeTableTags = "<closeTable/>\n" +
                    "<voidTransIfFailed/>"
            if (cardBrand == "On-Account-Tip") {
                shouldCloseTable = false
                paymentType = "On-Account-Tip"
                closeTableTags = ""
            } else {
                shouldCloseTable = true
                closeTableTags = "<closeTable/>\n" +
                        "<voidTransIfFailed/>"
                paymentType = "Mobile ${cardBrand.toUpperCase()}"
            }
            return "<payment\n" +
                    "            paymentProcessor='$paymentProcessor'\n" +
                    "            amount='$amount'\n" +
                    "            tip='$tip'\n" +
                    "            paymentType='$paymentType'\n" +
                    "            referenceId='$id'\n" +
                    "        />\n" +
                    "$closeTableTags"

        }

        fun addOfflineGiftCardPayment(
            id: String, amount: String, tableTransactionId: String, cardBrand: String,
            tip: String = "0.0"
        ): RequestBody {
            val sessionId = AppPreferenceManager.getValue("sessionId")
            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val tableTransaction = "<tableTransaction id=\"${tableTransactionId}\"/>"
            val transaction = "<transaction id=\"${tableTransactionId}\"/>"
            val payment = payment(amount, cardBrand, id, tip)

            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.ADD_OFFLINE_GIFT_PAYMENT}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.API_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + tableTransaction.trim() + '\n' + transaction.trim() + '\n' + payment.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        fun addAccountTransaction(
            amount_: String
        ): RequestBody {
            val sessionId = AppPreferenceManager.getValue("sessionId")
            val customerIdd = AppPreferenceManager.getUserFromSharedPreferences()?.customer_id ?: ""
            val customerAccountId =
                AppPreferenceManager.getUserFromSharedPreferences()?.customer_account_id ?: ""

            val note = "v_auto"
            val auth = "2"
            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val customerId = "<customerId value=\"${customerIdd}\"/>"
            val accountId = "<accountId value=\"${customerAccountId}\"/>"
            val amount = "<amount value=\"${amount_}\"/>"
            val authType = "<authType value=\"${auth}\"/>"
            val notes = "<notes value=\"${note}\"/>"

            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.ADD_ACCOUNT_TRANSACTION}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + customerId.trim() + '\n' + accountId.trim() + '\n' + amount.trim() + '\n' + authType.trim() + '\n' + notes.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        fun updateUserInfo(
            pos_package_id: String
        ): RequestBody {
            val sessionId = AppPreferenceManager.getValue("sessionId")
            val user_ = Constants.USER_NAME_VOLANTE
            val password_ = Constants.PASSWORD_VOLANTE
            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val user = "<user value=\"${user_}\"/>"
            val password = "<password value=\"${password_}\"/>"
            val customer = Customer_(pos_package_id)

            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.UPDATE_CUSTOMER_INFO}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.CRM_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + user.trim() + '\n' + password.trim() + '\n' + customer.trim()}\n</parameters>\n" +
                        "</request>"
            )
        }

        private fun Customer_(
            pos_package_id: String
        ): String {
            val customerId_ = AppPreferenceManager.getUserFromSharedPreferences()!!.customer_id
            return "<customer\n" +
                    "            customerId='$customerId_'\n" +
                    "            customerTypeId='$pos_package_id'\n" +
                    "        />"
        }

        fun createDataCandyProfileRequestBody(
            cardId: String, firstName: String, lastName: String,
            phone: String, email: String, address: String, password: String
        ): RequestBody {
            val json = JSONObject()
            json.apply {
                put("application_key", Constants.DC_APP_KEY)
                put("accesskey", Constants.DC_ACCESS_KEY)
                put("card_id", cardId)
                put("name_first", firstName)
                put("name_last", lastName)
                put("phone", phone)
                put("address", address)
                put("email", email)
                put("password", password)
                put("is_complete", "y")
            }
            return json.toString()
                .toRequestBody(Constants.MEDIA_TYPE_PARSE.toMediaTypeOrNull())
        }

        fun getLoyaltyCardIdRequestBody(
        ): RequestBody {
            val json = JSONObject()
            json.apply {
                put("application_key", Constants.DC_APP_KEY)
                put("accesskey", Constants.DC_ACCESS_KEY)
                put("card_format_id", Constants.CARD_FORMAT_ID)
            }
            return json.toString()
                .toRequestBody(Constants.MEDIA_TYPE_PARSE.toMediaTypeOrNull())
        }

        fun getLoyaltyCardIdUrl(
        ): String {
            return "${ApiUrls.DATA_CANDY_AUTH_URL}/api2/cardcreation/eloyalty?application_key=${Constants.DC_APP_KEY}&accesskey=${Constants.DC_ACCESS_KEY}&card_format_id=${Constants.CARD_FORMAT_ID}"
        }

        fun getDataCandyCreateProfileUrl(
            cardId: String,
            firstName: String,
            lastName: String,
            phone: String,
            email: String,
            password: String,
            address: String = "",
            city: String = "",
            zip: String = "",
            state: String = ""
        ): String {
            // phone=$phone
            // &postal_code=$zip
            return "${ApiUrls.DATA_CANDY_AUTH_URL}/api2/profile?application_key=${Constants.DC_APP_KEY}&accesskey=${Constants.DC_ACCESS_KEY}&card_id=$cardId&name_first=$firstName&name_last=$lastName&email=$email&password=$password&lang=en&address=$address&country_id=CA&city=$city&phone=$phone&region_id=$state&is_complete=y"

        }


        fun updateDataCandyProfileUrl(
            cardId: String,
            firstName: String,
            lastName: String,
            phone: String
        ): String {
            // phone=$phone
            return "${ApiUrls.DATA_CANDY_AUTH_URL}/api2/profile?application_key=${Constants.DC_APP_KEY}&accesskey=${Constants.DC_ACCESS_KEY}&card_id=$cardId&name_first=$firstName&name_last=$lastName&contact_id=${AppPreferenceManager.getUserFromSharedPreferences()?.dc_contact_id ?: ""}"

        }


        fun updateDataCandyPasswordUrl(
            cardId: String,
            password: String
        ): String {
            // phone=$phone
            return "${ApiUrls.DATA_CANDY_AUTH_URL}/api2/profile?application_key=${Constants.DC_APP_KEY}&accesskey=${Constants.DC_ACCESS_KEY}&card_id=$cardId&password=$password&contact_id=${AppPreferenceManager.getUserFromSharedPreferences()?.dc_contact_id ?: ""}"

        }

        fun updateDataCandyCreateProfileAddressUrl(
            cardId: String,
            address: String = "",
            city: String = "",
            zip: String = "",
            state: String = ""
        ): String {
            return "${ApiUrls.DATA_CANDY_AUTH_URL}/api2/profile?application_key=${Constants.DC_APP_KEY}&accesskey=${Constants.DC_ACCESS_KEY}&card_id=$cardId&lang=en&address=$address&country_id=CA&city=$city&postal_code=$zip&region_id=$state&contact_id=${AppPreferenceManager.getUserFromSharedPreferences()?.dc_contact_id ?: ""}"

        }

        fun getDataCandyLoyaltyPointsURl(
            cardId: String
        ): String {
            // phone=$phone
            return "${ApiUrls.DATA_CANDY_LOYALTY_URL}/ms2v2/trx/${Constants.DC_ACCESS_KEY}/fit/?MID=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantID ?: Constants.DC_MID}&MPW=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantPassword ?: Constants.DC_MPW}&PRG=loy&TRX=bal&VER=${Constants.DC_VER}&LNG=en&WAN=${Constants.DC_WAN}&WSN=${Constants.DC_WSN}&CID=$cardId"

        }

        fun getAddDataCandyPointsURl(
            cardId: String, amount: String,
            RAM: String = "0",
            inv: String = ""
        ): String {
            // phone=$phone

            return "${ApiUrls.DATA_CANDY_LOYALTY_URL}/ms2v2/trx/${Constants.DC_ACCESS_KEY}/fit/?MID=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantID ?: Constants.DC_MID}&MPW=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantPassword ?: Constants.DC_MPW}&PRG=loy&TRX=acc&VER=${Constants.DC_VER}&LNG=en&WAN=${Constants.DC_WAN}&WSN=${Constants.DC_WSN}&CID=$cardId&AMT=$amount&RAM=$RAM&DAM=0&INV=$inv&ACT=1&PTS=1&IMI=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantName ?: Constants.DC_MERCHANT_NAME}"

        }

        fun getRedeemDataCandyPointsURl(
            cardId: String, amount: String,
            redemablePoints: String = "350.00",
            inv: String = ""
        ): String {
            // phone=$phone
            return "${ApiUrls.DATA_CANDY_LOYALTY_URL}/ms2v2/trx/${Constants.DC_ACCESS_KEY}/fit/?MID=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantID ?: Constants.DC_MID}&MPW=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantPassword ?: Constants.DC_MPW}&PRG=loy&TRX=red&VER=${Constants.DC_VER}&LNG=en&WAN=${Constants.DC_WAN}&WSN=${Constants.DC_WSN}&CID=$cardId&AMT=$amount&INV=$inv&IMI=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantName ?: Constants.DC_MERCHANT_NAME}"

        }

        fun getComitDataCandyPointsURl(
            cardId: String, tcr: String
        ): String {
            // phone=$phone
            return "${ApiUrls.DATA_CANDY_LOYALTY_URL}/ms2v2/trx/${Constants.DC_ACCESS_KEY}/fit/?MID=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantID ?: Constants.DC_MID}&MPW=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantPassword ?: Constants.DC_MPW}&PRG=loy&TRX=cmt&VER=${Constants.DC_VER}&LNG=en&WAN=${Constants.DC_WAN}&WSN=${Constants.DC_WSN}&CID=$cardId&TCR=$tcr&IMI=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantName ?: Constants.DC_MERCHANT_NAME}"

        }

        fun getAddDataCandyPointsRequestBody(
            packageId: Int, packagePosId: String,
            amount: String
        ): RequestBody {
            return getRequestBody(
                body = "<items>\n" +
                        "\n" +
                        "<item id=\"${System.currentTimeMillis()}\">\n" +
                        "<upc>$packagePosId</upc>\n" +
                        "<ipc>$packageId</ipc>\n" +
                        "<qty>1</qty>\n" +
                        "<amt>$amount</amt> \n" +
                        "<eligible_for_accumulation>true</eligible_for_accumulation>\n" +
                        "</item>\n" +
                        "\n" +
                        "</items>"
            )
        }


        fun getAddDataCandyPointsCheckOutRequestBody(
            orderItems: ArrayList<OrderModel>
        ): RequestBody {

            var itemString = ""
            for (item in orderItems) {
                var extraOptionsPrice = 0F
                for (i in 0 until item.options.size) {
                    extraOptionsPrice += item.options[i].price.toFloat()
                }
                itemString =
                    itemString + "<item id=\"${System.currentTimeMillis()}${item.item_id}\">\n" +
                            "<upc>${item.item_id}</upc>\n" +
                            "<ipc>${item.item_id}</ipc>\n" +
                            "<qty>${item.quantity}</qty>\n" +
                            "<amt>${((item.quantity.toFloat()) * ((item.price.toFloat() + extraOptionsPrice)))}</amt> \n" +
                            "<eligible_for_accumulation>true</eligible_for_accumulation>\n" +
                            "</item>\n" +
                            "\n"
            }

            itemString = "<items>\n" + itemString + "\n" +
                    "</items>"
            return getRequestBody(
                body = itemString
            )
        }


        fun getDiscountRequestBody(
            tableTransactionId: String,
            absolutePrice: String
        ): RequestBody {
            val sessionId = AppPreferenceManager.getValue("sessionId")
            val user_ = Constants.USER_NAME_VOLANTE
            val password_ = Constants.PASSWORD_VOLANTE
            val sessionIdParam = "<session id=\"${sessionId}\"/>"
            val user = "<user value=\"${user_}\"/>"
            val password = "<password value=\"${password_}\"/>"

            return getRequestBody(
                body = "<request ${Constants.API_VERSION_KEY}=\"${Constants.API_VERSION_KEY_VALUE}\" ${Constants.API_KEY}=\"${Constants.API_KEY_CRM_VALUE}\" ${Constants.CLIENT_ID_KEY}=\"${Constants.CLIENT_ID_CRM_KEY_VALUE}\" ${Constants.API_NAME_KEY}=\"${ApiUrls.DISCOUNT_VOLANTE}\" ${Constants.API_CATEGORY_KEY}=\"${ApiUrls.API_CATEGORY}\" ${Constants.LOCAL_KEY}=\"${Constants.LOCAL_KEY_VALUE}\" ${Constants.REQUEST_TIMESTAMP_KEY}=\"${Constants.REQUEST_TIMESTAMP_KEY_VALUE}\">\n" +
                        "<parameters>\n${sessionIdParam.trim() + '\n' + user.trim() + '\n' + password.trim() + '\n' + "<tableTransaction id=\"${tableTransactionId}\"/>" + '\n' + "<transaction id=\"${tableTransactionId}\"/>" + '\n' + "<priceMod id=\"${Constants.REWARD_PRICE_MODIFIER}\" absolute=\"-$absolutePrice\" percentage=\"-0.0\" />"}\n</parameters>\n" +
                        "</request>"
            )
        }


    }
}