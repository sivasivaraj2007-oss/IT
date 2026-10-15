package com.example.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AttendanceStatus
import com.example.data.Student
import com.example.ui.theme.ActiveBlue
import com.example.ui.theme.ActiveBlueContainer
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenContainer
import com.example.ui.theme.PresentGreenText

@Composable
fun AdminDashboardScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val allRecords by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()
    val verificationReport by viewModel.verificationReport.collectAsStateWithLifecycle()
    val isAdminAuthorized by viewModel.isAdminAuthorized.collectAsStateWithLifecycle()

    var adminSearchQuery by remember { mutableStateOf("") }
    var showPinDialog by remember { mutableStateOf(false) }
    var showBackendDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    val filteredList = remember(students, adminSearchQuery) {
        if (adminSearchQuery.isBlank()) {
            students
        } else {
            students.filter {
                it.name.contains(adminSearchQuery.trim(), ignoreCase = true) ||
                        it.rollNumber.contains(adminSearchQuery.trim(), ignoreCase = true) ||
                        it.sNo.toString() == adminSearchQuery.trim()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // --- 1. Database Verification & Status Card ---
        item {
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (verificationReport?.isVerified == true) PresentGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (verificationReport?.isVerified == true) Icons.Default.Verified else Icons.Default.Warning,
                                contentDescription = "Verification Status",
                                tint = if (verificationReport?.isVerified == true) PresentGreenText else NavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Database Verification Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (verificationReport?.isVerified == true) PresentGreenText else NavyPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (verificationReport?.isVerified == true) PresentGreen else NavyPrimary
                        ) {
                            Text(
                                text = "${students.size}/56 Active",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = verificationReport?.message ?: "Verifying database integrity...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.verifyDatabase() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("verify_database_button")
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verify Integrity", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.import56Students(overwrite = false) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reimport_students_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Re-import / Sync", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showBackendDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_backend_config_button")
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Connect Backend / Supabase Cloud DB", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 2. Admin Security Authorization Banner ---
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAdminAuthorized) ActiveBlueContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isAdminAuthorized) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Admin Lock",
                            tint = if (isAdminAuthorized) ActiveBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isAdminAuthorized) "Admin Authorization: Active" else "Admin Authorization: Locked",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isAdminAuthorized) ActiveBlue else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAdminAuthorized) "Authorized to modify student records" else "Required to modify roll numbers/names",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isAdminAuthorized) {
                        TextButton(
                            onClick = { viewModel.revokeAdmin() },
                            modifier = Modifier.testTag("lock_admin_button")
                        ) {
                            Text("Lock", fontWeight = FontWeight.Bold, color = ActiveBlue)
                        }
                    } else {
                        Button(
                            onClick = { showPinDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("authorize_admin_button")
                        ) {
                            Text("Authorize", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // --- 3. Search Bar ---
        item {
            OutlinedTextField(
                value = adminSearchQuery,
                onValueChange = { adminSearchQuery = it },
                label = { Text("Filter student directory by roll or name") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (adminSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { adminSearchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_search_input")
            )
        }

        // --- 4. Student Roster Title with Count ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enrolled Students (${filteredList.size} of ${students.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "VARCHAR / TEXT IDs",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- 5. Student List Items ---
        items(
            items = filteredList,
            key = { it.rollNumber }
        ) { student ->
            val studentRecords = allRecords.filter { it.rollNumber == student.rollNumber }
            val presentCount = studentRecords.count { it.status == AttendanceStatus.PRESENT.name }
            val totalCount = studentRecords.size
            val percent = if (totalCount > 0) (presentCount.toFloat() / totalCount.toFloat() * 100f) else 100f

            AdminStudentCard(
                student = student,
                overallPercentage = percent,
                isAdminAuthorized = isAdminAuthorized,
                onEditClick = {
                    if (isAdminAuthorized) {
                        editingStudent = student
                    } else {
                        showPinDialog = true
                    }
                },
                onCardClick = {
                    viewModel.selectStudent(student)
                }
            )
        }
    }

    // --- Admin PIN Dialog ---
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                enteredPin = ""
                pinError = null
            },
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = NavyPrimary) },
            title = { Text("Administrator Authorization") },
            text = {
                Column {
                    Text(
                        "Modifying student names or roll numbers requires administrator credentials.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            enteredPin = it
                            pinError = null
                        },
                        label = { Text("Enter Admin PIN (Default: 1234)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = pinError != null,
                        supportingText = pinError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.authorizeAdmin(enteredPin)) {
                            showPinDialog = false
                            enteredPin = ""
                            pinError = null
                        } else {
                            pinError = "Incorrect PIN. Try '1234'."
                        }
                    },
                    modifier = Modifier.testTag("submit_pin_button")
                ) {
                    Text("Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPinDialog = false
                    enteredPin = ""
                    pinError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- Student Edit Dialog ---
    editingStudent?.let { student ->
        var editName by remember { mutableStateOf(student.name) }
        var editRoll by remember { mutableStateOf(student.rollNumber) }
        var editActive by remember { mutableStateOf(student.isActive) }

        AlertDialog(
            onDismissRequest = { editingStudent = null },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = NavyPrimary) },
            title = { Text("Edit Student Record") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Authorized Admin Mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = ActiveBlue,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = editRoll,
                        onValueChange = { editRoll = it },
                        label = { Text("Roll Number (VARCHAR/TEXT)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Student Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Active Status:", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = editActive,
                            onCheckedChange = { editActive = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = student.copy(
                            rollNumber = editRoll.trim().uppercase(),
                            name = editName.trim().uppercase(),
                            isActive = editActive,
                            status = if (editActive) "Active" else "Inactive"
                        )
                        viewModel.updateStudentDetails(updated, "1234")
                        editingStudent = null
                    },
                    modifier = Modifier.testTag("save_student_edit_button")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBackendDialog) {
        BackendConfigDialog(
            viewModel = viewModel,
            onDismiss = { showBackendDialog = false }
        )
    }
}

@Composable
fun AdminStudentCard(
    student: Student,
    overallPercentage: Float,
    isAdminAuthorized: Boolean,
    onEditClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("admin_student_card_${student.rollNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
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
                            fontSize = 14.sp,
                            color = NavyPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (student.isActive) PresentGreenContainer else MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = student.status,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (student.isActive) PresentGreenText else MaterialTheme.colorScheme.error,
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

                    Text(
                        text = "Dept: ${student.department} • Batch: ${student.batch}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Percentage badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (overallPercentage >= 75f) PresentGreenContainer else MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = "%.1f%%".format(overallPercentage),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (overallPercentage >= 75f) PresentGreenText else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.testTag("edit_student_${student.rollNumber}")
                ) {
                    Icon(
                        imageVector = if (isAdminAuthorized) Icons.Default.Edit else Icons.Default.Lock,
                        contentDescription = "Edit Student Details",
                        tint = if (isAdminAuthorized) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
