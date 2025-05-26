package com.example.gecbadminapp.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

data class TimetableEntry(
    val id: String = "",
    val department: String = "",
    val semester: String = "",
    val day: String = "",
    val timeSlot: String = "",
    val subject: String = "",
    val faculty: String = "",
    val room: String = "",
    val type: String = "Lecture",
    val subjectCode: String = "",
    val isActive: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen() {
    var timetableData by remember { mutableStateOf<List<TimetableEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedDepartment by remember { mutableStateOf("All") }
    var selectedSemester by remember { mutableStateOf("All") }
    var selectedDay by remember { mutableStateOf(getCurrentDay()) }
    var error by remember { mutableStateOf<String?>(null) }

    // Dynamic lists that will be populated from Firebase data
    var availableDepartments by remember { mutableStateOf<List<String>>(listOf("All")) }
    var availableSemesters by remember { mutableStateOf<List<String>>(listOf("All")) }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    // Fixed Firebase data loading with correct field mapping
    LaunchedEffect(Unit) {
        val db = Firebase.firestore
        Log.d("TimetableScreen", "Fetching timetable data...")

        db.collection("timetable")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("TimetableScreen", "Found ${documents.size()} documents")
                val entries = documents.mapNotNull { doc ->
                    try {
                        val data = doc.data
                        Log.d("TimetableScreen", "Document ${doc.id}: $data")

                        // Only process active entries
                        val isActive = data["isActive"] as? Boolean ?: true
                        if (!isActive) {
                            Log.d("TimetableScreen", "Skipping inactive entry: ${doc.id}")
                            return@mapNotNull null
                        }

                        // Helper function to safely get string value and normalize it
                        fun getFieldValue(vararg fieldNames: String): String {
                            for (fieldName in fieldNames) {
                                val value = data[fieldName]?.toString()?.trim()
                                if (!value.isNullOrEmpty() && value != "null") {
                                    return value
                                }
                            }
                            return ""
                        }

                        // Get and normalize department
                        val rawDepartment = getFieldValue("branch", "department", "Department", "dept")
                        val normalizedDepartment = normalizeDepartment(rawDepartment)

                        // Get and normalize semester/year
                        val rawSemester = getFieldValue("year", "semester", "Semester", "class", "Year")
                        val normalizedSemester = normalizeSemester(rawSemester)

                        val entry = TimetableEntry(
                            id = doc.id,
                            department = normalizedDepartment,
                            semester = normalizedSemester,
                            day = getFieldValue("day", "Day", "weekday"),
                            timeSlot = getFieldValue("timeSlot", "time_slot", "Time", "time"),
                            subject = getFieldValue("subject", "Subject", "course"),
                            faculty = getFieldValue("teacherName", "faculty", "Faculty", "teacher"),
                            room = getFieldValue("roomNumber", "room", "Room", "room_number"),
                            type = getFieldValue("type", "Type", "class_type", "classType").let {
                                if (it.isEmpty()) "Lecture" else it
                            },
                            subjectCode = getFieldValue("subjectCode", "subject_code", "code"),
                            isActive = isActive
                        )

                        Log.d("TimetableScreen", "Created entry: $entry")

                        // Only include entry if it has essential data
                        if (entry.subject.isNotEmpty() && entry.day.isNotEmpty() && entry.timeSlot.isNotEmpty()) {
                            entry
                        } else {
                            Log.w("TimetableScreen", "Skipping entry with missing essential data: ${doc.id}")
                            null
                        }

                    } catch (e: Exception) {
                        Log.e("TimetableScreen", "Error parsing document ${doc.id}: ${e.message}", e)
                        null
                    }
                }

                timetableData = entries

                // Extract unique departments and semesters from the data
                val uniqueDepartments = entries
                    .map { it.department }
                    .filter { it.isNotEmpty() }
                    .distinct()
                    .sorted()

                val uniqueSemesters = entries
                    .map { it.semester }
                    .filter { it.isNotEmpty() }
                    .distinct()
                    .sortedWith { a, b ->
                        // Custom sorting for semesters (1st Year, 2nd Year, etc.)
                        val aNum = extractYearNumber(a)
                        val bNum = extractYearNumber(b)
                        aNum.compareTo(bNum)
                    }

                availableDepartments = listOf("All") + uniqueDepartments
                availableSemesters = listOf("All") + uniqueSemesters

                isLoading = false
                error = null
                Log.d("TimetableScreen", "Successfully loaded ${entries.size} entries")
                Log.d("TimetableScreen", "Available departments: $availableDepartments")
                Log.d("TimetableScreen", "Available semesters: $availableSemesters")

            }
            .addOnFailureListener { exception ->
                Log.e("TimetableScreen", "Failed to load data: ${exception.message}", exception)
                isLoading = false
                error = "Failed to load timetable: ${exception.message}"
            }
    }

    // Improved filter data with better matching logic
    val filteredData = remember(timetableData, selectedDepartment, selectedSemester, selectedDay) {
        Log.d("TimetableScreen", "Filtering data: dept='$selectedDepartment', sem='$selectedSemester', day='$selectedDay'")

        val filtered = timetableData.filter { entry ->
            // Department matching
            val departmentMatch = if (selectedDepartment == "All") {
                true
            } else {
                entry.department.equals(selectedDepartment, ignoreCase = true) ||
                        entry.department.contains(selectedDepartment, ignoreCase = true) ||
                        selectedDepartment.contains(entry.department, ignoreCase = true)
            }

            // Semester matching
            val semesterMatch = if (selectedSemester == "All") {
                true
            } else {
                entry.semester.equals(selectedSemester, ignoreCase = true) ||
                        entry.semester.contains(selectedSemester, ignoreCase = true) ||
                        selectedSemester.contains(entry.semester, ignoreCase = true)
            }

            // Day matching (exact match required)
            val dayMatch = entry.day.equals(selectedDay, ignoreCase = true)

            val matches = departmentMatch && semesterMatch && dayMatch

            if (matches) {
                Log.d("TimetableScreen", "Entry matches: ${entry.subject} - ${entry.department} - ${entry.semester} - ${entry.day}")
            } else {
                Log.d("TimetableScreen", "Entry filtered out: ${entry.subject} - dept:${entry.department}(${departmentMatch}) - sem:${entry.semester}(${semesterMatch}) - day:${entry.day}(${dayMatch})")
            }

            matches
        }.sortedBy { parseTimeSlot(it.timeSlot) }

        Log.d("TimetableScreen", "Filtered ${filtered.size} entries from ${timetableData.size} total")
        filtered
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with updated stats
            HeaderSection(
                totalClasses = timetableData.size,
                todayClasses = filteredData.size,
                availableDepartments = availableDepartments.size - 1, // Exclude "All"
                availableSemesters = availableSemesters.size - 1 // Exclude "All"
            )

            // Main Content
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    when {
                        isLoading -> LoadingContent()
                        error != null -> ErrorContent(error ?: "Unknown error") {
                            // Retry button
                            isLoading = true
                            error = null
                        }
                        else -> {
                            // Filters with dynamic data
                            FiltersSection(
                                departments = availableDepartments,
                                semesters = availableSemesters,
                                days = days,
                                selectedDepartment = selectedDepartment,
                                selectedSemester = selectedSemester,
                                selectedDay = selectedDay,
                                onDepartmentChange = {
                                    selectedDepartment = it
                                    Log.d("TimetableScreen", "Department changed to: $it")
                                },
                                onSemesterChange = {
                                    selectedSemester = it
                                    Log.d("TimetableScreen", "Semester changed to: $it")
                                },
                                onDayChange = {
                                    selectedDay = it
                                    Log.d("TimetableScreen", "Day changed to: $it")
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Debug info (remove in production)
                            if (timetableData.isNotEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Debug Info:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E40AF)
                                        )
                                        Text(
                                            text = "Total entries: ${timetableData.size}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF1E40AF)
                                        )
                                        Text(
                                            text = "Filtered entries: ${filteredData.size}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF1E40AF)
                                        )
                                        Text(
                                            text = "Filters: $selectedDepartment | $selectedSemester | $selectedDay",
                                            fontSize = 10.sp,
                                            color = Color(0xFF1E40AF)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Timetable content
                            TimetableContent(filteredData, selectedDay)
                        }
                    }
                }
            }
        }
    }
}

