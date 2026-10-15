package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    // --- Student Queries ---
    @Query("SELECT * FROM students ORDER BY s_no ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE roll_number = :rollNumber LIMIT 1")
    fun getStudentByRoll(rollNumber: String): Flow<Student?>

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Query("SELECT COUNT(*) FROM students WHERE is_active = 1")
    suspend fun getActiveStudentCount(): Int

    @Query("SELECT * FROM students WHERE is_active = 1 ORDER BY s_no ASC")
    fun getActiveStudents(): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStudents(students: List<Student>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudentsIgnoreDuplicates(students: List<Student>): List<Long>

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    // --- Subject Queries ---
    @Query("SELECT * FROM subjects ORDER BY subject_id ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    // --- Attendance Queries ---
    @Query("SELECT * FROM attendance_records WHERE subject_id = :subjectId AND date = :date AND period = :period")
    fun getSessionAttendance(subjectId: String, date: String, period: Int): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records ORDER BY date DESC, period DESC")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendanceRecords(records: List<AttendanceRecord>)

    @Query("DELETE FROM attendance_records WHERE subject_id = :subjectId AND date = :date AND period = :period")
    suspend fun deleteSessionAttendance(subjectId: String, date: String, period: Int)

    @Query("SELECT * FROM attendance_records WHERE roll_number = :rollNumber")
    fun getAttendanceForStudent(rollNumber: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE subject_id = :subjectId")
    fun getAttendanceForSubject(subjectId: String): Flow<List<AttendanceRecord>>

    @Query("SELECT COUNT(DISTINCT date || '_' || subject_id || '_' || period) FROM attendance_records")
    suspend fun getTotalSessionsCount(): Int
}
