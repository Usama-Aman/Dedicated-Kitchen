package com.codingpixel.dedicatekitchen.updates.ApiResponse

import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum


data class Resource<out T>(val status: ApiStatus, val data: T?, val message: String?, val enum: ApplicationEnum?) {

    companion object {

        fun <T> success(data: T?, enum: ApplicationEnum): Resource<T> {
            return Resource(ApiStatus.SUCCESS, data, null, enum)
        }

        fun <T> error(msg: String, data: T?, enum: ApplicationEnum): Resource<T> {
            return Resource(ApiStatus.ERROR, data, msg, enum)
        }

        fun <T> loading(enum : ApplicationEnum? = null): Resource<T> {
            return Resource(ApiStatus.LOADING, null, null, enum)
        }

    }

}