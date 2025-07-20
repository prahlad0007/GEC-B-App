// Main Timetable Management Screen
package com.example.gecbadminapp.admin.Screens

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*


// Data Classes
data class TimetableEntry(
    val id: String = "",
    val year: String = "",
    val branch: String = "",
    val day: String = "",
    val timeSlot: String = "",
    val subject: String = "",
    val teacherName: String = "",
    val roomNumber: String = "",
    val subjectCode: String = "",
    val isActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class Teacher(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val department: String = "",
    val isActive: Boolean = true
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTimetable() {
    var timetableEntries by remember { mutableStateOf<List<TimetableEntry>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<TimetableEntry?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedYear by remember { mutableStateOf("All") }
    var selectedBranch by remember { mutableStateOf("All") }
    var selectedDay by remember { mutableStateOf("All") }
    var showDeleteConfirmation by remember { mutableStateOf<TimetableEntry?>(null) }
    var isDeletingEntry by remember { mutableStateOf(false) }

    val years = listOf("All", "1st Year", "2nd Year", "3rd Year", "4th Year")
    val branches = listOf("All", "Computer Science", "Information Technology", "Electronics & Communication", "Electrical", "Mechanical", "Civil")
    val days = listOf("All", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val coroutineScope = rememberCoroutineScope()

    // Load timetable data
    LaunchedEffect(Unit) {
        loadTimetableData { entries ->
            timetableEntries = entries
            isLoading = false
        }
    }

    // Filter timetable entries
    val filteredEntries = timetableEntries.filter { entry ->
        val matchesYear = selectedYear == "All" || entry.year == selectedYear
        val matchesBranch = selectedBranch == "All" || entry.branch == selectedBranch
        val matchesDay = selectedDay == "All" || entry.day == selectedDay
        matchesYear && matchesBranch && matchesDay
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFFF093FB)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = "Timetable",
                                tint = Color(0xFF667eea),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "GEC Bilaspur",
                                    color = Color(0xFF1F2937),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Timetable Management",
                                    color = Color(0xFF6B7280),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF667eea)
                            ),
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { showAddDialog = true }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Entry",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Enhanced Filter Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Color(0xFF667eea),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Filter Timetable",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937)
                            )
                        }

                        // Year Filter
                        Text(
                            text = "Academic Year",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            items(years) { year ->
                                FilterChip(
                                    year = year,
                                    isSelected = selectedYear == year,
                                    onClick = { selectedYear = year }
                                )
                            }
                        }

                        // Branch Filter
                        Text(
                            text = "Branch/Department",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            items(branches) { branch ->
                                FilterChip(
                                    year = branch,
                                    isSelected = selectedBranch == branch,
                                    onClick = { selectedBranch = branch }
                                )
                            }
                        }

                        // Day Filter
                        Text(
                            text = "Day of Week",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(days) { day ->
                                FilterChip(
                                    year = day,
                                    isSelected = selectedDay == day,
                                    onClick = { selectedDay = day }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Timetable Entries
                if (isLoading) {
                    LoadingVieww()
                } else if (filteredEntries.isEmpty()) {
                    EmptyTimetableView()
                } else {
                    // Group entries by day for better organization
                    val groupedEntries = filteredEntries.groupBy { it.day }
                    val sortedDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
                        .filter { day -> groupedEntries.containsKey(day) }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sortedDays) { day ->
                            DayTimetableSection(
                                day = day,
                                entries = groupedEntries[day]?.sortedBy { it.timeSlot } ?: emptyList(),
                                onEdit = { selectedEntry = it },
                                onDelete = { showDeleteConfirmation = it },
                                isDeleting = isDeletingEntry
                            )
                        }
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog) {
        AddEditTimetableDialog(
            entry = null,
            onDismiss = { showAddDialog = false },
            onSave = { newEntry ->
                saveTimetableEntry(newEntry) { savedEntry ->
                    timetableEntries = timetableEntries + savedEntry
                    showAddDialog = false
                }
            }
        )
    }

    selectedEntry?.let { entry ->
        AddEditTimetableDialog(
            entry = entry,
            onDismiss = { selectedEntry = null },
            onSave = { updatedEntry ->
                updateTimetableEntry(updatedEntry) {
                    timetableEntries = timetableEntries.map {
                        if (it.id == updatedEntry.id) updatedEntry else it
                    }
                    selectedEntry = null
                }
            }
        )
    }

    // Delete Confirmation Dialog
    showDeleteConfirmation?.let { entry ->
        DeleteConfirmationDialog(
            entry = entry,
            isDeleting = isDeletingEntry,
            onConfirm = {
                coroutineScope.launch {
                    isDeletingEntry = true
                    deleteTimetableEntry(entry) { success ->
                        if (success) {
                            timetableEntries = timetableEntries.filter { it.id != entry.id }
                        }
                        isDeletingEntry = false
                        showDeleteConfirmation = null
                    }
                }
            },
            onDismiss = {
                if (!isDeletingEntry) {
                    showDeleteConfirmation = null
                }
            }
        )
    }
}

@Composable
fun FilterChip(
    year: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFF667eea)
            } else {
                Color(0xFFF8FAFC)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = year,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else Color(0xFF6B7280),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
fun DayTimetableSection(
    day: String,
    entries: List<TimetableEntry>,
    onEdit: (TimetableEntry) -> Unit,
    onDelete: (TimetableEntry) -> Unit,
    isDeleting: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Day Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF667eea)
                    )
                ) {
                    Text(
                        text = day.uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${entries.size} Classes",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Time slots for the day
            entries.forEach { entry ->
                TimetableEntryCard(
                    entry = entry,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    isDeleting = isDeleting
                )
                if (entry != entries.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TimetableEntryCard(
    entry: TimetableEntry,
    onEdit: (TimetableEntry) -> Unit,
    onDelete: (TimetableEntry) -> Unit,
    isDeleting: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(entry) },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time slot
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = entry.timeSlot,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Subject and details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.subject,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                if (entry.subjectCode.isNotEmpty()) {
                    Text(
                        text = entry.subjectCode,
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = entry.teacherName,
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                    if (entry.roomNumber.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Room,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFF9CA3AF)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = entry.roomNumber,
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            // Action buttons
            Row {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                    ),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onEdit(entry) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEF4444).copy(alpha = 0.1f)
                    ),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onDelete(entry) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTimetableDialog(
    entry: TimetableEntry?,
    onDismiss: () -> Unit,
    onSave: (TimetableEntry) -> Unit
) {
    var year by remember { mutableStateOf(entry?.year ?: "") }
    var branch by remember { mutableStateOf(entry?.branch ?: "") }
    var day by remember { mutableStateOf(entry?.day ?: "") }
    var timeSlot by remember { mutableStateOf(entry?.timeSlot ?: "") }
    var subject by remember { mutableStateOf(entry?.subject ?: "") }
    var subjectCode by remember { mutableStateOf(entry?.subjectCode ?: "") }
    var teacherName by remember { mutableStateOf(entry?.teacherName ?: "") }
    var roomNumber by remember { mutableStateOf(entry?.roomNumber ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    val years = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    val branches = listOf("Computer Science", "Information Technology", "Electronics & Communication", "Electrical", "Mechanical", "Civil")
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val timeSlots = listOf(
        "9:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-1:00",
        "1:00-2:00", "2:00-3:00", "3:00-4:00", "4:00-5:00", "5:00-6:00"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (entry == null) "Add Timetable Entry" else "Edit Timetable Entry",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }
                }

                item {
                    // Year Dropdown
                    var expandedYear by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedYear,
                        onExpandedChange = { expandedYear = !expandedYear }
                    ) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Academic Year *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear) },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedYear,
                            onDismissRequest = { expandedYear = false }
                        ) {
                            years.forEach { yearOption ->
                                DropdownMenuItem(
                                    text = { Text(yearOption) },
                                    onClick = {
                                        year = yearOption
                                        expandedYear = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Branch Dropdown
                    var expandedBranch by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedBranch,
                        onExpandedChange = { expandedBranch = !expandedBranch }
                    ) {
                        OutlinedTextField(
                            value = branch,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Branch/Department *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBranch) },
                            leadingIcon = { Icon(Icons.Default.Engineering, contentDescription = null) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBranch,
                            onDismissRequest = { expandedBranch = false }
                        ) {
                            branches.forEach { branchOption ->
                                DropdownMenuItem(
                                    text = { Text(branchOption) },
                                    onClick = {
                                        branch = branchOption
                                        expandedBranch = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Day Dropdown
                    var expandedDay by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedDay,
                        onExpandedChange = { expandedDay = !expandedDay }
                    ) {
                        OutlinedTextField(
                            value = day,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Day of Week *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDay) },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDay,
                            onDismissRequest = { expandedDay = false }
                        ) {
                            days.forEach { dayOption ->
                                DropdownMenuItem(
                                    text = { Text(dayOption) },
                                    onClick = {
                                        day = dayOption
                                        expandedDay = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Time Slot Dropdown
                    var expandedTime by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedTime,
                        onExpandedChange = { expandedTime = !expandedTime }
                    ) {
                        OutlinedTextField(
                            value = timeSlot,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Time Slot *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTime) },
                            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTime,
                            onDismissRequest = { expandedTime = false }
                        ) {
                            timeSlots.forEach { timeOption ->
                                DropdownMenuItem(
                                    text = { Text(timeOption) },
                                    onClick = {
                                        timeSlot = timeOption
                                        expandedTime = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject Name *") },
                        placeholder = { Text("e.g., Data Structures") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Book, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            focusedLabelColor = Color(0xFF667eea)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Subject Code") },
                        placeholder = { Text("e.g., CS301") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            focusedLabelColor = Color(0xFF667eea)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("Teacher Name *") },
                        placeholder = { Text("e.g., Prof. John Doe") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            focusedLabelColor = Color(0xFF667eea)
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it },
                        label = { Text("Room Number") },
                        placeholder = { Text("e.g., Room 101") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Room, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            focusedLabelColor = Color(0xFF667eea)
                        )
                    )
                }

                item {
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel Button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6B7280)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF6B7280))
                        ) {
                            Text("Cancel")
                        }

                        // Save Button
                        Button(
                            onClick = {
                                if (year.isNotEmpty() && branch.isNotEmpty() && day.isNotEmpty() &&
                                    timeSlot.isNotEmpty() && subject.isNotEmpty() && teacherName.isNotEmpty()) {
                                    isSaving = true
                                    val timetableEntry = TimetableEntry(
                                        id = entry?.id ?: UUID.randomUUID().toString(),
                                        year = year,
                                        branch = branch,
                                        day = day,
                                        timeSlot = timeSlot,
                                        subject = subject,
                                        teacherName = teacherName,
                                        roomNumber = roomNumber,
                                        subjectCode = subjectCode,
                                        lastUpdated = System.currentTimeMillis()
                                    )
                                    onSave(timetableEntry)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving && year.isNotEmpty() && branch.isNotEmpty() &&
                                    day.isNotEmpty() && timeSlot.isNotEmpty() && subject.isNotEmpty() &&
                                    teacherName.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667eea)
                            )
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(if (entry == null) "Add Entry" else "Update Entry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    entry: TimetableEntry,
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning Icon
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEF4444).copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Delete Timetable Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Are you sure you want to delete this timetable entry? This action cannot be undone.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Entry details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "${entry.subject} (${entry.subjectCode})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = "${entry.day} • ${entry.timeSlot}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = "${entry.teacherName} • ${entry.year} ${entry.branch}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isDeleting,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6B7280)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF6B7280))
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = !isDeleting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        )
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingVieww() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF667eea),
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading Timetable...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
            }
        }
    }
}

@Composable
fun EmptyTimetableView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF667eea).copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "No Timetable Entries",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No timetable entries found for the selected filters. Try adjusting your filters or add a new entry.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// Firebase operations
fun loadTimetableData(onComplete: (List<TimetableEntry>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("timetable")
        .whereEqualTo("isActive", true)
        .get()
        .addOnSuccessListener { documents ->
            val entries = documents.map { doc ->
                TimetableEntry(
                    id = doc.id,
                    year = doc.getString("year") ?: "",
                    branch = doc.getString("branch") ?: "",
                    day = doc.getString("day") ?: "",
                    timeSlot = doc.getString("timeSlot") ?: "",
                    subject = doc.getString("subject") ?: "",
                    teacherName = doc.getString("teacherName") ?: "",
                    roomNumber = doc.getString("roomNumber") ?: "",
                    subjectCode = doc.getString("subjectCode") ?: "",
                    isActive = doc.getBoolean("isActive") ?: true,
                    lastUpdated = doc.getLong("lastUpdated") ?: System.currentTimeMillis()
                )
            }
            onComplete(entries)
        }
        .addOnFailureListener {
            onComplete(emptyList())
        }
}

fun saveTimetableEntry(entry: TimetableEntry, onComplete: (TimetableEntry) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val entryMap = mapOf(
        "year" to entry.year,
        "branch" to entry.branch,
        "day" to entry.day,
        "timeSlot" to entry.timeSlot,
        "subject" to entry.subject,
        "teacherName" to entry.teacherName,
        "roomNumber" to entry.roomNumber,
        "subjectCode" to entry.subjectCode,
        "isActive" to entry.isActive,
        "lastUpdated" to entry.lastUpdated
    )

    if (entry.id.isEmpty()) {
        // Add new entry
        db.collection("timetable")
            .add(entryMap)
            .addOnSuccessListener { documentReference ->
                onComplete(entry.copy(id = documentReference.id))
            }
            .addOnFailureListener {
                // Handle error
            }
    } else {
        // Update existing entry
        db.collection("timetable")
            .document(entry.id)
            .set(entryMap)
            .addOnSuccessListener {
                onComplete(entry)
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}

fun updateTimetableEntry(entry: TimetableEntry, onComplete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val entryMap = mapOf(
        "year" to entry.year,
        "branch" to entry.branch,
        "day" to entry.day,
        "timeSlot" to entry.timeSlot,
        "subject" to entry.subject,
        "teacherName" to entry.teacherName,
        "roomNumber" to entry.roomNumber,
        "subjectCode" to entry.subjectCode,
        "isActive" to entry.isActive,
        "lastUpdated" to System.currentTimeMillis()
    )

    db.collection("timetable")
        .document(entry.id)
        .update(entryMap)
        .addOnSuccessListener {
            onComplete()
        }
        .addOnFailureListener {
            // Handle error
        }
}

fun deleteTimetableEntry(entry: TimetableEntry, onComplete: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("timetable")
        .document(entry.id)
        .update("isActive", false)
        .addOnSuccessListener {
            onComplete(true)
        }
        .addOnFailureListener {
            onComplete(false)
        }
}