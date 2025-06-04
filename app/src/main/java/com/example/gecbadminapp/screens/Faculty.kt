import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

// Data class for Faculty
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

// Responsive dimension class
@Composable
fun rememberResponsiveDimensions(): ResponsiveDimensions {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    return remember(configuration.screenWidthDp, configuration.screenHeightDp) {
        ResponsiveDimensions(
            screenWidth = configuration.screenWidthDp.dp,
            screenHeight = configuration.screenHeightDp.dp,
            density = density.density
        )
    }
}
data class ResponsiveDimensions(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val density: Float
) {
    val isTablet: Boolean = screenWidth >= 600.dp
    val isLandscape: Boolean = screenWidth > screenHeight

    // Responsive padding values
    val screenPadding: Dp = if (isTablet) 32.dp else 16.dp
    val cardPadding: Dp = if (isTablet) 20.dp else 16.dp
    val smallPadding: Dp = if (isTablet) 12.dp else 8.dp
    val mediumPadding: Dp = if (isTablet) 16.dp else 12.dp

    // Responsive text sizes
    val titleTextSize = if (isTablet) 36.sp else 28.sp
    val subtitleTextSize = if (isTablet) 18.sp else 14.sp
    val bodyTextSize = if (isTablet) 16.sp else 14.sp
    val captionTextSize = if (isTablet) 14.sp else 12.sp
    val smallTextSize = if (isTablet) 12.sp else 10.sp

    // Card dimensions
    val facultyImageSize: Dp = if (isTablet) 100.dp else 80.dp
    val facultyImageSizeSmall: Dp = if (isTablet) 80.dp else 60.dp
    val statsCardHeight: Dp = if (isTablet) 120.dp else 100.dp

    // FIXED: Better grid columns logic - Always show at least 2 columns on phones
    val gridColumns: Int = when {
        screenWidth >= 900.dp -> 4
        screenWidth >= 600.dp -> 3
        screenWidth >= 350.dp -> 2  // Changed from 400.dp to 350.dp
        else -> 2  // Changed from 1 to 2 - always show 2 columns even on very small screens
    }

    // Bottom sheet height
    val bottomSheetMaxHeight: Dp = screenHeight * 0.85f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Faculty() {
    var facultyList by remember { mutableStateOf<List<FacultyData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("All") }
    var viewMode by remember { mutableStateOf("Grid") }
    var selectedFaculty by remember { mutableStateOf<FacultyData?>(null) }

    val dimensions = rememberResponsiveDimensions()

    val departments = listOf(
        "All", "Computer Engineering",   "Electronics & Communication",
        "Mechanical Engineering", "Civil Engineering", "Electrical Engineering", "Information Technology"
    )


    LaunchedEffect(Unit) {
        loadFacultyData { faculty ->
            facultyList = faculty
            isLoading = false
        }
    }

    // Filter faculty
    val filteredFaculty = facultyList.filter { faculty ->
        val matchesSearch = faculty.name.contains(searchQuery, ignoreCase = true) ||
                faculty.designation.contains(searchQuery, ignoreCase = true) ||
                faculty.department.contains(searchQuery, ignoreCase = true) ||
                faculty.specialization.contains(searchQuery, ignoreCase = true)
        val matchesDepartment = selectedDepartment == "All" ||
                faculty.department.contains(selectedDepartment, ignoreCase = true)
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
            // Header Section - Responsive
            ResponsiveHeader(
                dimensions = dimensions,
                viewMode = viewMode,
                onViewModeChange = { viewMode = it },
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                departments = departments,
                selectedDepartment = selectedDepartment,
                onDepartmentChange = { selectedDepartment = it }
            )

            // Content Section
            ResponsiveContent(
                dimensions = dimensions,
                isLoading = isLoading,
                filteredFaculty = filteredFaculty,
                viewMode = viewMode,
                onFacultyClick = { selectedFaculty = it }
            )
        }
    }

    // Faculty Detail Sheet
    selectedFaculty?.let { faculty ->
        FacultyDetailBottomSheet(
            faculty = faculty,
            dimensions = dimensions,
            onDismiss = { selectedFaculty = null }
        )
    }
}

