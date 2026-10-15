package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentDto(
    @Json(name = "roll_number")
    val rollNumber: String,

    @Json(name = "s_no")
    val sNo: Int,

    @Json(name = "student_name")
    val studentName: String,

    @Json(name = "department")
    val department: String = "Information Technology",

    @Json(name = "batch")
    val batch: String = "2025-2029",

    @Json(name = "is_active")
    val isActive: Boolean = true,

    @Json(name = "status")
    val status: String = "Active"
)

@JsonClass(generateAdapter = true)
data class SubjectDto(
    @Json(name = "subject_id")
    val subjectId: String,

    @Json(name = "subject_code")
    val subjectCode: String,

    @Json(name = "subject_name")
    val subjectName: String,

    @Json(name = "faculty_name")
    val facultyName: String,

    @Json(name = "credits")
    val credits: Int = 4
)

@JsonClass(generateAdapter = true)
data class AttendanceRecordDto(
    @Json(name = "roll_number")
    val rollNumber: String,

    @Json(name = "subject_id")
    val subjectId: String,

    @Json(name = "date")
    val date: String,

    @Json(name = "period")
    val period: Int,

    @Json(name = "status")
    val status: String,

    @Json(name = "marked_at")
    val markedAt: Long
)
