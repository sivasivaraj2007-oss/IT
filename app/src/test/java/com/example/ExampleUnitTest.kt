package com.example

import com.example.data.AttendanceStatus
import com.example.data.InitialData
import com.example.network.StudentDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StudentDatabaseUnitTest {

    @Test
    fun testExactStudentCountIs56() {
        assertEquals(
            "Student database must have exactly 56 records",
            56,
            InitialData.STUDENTS.size
        )
    }

    @Test
    fun testAllStudentsAreActive() {
        InitialData.STUDENTS.forEach { student ->
            assertTrue("Student ${student.rollNumber} must be active", student.isActive)
            assertEquals("Student ${student.rollNumber} status must be Active", "Active", student.status)
        }
    }

    @Test
    fun testRollNumbersAreUniqueAndPreserveGaps() {
        val rollNumbers = InitialData.STUDENTS.map { it.rollNumber }
        val distinctRollNumbers = rollNumbers.distinct()
        assertEquals("All 56 roll numbers must be distinct (no duplicates)", 56, distinctRollNumbers.size)

        // Check specific missing sequence numbers are NOT present
        assertFalse("25BIT019 should be skipped in sequence", rollNumbers.contains("25BIT019"))
        assertFalse("25BIT026 should be skipped in sequence", rollNumbers.contains("25BIT026"))
        assertFalse("25BIT052 should be skipped in sequence", rollNumbers.contains("25BIT052"))

        // Check lateral entries are present
        assertTrue("25BIT301 must be present", rollNumbers.contains("25BIT301"))
        assertTrue("25BIT302 must be present", rollNumbers.contains("25BIT302"))
    }

    @Test
    fun testSpecificStudentRecordsIntegrity() {
        val student1 = InitialData.STUDENTS.first()
        assertEquals("25BIT001", student1.rollNumber)
        assertEquals("ABISHEK M", student1.name)
        assertEquals(1, student1.sNo)

        val student19 = InitialData.STUDENTS[18] // S.No 19
        assertEquals("25BIT020", student19.rollNumber)
        assertEquals("HARIHARAN G", student19.name)

        val student55 = InitialData.STUDENTS[54] // S.No 55
        assertEquals("25BIT301", student55.rollNumber)
        assertEquals("HARI PRASADH G", student55.name)

        val student56 = InitialData.STUDENTS[55] // S.No 56
        assertEquals("25BIT302", student56.rollNumber)
        assertEquals("PRADHEESH S", student56.name)
    }

    @Test
    fun testSubjectWiseAttendanceCalculation() {
        val total = 50
        val present = 40
        val percentage = (present.toFloat() / total.toFloat()) * 100f
        assertEquals(80.0f, percentage, 0.01f)
    }

    @Test
    fun testBackendStudentDtoMapping() {
        val student = InitialData.STUDENTS.first()
        val dto = StudentDto(
            rollNumber = student.rollNumber,
            sNo = student.sNo,
            studentName = student.name,
            department = student.department,
            batch = student.batch,
            isActive = student.isActive,
            status = student.status
        )
        assertEquals("25BIT001", dto.rollNumber)
        assertEquals("ABISHEK M", dto.studentName)
        assertTrue(dto.isActive)
    }
}