@Composable
fun ResponsiveHeader(
    dimensions: ResponsiveDimensions,
    viewMode: String,
    onViewModeChange: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    departments: List<String>,
    selectedDepartment: String,
    onDepartmentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.screenPadding)
    ) {
        // Title and View Toggle in same row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderTitle(dimensions = dimensions)
            ViewModeToggle(dimensions = dimensions, viewMode = viewMode, onViewModeChange = onViewModeChange)
        }

        Spacer(modifier = Modifier.height(dimensions.cardPadding))

        // Search Bar - Responsive
        ResponsiveSearchBar(
            dimensions = dimensions,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange
        )

        Spacer(modifier = Modifier.height(dimensions.mediumPadding))

        // Department Filter Chips - Responsive
        ResponsiveDepartmentFilters(
            dimensions = dimensions,
            departments = departments,
            selectedDepartment = selectedDepartment,
            onDepartmentChange = onDepartmentChange
        )
    }

}

@Composable
fun HeaderTitle(dimensions: ResponsiveDimensions) {
    Column {
        Text(
            text = "Our Faculty",
            fontSize = dimensions.titleTextSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Meet our dedicated educators",
            fontSize = dimensions.subtitleTextSize,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun ViewModeToggle(
    dimensions: ResponsiveDimensions,
    viewMode: String,
    onViewModeChange: (String) -> Unit
) {
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
                onClick = { onViewModeChange("Grid") },
                modifier = Modifier
                    .size(if (dimensions.isTablet) 48.dp else 40.dp)
                    .background(
                        if (viewMode == "Grid") Color.White else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.GridView,
                    contentDescription = "Grid View",
                    tint = if (viewMode == "Grid") Color(0xFF667eea) else Color.White,
                    modifier = Modifier.size(if (dimensions.isTablet) 24.dp else 20.dp)
                )
            }
            IconButton(
                onClick = { onViewModeChange("List") },
                modifier = Modifier
                    .size(if (dimensions.isTablet) 48.dp else 40.dp)
                    .background(
                        if (viewMode == "List") Color.White else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.ViewList,
                    contentDescription = "List View",
                    tint = if (viewMode == "List") Color(0xFF667eea) else Color.White,
                    modifier = Modifier.size(if (dimensions.isTablet) 24.dp else 20.dp)
                )
            }
        }
    }
}

@Composable
fun ResponsiveSearchBar(
    dimensions: ResponsiveDimensions,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (dimensions.isTablet) 30.dp else 25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = if (dimensions.isTablet) "Search faculty by name, designation, or specialization..."
                    else "Search faculty...",
                    fontSize = dimensions.bodyTextSize
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(if (dimensions.isTablet) 24.dp else 20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color(0xFF667eea)
            ),
            shape = RoundedCornerShape(if (dimensions.isTablet) 30.dp else 25.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = dimensions.bodyTextSize)
        )
    }
}

@Composable
fun ResponsiveDepartmentFilters(
    dimensions: ResponsiveDimensions,
    departments: List<String>,
    selectedDepartment: String,
    onDepartmentChange: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(dimensions.smallPadding)
    ) {
        items(departments) { department ->
            FilterChip(
                onClick = { onDepartmentChange(department) },
                label = {
                    Text(
                        text = if (dimensions.isTablet || department.length <= 15) department
                        else department.take(12) + "...",
                        fontSize = dimensions.captionTextSize,
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
                ),
                modifier = Modifier.height(if (dimensions.isTablet) 40.dp else 36.dp)
            )
        }
    }
}

