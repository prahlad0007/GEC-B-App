
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore

// Data class for Faculty (renamed to FacultyData to avoid naming conflicts)
data class FacultyData(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val department: String = "",
    val designation: String = "",
    val qualification: String = "",
    val experience: String = "",
    val specialization: String = "",
    val imageUrl: String = "",
    val joiningDate: String = "",
    val isActive: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Faculty() {
    var facultyList by remember { mutableStateOf<List<FacultyData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("All") }
    var viewMode by remember { mutableStateOf("Grid") } // Grid or List
    var selectedFaculty by remember { mutableStateOf<FacultyData?>(null) }

    val departments = listOf("All", "Computer Engineering", "Electronics & Communication",
        "Mechanical Engineering", "Civil Engineering", "Electrical Engineering", "Information Technology")

    // Load faculty data
    LaunchedEffect(Unit) {
        println("Starting to load faculty data...")
        loadFacultyData { faculty ->
            println("Received ${faculty.size} faculty members")
            facultyList = faculty // Already filtered in loadFacultyData
            isLoading = false
        }
    }

    // Filter faculty based on search and department
    val filteredFaculty = facultyList.filter { faculty ->
        val matchesSearch = faculty.name.contains(searchQuery, ignoreCase = true) ||
                faculty.designation.contains(searchQuery, ignoreCase = true) ||
                faculty.department.contains(searchQuery, ignoreCase = true) ||
                faculty.specialization.contains(searchQuery, ignoreCase = true)
        val matchesDepartment = selectedDepartment == "All" || faculty.department.contains(selectedDepartment, ignoreCase = true)
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Our Faculty",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Meet our dedicated educators",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // View Mode Toggle
                    Card(
                        shape = RoundedCornerShape(50.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp)
                        ) {
                            IconButton(
                                onClick = { viewMode = "Grid" },
                                modifier = Modifier
                                    .background(
                                        if (viewMode == "Grid") Color.White else Color.Transparent,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.GridView,
                                    contentDescription = "Grid View",
                                    tint = if (viewMode == "Grid") Color(0xFF667eea) else Color.White
                                )
                            }
                            IconButton(
                                onClick = { viewMode = "List" },
                                modifier = Modifier
                                    .background(
                                        if (viewMode == "List") Color.White else Color.Transparent,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.ViewList,
                                    contentDescription = "List View",
                                    tint = if (viewMode == "List") Color(0xFF667eea) else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Search Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search faculty by name, designation, or specialization...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF667eea))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color(0xFF667eea)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Department Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(departments) { department ->
                        FilterChip(
                            onClick = { selectedDepartment = department },
                            label = {
                                Text(
                                    text = department,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            selected = selectedDepartment == department,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White,
                                selectedLabelColor = Color(0xFF667eea),
                                containerColor = Color.White.copy(alpha = 0.3f),
                                labelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedDepartment == department,
                                borderColor = Color.White.copy(alpha = 0.5f),
                                selectedBorderColor = Color.White
                            )
                        )
                    }
                }
            }

            // Faculty Content
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
                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatsCard(
                            title = "Total Faculty",
                            value = filteredFaculty.size.toString(),
                            icon = Icons.Default.Person,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        StatsCard(
                            title = "Departments",
                            value = filteredFaculty.map { it.department }.distinct().size.toString(),
                            icon = Icons.Default.School,
                            color = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1f)
                        )
                        StatsCard(
                            title = "Professors",
                            value = filteredFaculty.count { it.designation.contains("Professor", ignoreCase = true) }.toString(),
                            icon = Icons.Default.WorkspacePremium,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Faculty List/Grid
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF667eea),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Loading faculty data...",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    } else {
                        // Debug info
                        Text(
                            text = "Debug: Total faculty: ${facultyList.size}, Filtered: ${filteredFaculty.size}",
                            fontSize = 12.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )

                        if (filteredFaculty.isEmpty()) {
                            EmptyFacultyView()
                        } else {
                            if (viewMode == "Grid") {
                                FacultyGridView(
                                    facultyList = filteredFaculty,
                                    onFacultyClick = { selectedFaculty = it }
                                )
                            } else {
                                FacultyListView(
                                    facultyList = filteredFaculty,
                                    onFacultyClick = { selectedFaculty = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Faculty Detail Dialog
    selectedFaculty?.let { faculty ->
        FacultyDetailDialog(
            faculty = faculty,
            onDismiss = { selectedFaculty = null }
        )
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FacultyGridView(
    facultyList: List<FacultyData>,
    onFacultyClick: (FacultyData) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(facultyList.chunked(2)) { rowFaculty ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowFaculty.forEach { faculty ->
                    FacultyGridCard(
                        faculty = faculty,
                        onClick = { onFacultyClick(faculty) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number
                if (rowFaculty.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun FacultyListView(
    facultyList: List<FacultyData>,
    onFacultyClick: (FacultyData) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(facultyList) { faculty ->
            FacultyListCard(
                faculty = faculty,
                onClick = { onFacultyClick(faculty) }
            )
        }
    }
}

@Composable
fun FacultyGridCard(
    faculty: FacultyData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Faculty Image
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(faculty.imageUrl.ifEmpty { "https://via.placeholder.com/150x150/667eea/ffffff?text=${faculty.name.take(2)}" })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Faculty Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                            )
                        ),
                    contentScale = ContentScale.Crop
                )

                // Online status indicator
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF10B981), CircleShape)
                        .align(Alignment.BottomEnd)
                        .border(3.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = faculty.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = faculty.designation,
                fontSize = 13.sp,
                color = Color(0xFF667eea),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = faculty.department,
                fontSize = 11.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick info chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (faculty.experience.isNotEmpty()) {
                    Chip(
                        text = "${faculty.experience}y",
                        color = Color(0xFFF59E0B)
                    )
                }
                if (faculty.qualification.isNotEmpty()) {
                    Chip(
                        text = faculty.qualification.take(6),
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
fun FacultyListCard(
    faculty: FacultyData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Faculty Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(faculty.imageUrl.ifEmpty { "https://via.placeholder.com/150x150/667eea/ffffff?text=${faculty.name.take(2)}" })
                    .crossfade(true)
                    .build(),
                contentDescription = "Faculty Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Faculty Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = faculty.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = faculty.designation,
                    fontSize = 14.sp,
                    color = Color(0xFF667eea),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = faculty.department,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )

                if (faculty.specialization.isNotEmpty()) {
                    Text(
                        text = "Specialization: ${faculty.specialization}",
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Contact Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (faculty.email.isNotEmpty()) {
                    IconButton(
                        onClick = { /* Handle email click */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (faculty.phone.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = { /* Handle phone click */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF3B82F6).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Chip(
    text: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FacultyDetailDialog(
    faculty: FacultyData,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
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
                    // Header with image and basic info
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(faculty.imageUrl.ifEmpty { "https://via.placeholder.com/200x200/667eea/ffffff?text=${faculty.name.take(2)}" })
                                .crossfade(true)
                                .build(),
                            contentDescription = "Faculty Image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                                    )
                                ),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = faculty.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = faculty.designation,
                            fontSize = 16.sp,
                            color = Color(0xFF667eea),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = faculty.department,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                }

                // Contact Information
                if (faculty.email.isNotEmpty() || faculty.phone.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "Contact Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (faculty.email.isNotEmpty()) {
                                ContactInfoRow(
                                    icon = Icons.Default.Email,
                                    label = "Email",
                                    value = faculty.email,
                                    color = Color(0xFF10B981)
                                )
                            }

                            if (faculty.phone.isNotEmpty()) {
                                ContactInfoRow(
                                    icon = Icons.Default.Phone,
                                    label = "Phone",
                                    value = faculty.phone,
                                    color = Color(0xFF3B82F6)
                                )
                            }
                        }
                    }
                }

                // Professional Information
                item {
                    Column {
                        Text(
                            text = "Professional Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (faculty.qualification.isNotEmpty()) {
                            DetailRow(label = "Qualification", value = faculty.qualification)
                        }

                        if (faculty.experience.isNotEmpty()) {
                            DetailRow(label = "Experience", value = "${faculty.experience} years")
                        }

                        if (faculty.specialization.isNotEmpty()) {
                            DetailRow(label = "Specialization", value = faculty.specialization)
                        }
                    }
                }

                item {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667eea)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Close",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF1F2937)
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF1F2937)
        )
    }
}

@Composable
fun EmptyFacultyView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFF9CA3AF)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Faculty Found",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Try adjusting your search criteria or filters",
            fontSize = 14.sp,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center
        )
    }
}

// Firebase Functions
private fun loadFacultyData(onComplete: (List<FacultyData>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    // First try to get all documents without any filter
    db.collection("faculty")
        .get()
        .addOnSuccessListener { documents ->
            println("Total documents found: ${documents.size()}")

            val facultyList = documents.mapNotNull { doc ->
                println("Document ID: ${doc.id}")
                println("Document data: ${doc.data}")

                try {
                    val faculty = doc.toObject(FacultyData::class.java).copy(id = doc.id)
                    println("Parsed faculty: ${faculty.name}, isActive: ${faculty.isActive}")
                    faculty
                } catch (e: Exception) {
                    println("Error parsing document ${doc.id}: ${e.message}")
                    null
                }
            }

            // Filter active faculty here instead of in query
            val activeFaculty = facultyList.filter { it.isActive }
            println("Active faculty count: ${activeFaculty.size}")

            onComplete(activeFaculty)
        }
        .addOnFailureListener { exception ->
            println("Error loading faculty data: ${exception.message}")
            exception.printStackTrace()
            onComplete(emptyList())
        }
}