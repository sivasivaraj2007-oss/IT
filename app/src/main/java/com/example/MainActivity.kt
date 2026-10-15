package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.AttendanceRepository
import com.example.network.BackendStatus
import com.example.network.BackendSyncManager
import com.example.ui.AdminDashboardScreen
import com.example.ui.AppTab
import com.example.ui.AttendanceViewModel
import com.example.ui.BackendConfigDialog
import com.example.ui.FacultyAttendanceScreen
import com.example.ui.StudentDetailDialog
import com.example.ui.SubjectAnalyticsScreen
import com.example.ui.theme.ActiveBlue
import com.example.ui.theme.ActiveBlueContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenContainer
import com.example.ui.theme.PresentGreenText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AttendanceRepository(database.attendanceDao())
        val backendSyncManager = BackendSyncManager(applicationContext, database.attendanceDao())

        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AttendanceViewModel(repository, backendSyncManager) as T
            }
        }

        setContent {
            MyApplicationTheme {
                val viewModel: AttendanceViewModel = viewModel(factory = viewModelFactory)
                AttendanceApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceApp(viewModel: AttendanceViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val selectedStudent by viewModel.selectedStudent.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val records by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()
    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val backendStatus by viewModel.backendStatus.collectAsStateWithLifecycle()

    var showBackendDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Smart Attendance",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = NavyPrimary
                            )
                        }
                        Text(
                            text = "Department of IT • 56 Students Enrolled",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        // Backend Connection Icon Button
                        IconButton(
                            onClick = { showBackendDialog = true },
                            modifier = Modifier.testTag("top_bar_backend_button")
                        ) {
                            Icon(
                                imageVector = if (backendStatus == BackendStatus.CONNECTED) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                contentDescription = "Backend Cloud Connection",
                                tint = if (backendStatus == BackendStatus.CONNECTED) PresentGreen else NavyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Students Count Badge
                        Surface(
                            shape = CircleShape,
                            color = PresentGreenContainer,
                            modifier = Modifier.clickable { showBackendDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(PresentGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${students.size} Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PresentGreenText
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.MARK_ATTENDANCE,
                    onClick = { viewModel.setTab(AppTab.MARK_ATTENDANCE) },
                    icon = { Icon(Icons.Default.HowToReg, contentDescription = "Mark Attendance") },
                    label = { Text("Faculty Marking", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyPrimary,
                        selectedTextColor = NavyPrimary
                    ),
                    modifier = Modifier.testTag("nav_faculty_marking")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.ADMIN_ROSTER,
                    onClick = { viewModel.setTab(AppTab.ADMIN_ROSTER) },
                    icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = "Admin Roster") },
                    label = { Text("Admin Roster", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyPrimary,
                        selectedTextColor = NavyPrimary
                    ),
                    modifier = Modifier.testTag("nav_admin_roster")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.SUBJECT_ANALYTICS,
                    onClick = { viewModel.setTab(AppTab.SUBJECT_ANALYTICS) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Subject Analytics") },
                    label = { Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyPrimary,
                        selectedTextColor = NavyPrimary
                    ),
                    modifier = Modifier.testTag("nav_analytics")
                )
            }
        },
        floatingActionButton = {
            if (currentTab == AppTab.MARK_ATTENDANCE) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.saveSessionAttendance() },
                    icon = { Icon(Icons.Default.Save, contentDescription = "Save Attendance") },
                    text = { Text("Save & Sync Attendance", fontWeight = FontWeight.Bold) },
                    containerColor = NavyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("save_attendance_fab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.MARK_ATTENDANCE -> FacultyAttendanceScreen(viewModel = viewModel)
                AppTab.ADMIN_ROSTER -> AdminDashboardScreen(viewModel = viewModel)
                AppTab.SUBJECT_ANALYTICS -> SubjectAnalyticsScreen(viewModel = viewModel)
            }

            // Student Detail Sheet / Dialog
            selectedStudent?.let { student ->
                StudentDetailDialog(
                    student = student,
                    subjects = subjects,
                    records = records,
                    onDismiss = { viewModel.selectStudent(null) }
                )
            }

            // Backend Connection Dialog
            if (showBackendDialog) {
                BackendConfigDialog(
                    viewModel = viewModel,
                    onDismiss = { showBackendDialog = false }
                )
            }
        }
    }
}
