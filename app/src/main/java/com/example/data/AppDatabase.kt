package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Student::class, Subject::class, AttendanceRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_attendance.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.attendanceDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(dao: AttendanceDao) {
            // Insert all 56 students with duplicate prevention
            dao.insertStudentsIgnoreDuplicates(InitialData.STUDENTS)
            // Insert subjects
            dao.insertSubjects(InitialData.SUBJECTS)

            // Seed sample past attendance sessions to provide realistic subject-wise percentages immediately
            val sampleDates = listOf("2026-10-12", "2026-10-13", "2026-10-14")
            val seedRecords = mutableListOf<AttendanceRecord>()

            for (date in sampleDates) {
                for (subject in InitialData.SUBJECTS) {
                    val period = 1
                    for (student in InitialData.STUDENTS) {
                        // High overall attendance with realistic variations
                        val status = when {
                            (student.sNo + date.hashCode()) % 17 == 0 -> AttendanceStatus.ABSENT.name
                            (student.sNo + date.hashCode()) % 29 == 0 -> AttendanceStatus.ON_LEAVE.name
                            else -> AttendanceStatus.PRESENT.name
                        }
                        seedRecords.add(
                            AttendanceRecord(
                                rollNumber = student.rollNumber,
                                subjectId = subject.id,
                                date = date,
                                period = period,
                                status = status,
                                markedAt = System.currentTimeMillis() - 86400000L * 2
                            )
                        )
                    }
                }
            }
            dao.insertOrUpdateAttendanceRecords(seedRecords)
        }
    }
}
