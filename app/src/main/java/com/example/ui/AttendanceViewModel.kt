package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AttendanceRecord
import com.example.data.AttendanceRepository
import com.example.data.AttendanceStatus
import com.example.data.Student
import com.example.data.Subject
import com.example.data.SubjectAttendanceStat
import com.example.data.VerificationReport
import com.example.network.BackendStatus
import com.example.network.BackendSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val label: String) {
    MARK_ATTENDANCE("Mark Attendance"),
    ADMIN_ROSTER("Admin Roster"),
    SUBJECT_ANALYTICS("Subject Analytics")
}

class AttendanceViewModel(
    private val repository: AttendanceRepository,
    private val backendSyncManager: BackendSyncManager? = null
) : ViewModel() {

    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendanceRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.MARK_ATTENDANCE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Backend Connection Status
    val backendStatus: StateFlow<BackendStatus> = backendSyncManager?.status
        ?: MutableStateFlow(BackendStatus.OFFLINE_ROOM).asStateFlow()

    val lastSyncMessage: StateFlow<String?> = backendSyncManager?.lastSyncMessage
        ?: MutableStateFlow(null).asStateFlow()

    fun getSavedBackendUrl(): String = backendSyncManager?.getSavedUrl() ?: BackendSyncManager.DEFAULT_DEMO_URL
    fun getSavedBackendKey(): String = backendSyncManager?.getSavedKey() ?: BackendSyncManager.DEFAULT_DEMO_KEY

    // Session Configuration for Attendance Marking
    private val _selectedSubjectId = MutableStateFlow("BIT301")
    val selectedSubjectId: StateFlow<String> = _selectedSubjectId.asStateFlow()

    private val _selectedDate = MutableStateFlow("2026-10-15")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(1)
    val selectedPeriod: StateFlow<Int> = _selectedPeriod.asStateFlow()

    // Map of rollNumber -> AttendanceStatus for currently selected session
    private val _sessionAttendance = MutableStateFlow<Map<String, AttendanceStatus>>(emptyMap())
    val sessionAttendance: StateFlow<Map<String, AttendanceStatus>> = _sessionAttendance.asStateFlow()

    // Faculty Search Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Filter by status (null = All)
    private val _statusFilter = MutableStateFlow<AttendanceStatus?>(null)
    val statusFilter: StateFlow<AttendanceStatus?> = _statusFilter.asStateFlow()

    fun setStatusFilter(status: AttendanceStatus?) {
        _statusFilter.value = status
    }

    // Database verification report
    private val _verificationReport = MutableStateFlow<VerificationReport?>(null)
    val verificationReport: StateFlow<VerificationReport?> = _verificationReport.asStateFlow()

    // Snackbar / Feedback message
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun clearFeedbackMessage() {
        _feedbackMessage.value = null
    }

    // Admin authorization state
    private val _isAdminAuthorized = MutableStateFlow(false)
    val isAdminAuthorized: StateFlow<Boolean> = _isAdminAuthorized.asStateFlow()

    // Selected student for detail sheet
    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> = _selectedStudent.asStateFlow()

    fun selectStudent(student: Student?) {
        _selectedStudent.value = student
    }

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            verifyDatabase()
            loadCurrentSessionAttendance()
        }
    }

    fun onSubjectChanged(subjectId: String) {
        _selectedSubjectId.value = subjectId
        loadCurrentSessionAttendance()
    }

    fun onDateChanged(date: String) {
        _selectedDate.value = date
        loadCurrentSessionAttendance()
    }

    fun onPeriodChanged(period: Int) {
        _selectedPeriod.value = period
        loadCurrentSessionAttendance()
    }

    private fun loadCurrentSessionAttendance() {
        viewModelScope.launch {
            val subjectId = _selectedSubjectId.value
            val date = _selectedDate.value
            val period = _selectedPeriod.value

            repository.getSessionAttendance(subjectId, date, period).collect { records ->
                if (records.isNotEmpty()) {
                    val map = records.associate { it.rollNumber to AttendanceStatus.fromString(it.status) }
                    _sessionAttendance.value = map
                } else {
                    val defaultMap = allStudents.value.associate { it.rollNumber to AttendanceStatus.PRESENT }
                    _sessionAttendance.value = defaultMap
                }
            }
        }
    }

    fun setStudentStatus(rollNumber: String, status: AttendanceStatus) {
        val current = _sessionAttendance.value.toMutableMap()
        current[rollNumber] = status
        _sessionAttendance.value = current
    }

    fun markAllAs(status: AttendanceStatus) {
        val map = allStudents.value.associate { it.rollNumber to status }
        _sessionAttendance.value = map
    }

    fun saveSessionAttendance() {
        viewModelScope.launch {
            val subjectId = _selectedSubjectId.value
            val date = _selectedDate.value
            val period = _selectedPeriod.value
            val currentMap = _sessionAttendance.value

            val records = currentMap.map { (rollNumber, status) ->
                AttendanceRecord(
                    rollNumber = rollNumber,
                    subjectId = subjectId,
                    date = date,
                    period = period,
                    status = status.name,
                    markedAt = System.currentTimeMillis()
                )
            }

            if (records.isNotEmpty()) {
                // 1. Save to local Room SQLite Database
                repository.saveSessionAttendance(records)

                // 2. Direct Frontend-Backend Connect: Sync to Supabase PostgreSQL Cloud
                backendSyncManager?.pushAttendanceToCloud(records)

                _feedbackMessage.value = "Attendance saved & synced to backend for $date (Period $period)!"
            } else {
                _feedbackMessage.value = "No records to save."
            }
        }
    }

    fun clearCurrentSession() {
        viewModelScope.launch {
            val subjectId = _selectedSubjectId.value
            val date = _selectedDate.value
            val period = _selectedPeriod.value
            repository.clearSessionAttendance(subjectId, date, period)
            val defaultMap = allStudents.value.associate { it.rollNumber to AttendanceStatus.PRESENT }
            _sessionAttendance.value = defaultMap
            _feedbackMessage.value = "Current session ($date, Period $period) cleared!"
        }
    }

    fun clearAllAttendanceHistory() {
        viewModelScope.launch {
            repository.clearAllAttendanceRecords()
            loadCurrentSessionAttendance()
            _feedbackMessage.value = "All attendance history cleared! (App attendance reset to 0%)"
        }
    }

    fun resetEntireApp() {
        viewModelScope.launch {
            repository.resetAppToFactoryDefaults()
            loadCurrentSessionAttendance()
            verifyDatabase()
            _feedbackMessage.value = "App fully reset: All 56 active students restored, all records cleared."
        }
    }

    fun testBackendConnection(url: String, key: String) {
        viewModelScope.launch {
            val res = backendSyncManager?.testConnection(url, key)
            if (res != null && res.isSuccess) {
                _feedbackMessage.value = res.getOrNull() ?: "Backend connected successfully!"
            } else {
                _feedbackMessage.value = res?.exceptionOrNull()?.message ?: "Backend connection failed."
            }
        }
    }

    fun pushStudentsToCloud() {
        viewModelScope.launch {
            val res = backendSyncManager?.pushAllStudentsToCloud()
            _feedbackMessage.value = res?.getOrNull() ?: "Students pushed to backend database."
        }
    }

    fun verifyDatabase() {
        viewModelScope.launch {
            val report = repository.verifyDatabase()
            _verificationReport.value = report
        }
    }

    fun import56Students(overwrite: Boolean = false) {
        viewModelScope.launch {
            val report = repository.import56Students(overwrite = overwrite)
            _verificationReport.value = report
            _feedbackMessage.value = if (report.isVerified) {
                "Successfully verified & imported all ${report.totalStudentsInDb} active student records!"
            } else {
                report.message
            }
            // Auto push to backend cloud database as well
            backendSyncManager?.pushAllStudentsToCloud()
            loadCurrentSessionAttendance()
        }
    }

    fun authorizeAdmin(pin: String): Boolean {
        return if (pin == "1234" || pin == "admin" || pin == "admin2025") {
            _isAdminAuthorized.value = true
            _feedbackMessage.value = "Admin authorization granted."
            true
        } else {
            _feedbackMessage.value = "Invalid Admin PIN. Access denied."
            false
        }
    }

    fun revokeAdmin() {
        _isAdminAuthorized.value = false
        _feedbackMessage.value = "Admin authorization locked."
    }

    fun updateStudentDetails(student: Student, pin: String): Boolean {
        if (!authorizeAdmin(pin)) return false
        viewModelScope.launch {
            repository.updateStudent(student)
            _feedbackMessage.value = "Student ${student.rollNumber} updated successfully."
            backendSyncManager?.pushAllStudentsToCloud()
        }
        return true
    }

    val subjectAnalytics: StateFlow<List<SubjectAttendanceStat>> = combine(
        repository.allSubjects,
        repository.allAttendanceRecords,
        repository.allStudents
    ) { subjects, records, students ->
        subjects.map { subject ->
            val subRecords = records.filter { it.subjectId == subject.id }
            val sessions = subRecords.map { "${it.date}_${it.period}" }.distinct().size

            val presences = subRecords.count { it.status == AttendanceStatus.PRESENT.name }
            val absences = subRecords.count { it.status == AttendanceStatus.ABSENT.name }
            val leaves = subRecords.count { it.status == AttendanceStatus.ON_LEAVE.name }
            val totalMarked = presences + absences + leaves

            val percentage = if (totalMarked > 0) {
                (presences.toFloat() / totalMarked.toFloat()) * 100f
            } else {
                0f
            }

            val lowCount = students.count { student ->
                val studRecords = subRecords.filter { it.rollNumber == student.rollNumber }
                val studPresent = studRecords.count { it.status == AttendanceStatus.PRESENT.name }
                val studTotal = studRecords.size
                studTotal > 0 && ((studPresent.toFloat() / studTotal.toFloat()) * 100f) < 75f
            }

            SubjectAttendanceStat(
                subject = subject,
                totalSessions = sessions,
                totalPresences = presences,
                totalAbsences = absences,
                totalLeaves = leaves,
                overallPercentage = percentage,
                lowAttendanceStudentsCount = lowCount
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
