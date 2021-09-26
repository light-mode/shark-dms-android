package vn.sharkdms.api

data class LoginResponseData(val token: String, val bhId: Int, val bhName: String,
    val bhPhone: String, val bhAvatar: String, val bhCreatedAt: String, val bhEmail: String,
    val bhPosition: String, val bhCompany: String)
