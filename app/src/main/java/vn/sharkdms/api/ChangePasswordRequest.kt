package vn.sharkdms.api

data class ChangePasswordRequest(val oldPassword: String, val newPassword: String)
