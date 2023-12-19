package com.codingpixel.dedicatekitchen.helpers

import com.codingpixel.dedicatekitchen.models.*
import com.codingpixel.dedicatekitchen.models.local.FavouriteProduct
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class JsonManager {
    private val gson = Gson()

    companion object {

        private var ourInstance: JsonManager? =
            JsonManager()

        fun getInstance(): JsonManager {
            if (ourInstance == null)
                ourInstance =
                    JsonManager()
            return ourInstance as JsonManager
        }
    }

    fun getUser(jsonObject: JsonObject?, keyName: String = "result"): User? {
        return try {
            if (jsonObject != null && jsonObject.has(keyName) && jsonObject.getAsJsonObject(
                    keyName
                ) != null
            ) {
                gson.fromJson(jsonObject.getAsJsonObject(keyName), User::class.java)
            } else
                null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun getApiResponse(jsonObject: JsonObject): ApiResponseDb {
        return gson.fromJson(jsonObject, ApiResponseDb::class.java)
    }

    fun getVolanteApiResponse(jsonObject: JsonObject): ApiResponse {
        return gson.fromJson(jsonObject, ApiResponse::class.java)
    }

    fun getIntValue(jsonObject: JsonObject?, keyName: String): Int {
        return if (jsonObject != null && jsonObject.has(keyName))
            jsonObject.get(keyName).asInt
        else
            0
    }

    fun getBoolValue(jsonObject: JsonObject?, keyName: String): Boolean {
        return if (jsonObject != null && jsonObject.has(keyName))
            jsonObject.get(keyName).asBoolean
        else
            false
    }

    fun getString(jsonObject: JsonObject?, keyName: String): String {
        return if (jsonObject != null && jsonObject.has(keyName) && !jsonObject.get(keyName).isJsonNull)
            jsonObject.get(keyName).asString ?: ""
        else
            ""
    }

    fun getCardList(jsonObject: JsonObject?): List<CardModel> {
        val type = object : TypeToken<List<CardModel>>() {}.type
        return if (jsonObject != null && jsonObject.has("cards"))
            gson.fromJson(jsonObject.get("cards"), type)
        else
            listOf()
    }

    fun getPurchaseHistoryList(jsonObject: JsonObject?): List<PurchaseHistory> {
        val type = object : TypeToken<List<PurchaseHistory>>() {}.type
        return if (jsonObject != null && jsonObject.has("data"))
            gson.fromJson(jsonObject.get("data"), type)
        else
            listOf()
    }

    fun getOrders(jsonObject: JsonObject?): List<Order> {
        val type = object : TypeToken<List<Order>>() {}.type
        return if (jsonObject != null && jsonObject.has("data"))
            gson.fromJson(jsonObject.get("data"), type)
        else
            listOf()
    }

    fun getLocationsList(jsonObject: JsonObject?): List<UserLocation> {
        val type = object : TypeToken<List<UserLocation>>() {}.type
        return if (jsonObject != null && jsonObject.has("addresses"))
            gson.fromJson(jsonObject.get("addresses"), type)
        else
            listOf()
    }

    fun getNullableString(jsonObject: JsonObject, keyName: String): String? {
        return if (jsonObject.has(keyName))
            jsonObject.get(keyName).asString
        else
            ""
    }

    fun getAddedLocation(jsonObject: JsonObject?, keyName: String = "address"): UserLocation? {
        return try {
            if (jsonObject != null && jsonObject.has(keyName) && jsonObject.getAsJsonObject(
                    keyName
                ) != null
            ) {
                gson.fromJson(jsonObject.getAsJsonObject(keyName), UserLocation::class.java)
            } else
                null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAddedCard(jsonObject: JsonObject?, keyName: String = "card"): CardModel? {
        return try {
            if (jsonObject != null && jsonObject.has(keyName) && jsonObject.getAsJsonObject(
                    keyName
                ) != null
            ) {
                gson.fromJson(jsonObject.getAsJsonObject(keyName), CardModel::class.java)
            } else
                null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFavouriteListing(jsonObject: JsonObject?): List<FavouriteProduct> {
        val type = object : TypeToken<List<FavouriteProduct>>() {}.type
        return if (jsonObject != null && jsonObject.has("data"))
            gson.fromJson(jsonObject.get("data"), type)
        else
            listOf()
    }

    fun getCategoryImages_(jsonObject: JsonObject?): List<CategoryImage> {
        val type = object : TypeToken<List<CategoryImage>>() {}.type
        return if (jsonObject != null && jsonObject.has("data"))
            gson.fromJson(jsonObject.get("data"), type)
        else
            listOf()
    }

    fun getProductImages(jsonObject: JsonObject?): List<ProductImage> {
        val type = object : TypeToken<List<ProductImage>>() {}.type
        return if (jsonObject != null && jsonObject.has("data"))
            gson.fromJson(jsonObject.get("data"), type)
        else
            listOf()
    }

    fun getPackages(jsonObject: JsonObject?): List<PackageModel> {
        return try {
            val type = object : TypeToken<List<PackageModel>>() {}.type
            if (jsonObject != null && jsonObject.has("data"))
                gson.fromJson(jsonObject.get("data"), type)
            else
                listOf()
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }

    }

    fun getUserPackage(jsonObject: JsonObject?, keyName: String = "data"): PackageModel? {
        return try {
            if (jsonObject != null && jsonObject.has(keyName) && jsonObject.getAsJsonObject(
                    keyName
                ) != null
            ) {
                gson.fromJson(jsonObject.getAsJsonObject(keyName), PackageModel::class.java)
            } else
                null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}