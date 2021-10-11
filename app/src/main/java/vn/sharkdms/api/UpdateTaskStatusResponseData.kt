package vn.sharkdms.api

data class UpdateTaskStatusResponseData(val id: Int, val taskName: String, val taskDescription: String,
    val userAssignedId: Int, val userAssignId: Int, val status: Int, val createdAt: String,
    val updatedAt: String, val userCompanyId: Int)
