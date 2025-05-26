package com.example.gecbadminapp.admin.Screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class NoticeData(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val priority: String = "Normal",
    val pdfUrl: String = "",
    val publishDate: String = "",
    val publishedBy: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageNotice() {
    var noticeList by remember { mutableStateOf<List<NoticeData>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedNotice by remember { mutableStateOf<NoticeData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showDeleteConfirmation by remember { mutableStateOf<NoticeData?>(null) }

    val categories = listOf("All", "Academic", "Examination", "Events", "General", "Urgent", "Placement")
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loadNoticeData { notices ->
            noticeList = notices.sortedByDescending { it.createdAt }
            isLoading = false
        }
    }

    val filteredNotices = noticeList.filter { notice ->
        val matchesSearch = notice.title.contains(searchQuery, ignoreCase = true) ||
                notice.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || notice.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopBar(onAddClick = { showAddDialog = true })
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SearchFilterSection(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    categories = categories
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    LoadingView1()
                } else if (filteredNotices.isEmpty()) {
                    EmptyNoticeView()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredNotices) { notice ->
                            NoticeCard(
                                notice = notice,
                                onEdit = { selectedNotice = it },
                                onDelete = { showDeleteConfirmation = it }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditNoticeDialog(
            notice = null,
            onDismiss = { showAddDialog = false },
            onSave = { newNotice ->
                saveNotice(newNotice) { savedNotice ->
                    noticeList = listOf(savedNotice) + noticeList
                    showAddDialog = false
                }
            }
        )
    }

    selectedNotice?.let { notice ->
        AddEditNoticeDialog(
            notice = notice,
            onDismiss = { selectedNotice = null },
            onSave = { updatedNotice ->
                updateNotice(updatedNotice) {
                    noticeList = noticeList.map {
                        if (it.id == updatedNotice.id) updatedNotice else it
                    }
                    selectedNotice = null
                }
            }
        )
    }

    showDeleteConfirmation?.let { notice ->
        DeleteConfirmationDialog(
            notice = notice,
            onConfirm = {
                coroutineScope.launch {
                    deleteNoticeWithPdf(notice) { success ->
                        if (success) {
                            noticeList = noticeList.filter { it.id != notice.id }
                        }
                        showDeleteConfirmation = null
                    }
                }
            },
            onDismiss = { showDeleteConfirmation = null }
        )
    }
}

@Composable
fun TopBar(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Manage Notices",
                    color = Color(0xFF1F2937),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.size(48.dp),
                containerColor = Color(0xFF667eea)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Notice",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SearchFilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    categories: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Search Notices") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Category",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        onClick = { onCategoryChange(category) },
                        label = { Text(category) },
                        selected = selectedCategory == category
                    )
                }
            }
        }
    }
}