// Helper function to normalize department names
fun normalizeDepartment(rawDepartment: String): String {
    if (rawDepartment.isEmpty()) return ""

    return when (rawDepartment.uppercase().trim()) {
        "COMPUTER SCIENCE", "COMPUTER SCIENCE ENGINEERING", "COMPUTER SCIENCE & ENGINEERING", "CS", "CSE" -> "CSE"
        "ELECTRONICS", "ELECTRONICS & COMMUNICATION", "ELECTRONICS AND COMMUNICATION", "ECE", "EC" -> "ECE"
        "MECHANICAL", "MECHANICAL ENGINEERING", "ME", "MECH" -> "ME"
        "CIVIL", "CIVIL ENGINEERING", "CE" -> "CE"
        "ELECTRICAL", "ELECTRICAL ENGINEERING", "EE", "ELECT" -> "EE"
        "INFORMATION TECHNOLOGY", "IT" -> "IT"
        "MINING", "MINING ENGINEERING" -> "Mining"
        "METALLURGY", "METALLURGICAL ENGINEERING" -> "Metallurgy"
        else -> rawDepartment.trim()
    }
}

// Helper function to normalize semester/year names
fun normalizeSemester(rawSemester: String): String {
    if (rawSemester.isEmpty()) return ""

    val normalized = rawSemester.trim()

    // Check for year patterns
    return when {
        normalized.matches(Regex(".*1.*year.*", RegexOption.IGNORE_CASE)) ||
                normalized.matches(Regex(".*first.*year.*", RegexOption.IGNORE_CASE)) ||
                normalized == "1" -> "1st Year"

        normalized.matches(Regex(".*2.*year.*", RegexOption.IGNORE_CASE)) ||
                normalized.matches(Regex(".*second.*year.*", RegexOption.IGNORE_CASE)) ||
                normalized == "2" -> "2nd Year"

        normalized.matches(Regex(".*3.*year.*", RegexOption.IGNORE_CASE)) ||
                normalized.matches(Regex(".*third.*year.*", RegexOption.IGNORE_CASE)) ||
                normalized == "3" -> "3rd Year"

        normalized.matches(Regex(".*4.*year.*", RegexOption.IGNORE_CASE)) ||
                normalized.matches(Regex(".*fourth.*year.*", RegexOption.IGNORE_CASE)) ||
                normalized == "4" -> "4th Year"

        else -> normalized
    }
}

