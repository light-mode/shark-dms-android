package vn.sharkdms.api

data class BaseResponse<T>(val code: String, val status: String, val message: String, val data: T)