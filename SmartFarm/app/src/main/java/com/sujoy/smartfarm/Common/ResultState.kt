package com.sujoy.smartfarm.Common

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error<out T>(val message: String) : ResultState<T>()
    data object Loading : ResultState<Nothing>()
}