@Composable
fun ResponsiveContent(
    dimensions: ResponsiveDimensions,
    isLoading: Boolean,
    filteredFaculty: List<FacultyData>,
    viewMode: String,
    onFacultyClick: (FacultyData) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.screenPadding),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.cardPadding)
        ) {
            // Stats Row - Responsive
            ResponsiveStatsRow(
                dimensions = dimensions,
                filteredFaculty = filteredFaculty
            )

            Spacer(modifier = Modifier.height(dimensions.cardPadding))

            // Faculty Content
            if (isLoading) {
                LoadingView(dimensions = dimensions)
            } else if (filteredFaculty.isEmpty()) {
                EmptyFacultyView(dimensions = dimensions)
            } else {
                when (viewMode) {
                    "Grid" -> ResponsiveFacultyGrid(
                        dimensions = dimensions,
                        facultyList = filteredFaculty,
                        onFacultyClick = onFacultyClick
                    )
                    "List" -> ResponsiveFacultyList(
                        dimensions = dimensions,
                        facultyList = filteredFaculty,
                        onFacultyClick = onFacultyClick
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveStatsRow(
    dimensions: ResponsiveDimensions,
    filteredFaculty: List<FacultyData>
) {
    // Calculate stats with proper handling of empty data
    val totalFaculty = filteredFaculty.size
    val uniqueDepartments = filteredFaculty.map { it.department }.distinct().filter { it.isNotEmpty() }.size
    val professorsCount = filteredFaculty.count {
        it.designation.contains("Professor", ignoreCase = true) ||
                it.designation.contains("Prof", ignoreCase = true) ||
                it.designation.contains("HOD", ignoreCase = true)
    }

    val statsData = listOf(
        Triple("Total Faculty", totalFaculty.toString(), Icons.Default.Person to Color(0xFF10B981)),
        Triple("Departments", uniqueDepartments.toString(), Icons.Default.School to Color(0xFF3B82F6)),
        Triple("Professors", professorsCount.toString(), Icons.Default.WorkspacePremium to Color(0xFFF59E0B))
    )


}

@Composable
fun ResponsiveFacultyGrid(
    dimensions: ResponsiveDimensions,
    facultyList: List<FacultyData>,
    onFacultyClick: (FacultyData) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(dimensions.gridColumns),
        verticalItemSpacing = dimensions.mediumPadding,
        horizontalArrangement = Arrangement.spacedBy(dimensions.mediumPadding),
        contentPadding = PaddingValues(vertical = dimensions.smallPadding)
    ) {
        items(facultyList) { faculty ->
            ResponsiveFacultyGridCardDynamic(
                faculty = faculty,
                dimensions = dimensions,
                onClick = { onFacultyClick(faculty) }
            )
        }
    }
}

@Composable
fun ResponsiveFacultyList(
    dimensions: ResponsiveDimensions,
    facultyList: List<FacultyData>,
    onFacultyClick: (FacultyData) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensions.mediumPadding),
        contentPadding = PaddingValues(vertical = dimensions.smallPadding)
    ) {
        items(facultyList) { faculty ->
            ResponsiveFacultyListCard(
                faculty = faculty,
                dimensions = dimensions,
                onClick = { onFacultyClick(faculty) }
            )
        }
    }
}
// Option 3: Dynamic height with minimum constraint
@Composable
fun ResponsiveFacultyGridCardDynamic(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = if (dimensions.isTablet) 240.dp else 200.dp,
                max = if (dimensions.isTablet) 300.dp else 260.dp
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Faculty Image
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(faculty.imageUrl.ifEmpty {
                            "https://via.placeholder.com/150x150/667eea/ffffff?text=${faculty.name.take(2)}"
                        })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Faculty Image",
                    modifier = Modifier
                        .size(dimensions.facultyImageSize)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                            )
                        ),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .size(if (dimensions.isTablet) 24.dp else 20.dp)
                        .background(Color(0xFF10B981), CircleShape)
                        .align(Alignment.BottomEnd)
                        .border(3.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(dimensions.mediumPadding))

            // Text content with smart truncation
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (faculty.name.length > 20 && !dimensions.isTablet) {
                        faculty.name.take(18) + "..."
                    } else faculty.name,
                    fontSize = dimensions.bodyTextSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center,
                    maxLines = if (dimensions.isTablet) 2 else 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = faculty.designation,
                    fontSize = dimensions.captionTextSize,
                    color = Color(0xFF667eea),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = faculty.department,
                    fontSize = dimensions.smallTextSize,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(dimensions.smallPadding))

            // Quick info chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (faculty.experience.isNotEmpty()) {
                    ResponsiveChip(
                        text = "${faculty.experience}y",
                        color = Color(0xFFF59E0B),
                        dimensions = dimensions,
                        modifier = Modifier.weight(1f, false)
                    )
                }
                if (faculty.qualification.isNotEmpty()) {
                    ResponsiveChip(
                        text = faculty.qualification.take(if (dimensions.isTablet) 8 else 6),
                        color = Color(0xFF10B981),
                        dimensions = dimensions,
                        modifier = Modifier.weight(1f, false)
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveFacultyListCard(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions,
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
                .padding(dimensions.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Faculty Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(faculty.imageUrl.ifEmpty {
                        "https://via.placeholder.com/150x150/667eea/ffffff?text=${faculty.name.take(2)}"
                    })
                    .crossfade(true)
                    .build(),
                contentDescription = "Faculty Image",
                modifier = Modifier
                    .size(dimensions.facultyImageSizeSmall)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(dimensions.mediumPadding))

            // Faculty Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = faculty.name,
                    fontSize = dimensions.bodyTextSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = faculty.designation,
                    fontSize = dimensions.captionTextSize,
                    color = Color(0xFF667eea),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = faculty.department,
                    fontSize = dimensions.smallTextSize,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (faculty.specialization.isNotEmpty() && dimensions.isTablet) {
                    Text(
                        text = "Specialization: ${faculty.specialization}",
                        fontSize = dimensions.smallTextSize,
                        color = Color(0xFF9CA3AF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Contact Actions - Responsive
            if (dimensions.isTablet) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensions.smallPadding)
                ) {
                    if (faculty.email.isNotEmpty()) {
                        ContactActionButton(
                            icon = Icons.Default.Email,
                            color = Color(0xFF10B981),
                            dimensions = dimensions,
                            onClick = { /* Handle email */ }
                        )
                    }
                    if (faculty.phone.isNotEmpty()) {
                        ContactActionButton(
                            icon = Icons.Default.Phone,
                            color = Color(0xFF3B82F6),
                            dimensions = dimensions,
                            onClick = { /* Handle phone */ }
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (faculty.email.isNotEmpty()) {
                        ContactActionButton(
                            icon = Icons.Default.Email,
                            color = Color(0xFF10B981),
                            dimensions = dimensions,
                            onClick = { /* Handle email */ }
                        )
                    }
                    if (faculty.phone.isNotEmpty()) {
                        ContactActionButton(
                            icon = Icons.Default.Phone,
                            color = Color(0xFF3B82F6),
                            dimensions = dimensions,
                            onClick = { /* Handle phone */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    dimensions: ResponsiveDimensions,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(if (dimensions.isTablet) 44.dp else 36.dp)
            .background(color.copy(alpha = 0.1f), CircleShape)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(if (dimensions.isTablet) 20.dp else 16.dp)
        )
    }
}

@Composable
fun ResponsiveChip(
    text: String,
    color: Color,
    dimensions: ResponsiveDimensions,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = dimensions.smallPadding, vertical = 4.dp),
            fontSize = dimensions.smallTextSize,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LoadingView(dimensions: ResponsiveDimensions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(if (dimensions.isTablet) 60.dp else 48.dp),
            color = Color(0xFF667eea),
            strokeWidth = if (dimensions.isTablet) 6.dp else 4.dp
        )
        Spacer(modifier = Modifier.height(dimensions.mediumPadding))
        Text(
            text = "Loading Faculty...",
            fontSize = dimensions.bodyTextSize,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyFacultyView(dimensions: ResponsiveDimensions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(if (dimensions.isTablet) 100.dp else 80.dp),
            tint = Color(0xFF9CA3AF)
        )
        Spacer(modifier = Modifier.height(dimensions.mediumPadding))
        Text(
            text = "No Faculty Found",
            fontSize = if (dimensions.isTablet) 24.sp else 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensions.smallPadding))
        Text(
            text = if (dimensions.isTablet)
                "Try adjusting your search criteria or department filters to find faculty members"
            else "Try adjusting your search or filters",
            fontSize = dimensions.captionTextSize,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center,
            lineHeight = if (dimensions.isTablet) 20.sp else 16.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacultyDetailBottomSheet(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = Modifier.heightIn(max = dimensions.bottomSheetMaxHeight),
        containerColor = Color.White,
        contentColor = Color(0xFF1F2937),
        dragHandle = {
            Card(
                modifier = Modifier
                    .width(60.dp)
                    .height(6.dp)
                    .padding(vertical = dimensions.smallPadding),
                shape = RoundedCornerShape(3.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E7EB))
            ) {}
        }
    ) {
        ResponsiveFacultyDetailContent(
            faculty = faculty,
            dimensions = dimensions,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun ResponsiveFacultyDetailContent(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions,
    onDismiss: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.screenPadding),
        verticalArrangement = Arrangement.spacedBy(dimensions.mediumPadding),
        contentPadding = PaddingValues(bottom = dimensions.screenPadding)
    ) {
        // Profile Header
        item {
            ProfileHeader(faculty = faculty, dimensions = dimensions)
        }

        // Quick Info Cards
        item {
            QuickInfoSection(faculty = faculty, dimensions = dimensions)
        }

        // Contact & Actions
        item {
            ContactActionsSection(faculty = faculty, dimensions = dimensions, onDismiss = onDismiss)
        }

        // Professional Details
        item {
            ProfessionalDetailsSection(faculty = faculty, dimensions = dimensions)
        }
    }
}

@Composable
private fun ProfileHeader(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF667eea).copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, Color(0xFF667eea).copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image with Status
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(faculty.imageUrl.ifEmpty {
                            "https://via.placeholder.com/200x200/667eea/ffffff?text=${faculty.name.take(2)}"
                        })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Faculty Profile",
                    modifier = Modifier
                        .size(if (dimensions.isTablet) 120.dp else 100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                            )
                        ),
                    contentScale = ContentScale.Crop
                )

                // Online Status Badge
                Box(
                    modifier = Modifier
                        .size(if (dimensions.isTablet) 28.dp else 24.dp)
                        .background(Color(0xFF10B981), CircleShape)
                        .align(Alignment.BottomEnd)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Active",
                        tint = Color.White,
                        modifier = Modifier.size(if (dimensions.isTablet) 16.dp else 14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.mediumPadding))

            // Name & Title
            Text(
                text = faculty.name,
                fontSize = if (dimensions.isTablet) 28.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )

            Text(
                text = faculty.designation,
                fontSize = if (dimensions.isTablet) 18.sp else 16.sp,
                color = Color(0xFF667eea),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Text(
                text = faculty.department,
                fontSize = dimensions.bodyTextSize,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickInfoSection(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions
) {
    val infoItems = listOf(
        Triple("Experience", "${faculty.experience}y", Color(0xFF10B981)),
        Triple("Qualification", faculty.qualification, Color(0xFF3B82F6)),
        Triple("Specialization", faculty.specialization.take(15), Color(0xFFF59E0B))
    ).filter { it.second.isNotEmpty() }

    if (infoItems.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(dimensions.smallPadding),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(infoItems) { (label, value, color) ->
                QuickInfoChip(
                    label = label,
                    value = value,
                    color = color,
                    dimensions = dimensions
                )
            }
        }
    }
}

@Composable
private fun QuickInfoChip(
    label: String,
    value: String,
    color: Color,
    dimensions: ResponsiveDimensions
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = dimensions.mediumPadding,
                vertical = dimensions.smallPadding
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = if (dimensions.isTablet) 16.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = dimensions.smallTextSize,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ContactActionsSection(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.cardPadding)
        ) {
            Text(
                text = "Contact & Actions",
                fontSize = if (dimensions.isTablet) 20.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = dimensions.mediumPadding)
            )

            // Contact Info
            if (faculty.email.isNotEmpty() || faculty.phone.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensions.smallPadding)
                ) {
                    if (faculty.email.isNotEmpty()) {
                        ContactInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = faculty.email,
                            color = Color(0xFF10B981),
                            dimensions = dimensions,
                            onClick = { /* Handle email */ }
                        )
                    }

                    if (faculty.phone.isNotEmpty()) {
                        ContactInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = faculty.phone,
                            color = Color(0xFF3B82F6),
                            dimensions = dimensions,
                            onClick = { /* Handle phone */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.mediumPadding))
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (faculty.email.isNotEmpty())
                    Arrangement.spacedBy(dimensions.smallPadding)
                else Arrangement.Center
            ) {
                if (faculty.email.isNotEmpty()) {
                    Button(
                        onClick = { /* Handle email */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Message", fontSize = dimensions.captionTextSize)
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = if (faculty.email.isNotEmpty()) Modifier.weight(1f) else Modifier,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF667eea))
                ) {
                    Text(
                        "Close",
                        fontSize = dimensions.captionTextSize,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    dimensions: ResponsiveDimensions,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(color.copy(alpha = 0.05f))
            .padding(dimensions.smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (dimensions.isTablet) 36.dp else 32.dp)
                .background(color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(if (dimensions.isTablet) 18.dp else 16.dp)
            )
        }

        Spacer(modifier = Modifier.width(dimensions.smallPadding))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = dimensions.smallTextSize,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = dimensions.captionTextSize,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(if (dimensions.isTablet) 20.dp else 18.dp)
        )
    }
}

@Composable
private fun ProfessionalDetailsSection(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.cardPadding)
        ) {
            Text(
                text = "Professional Details",
                fontSize = if (dimensions.isTablet) 20.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = dimensions.mediumPadding)
            )

            val details = listOf(
                "Qualification" to faculty.qualification,
                "Experience" to if (faculty.experience.isNotEmpty()) "${faculty.experience} years" else "",
                "Specialization" to faculty.specialization,
                "Joining Date" to faculty.joiningDate
            ).filter { it.second.isNotEmpty() }

            details.forEach { (label, value) ->
                DetailRow(
                    label = label,
                    value = value,
                    dimensions = dimensions
                )
                if (details.last() != (label to value)) {
                    Spacer(modifier = Modifier.height(dimensions.smallPadding))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    dimensions: ResponsiveDimensions
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label:",
            fontSize = dimensions.captionTextSize,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(if (dimensions.isTablet) 140.dp else 120.dp)
        )

        Text(
            text = value,
            fontSize = dimensions.captionTextSize,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ResponsiveFacultyHeader(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions
) {
    if (dimensions.isTablet && dimensions.isLandscape) {
        // Horizontal layout for tablets in landscape
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensions.cardPadding)
        ) {
            ResponsiveFacultyImage(faculty = faculty, dimensions = dimensions)

            Column(
                modifier = Modifier.weight(1f)
            ) {
                ResponsiveFacultyBasicInfo(faculty = faculty, dimensions = dimensions)
            }
        }
    } else {
        // Vertical layout for smaller screens
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResponsiveFacultyImage(faculty = faculty, dimensions = dimensions)
            Spacer(modifier = Modifier.height(dimensions.mediumPadding))
            ResponsiveFacultyBasicInfo(
                faculty = faculty,
                dimensions = dimensions,
                centerAlign = true
            )
        }
    }
}

@Composable
fun ResponsiveFacultyImage(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions
) {
    Box {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(faculty.imageUrl.ifEmpty {
                    "https://via.placeholder.com/200x200/667eea/ffffff?text=${faculty.name.take(2)}"
                })
                .crossfade(true)
                .build(),
            contentDescription = "Faculty Image",
            modifier = Modifier
                .size(if (dimensions.isTablet) 140.dp else 120.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                    )
                ),
            contentScale = ContentScale.Crop
        )

        // Status indicator
        Box(
            modifier = Modifier
                .size(if (dimensions.isTablet) 32.dp else 28.dp)
                .background(Color(0xFF10B981), CircleShape)
                .align(Alignment.BottomEnd)
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Active",
                tint = Color.White,
                modifier = Modifier.size(if (dimensions.isTablet) 18.dp else 16.dp)
            )
        }
    }
}

