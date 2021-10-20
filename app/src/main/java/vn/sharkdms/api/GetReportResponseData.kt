package vn.sharkdms.api

data class GetReportResponseData(val id: Int, val title: String, val description: String,
    val userId: Int, val createdAt: String, val updatedAt: String)
