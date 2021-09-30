package vn.sharkdms.api

import com.google.gson.annotations.SerializedName

data class LoginResponseData(val token: String, val id: Int, val name: String, val phone: String,
    val avatar: String, val createdAt: String, val email: String, val position: String,
    val company: String, @SerializedName("roleId") val roleId: Int,
    @SerializedName("roleName") val roleName: String)
