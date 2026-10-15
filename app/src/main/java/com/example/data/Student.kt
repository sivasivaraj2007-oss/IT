package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a student record.
 * Roll numbers are stored as VARCHAR/TEXT.
 * Unique constraint on roll_number prevents duplicates during repeated imports.
 */
@Entity(
    tableName = "students",
    indices = [Index(value = ["roll_number"], unique = true)]
)
data class Student(
    @PrimaryKey
    @ColumnInfo(name = "roll_number")
    val rollNumber: String,

    @ColumnInfo(name = "s_no")
    val sNo: Int,

    @ColumnInfo(name = "student_name")
    val name: String,

    @ColumnInfo(name = "department")
    val department: String = "Information Technology",

    @ColumnInfo(name = "batch")
    val batch: String = "2025-2029",

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "status")
    val status: String = "Active",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
