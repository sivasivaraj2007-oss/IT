package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey
    @ColumnInfo(name = "subject_id")
    val id: String,

    @ColumnInfo(name = "subject_code")
    val code: String,

    @ColumnInfo(name = "subject_name")
    val name: String,

    @ColumnInfo(name = "faculty_name")
    val facultyName: String,

    @ColumnInfo(name = "credits")
    val credits: Int = 4
)
