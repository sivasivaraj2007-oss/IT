package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AttendanceStatus
import com.example.data.Student
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedContainer
import com.example.ui.theme.AbsentRedText
import com.example.ui.theme.ActiveBlue
import com.example.ui.theme.ActiveBlueContainer
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.LeaveAmber
import com.example.ui.theme.LeaveAmberContainer
import com.example.ui.theme.LeaveAmberText
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenContainer
import com.example.ui.theme.PresentGreenText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyAttendanceScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val selectedSubjectId by viewModel.selectedSubjectId.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val sessionAttendance by viewModel.sessionAttendance.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()

    val currentSubject = subjects.firstOrNull { it.id == selectedSubjectId }

    // Filter students based on search query (name or roll number) and status filter
    val filteredStudents = remember(students, searchQuery, sessionAttendance, statusFilter) {
        students.filter { student ->
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                student.name.contains(searchQuery.trim(), ignoreCase = true) ||
                        student.rollNumber.contains(searchQuery.trim(), ignoreCase = true) ||
                        student.sNo.toString() == searchQuery.trim()
            }

            val currentStatus = sessionAttendance[student.rollNumber] ?: AttendanceStatus.PRESENT
            val matchesStatus = statusFilter == null || currentStatus == statusFilter

            matchesQuery && matchesStatus
        }
    }

    // Counts for the summary
    val presentCount = sessionAttendance.values.count { it == AttendanceStatus.PRESENT }
    val absentCount = sessionAttendance.values.count { it == AttendanceStatus.ABSENT }
    val leaveCount = sessionAttendance.values.count { it == AttendanceStatus.ON_LEAVE }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // --- 1. Subject Selector Chips ---
        item {
            Text(
                text = "Select Course / Subject",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subjects) { subject ->
                    val isSelected = subject.id == selectedSubjectId
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onSubjectChanged(subject.id) },
                        label = {
                            Column {
                                Text(
                                    text = subject.code,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = subject.name,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // --- 2. Session Details & Period Selector ---
        item {
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentSubject?.name ?: "Selected Subject",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "Faculty: ${currentSubject?.facultyName ?: "Instructor"} • Date: $selectedDate",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = "Period $selectedPeriod",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Period Quick Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Period:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..6).forEach { p ->
                                val isSelected = p == selectedPeriod
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { viewModel.onPeriodChanged(p) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$p",
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Live Session Statistics Counter ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(
                    label = "Total Active",
                    count = students.size,
                    containerColor = ActiveBlueContainer,
                    contentColor = ActiveBlue,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "Present",
                    count = presentCount,
                    containerColor = PresentGreenContainer,
                    contentColor = PresentGreenText,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "Absent",
                    count = absentCount,
                    containerColor = AbsentRedContainer,
                    contentColor = AbsentRedText,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "On Leave",
                    count = leaveCount,
                    containerColor = LeaveAmberContainer,
                    contentColor = LeaveAmberText,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- 4. Batch Actions Bar ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.markAllAs(AttendanceStatus.PRESENT) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PresentGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mark_all_present_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Mark All Present",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("All Present", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.markAllAs(AttendanceStatus.ABSENT) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AbsentRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mark_all_absent_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Mark All Absent",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("All Absent", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { viewModel.saveSessionAttendance() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NavyPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_save_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save Attendance",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 5. Search & Status Filter ---
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("Search by name or roll number (e.g. Abishek, 25BIT001)") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("faculty_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary,
                    unfocusedBorderColor = CardBorderLight
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilterChip(
                    selected = statusFilter == null,
                    onClick = { viewModel.setStatusFilter(null) },
                    label = { Text("All (${students.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = statusFilter == AttendanceStatus.PRESENT,
                    onClick = { viewModel.setStatusFilter(if (statusFilter == AttendanceStatus.PRESENT) null else AttendanceStatus.PRESENT) },
                    label = { Text("Present ($presentCount)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = statusFilter == AttendanceStatus.ABSENT,
                    onClick = { viewModel.setStatusFilter(if (statusFilter == AttendanceStatus.ABSENT) null else AttendanceStatus.ABSENT) },
                    label = { Text("Absent ($absentCount)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = statusFilter == AttendanceStatus.ON_LEAVE,
                    onClick = { viewModel.setStatusFilter(if (statusFilter == AttendanceStatus.ON_LEAVE) null else AttendanceStatus.ON_LEAVE) },
                    label = { Text("Leave ($leaveCount)", fontSize = 11.sp) }
                )
            }
        }

        // --- 6. Student Attendance Roster List ---
        items(
            items = filteredStudents,
            key = { it.rollNumber }
        ) { student ->
            val status = sessionAttendance[student.rollNumber] ?: AttendanceStatus.PRESENT
            StudentAttendanceItem(
                student = student,
                status = status,
                onStatusChange = { newStatus ->
                    viewModel.setStudentStatus(student.rollNumber, newStatus)
                },
                onStudentClick = {
                    viewModel.selectStudent(student)
                }
            )
        }

        if (filteredStudents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No students match '$searchQuery'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBadge(
    label: String,
    count: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = contentColor
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

@Composable
fun StudentAttendanceItem(
    student: Student,
    status: AttendanceStatus,
    onStatusChange: (AttendanceStatus) -> Unit,
    onStudentClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStudentClick() }
            .testTag("student_card_${student.rollNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // S.No. and Student Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // S.No circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${student.sNo}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = student.rollNumber,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = NavyPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Active status pill
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ActiveBlueContainer
                        ) {
                            Text(
                                text = "Active",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActiveBlue,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Three Action Toggle: [P] [A] [L]
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusToggleButton(
                    label = "P",
                    isSelected = status == AttendanceStatus.PRESENT,
                    activeColor = PresentGreen,
                    activeTextColor = Color.White,
                    testTag = "btn_p_${student.rollNumber}",
                    onClick = { onStatusChange(AttendanceStatus.PRESENT) }
                )
                StatusToggleButton(
                    label = "A",
                    isSelected = status == AttendanceStatus.ABSENT,
                    activeColor = AbsentRed,
                    activeTextColor = Color.White,
                    testTag = "btn_a_${student.rollNumber}",
                    onClick = { onStatusChange(AttendanceStatus.ABSENT) }
                )
                StatusToggleButton(
                    label = "L",
                    isSelected = status == AttendanceStatus.ON_LEAVE,
                    activeColor = LeaveAmber,
                    activeTextColor = Color.White,
                    testTag = "btn_l_${student.rollNumber}",
                    onClick = { onStatusChange(AttendanceStatus.ON_LEAVE) }
                )
            }
        }
    }
}

@Composable
fun StatusToggleButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    activeTextColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Transparent,
        label = "status_toggle_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) activeTextColor else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "status_toggle_text"
    )

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else CardBorderLight,
                shape = RoundedCornerShape(8.dp)
            )
            .background(bgColor)
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = textColor
        )
    }
}
