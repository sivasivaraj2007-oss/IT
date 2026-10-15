package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AttendanceStatus(val label: String, val shortCode: String) {
    PRESENT("Present", "P"),
    ABSENT("Absent", "A"),
    ON_LEAVE("On Leave", "L");

    companion object {
        fun fromString(value: String): AttendanceStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PRESENT
        }
    }
}

@Entity(
    tableName = "attendance_records",
    indices = [
        Index(value = ["roll_number", "subject_id", "date", "period"], unique = true),
        Index(value = ["roll_number"]),
        Index(value = ["subject_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["roll_number"],
            childColumns = ["roll_number"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "roll_number")
    val rollNumber: String,

    @ColumnInfo(name = "subject_id")
    val subjectId: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "period")
    val period: Int = 1,

    @ColumnInfo(name = "status")
    val status: String = AttendanceStatus.PRESENT.name,

    @ColumnInfo(name = "marked_at")
    val markedAt: Long = System.currentTimeMillis()
)
