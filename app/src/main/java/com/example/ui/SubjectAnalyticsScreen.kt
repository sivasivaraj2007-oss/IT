package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AttendanceStatus
import com.example.data.SubjectAttendanceStat
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedContainer
import com.example.ui.theme.AbsentRedText
import com.example.ui.theme.LeaveAmber
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavyPrimaryContainer
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenContainer
import com.example.ui.theme.PresentGreenText

@Composable
fun SubjectAnalyticsScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.subjectAnalytics.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val allRecords by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // --- 1. Header Overview Banner ---
        item {
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = NavyPrimaryContainer
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Subject-Wise Attendance Analytics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Automated calculations across ${analytics.size} courses for 56 active students",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // --- 2. Subject Cards ---
        items(
            items = analytics,
            key = { it.subject.id }
        ) { stat ->
            SubjectStatCard(
                stat = stat,
                lowAttendanceStudents = remember(allStudents, allRecords, stat.subject.id) {
                    val subRecords = allRecords.filter { it.subjectId == stat.subject.id }
                    allStudents.mapNotNull { student ->
                        val studRecs = subRecords.filter { it.rollNumber == student.rollNumber }
                        val presences = studRecs.count { it.status == AttendanceStatus.PRESENT.name }
                        val total = studRecs.size
                        val pct = if (total > 0) (presences.toFloat() / total.toFloat()) * 100f else 100f
                        if (total > 0 && pct < 75f) {
                            Pair(student, pct)
                        } else null
                    }
                },
                onStudentClick = { student ->
                    viewModel.selectStudent(student)
                }
            )
        }
    }
}

@Composable
fun SubjectStatCard(
    stat: SubjectAttendanceStat,
    lowAttendanceStudents: List<Pair<com.example.data.Student, Float>>,
    onStudentClick: (com.example.data.Student) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_stat_card_${stat.subject.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Code, Name, Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = stat.subject.code,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stat.subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "Faculty: ${stat.subject.facultyName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (stat.overallPercentage >= 75f) PresentGreenContainer else AbsentRedContainer
                ) {
                    Text(
                        text = "%.1f%%".format(stat.overallPercentage),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (stat.overallPercentage >= 75f) PresentGreenText else AbsentRedText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Indicator
            LinearProgressIndicator(
                progress = { (stat.overallPercentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (stat.overallPercentage >= 75f) PresentGreen else AbsentRed,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Session Stats Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sessions Held: ${stat.totalSessions}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Present: ${stat.totalPresences}",
                    fontSize = 12.sp,
                    color = PresentGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Absent: ${stat.totalAbsences}",
                    fontSize = 12.sp,
                    color = AbsentRed,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Leave: ${stat.totalLeaves}",
                    fontSize = 12.sp,
                    color = LeaveAmber,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Low Attendance Notice Toggle
            if (lowAttendanceStudents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AbsentRedContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = AbsentRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${lowAttendanceStudents.size} student(s) below 75% attendance threshold",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AbsentRedText
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = AbsentRedText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        lowAttendanceStudents.forEach { (student, pct) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onStudentClick(student) }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${student.rollNumber} - ${student.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "%.1f%%".format(pct),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AbsentRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
