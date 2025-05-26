package com.example.gecbadminapp.screens

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.gecbadminapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
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
fun NoticeScreen() {
    var noticeList by remember { mutableStateOf<List<NoticeData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedNotice by remember { mutableStateOf<NoticeData?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val categories = listOf("All", "Academic", "Examination", "Events", "General", "Urgent", "Placement")

    // Enhanced animation for loading
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        loadNoticeData { notices ->
            noticeList = notices.filter { it.isActive }
                .sortedWith(
                    compareByDescending<NoticeData> {
                        when (it.priority) {
                            "High" -> 3
                            "Medium" -> 2
                            "Normal" -> 1
                            else -> 0
                        }
                    }.thenByDescending { it.createdAt }
                )
            isLoading = false
        }
    }

    val filteredNotices = noticeList.filter { notice ->
        val matchesSearch = notice.title.contains(searchQuery, ignoreCase = true) ||
                notice.description.contains(searchQuery, ignoreCase = true) ||
                notice.publishedBy.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || notice.category == selectedCategory
        matchesSearch && matchesCategory
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
            // Enhanced Header Section
            EnhancedNoticeHeader(
                showSearch = showSearchBar,
                onSearchToggle = { showSearchBar = !showSearchBar },
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                noticeCount = filteredNotices.size
            )

            // Category Filter Section
            if (!showSearchBar) {
                CategoryFilterSection(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it }
                )
            }

            // Main Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    isLoading -> {
                        EnhancedLoadingView(shimmerAlpha)
                    }
                    filteredNotices.isEmpty() -> {
                        EnhancedEmptyNoticeView(selectedCategory, searchQuery)
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            // High Priority Notices Section
                            val highPriorityNotices = filteredNotices.filter { it.priority == "High" }
                            if (highPriorityNotices.isNotEmpty()) {
                                item {
                                    PrioritySection(
                                        title = "🚨 Urgent Notices",
                                        notices = highPriorityNotices,
                                        onNoticeClick = { selectedNotice = it }
                                    )
                                }
                            }

                            // Regular Notices
                            val regularNotices = filteredNotices.filter { it.priority != "High" }
                            items(regularNotices) { notice ->
                                EnhancedNoticeCard(
                                    notice = notice,
                                    onClick = { selectedNotice = it }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Notice Detail Dialog
        selectedNotice?.let { notice ->
            NoticeDetailDialog(
                notice = notice,
                onDismiss = { selectedNotice = null },
                onOpenPdf = { url ->
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Unable to open PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun EnhancedNoticeHeader(
    showSearch: Boolean,
    onSearchToggle: () -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    noticeCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            if (!showSearch) {
                // Header with college info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF667eea).copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = Color(0xFF667eea),
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Notice Board",
                                color = Color(0xFF1F2937),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Government Engineering College",
                                color = Color(0xFF6B7280),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onSearchToggle,
                        modifier = Modifier
                            .background(
                                Color(0xFF667eea).copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF667eea)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notice count and status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "$noticeCount Active Notices",
                            color = Color(0xFF10B981),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val currentTime = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                            .format(Date())
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentTime,
                            color = Color(0xFF6B7280),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Search Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search notices...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(onClick = onSearchToggle) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close Search",
                            tint = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterSection(
    categories: List<String>,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF667eea) else Color.White.copy(alpha = 0.9f)
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color(0xFF1F2937)
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 4.dp),
                modifier = Modifier.clickable { onCategoryChange(category) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (category) {
                        "Academic" -> Icons.Default.School
                        "Examination" -> Icons.Default.Quiz
                        "Events" -> Icons.Default.Event
                        "General" -> Icons.Default.Info
                        "Urgent" -> Icons.Default.PriorityHigh
                        "Placement" -> Icons.Default.Work
                        else -> Icons.Default.ViewList
                    }

                    Icon(
                        icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PrioritySection(
    title: String,
    notices: List<NoticeData>,
    onNoticeClick: (NoticeData) -> Unit
) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEF4444).copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )

                Spacer(modifier = Modifier.height(12.dp))

                notices.forEach { notice ->
                    UrgentNoticeItem(
                        notice = notice,
                        onClick = { onNoticeClick(notice) }
                    )
                    if (notice != notices.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UrgentNoticeItem(
    notice: NoticeData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.PriorityHigh,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notice.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notice.category,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            if (notice.pdfUrl.isNotEmpty()) {
                Icon(
                    Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EnhancedNoticeCard(
    notice: NoticeData,
    onClick: (NoticeData) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val publishDate = try {
        dateFormat.format(Date(notice.publishDate.toLong()))
    } catch (e: Exception) {
        notice.publishDate
    }

    val priorityColor = when (notice.priority) {
        "High" -> Color(0xFFEF4444)
        "Medium" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(notice) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with priority and category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = priorityColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = notice.priority,
                            color = priorityColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF667eea).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = notice.category,
                            color = Color(0xFF667eea),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (notice.pdfUrl.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDC2626).copy(alpha = 0.1f)
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Available",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier
                                .size(32.dp)
                                .padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title and Description
            Text(
                text = notice.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notice.description,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer with date and publisher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = publishDate,
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (notice.publishedBy.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = notice.publishedBy,
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF667eea).copy(alpha = 0.1f)
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "View Details",
                        tint = Color(0xFF667eea),
                        modifier = Modifier
                            .size(32.dp)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedLoadingView(shimmerAlpha: Float) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(6) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = shimmerAlpha)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(20.dp)
                            .background(
                                Color.Gray.copy(alpha = shimmerAlpha),
                                RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                Color.Gray.copy(alpha = shimmerAlpha * 0.7f),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedEmptyNoticeView(selectedCategory: String, searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (searchQuery.isNotEmpty()) "No Results Found" else "No Notices Available",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when {
                        searchQuery.isNotEmpty() -> "Try adjusting your search terms"
                        selectedCategory != "All" -> "No notices in $selectedCategory category"
                        else -> "Check back later for new notices"
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// SOLUTION 1: Firebase Storage (Recommended)
// First, upload PDFs to Firebase Storage instead of Cloudinary

// Updated NoticeDetailDialog with multiple PDF opening options
@Composable
fun NoticeDetailDialog(
    notice: NoticeData,
    onDismiss: () -> Unit,
    onOpenPdf: (String) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val publishDate = try {
        dateFormat.format(Date(notice.publishDate.toLong()))
    } catch (e: Exception) {
        notice.publishDate
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // ... existing header code ...

                // PDF Action Buttons
                if (notice.pdfUrl.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Multiple PDF opening options
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: Download and Open
                        Button(
                            onClick = {
                                downloadAndOpenPdf(context, notice.pdfUrl, notice.title)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download & Open PDF")
                        }

                        // Option 2: Open in Browser
                        OutlinedButton(
                            onClick = {
                                openPdfInBrowser(context, notice.pdfUrl)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open in Browser")
                        }

                        // Option 3: Share PDF Link
                        OutlinedButton(
                            onClick = {
                                sharePdfLink(context, notice.pdfUrl, notice.title)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share PDF Link")
                        }
                    }
                }
            }
        }
    }
}

// SOLUTION 2: Download and Open PDF Functions
fun downloadAndOpenPdf(context: Context, url: String, fileName: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("$fileName.pdf")
            .setDescription("Downloading notice PDF...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$fileName.pdf")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Listen for download completion
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    context.unregisterReceiver(this)
                    openDownloadedPdf(context, downloadManager, downloadId)
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        Toast.makeText(context, "Downloading PDF...", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun openDownloadedPdf(context: Context, downloadManager: DownloadManager, downloadId: Long) {
    try {
        val uri = downloadManager.getUriForDownloadedFile(downloadId)
        if (uri != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_LONG).show()
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to open PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

// SOLUTION 3: Open in Browser (Most Reliable)
fun openPdfInBrowser(context: Context, url: String) {
    try {
        // Use Google Docs viewer for better compatibility
        val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$url"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDocsUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to direct URL
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Unable to open PDF", Toast.LENGTH_SHORT).show()
        }
    }
}

// SOLUTION 4: Share PDF Link
fun sharePdfLink(context: Context, url: String, title: String) {
    try {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this notice: $title\n\nPDF Link: $url")
            putExtra(Intent.EXTRA_SUBJECT, title)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share PDF Link"))
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to share", Toast.LENGTH_SHORT).show()
    }
}

// SOLUTION 5: Firebase Storage Implementation (Recommended Backend)
// Replace Cloudinary with Firebase Storage for better Android integration

class NoticeRepository {
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Upload PDF to Firebase Storage
    suspend fun uploadPdfToFirebase(pdfUri: Uri, fileName: String): String? {
        return try {
            val pdfRef = storage.reference.child("notices/pdfs/$fileName.pdf")
            pdfRef.putFile(pdfUri).await()
            pdfRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    // Get direct download URL from Firebase Storage
    suspend fun getFirebasePdfUrl(fileName: String): String? {
        return try {
            val pdfRef = storage.reference.child("notices/pdfs/$fileName.pdf")
            pdfRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}

// SOLUTION 6: In-App PDF Viewer (Advanced Option)
// Add to build.gradle (Module: app)
/*
dependencies {
    implementation 'com.github.barteksc:android-pdf-viewer:3.2.0-beta.1'
}
*/

// PDF Viewer Activity
/*
class PdfViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pdfUrl = intent.getStringExtra("PDF_URL") ?: return
        val title = intent.getStringExtra("PDF_TITLE") ?: "PDF Document"

        setContent {
            PdfViewerScreen(pdfUrl = pdfUrl, title = title)
        }
    }
}

@Composable
fun PdfViewerScreen(pdfUrl: String, title: String) {
    var isLoading by remember { mutableStateOf(true) }
    var pdfFile by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(pdfUrl) {
        // Download PDF file for viewing
        downloadPdfForViewing(pdfUrl) { file ->
            pdfFile = file
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // PDF Content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            pdfFile?.let { file ->
                AndroidView(
                    factory = { context ->
                        PDFView(context, null).apply {
                            fromFile(file)
                                .defaultPage(0)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .load()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
*/

// Required permissions in AndroidManifest.xml
/*
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
*/

// Function to load notice data from Firestore
private fun loadNoticeData(onResult: (List<NoticeData>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("notices")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { documents ->
            val notices = mutableListOf<NoticeData>()
            for (document in documents) {
                try {
                    val notice = NoticeData(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        category = document.getString("category") ?: "General",
                        priority = document.getString("priority") ?: "Normal",
                        pdfUrl = document.getString("pdfUrl") ?: "",
                        publishDate = document.getString("publishDate") ?: "",
                        publishedBy = document.getString("publishedBy") ?: "",
                        isActive = document.getBoolean("isActive") ?: true,
                        createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                    notices.add(notice)
                } catch (e: Exception) {
                    // Handle parsing errors for individual documents
                    continue
                }
            }
            onResult(notices)
        }
        .addOnFailureListener { exception ->
            // Handle failure case
            onResult(emptyList())
        }
}