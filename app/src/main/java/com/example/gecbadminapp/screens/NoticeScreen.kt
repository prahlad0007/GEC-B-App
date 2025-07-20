package com.example.gecbadminapp.screens

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

// Optimized Data Classes
data class NoticeData(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "General",
    val priority: String = "Normal",
    val pdfUrl: String = "",
    val publishDate: String = "",
    val publishedBy: String = "",
    val department: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val viewCount: Int = 0,
    val isStarred: Boolean = false
)

data class NoticeState(
    val notices: List<NoticeData> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedNotice: NoticeData? = null,
    val showSearchBar: Boolean = false,
    val sortBy: SortOption = SortOption.NEWEST
)

enum class SortOption(val label: String) {
    NEWEST("Newest"), OLDEST("Oldest"), PRIORITY("Priority"), TITLE("A-Z")
}

// Consolidated Configuration
object Config {
    val categories = listOf("All", "Academic", "Examination", "Events", "General", "Urgent", "Placement", "Library", "Sports", "Cultural", "Administrative")

    val categoryData = mapOf(
        "Academic" to Pair(Icons.Outlined.School, Color(0xFF3B82F6)),
        "Examination" to Pair(Icons.Outlined.Quiz, Color(0xFF8B5CF6)),
        "Events" to Pair(Icons.Outlined.Event, Color(0xFF10B981)),
        "General" to Pair(Icons.Outlined.Info, Color(0xFF6B7280)),
        "Urgent" to Pair(Icons.Outlined.PriorityHigh, Color(0xFFEF4444)),
        "Placement" to Pair(Icons.Outlined.Work, Color(0xFFF59E0B)),
        "Library" to Pair(Icons.Outlined.MenuBook, Color(0xFF06B6D4)),
        "Sports" to Pair(Icons.Outlined.SportsBasketball, Color(0xFFEC4899)),
        "Cultural" to Pair(Icons.Outlined.Palette, Color(0xFF8B5CF6)),
        "Administrative" to Pair(Icons.Outlined.Business, Color(0xFF6366F1))
    )

    val priorityColors = mapOf(
        "High" to Pair(Color(0xFFEF4444), Color(0xFFFEE2E2)),
        "Medium" to Pair(Color(0xFFF59E0B), Color(0xFFFEF3C7)),
        "Normal" to Pair(Color(0xFF10B981), Color(0xFFD1FAE5))
    )

    object Colors {
        val Primary = Color(0xFF667eea)
        val Secondary = Color(0xFF764ba2)
        val Surface = Color(0xFFFAFBFC)
        val OnSurface = Color(0xFF1F2937)
        val OnSurfaceVariant = Color(0xFF6B7280)
    }
}

// Main Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticeScreen() {
    var state by remember { mutableStateOf(NoticeState()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        NoticeRepository.loadNotices { notices ->
            state = state.copy(notices = notices.filter { it.isActive }.sortedWith(getSortComparator(state.sortBy)), isLoading = false)
        }
    }

    val filteredNotices = remember(state.notices, state.searchQuery, state.selectedCategory, state.sortBy) {
        state.notices.filter { notice ->
            val matchesSearch = state.searchQuery.isEmpty() ||
                    listOf(notice.title, notice.description, notice.publishedBy, notice.department)
                        .any { it.contains(state.searchQuery, true) }
            val matchesCategory = state.selectedCategory == "All" || notice.category == state.selectedCategory
            matchesSearch && matchesCategory
        }.sortedWith(getSortComparator(state.sortBy))
    }

    Column(modifier = Modifier.fillMaxSize().background(Config.Colors.Surface)) {
        NoticeHeader(
            state = state,
            noticeCount = filteredNotices.size,
            onSearchToggle = { state = state.copy(showSearchBar = !state.showSearchBar) },
            onSearchChange = { state = state.copy(searchQuery = it) }
        )

        AnimatedVisibility(
            visible = !state.showSearchBar,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            CategoryFilter(
                selectedCategory = state.selectedCategory,
                onCategoryChange = { state = state.copy(selectedCategory = it) }
            )
        }

        NoticeContent(
            isLoading = state.isLoading,
            notices = filteredNotices,
            onNoticeClick = { state = state.copy(selectedNotice = it) }
        )

        state.selectedNotice?.let { notice ->
            NoticeDetailDialog(
                notice = notice,
                onDismiss = { state = state.copy(selectedNotice = null) },
                context = context
            )
        }
    }
}

