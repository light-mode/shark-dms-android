package vn.sharkdms.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.internal.LinkedTreeMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.sharkdms.api.BaseApi
import vn.sharkdms.api.CreateReportRequest
import vn.sharkdms.api.GetReportResponseData
import vn.sharkdms.api.UpdateReportRequest
import vn.sharkdms.util.Constant
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val baseApi: BaseApi) : ViewModel() {

    private val reportEventChannel = Channel<ReportEvent>()
    val reportEvent = reportEventChannel.receiveAsFlow()
    var reportId = 0
    var reportTitle = ""
    var reportDescription = ""

    fun getReport(token: String) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            try {
                val response = baseApi.getReport(authorization)
                val any = response.data
                if (any is List<*>) {
                    val list = any.filterIsInstance<GetReportResponseData>()
                    if (list.isEmpty()) {
                        reportEventChannel.send(ReportEvent.OnGetReportResponse(null))
                    } else {
                        val data = list[0]
                        reportId = data.id
                        reportTitle = data.title
                        reportDescription = data.description
                        reportEventChannel.send(ReportEvent.OnGetReportResponse(data))
                    }
                } else {
                    val linkedTreeMap = any as LinkedTreeMap<*, *>
                    val id = (linkedTreeMap["id"] as Double).toInt()
                    val title = linkedTreeMap["title"] as String
                    val description = linkedTreeMap["description"] as String
                    val userId = (linkedTreeMap["user_id"] as Double).toInt()
                    val createdAt = linkedTreeMap["created_at"] as String
                    val updatedAt = linkedTreeMap["updated_at"] as String
                    val data = GetReportResponseData(id, title, description, userId, createdAt,
                        updatedAt)
                    reportId = id
                    reportTitle = title
                    reportDescription = description
                    reportEventChannel.send(ReportEvent.OnGetReportResponse(data))
                }
            } catch (ste: SocketTimeoutException) {
                reportEventChannel.send(ReportEvent.OnFailure)
            }
        }
    }

    fun getReportAfterCreate(token: String) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            try {
                val response = baseApi.getReport(authorization)
                val any = response.data
                if (any !is List<*>) {
                    val linkedTreeMap = any as LinkedTreeMap<*, *>
                    reportId = (linkedTreeMap["id"] as Double).toInt()
                }
            } catch (ste: SocketTimeoutException) {
                reportEventChannel.send(ReportEvent.OnFailure)
            }
        }
    }

    fun createOrEditReport(token: String) {
        if (reportId == 0) {
            createReport(token)
        } else {
            editReport(token)
        }
    }

    private fun createReport(token: String) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            val body = CreateReportRequest(reportTitle, reportDescription)
            try {
                val response = baseApi.createReport(authorization, body)
                reportEventChannel.send(ReportEvent.OnCreateReportResponse(response.message))
            } catch (ste: SocketTimeoutException) {
                reportEventChannel.send(ReportEvent.OnFailure)
            }
        }
    }

    private fun editReport(token: String) {
        viewModelScope.launch {
            val authorization = Constant.TOKEN_PREFIX + token
            val body = UpdateReportRequest(reportId, reportTitle, reportDescription)
            try {
                val response = baseApi.editReport(authorization, body)
                reportEventChannel.send(ReportEvent.OnEditReportResponse(response.message))
            } catch (ste: SocketTimeoutException) {
                reportEventChannel.send(ReportEvent.OnFailure)
            }
        }
    }

    sealed class ReportEvent {
        data class OnGetReportResponse(val data: GetReportResponseData?) : ReportEvent()
        data class OnCreateReportResponse(val message: String) : ReportEvent()
        data class OnEditReportResponse(val message: String) : ReportEvent()
        object OnFailure : ReportEvent()
    }
}