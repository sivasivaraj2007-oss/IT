package com.example.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApiService {

    // --- Students ---
    @GET("rest/v1/students")
    suspend fun getStudents(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<StudentDto>>

    @POST("rest/v1/students")
    suspend fun upsertStudents(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body students: List<StudentDto>
    ): Response<Unit>

    // --- Subjects ---
    @GET("rest/v1/subjects")
    suspend fun getSubjects(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<SubjectDto>>

    @POST("rest/v1/subjects")
    suspend fun upsertSubjects(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body subjects: List<SubjectDto>
    ): Response<Unit>

    // --- Attendance Records ---
    @GET("rest/v1/attendance_records")
    suspend fun getAttendanceRecords(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<AttendanceRecordDto>>

    @POST("rest/v1/attendance_records")
    suspend fun upsertAttendanceRecords(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body records: List<AttendanceRecordDto>
    ): Response<Unit>
}
