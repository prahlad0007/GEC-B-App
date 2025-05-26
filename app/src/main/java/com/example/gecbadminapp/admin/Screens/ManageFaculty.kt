package com.example.gecbadminapp.admin.Screens

import FacultyData
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gecbadminapp.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import org.json.JSONObject
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFaculty() {
    var facultyList by remember { mutableStateOf<List<FacultyData>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFaculty by remember { mutableStateOf<FacultyData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("All") }
    var showDeleteConfirmation by remember { mutableStateOf<FacultyData?>(null) }
    var isDeletingFaculty by remember { mutableStateOf(false) }

    val departments = listOf("All", "Computer Engineering", "Electronics", "Mechanical", "Civil", "Electrical")
    val coroutineScope = rememberCoroutineScope()

    // Load faculty data
    LaunchedEffect(Unit) {
        loadFacultyData { faculty ->
            facultyList = faculty
            isLoading = false
        }
    }

    // Filter faculty based on search and department
    val filteredFaculty = facultyList.filter { faculty ->
        val matchesSearch = faculty.name.contains(searchQuery, ignoreCase = true) ||
                faculty.email.contains(searchQuery, ignoreCase = true) ||
                faculty.department.contains(searchQuery, ignoreCase = true)
        val matchesDepartment = selectedDepartment == "All" || faculty.department == selectedDepartment
        matchesSearch && matchesDepartment
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
                                Icons.Default.Groups,
                                contentDescription = "Faculty",
                                tint = Color(0xFF667eea),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Manage Faculty",
                                color = Color(0xFF1F2937),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
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
                                    contentDescription = "Add Faculty",
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

                // Enhanced Search and Filter Section
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
                        // Enhanced Search Field
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = {
                                    Text(
                                        "Search Faculty",
                                        color = Color(0xFF6B7280)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color(0xFF667eea)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF667eea),
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color(0xFF1F2937),
                                    unfocusedTextColor = Color(0xFF1F2937)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Filter by Department",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF374151),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Enhanced Department Filter
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(departments) { department ->
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selectedDepartment == department) {
                                            Color(0xFF667eea)
                                        } else {
                                            Color(0xFFF3F4F6)
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (selectedDepartment == department) 4.dp else 2.dp
                                    ),
                                    modifier = Modifier.clickable { selectedDepartment = department }
                                ) {
                                    Text(
                                        text = department,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                        color = if (selectedDepartment == department) {
                                            Color.White
                                        } else {
                                            Color(0xFF6B7280)
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedDepartment == department) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Medium
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Faculty List with enhanced styling
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF667eea),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Loading Faculty Data...",
                                    color = Color(0xFF6B7280),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else if (filteredFaculty.isEmpty()) {
                    EnhancedEmptyStateView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredFaculty) { faculty ->
                            EnhancedFacultyCard(
                                faculty = faculty,
                                onEdit = { selectedFaculty = it },
                                onDelete = { showDeleteConfirmation = it },
                                isDeleting = isDeletingFaculty && showDeleteConfirmation?.id == faculty.id
                            )
                        }
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog) {
        EnhancedAddEditFacultyDialog(
            faculty = null,
            onDismiss = { showAddDialog = false },
            onSave = { newFaculty ->
                saveFaculty(newFaculty) { savedFaculty ->
                    facultyList = facultyList + savedFaculty
                    showAddDialog = false
                }
            }
        )
    }

    selectedFaculty?.let { faculty ->
        EnhancedAddEditFacultyDialog(
            faculty = faculty,
            onDismiss = { selectedFaculty = null },
            onSave = { updatedFaculty ->
                updateFaculty(updatedFaculty) {
                    facultyList = facultyList.map {
                        if (it.id == updatedFaculty.id) updatedFaculty else it
                    }
                    selectedFaculty = null
                }
            }
        )
    }

    // Enhanced Delete Confirmation Dialog
    showDeleteConfirmation?.let { faculty ->
        AlertDialog(
            onDismissRequest = {
                if (!isDeletingFaculty) {
                    showDeleteConfirmation = null
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Delete Faculty",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }
            },
            text = {
                Column {
                    Text(
                        "Are you sure you want to delete ${faculty.name}?",
                        color = Color(0xFF374151),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone. The faculty record and associated image will be permanently deleted.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            },
            confirmButton = {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    modifier = Modifier.clickable(enabled = !isDeletingFaculty) {
                        coroutineScope.launch {
                            isDeletingFaculty = true
                            deleteFacultyWithImage(faculty) { success ->
                                if (success) {
                                    facultyList = facultyList.filter { f -> f.id != faculty.id }
                                }
                                isDeletingFaculty = false
                                showDeleteConfirmation = null
                            }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDeletingFaculty) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Deleting...",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Text(
                                "Delete",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            dismissButton = {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF3F4F6)
                    ),
                    modifier = Modifier.clickable(enabled = !isDeletingFaculty) {
                        showDeleteConfirmation = null
                    }
                ) {
                    Text(
                        "Cancel",
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun EnhancedFacultyCard(
    faculty: FacultyData,
    onEdit: (FacultyData) -> Unit,
    onDelete: (FacultyData) -> Unit,
    isDeleting: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(faculty) }
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced Faculty Image
            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(faculty.imageUrl.ifEmpty { "https://via.placeholder.com/150" })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Faculty Image",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Faculty Details with enhanced styling
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = faculty.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF667eea).copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = faculty.designation,
                        fontSize = 12.sp,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = faculty.department,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = faculty.email,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // Enhanced Action Buttons
            Column {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = !isDeleting) { onEdit(faculty) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = if (isDeleting) Color.Gray else Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEF4444).copy(alpha = 0.1f)
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = !isDeleting) { onDelete(faculty) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFEF4444),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAddEditFacultyDialog(
    faculty: FacultyData?,
    onDismiss: () -> Unit,
    onSave: (FacultyData) -> Unit
) {
    var name by remember { mutableStateOf(faculty?.name ?: "") }
    var email by remember { mutableStateOf(faculty?.email ?: "") }
    var phone by remember { mutableStateOf(faculty?.phone ?: "") }
    var department by remember { mutableStateOf(faculty?.department ?: "") }
    var designation by remember { mutableStateOf(faculty?.designation ?: "") }
    var qualification by remember { mutableStateOf(faculty?.qualification ?: "") }
    var experience by remember { mutableStateOf(faculty?.experience ?: "") }
    var specialization by remember { mutableStateOf(faculty?.specialization ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = if (faculty == null) "Add Faculty" else "Edit Faculty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    // Image Selection with Upload Progress
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box {
                            AsyncImage(
                                model = selectedImageUri ?: faculty?.imageUrl ?: "https://via.placeholder.com/150x150/e2e8f0/64748b?text=Faculty",
                                contentDescription = "Faculty Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.1f))
                                    .clickable {
                                        if (!isUploading) {
                                            imagePickerLauncher.launch("image/*")
                                        }
                                    },
                                contentScale = ContentScale.Crop
                            )

                            if (isUploading) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isUploading) {
                            Text(
                                text = uploadProgress,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            TextButton(
                                onClick = { imagePickerLauncher.launch("image/*") }
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Select Image")
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    )
                }

                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        }
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null)
                        }
                    )
                }

                item {
                    var expanded by remember { mutableStateOf(false) }
                    val departments = listOf("Computer Engineering", "Electronics & Communication", "Mechanical Engineering", "Civil Engineering", "Electrical Engineering", "Information Technology")

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = department,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Department *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            leadingIcon = {
                                Icon(Icons.Default.School, contentDescription = null)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            departments.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text(dept) },
                                    onClick = {
                                        department = dept
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = designation,
                        onValueChange = { designation = it },
                        label = { Text("Designation *") },
                        placeholder = { Text("e.g., Assistant Professor, HOD") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Work, contentDescription = null)
                        }
                    )
                }

                item {
                    OutlinedTextField(
                        value = qualification,
                        onValueChange = { qualification = it },
                        label = { Text("Qualification") },
                        placeholder = { Text("e.g., M.Tech, Ph.D") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        leadingIcon = {
                            Icon(Icons.Default.MenuBook, contentDescription = null)
                        }
                    )
                }

                item {
                    OutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text("Experience (Years)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Timeline, contentDescription = null)
                        }
                    )
                }

                item {
                    OutlinedTextField(
                        value = specialization,
                        onValueChange = { specialization = it },
                        label = { Text("Specialization") },
                        placeholder = { Text("e.g., Machine Learning, Data Structures") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        leadingIcon = {
                            Icon(Icons.Default.Psychology, contentDescription = null)
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isUploading
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (name.isNotBlank() && email.isNotBlank() && department.isNotBlank() && designation.isNotBlank()) {
                                    coroutineScope.launch {
                                        isUploading = true
                                        uploadProgress = "Preparing..."

                                        var imageUrl = faculty?.imageUrl ?: ""

                                        // If there's a new image selected and we're editing
                                        if (selectedImageUri != null) {
                                            uploadProgress = "Uploading new image..."

                                            // Delete old image if updating existing faculty
                                            if (faculty != null && faculty.imageUrl.isNotEmpty()) {
                                                uploadProgress = "Removing old image..."
                                                deleteImageFromCloudinary(faculty.imageUrl)
                                            }

                                            // Upload new image
                                            uploadProgress = "Uploading new image..."
                                            imageUrl = uploadImageToCloudinary(selectedImageUri!!, context)
                                        }

                                        uploadProgress = "Saving faculty data..."

                                        val newFaculty = FacultyData(
                                            id = faculty?.id ?: UUID.randomUUID().toString(),
                                            name = name.trim(),
                                            email = email.trim().lowercase(),
                                            phone = phone.trim(),
                                            department = department,
                                            designation = designation.trim(),
                                            qualification = qualification.trim(),
                                            experience = experience.trim(),
                                            specialization = specialization.trim(),
                                            imageUrl = imageUrl,
                                            joiningDate = faculty?.joiningDate ?: System.currentTimeMillis().toString(),
                                            isActive = faculty?.isActive ?: true
                                        )

                                        onSave(newFaculty)
                                        isUploading = false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isUploading && name.isNotBlank() && email.isNotBlank() && department.isNotBlank() && designation.isNotBlank()
                        ) {
                            if (isUploading) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Saving...")
                                }
                            } else {
                                Text(if (faculty == null) "Add Faculty" else "Update Faculty")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedEmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Faculty Found",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Add faculty members to get started",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

// Firebase Functions
private fun loadFacultyData(onComplete: (List<FacultyData>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("faculty")
        .get()
        .addOnSuccessListener { documents ->
            val facultyList = documents.mapNotNull { doc ->
                doc.toObject(FacultyData::class.java).copy(id = doc.id)
            }
            onComplete(facultyList)
        }
        .addOnFailureListener {
            onComplete(emptyList())
        }
}

private fun saveFaculty(faculty: FacultyData, onComplete: (FacultyData) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("faculty")
        .add(faculty)
        .addOnSuccessListener { documentReference ->
            onComplete(faculty.copy(id = documentReference.id))
        }
        .addOnFailureListener {
            // Handle error
        }
}

private fun updateFaculty(faculty: FacultyData, onComplete: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("faculty")
        .document(faculty.id)
        .set(faculty)
        .addOnSuccessListener { onComplete() }
        .addOnFailureListener {
            // Handle error
        }
}

private suspend fun deleteFacultyWithImage(faculty: FacultyData, onComplete: (Boolean) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            // First delete the image from Cloudinary if it exists
            if (faculty.imageUrl.isNotEmpty() && faculty.imageUrl != "https://via.placeholder.com/150") {
                val imageDeleted = deleteImageFromCloudinary(faculty.imageUrl)
                if (!imageDeleted) {
                    println("Warning: Failed to delete image from Cloudinary")
                    // Continue with faculty deletion even if image deletion fails
                }
            }

            // Then delete the faculty record from Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("faculty")
                .document(faculty.id)
                .delete()
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener { exception ->
                    println("Error deleting faculty: ${exception.message}")
                    onComplete(false)
                }
        } catch (e: Exception) {
            println("Error in deleteFacultyWithImage: ${e.message}")
            onComplete(false)
        }
    }
}

// Cloudinary Configuration
private const val CLOUDINARY_CLOUD_NAME = "dmjori0fh"
private const val CLOUDINARY_API_KEY = "665783445794731"
private const val CLOUDINARY_API_SECRET = "JG0TcrTAN4f-uCCwW8CkFEDlb0o"
private const val CLOUDINARY_UPLOAD_PRESET = "manage_faculty" // Your upload preset

// Function to extract public_id from Cloudinary URL
private fun extractPublicIdFromUrl(imageUrl: String): String? {
    return try {
        // Example URL: https://res.cloudinary.com/dmjori0fh/image/upload/v1234567890/faculty_images/abc123.jpg
        val regex = Regex("/v\\d+/(.+)\\.[^.]+$")
        val matchResult = regex.find(imageUrl)
        matchResult?.groupValues?.get(1)
    } catch (e: Exception) {
        null
    }
}

// Function to delete image from Cloudinary
private suspend fun deleteImageFromCloudinary(imageUrl: String): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val publicId = extractPublicIdFromUrl(imageUrl)
            if (publicId.isNullOrEmpty()) {
                println("Could not extract public_id from URL: $imageUrl")
                return@withContext false
            }

            val timestamp = System.currentTimeMillis() / 1000
            val stringToSign = "public_id=${publicId}&timestamp=${timestamp}${CLOUDINARY_API_SECRET}"
            val signature = MessageDigest.getInstance("SHA-1")
                .digest(stringToSign.toByteArray())
                .joinToString("") { "%02x".format(it) }

            val url = URL("https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/destroy")
            val connection = url.openConnection() as HttpURLConnection

            val postData = "public_id=${publicId}&timestamp=${timestamp}&api_key=${CLOUDINARY_API_KEY}&signature=${signature}"

            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                setRequestProperty("Content-Length", postData.length.toString())
                doOutput = true
                doInput = true
            }

            // Write request body
            connection.outputStream.use { output ->
                output.write(postData.toByteArray())
                output.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)
                val result = jsonObject.getString("result")
                result == "ok"
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() }
                println("Cloudinary delete error: $errorResponse")
                false
            }
        } catch (e: Exception) {
            println("Error deleting image from Cloudinary: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}

// Cloudinary Upload Function
private suspend fun uploadImageToCloudinary(uri: Uri, context: android.content.Context): String {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return@withContext ""
            inputStream?.close()

            // Convert to Base64
            val base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
            val imageData = "data:image/jpeg;base64,$base64Image"

            // Create multipart request
            val boundary = "----WebKitFormBoundary${System.currentTimeMillis()}"
            val requestBody = buildCloudinaryRequestBody(imageData, boundary)

            val url = URL("https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload")
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                doOutput = true
                doInput = true
            }

            // Write request body
            connection.outputStream.use { output ->
                output.write(requestBody.toByteArray())
                output.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                parseCloudinaryResponse(response)
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() }
                println("Cloudinary upload error: $errorResponse")
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}

private fun buildCloudinaryRequestBody(imageData: String, boundary: String): String {
    val timestamp = System.currentTimeMillis() / 1000
    val signature = generateSignature(timestamp)

    return buildString {
        append("--$boundary\r\n")
        append("Content-Disposition: form-data; name=\"file\"\r\n\r\n")
        append("$imageData\r\n")

        append("--$boundary\r\n")
        append("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n")
        append("$CLOUDINARY_UPLOAD_PRESET\r\n")

        append("--$boundary\r\n")
        append("Content-Disposition: form-data; name=\"api_key\"\r\n\r\n")
        append("$CLOUDINARY_API_KEY\r\n")

        append("--$boundary\r\n")
        append("Content-Disposition: form-data; name=\"timestamp\"\r\n\r\n")
        append("$timestamp\r\n")

        append("--$boundary\r\n")
        append("Content-Disposition: form-data; name=\"signature\"\r\n\r\n")
        append("$signature\r\n")

        append("--$boundary\r\n")
        append("Content-Disposition: form-data; name=\"folder\"\r\n\r\n")
        append("faculty_images\r\n")

        append("--$boundary\r\n")
        append("Content-Disposition: form-data; name=\"resource_type\"\r\n\r\n")
        append("image\r\n")

        append("--$boundary--\r\n")
    }
}

private fun generateSignature(timestamp: Long): String {
    val params = "folder=faculty_images&timestamp=$timestamp&upload_preset=$CLOUDINARY_UPLOAD_PRESET$CLOUDINARY_API_SECRET"
    return MessageDigest.getInstance("SHA-1")
        .digest(params.toByteArray())
        .joinToString("") { "%02x".format(it) }
}
private fun parseCloudinaryResponse(response: String): String {
    return try {
        val jsonObject = JSONObject(response)
        jsonObject.getString("secure_url")
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}