@Composable
fun NoticeCard(
    notice: NoticeData,
    onEdit: (NoticeData) -> Unit,
    onDelete: (NoticeData) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val publishDate = try {
        dateFormat.format(Date(notice.publishDate.toLong()))
    } catch (e: Exception) {
        notice.publishDate
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(notice) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AssistChip(
                        onClick = { },
                        label = { Text(notice.priority, fontSize = 10.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = when (notice.priority) {
                                "High" -> Color(0xFFEF4444).copy(alpha = 0.1f)
                                "Medium" -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                                else -> Color(0xFF10B981).copy(alpha = 0.1f)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = notice.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = notice.category,
                        fontSize = 12.sp,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.Medium
                    )
                }

                Column {
                    IconButton(onClick = { onEdit(notice) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF10B981))
                    }
                    IconButton(onClick = { onDelete(notice) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notice.description,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = publishDate,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )

                if (notice.pdfUrl.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFDC2626)
                        )
                        Text(
                            text = "PDF",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoticeDialog(
    notice: NoticeData?,
    onDismiss: () -> Unit,
    onSave: (NoticeData) -> Unit
) {
    var title by remember { mutableStateOf(notice?.title ?: "") }
    var description by remember { mutableStateOf(notice?.description ?: "") }
    var category by remember { mutableStateOf(notice?.category ?: "") }
    var priority by remember { mutableStateOf(notice?.priority ?: "Normal") }
    var publishedBy by remember { mutableStateOf(notice?.publishedBy ?: "") }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedPdfUri = uri }

    val categories = listOf("Academic", "Examination", "Events", "General", "Urgent", "Placement")
    val priorities = listOf("Low", "Normal", "Medium", "High")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = if (notice == null) "Add Notice" else "Edit Notice",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Error message display
                errorMessage?.let { error ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = error,
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }

                item {
                    var categoryExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    var priorityExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = priorityExpanded,
                        onExpandedChange = { priorityExpanded = !priorityExpanded }
                    ) {
                        OutlinedTextField(
                            value = priority,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Priority") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = priorityExpanded,
                            onDismissRequest = { priorityExpanded = false }
                        ) {
                            priorities.forEach { pri ->
                                DropdownMenuItem(
                                    text = { Text(pri) },
                                    onClick = {
                                        priority = pri
                                        priorityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = publishedBy,
                        onValueChange = { publishedBy = it },
                        label = { Text("Published By") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Column {
                        OutlinedButton(
                            onClick = { if (!isUploading) pdfPickerLauncher.launch("application/pdf") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isUploading
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (selectedPdfUri != null || notice?.pdfUrl?.isNotEmpty() == true)
                                    "Change PDF" else "Select PDF (Optional)"
                            )
                        }

                        if (selectedPdfUri != null) {
                            Text(
                                text = "New PDF selected",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else if (notice?.pdfUrl?.isNotEmpty() == true) {
                            Text(
                                text = "PDF attached",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        if (isUploading) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            )
                        }
                    }
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
                                if (title.isNotBlank() && description.isNotBlank() && category.isNotBlank()) {
                                    coroutineScope.launch {
                                        isUploading = true
                                        errorMessage = null

                                        val baseNotice = if (notice == null) {
                                            NoticeData(
                                                title = title.trim(),
                                                description = description.trim(),
                                                category = category,
                                                priority = priority,
                                                publishedBy = publishedBy.trim(),
                                                publishDate = System.currentTimeMillis().toString(),
                                                createdAt = System.currentTimeMillis()
                                            )
                                        } else {
                                            notice.copy(
                                                title = title.trim(),
                                                description = description.trim(),
                                                category = category,
                                                priority = priority,
                                                publishedBy = publishedBy.trim()
                                            )
                                        }

                                        // FIXED: Proper if-else structure to prevent double upload
                                        if (selectedPdfUri != null) {
                                            // Upload PDF first, then save notice with PDF URL
                                            uploadPdfToCloudinary(
                                                context = context,
                                                uri = selectedPdfUri!!,
                                                onSuccess = { pdfUrl ->
                                                    val finalNotice = baseNotice.copy(pdfUrl = pdfUrl)
                                                    onSave(finalNotice)
                                                    isUploading = false
                                                },
                                                onFailure = { error ->
                                                    errorMessage = error
                                                    isUploading = false
                                                }
                                            )
                                        } else {
                                            // No PDF selected, save notice directly
                                            onSave(baseNotice)
                                            isUploading = false
                                        }
                                        // REMOVED: The problematic extra onSave call that was here
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isUploading && title.isNotBlank() && description.isNotBlank() && category.isNotBlank()
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(if (notice == null) "Add" else "Update")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingView1() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading Notices...")
            }
        }
    }
}

@Composable
fun EmptyNoticeView() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.NotificationsNone,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF9CA3AF)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Notices Found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Create your first notice or adjust filters",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    notice: NoticeData,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Notice") },
        text = { Text("Are you sure you want to delete \"${notice.title}\"?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Firebase Operations
private fun loadNoticeData(onDataLoaded: (List<NoticeData>) -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("notices")
        .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                onDataLoaded(emptyList())
                return@addSnapshotListener
            }

            val notices = snapshot?.documents?.mapNotNull { doc ->
                try {
                    doc.toObject(NoticeData::class.java)?.copy(id = doc.id)
                } catch (e: Exception) { null }
            } ?: emptyList()

            onDataLoaded(notices)
        }
}

private fun saveNotice(notice: NoticeData, onComplete: (NoticeData) -> Unit) {
    val noticeMap = mapOf(
        "title" to notice.title,
        "description" to notice.description,
        "category" to notice.category,
        "priority" to notice.priority,
        "pdfUrl" to notice.pdfUrl,
        "publishDate" to notice.publishDate,
        "publishedBy" to notice.publishedBy,
        "isActive" to notice.isActive,
        "createdAt" to notice.createdAt
    )

    FirebaseFirestore.getInstance()
        .collection("notices")
        .add(noticeMap)
        .addOnSuccessListener { doc ->
            onComplete(notice.copy(id = doc.id))
        }
}

private fun updateNotice(notice: NoticeData, onComplete: () -> Unit) {
    val noticeMap = mapOf(
        "title" to notice.title,
        "description" to notice.description,
        "category" to notice.category,
        "priority" to notice.priority,
        "pdfUrl" to notice.pdfUrl,
        "publishDate" to notice.publishDate,
        "publishedBy" to notice.publishedBy,
        "isActive" to notice.isActive
    )

    FirebaseFirestore.getInstance()
        .collection("notices")
        .document(notice.id)
        .update(noticeMap)
        .addOnSuccessListener { onComplete() }
}

private suspend fun deleteNoticeWithPdf(notice: NoticeData, onComplete: (Boolean) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            if (notice.pdfUrl.isNotEmpty()) {
                deletePdfFromCloudinary(notice.pdfUrl)
            }

            FirebaseFirestore.getInstance()
                .collection("notices")
                .document(notice.id)
                .delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } catch (e: Exception) {
            onComplete(false)
        }
    }
}

// Cloudinary Operations
val folder = "ManageNotices"
private suspend fun uploadPdfToCloudinary(
    context: android.content.Context,
    uri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        var outputStream: java.io.OutputStream? = null
        var inputStream: java.io.InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            inputStream = null

            if (bytes == null) {
                withContext(Dispatchers.Main) {
                    onFailure("Failed to read PDF file")
                }
                return@withContext
            }

            val cloudName = "dmjori0fh"
            val apiKey = "665783445794731"
            val apiSecret = "JG0TcrTAN4f-uCCwW8CkFEDlb0o"
            val uploadPreset = "manage_notices"
            val folder = "ManageNotices"
            val timestamp = System.currentTimeMillis() / 1000
            val publicId = "pdf_${timestamp}_${generateRandomString(8)}"

            // Signature params (must be sorted alphabetically by keys)
            val paramsToSign = "folder=$folder&public_id=$publicId&timestamp=$timestamp&upload_preset=$uploadPreset"
            val signature = generateSignature(paramsToSign + apiSecret)

            val url = URL("https://api.cloudinary.com/v1_1/$cloudName/raw/upload")
            connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true
            connection.useCaches = false
            connection.connectTimeout = 30000
            connection.readTimeout = 60000

            val boundary = "----WebKitFormBoundary${System.currentTimeMillis()}"
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

            outputStream = connection.outputStream
            val writer = outputStream.bufferedWriter()

            // File
            writer.write("--$boundary\r\n")
            writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"notice.pdf\"\r\n")
            writer.write("Content-Type: application/pdf\r\n\r\n")
            writer.flush()
            outputStream.write(bytes)
            writer.write("\r\n")

            // Params
            val params = mapOf(
                "upload_preset" to uploadPreset,
                "public_id" to publicId,
                "folder" to folder,
                "timestamp" to timestamp.toString(),
                "api_key" to apiKey,
                "signature" to signature
            )

            for ((key, value) in params) {
                writer.write("--$boundary\r\n")
                writer.write("Content-Disposition: form-data; name=\"$key\"\r\n\r\n")
                writer.write("$value\r\n")
            }

            writer.write("--$boundary--\r\n")
            writer.flush()
            writer.close()
            outputStream.close()
            outputStream = null

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(response)
                val secureUrl = jsonResponse.getString("secure_url")

                withContext(Dispatchers.Main) {
                    onSuccess(secureUrl)
                }
            } else {
                val errorStream = connection.errorStream
                val errorResponse = errorStream?.bufferedReader()?.readText() ?: "Upload failed with response code $responseCode"
                withContext(Dispatchers.Main) {
                    onFailure("Upload failed: $errorResponse")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onFailure("Upload error: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
            }
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
                connection?.disconnect()
            } catch (_: Exception) {}
        }
    }
}

private suspend fun deletePdfFromCloudinary(pdfUrl: String) {
    withContext(Dispatchers.IO) {
        try {
            val publicId = extractPublicIdFromUrl(pdfUrl)
            if (publicId.isEmpty()) return@withContext

            val cloudName = "dmjori0fh"
            val apiKey = "665783445794731"
            val apiSecret = "JG0TcrTAN4f-uCCwW8CkFEDlb0o"

            val timestamp = System.currentTimeMillis() / 1000
            val paramsToSign = "public_id=$publicId&timestamp=$timestamp"
            val signature = generateSignature(paramsToSign + apiSecret)

            val url = URL("https://api.cloudinary.com/v1_1/$cloudName/raw/destroy")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val postData = "public_id=$publicId&timestamp=$timestamp&api_key=$apiKey&signature=$signature"
            connection.outputStream.write(postData.toByteArray())
            connection.outputStream.flush()
            connection.outputStream.close()

            // Check response (optional - for debugging)
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                println("Delete response: $response")
            }
        } catch (e: Exception) {
            println("Error deleting PDF: ${e.message}")
        }
    }
}

private fun extractPublicIdFromUrl(url: String): String {
    return try {
        // Extract public_id from Cloudinary URL
        // URL format: https://res.cloudinary.com/dmjori0fh/raw/upload/v1234567890/notices/pdf_1234567890_abcd1234.pdf
        val parts = url.split("/")
        val uploadIndex = parts.indexOf("upload")
        if (uploadIndex != -1 && uploadIndex + 2 < parts.size) {
            val versionAndPath = parts.drop(uploadIndex + 1)
            // Skip version (v1234567890) and get the path
            val pathParts = versionAndPath.drop(1)
            val fullPath = pathParts.joinToString("/")
            // Remove file extension
            fullPath.substringBeforeLast(".")
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

private fun generateSignature(params: String): String {
    return try {
        val digest = MessageDigest.getInstance("SHA-1")
        val hashBytes = digest.digest(params.toByteArray())
        hashBytes.joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        ""
    }
}

private fun generateRandomString(length: Int): String {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}