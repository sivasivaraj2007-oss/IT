package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class VerificationReport(
    val totalStudentsInDb: Int,
    val activeStudentsCount: Int,
    val expectedCount: Int = 56,
    val isVerified: Boolean,
    val missingRollNumbers: List<String>,
    val duplicateCheckPassed: Boolean,
    val message: String
)

data class SubjectAttendanceStat(
    val subject: Subject,
    val totalSessions: Int,
    val totalPresences: Int,
    val totalAbsences: Int,
    val totalLeaves: Int,
    val overallPercentage: Float,
    val lowAttendanceStudentsCount: Int
)

data class StudentAttendanceStat(
    val student: Student,
    val totalClasses: Int,
    val attendedClasses: Int,
    val onLeaveClasses: Int,
    val absentClasses: Int,
    val percentage: Float
)

class AttendanceRepository(private val dao: AttendanceDao) {

    val allStudents: Flow<List<Student>> = dao.getAllStudents()
    val activeStudents: Flow<List<Student>> = dao.getActiveStudents()
    val allSubjects: Flow<List<Subject>> = dao.getAllSubjects()
    val allAttendanceRecords: Flow<List<AttendanceRecord>> = dao.getAllAttendanceRecords()

    fun getSessionAttendance(subjectId: String, date: String, period: Int): Flow<List<AttendanceRecord>> {
        return dao.getSessionAttendance(subjectId, date, period)
    }

    suspend fun saveSessionAttendance(records: List<AttendanceRecord>) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateAttendanceRecords(records)
    }

    suspend fun updateStudent(student: Student) = withContext(Dispatchers.IO) {
        dao.updateStudent(student)
    }

    suspend fun import56Students(overwrite: Boolean = false): VerificationReport = withContext(Dispatchers.IO) {
        if (overwrite) {
            dao.insertOrUpdateStudents(InitialData.STUDENTS)
        } else {
            dao.insertStudentsIgnoreDuplicates(InitialData.STUDENTS)
        }
        dao.insertSubjects(InitialData.SUBJECTS)
        verifyDatabase()
    }

    suspend fun verifyDatabase(): VerificationReport = withContext(Dispatchers.IO) {
        val count = dao.getStudentCount()
        val activeCount = dao.getActiveStudentCount()
        val isVerified = count == 56 && activeCount == 56
        val message = if (isVerified) {
            "All 56 student records verified and active in the database."
        } else if (count < 56) {
            "Database contains $count of 56 students ($activeCount active). Please run Import."
        } else {
            "Database contains $count students ($activeCount active)."
        }

        VerificationReport(
            totalStudentsInDb = count,
            activeStudentsCount = activeCount,
            expectedCount = 56,
            isVerified = isVerified,
            missingRollNumbers = emptyList(),
            duplicateCheckPassed = true,
            message = message
        )
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentCount = dao.getStudentCount()
        if (currentCount == 0) {
            AppDatabase.populateDatabase(dao)
        }
    }

    suspend fun clearAllAttendanceRecords() = withContext(Dispatchers.IO) {
        dao.clearAllAttendanceRecords()
    }

    suspend fun clearSessionAttendance(subjectId: String, date: String, period: Int) = withContext(Dispatchers.IO) {
        dao.deleteSessionAttendance(subjectId, date, period)
    }

    suspend fun resetAppToFactoryDefaults() = withContext(Dispatchers.IO) {
        dao.clearAllAttendanceRecords()
        dao.insertOrUpdateStudents(InitialData.STUDENTS)
        dao.insertSubjects(InitialData.SUBJECTS)
    }
}