@Composable
fun ResponsiveFacultyBasicInfo(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions,
    centerAlign: Boolean = false
) {
    Column(
        horizontalAlignment = if (centerAlign) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text(
            text = faculty.name,
            fontSize = if (dimensions.isTablet) 32.sp else 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            textAlign = if (centerAlign) TextAlign.Center else TextAlign.Start,
            lineHeight = if (dimensions.isTablet) 38.sp else 34.sp
        )

        Spacer(modifier = Modifier.height(dimensions.smallPadding))

        Text(
            text = faculty.designation,
            fontSize = if (dimensions.isTablet) 20.sp else 18.sp,
            color = Color(0xFF667eea),
            fontWeight = FontWeight.SemiBold,
            textAlign = if (centerAlign) TextAlign.Center else TextAlign.Start
        )

        Text(
            text = faculty.department,
            fontSize = dimensions.bodyTextSize,
            color = Color(0xFF6B7280),
            textAlign = if (centerAlign) TextAlign.Center else TextAlign.Start,
            lineHeight = if (dimensions.isTablet) 22.sp else 20.sp
        )

        if (faculty.specialization.isNotEmpty()) {
            Spacer(modifier = Modifier.height(dimensions.smallPadding))
            Text(
                text = "Specialization: ${faculty.specialization}",
                fontSize = dimensions.captionTextSize,
                color = Color(0xFF9CA3AF),
                textAlign = if (centerAlign) TextAlign.Center else TextAlign.Start,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun ResponsiveContactSection(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions
) {
    ResponsiveDetailSection(
        title = "Contact Information",
        icon = Icons.Default.ContactMail,
        dimensions = dimensions
    ) {
        if (dimensions.isTablet && dimensions.isLandscape) {
            // Horizontal layout for tablets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.cardPadding)
            ) {
                if (faculty.email.isNotEmpty()) {
                    ResponsiveContactCard(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = faculty.email,
                        color = Color(0xFF10B981),
                        dimensions = dimensions,
                        modifier = Modifier.weight(1f),
                        onClick = { /* Handle email action */ }
                    )
                }
                if (faculty.phone.isNotEmpty()) {
                    ResponsiveContactCard(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = faculty.phone,
                        color = Color(0xFF3B82F6),
                        dimensions = dimensions,
                        modifier = Modifier.weight(1f),
                        onClick = { /* Handle phone action */ }
                    )
                }
            }
        } else {
            // Vertical layout for smaller screens
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensions.smallPadding)
            ) {
                if (faculty.email.isNotEmpty()) {
                    ResponsiveContactCard(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = faculty.email,
                        color = Color(0xFF10B981),
                        dimensions = dimensions,
                        onClick = { /* Handle email action */ }
                    )
                }
                if (faculty.phone.isNotEmpty()) {
                    ResponsiveContactCard(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = faculty.phone,
                        color = Color(0xFF3B82F6),
                        dimensions = dimensions,
                        onClick = { /* Handle phone action */ }
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveContactCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    dimensions: ResponsiveDimensions,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (dimensions.isTablet) 48.dp else 40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(if (dimensions.isTablet) 24.dp else 20.dp)
                )
            }

            Spacer(modifier = Modifier.width(dimensions.mediumPadding))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = dimensions.captionTextSize,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = dimensions.bodyTextSize,
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(if (dimensions.isTablet) 24.dp else 20.dp)
            )
        }
    }
}

@Composable
fun ResponsiveProfessionalSection(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions
) {
    ResponsiveDetailSection(
        title = "Professional Details",
        icon = Icons.Default.WorkspacePremium,
        dimensions = dimensions
    ) {
        val professionalData = listOf(
            Triple("Qualification", faculty.qualification, Icons.Default.School),
            Triple("Experience", if (faculty.experience.isNotEmpty()) "${faculty.experience} years" else "", Icons.Default.Timeline),
            Triple("Specialization", faculty.specialization, Icons.Default.Psychology)
        ).filter { it.second.isNotEmpty() }

        if (dimensions.isTablet && professionalData.size <= 2) {
            // Horizontal layout for tablets with few items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.mediumPadding)
            ) {
                professionalData.forEach { (label, value, icon) ->
                    ResponsiveProfessionalCard(
                        label = label,
                        value = value,
                        icon = icon,
                        dimensions = dimensions,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Vertical layout
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensions.smallPadding)
            ) {
                professionalData.forEach { (label, value, icon) ->
                    ResponsiveProfessionalCard(
                        label = label,
                        value = value,
                        icon = icon,
                        dimensions = dimensions
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveProfessionalCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    dimensions: ResponsiveDimensions,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(if (dimensions.isTablet) 24.dp else 20.dp)
            )

            Spacer(modifier = Modifier.width(dimensions.mediumPadding))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = dimensions.captionTextSize,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = dimensions.bodyTextSize,
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Medium,
                    maxLines = if (dimensions.isTablet) 3 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ResponsiveAdditionalInfoSection(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions
) {
    ResponsiveDetailSection(
        title = "Additional Information",
        icon = Icons.Default.Info,
        dimensions = dimensions
    ) {
        ResponsiveProfessionalCard(
            label = "Joining Date",
            value = faculty.joiningDate,
            icon = Icons.Default.DateRange,
            dimensions = dimensions
        )
    }
}

@Composable
fun ResponsiveDetailSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    dimensions: ResponsiveDimensions,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = dimensions.mediumPadding)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(if (dimensions.isTablet) 28.dp else 24.dp)
            )
            Spacer(modifier = Modifier.width(dimensions.smallPadding))
            Text(
                text = title,
                fontSize = if (dimensions.isTablet) 22.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }
        content()
    }
}

@Composable
fun ResponsiveActionButtons(
    faculty: FacultyData,
    dimensions: ResponsiveDimensions,
    onDismiss: () -> Unit
) {
    if (dimensions.isTablet && dimensions.isLandscape) {
        // Horizontal layout for tablets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.mediumPadding)
        ) {
            if (faculty.email.isNotEmpty()) {
                Button(
                    onClick = { /* Handle email */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = dimensions.mediumPadding)
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Email",
                        fontSize = dimensions.bodyTextSize,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = dimensions.mediumPadding),
                border = BorderStroke(2.dp, Color(0xFF667eea))
            ) {
                Text(
                    text = "Close",
                    fontSize = dimensions.bodyTextSize,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF667eea)
                )
            }
        }
    } else {
        // Vertical layout for smaller screens
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensions.smallPadding)
        ) {
            if (faculty.email.isNotEmpty()) {
                Button(
                    onClick = { /* Handle email */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = dimensions.mediumPadding)
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Email",
                        fontSize = dimensions.bodyTextSize,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = dimensions.mediumPadding),
                border = BorderStroke(2.dp, Color(0xFF667eea))
            ) {
                Text(
                    text = "Close",
                    fontSize = dimensions.bodyTextSize,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF667eea)
                )
            }
        }
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