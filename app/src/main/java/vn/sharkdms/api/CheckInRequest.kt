package vn.sharkdms.api

data class CheckInRequest(
    val userKhId: Int,
    var address: String?,
    var lat: String,
    var long: String,
    var image: String
)