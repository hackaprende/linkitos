package com.example.linkitos.api

sealed class ApiResponseStatus<out T> {
    class None<T>: ApiResponseStatus<T>()
    class Success<T>(val data: T): ApiResponseStatus<T>()
    class Loading<T>: ApiResponseStatus<T>()
    class Error<T>(val message: String): ApiResponseStatus<T>()
}