// Helper function to extract year number for sorting
fun extractYearNumber(semester: String): Int {
    return when {
        semester.contains("1st") || semester.contains("1") -> 1
        semester.contains("2nd") || semester.contains("2") -> 2
        semester.contains("3rd") || semester.contains("3") -> 3
        semester.contains("4th") || semester.contains("4") -> 4
        else -> 5 // Put unknown years at the end
    }
}

@Composable
fun HeaderSection(totalClasses: Int, todayClasses: Int, availableDepartments: Int, availableSemesters: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Class Schedule",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = getCurrentDateFormatted(),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Schedule",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp),
                    tint = Color(0xFF667eea)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enhanced Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Today", todayClasses.toString())
                StatItem("Total", totalClasses.toString())
                StatItem("Depts", availableDepartments.toString())
                StatItem("Years", availableSemesters.toString())
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    departments: List<String>,
    semesters: List<String>,
    days: List<String>,
    selectedDepartment: String,
    selectedSemester: String,
    selectedDay: String,
    onDepartmentChange: (String) -> Unit,
    onSemesterChange: (String) -> Unit,
    onDayChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Filters",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color(0xFF1F2937)
            )

            // Day chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(days) { day ->
                    FilterChip(
                        onClick = { onDayChange(day) },
                        label = { Text(day.take(3)) },
                        selected = day == selectedDay,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF667eea),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Dropdowns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Department dropdown
                var deptExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = deptExpanded,
                    onExpandedChange = { deptExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedDepartment,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            focusedLabelColor = Color(0xFF667eea)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = deptExpanded,
                        onDismissRequest = { deptExpanded = false }
                    ) {
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text(dept) },
                                onClick = {
                                    onDepartmentChange(dept)
                                    deptExpanded = false
                                }
                            )
                        }
                    }
                }

                // Semester dropdown
                var semExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = semExpanded,
                    onExpandedChange = { semExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedSemester,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Year") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semExpanded) },
                        modifier = Modifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            focusedLabelColor = Color(0xFF667eea)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = semExpanded,
                        onDismissRequest = { semExpanded = false }
                    ) {
                        semesters.forEach { semester ->
                            DropdownMenuItem(
                                text = { Text(semester) },
                                onClick = {
                                    onSemesterChange(semester)
                                    semExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimetableContent(entries: List<TimetableEntry>, selectedDay: String) {
    when {
        entries.isEmpty() -> EmptyState(selectedDay)
        else -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Classes for $selectedDay (${entries.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = Color(0xFF1F2937)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.height(400.dp)
                    ) {
                        items(entries) { entry ->
                            ClassCard(entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClassCard(entry: TimetableEntry) {
    val typeColor = when (entry.type.lowercase()) {
        "lab" -> Color(0xFFF59E0B)
        "tutorial" -> Color(0xFF8B5CF6)
        "practical" -> Color(0xFFEF4444)
        else -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = typeColor.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = entry.timeSlot.ifEmpty { "Time TBD" },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = typeColor.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = entry.type.ifEmpty { "Lecture" },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subject and Subject Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.subject.ifEmpty { "Subject Not Available" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.weight(1f)
                )

                if (entry.subjectCode.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = entry.subjectCode,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Faculty",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = entry.faculty.ifEmpty { "Faculty TBD" },
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = if (entry.faculty.isNotEmpty()) FontWeight.Medium else FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Room,
                            contentDescription = "Room",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (entry.room.isNotEmpty()) "Room ${entry.room}" else "Room TBD",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = entry.department.ifEmpty { "Dept TBD" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                    if (entry.semester.isNotEmpty()) {
                        Text(
                            text = entry.semester,
                            fontSize = 10.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF667eea))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading timetable...", color = Color(0xFF6B7280))
        }
    }
}


@Composable
fun ErrorContent(error: String, onRetry: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error loading timetable",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC2626)
            )
            Text(
                text = error,
                fontSize = 12.sp,
                color = Color(0xFF991B1B),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Text("Retry", color = Color.White)
            }
        }
    }
}

@Composable
fun EmptyState(selectedDay: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = "No classes",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF9CA3AF)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No classes scheduled",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF374151)
            )
            Text(
                text = "No classes found for $selectedDay with current filters",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Utility functions
fun getCurrentDay(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        Calendar.SUNDAY -> "Sunday"
        else -> "Monday"
    }
}

fun getCurrentDateFormatted(): String {
    val formatter = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date())
}

fun parseTimeSlot(timeSlot: String): Int {
    if (timeSlot.isBlank()) return 0
    return try {
        val time = timeSlot.split("-").firstOrNull()?.trim() ?: return 0
        val hourStr = time.split(":").firstOrNull()?.replace(Regex("[^0-9]"), "") ?: return 0
        val hour = hourStr.toIntOrNull() ?: return 0

        when {
            timeSlot.contains("PM", ignoreCase = true) && hour != 12 -> hour + 12
            timeSlot.contains("AM", ignoreCase = true) && hour == 12 -> 0
            else -> hour
        }
    } catch (e: Exception) {
        Log.e("TimetableScreen", "Error parsing time: $timeSlot", e)
        0
    }
}