@Composable
private fun NoticeHeader(
    state: NoticeState,
    noticeCount: Int,
    onSearchToggle: () -> Unit,
    onSearchChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(listOf(Config.Colors.Primary, Config.Colors.Secondary))
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (!state.showSearchBar) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(48.dp).background(Color.White.copy(0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Campaign, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Notice Board", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Government Engineering College", fontSize = 14.sp, color = Color.White.copy(0.8f))
                            }
                        }
                        IconButton(onClick = onSearchToggle) {
                            Icon(Icons.Default.Search, "Search", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = Config.Colors.Primary.copy(0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("$noticeCount Active", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = onSearchChange,
                            placeholder = { Text("Search notices...", color = Color.White.copy(0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(0.8f)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        IconButton(onClick = onSearchToggle) {
                            Icon(Icons.Default.Close, "Close", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilter(selectedCategory: String, onCategoryChange: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    ) {
        items(Config.categories) { category ->
            val isSelected = selectedCategory == category
            val (icon, color) = Config.categoryData[category] ?: Pair(Icons.Outlined.Info, Config.Colors.Primary)

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(if (isSelected) color else Color.White),
                elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp),
                modifier = Modifier.clickable { onCategoryChange(category) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, null, tint = if (isSelected) Color.White else Config.Colors.OnSurface, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        category,
                        color = if (isSelected) Color.White else Config.Colors.OnSurface,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun NoticeContent(isLoading: Boolean, notices: List<NoticeData>, onNoticeClick: (NoticeData) -> Unit) {
    when {
        isLoading -> LoadingView()
        notices.isEmpty() -> EmptyView()
        else -> {
            val (urgent, high, regular) = notices.partition { it.priority == "High" && it.category == "Urgent" }
                .let { (urgent, rest) -> Triple(urgent, rest.filter { it.priority == "High" }, rest.filter { it.priority != "High" }) }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(20.dp)
            ) {
                if (urgent.isNotEmpty()) {
                    item { PrioritySection("🚨 Urgent", urgent, Config.Colors.Primary, onNoticeClick) }
                }
                if (high.isNotEmpty()) {
                    item { PrioritySection("⚠️ High Priority", high, Color(0xFFF59E0B), onNoticeClick) }
                }
                items(regular, key = { it.id }) { notice -> NoticeCard(notice, onNoticeClick) }
            }
        }
    }
}

@Composable
private fun PrioritySection(title: String, notices: List<NoticeData>, color: Color, onNoticeClick: (NoticeData) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(color.copy(0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(12.dp))
            notices.forEach { notice ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNoticeClick(notice) },
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notice.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Text("${notice.category} • ${notice.department}", fontSize = 12.sp, color = Config.Colors.OnSurfaceVariant)
                        }
                        if (notice.pdfUrl.isNotEmpty()) {
                            Icon(Icons.Default.PictureAsPdf, null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                        }
                    }
                }
                if (notice != notices.last()) Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun NoticeCard(notice: NoticeData, onClick: (NoticeData) -> Unit) {
    val (priorityTextColor, priorityBgColor) = Config.priorityColors[notice.priority] ?: Pair(Color.Gray, Color.Gray.copy(0.1f))
    val (categoryIcon, categoryColor) = Config.categoryData[notice.category] ?: Pair(Icons.Outlined.Info, Config.Colors.Primary)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(notice) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = priorityBgColor, shape = RoundedCornerShape(12.dp)) {
                        Text(notice.priority, color = priorityTextColor, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Surface(color = categoryColor.copy(0.1f), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(categoryIcon, null, tint = categoryColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(notice.category, color = categoryColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                if (notice.pdfUrl.isNotEmpty()) {
                    Icon(Icons.Default.PictureAsPdf, "PDF", tint = Color(0xFFDC2626), modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(notice.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Text(notice.description, fontSize = 14.sp, color = Config.Colors.OnSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("📅 ${notice.publishDate}", fontSize = 12.sp, color = Config.Colors.OnSurfaceVariant)
                    if (notice.publishedBy.isNotEmpty()) {
                        Text("👤 ${notice.publishedBy}", fontSize = 11.sp, color = Config.Colors.OnSurfaceVariant)
                    }
                }
                Icon(Icons.Default.ChevronRight, "View", tint = Config.Colors.Primary)
            }
        }
    }
}

@Composable
private fun LoadingView() {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(5) {
            Card(modifier = Modifier.fillMaxWidth().height(150.dp), colors = CardDefaults.cardColors(Color.Gray.copy(0.1f))) {
                // Shimmer effect placeholder
            }
        }
    }
}

@Composable
private fun EmptyView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.NotificationsNone, null, modifier = Modifier.size(64.dp), tint = Config.Colors.OnSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No Notices Available", fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("New notices will appear here", fontSize = 14.sp, color = Config.Colors.OnSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun NoticeDetailDialog(notice: NoticeData, onDismiss: () -> Unit, context: Context) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column {
                // Header
                Box(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Config.Colors.Primary, Config.Colors.Secondary)))) {
                    Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notice.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${notice.priority} • ${notice.category}", fontSize = 12.sp, color = Color.White.copy(0.8f))
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Close", tint = Color.White)
                        }
                    }
                }

                // Content
                LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(20.dp)) {
                    item {
                        Text("Description", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(notice.description, fontSize = 14.sp, lineHeight = 20.sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        listOf(
                            "📅 Published" to notice.publishDate,
                            "👤 Published by" to notice.publishedBy,
                            "🏢 Department" to notice.department,
                            "📋 Category" to notice.category
                        ).filter { it.second.isNotEmpty() }.forEach { (label, value) ->
                            Text("$label: $value", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                // Actions - Updated with 3 buttons including "Open in Browser"
                if (notice.pdfUrl.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        // First row: Download and Share
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { PdfHandler.openPdf(context, notice.pdfUrl, notice.title) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Download")
                            }
                            OutlinedButton(
                                onClick = { PdfHandler.sharePdf(context, notice.pdfUrl, notice.title) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Second row: Open in Browser (full width)
                        Button(
                            onClick = { PdfHandler.openPdfInBrowser(context, notice.pdfUrl) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4) // Google blue
                            )
                        ) {
                            Icon(Icons.Default.OpenInBrowser, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open in Browser")
                        }
                    }
                }
            }
        }
    }
}

// Utility Functions
private fun getSortComparator(sortBy: SortOption): Comparator<NoticeData> = when (sortBy) {
    SortOption.NEWEST -> compareByDescending { it.createdAt }
    SortOption.OLDEST -> compareBy { it.createdAt }
    SortOption.PRIORITY -> compareByDescending<NoticeData> { when (it.priority) { "High" -> 3; "Medium" -> 2; else -> 1 } }.thenByDescending { it.createdAt }
    SortOption.TITLE -> compareBy { it.title }
}

// Simplified Repository
object NoticeRepository {
    private val db = FirebaseFirestore.getInstance()

    fun loadNotices(onResult: (List<NoticeData>) -> Unit) {
        db.collection("notices")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val notices = documents.mapNotNull { doc ->
                    try {
                        NoticeData(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            category = doc.getString("category") ?: "General",
                            priority = doc.getString("priority") ?: "Normal",
                            pdfUrl = doc.getString("pdfUrl") ?: "",
                            publishDate = doc.getString("publishDate") ?: "",
                            publishedBy = doc.getString("publishedBy") ?: "",
                            department = doc.getString("department") ?: "",
                            isActive = doc.getBoolean("isActive") ?: true,
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
                            isStarred = doc.getBoolean("isStarred") ?: false
                        )
                    } catch (e: Exception) { null }
                }
                onResult(notices)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}

// Enhanced PDF Handler with Browser Option
object PdfHandler {
    fun openPdf(context: Context, url: String, title: String) {
        try {
            val fileName = "${title.replace("[^a-zA-Z0-9]".toRegex(), "_")}.pdf"
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("Downloading PDF...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        context.unregisterReceiver(this)
                        val uri = downloadManager.getUriForDownloadedFile(downloadId)
                        uri?.let {
                            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(it, "application/pdf")
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            context.startActivity(viewIntent)
                        }
                    }
                }
            }

            ContextCompat.registerReceiver(context, receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_NOT_EXPORTED)
            Toast.makeText(context, "Downloading PDF...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sharePdf(context: Context, url: String, title: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Notice: $title")
            putExtra(Intent.EXTRA_TEXT, "$title\n\nView PDF: $url\n\nShared from GEC Notice Board")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Notice"))
    }

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
}