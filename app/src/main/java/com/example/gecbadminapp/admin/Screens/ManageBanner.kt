package com.example.gecbadminapp.admin.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.gecbadminapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

// Data class to hold banner information
data class BannerData(
    val imageUrl: String,
    val publicId: String,
    val documentId: String,
    val timestamp: Long = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBanner(navController: NavController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var uploadStatus by remember { mutableStateOf<String?>(null) }
    var deleteStatus by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Store banner data objects
    val banners = remember { mutableStateListOf<BannerData>() }

    // Add your Cloudinary credentials here
    val CLOUDINARY_CLOUD_NAME = "dmjori0fh"
    val CLOUDINARY_API_KEY = "665783445794731"
    val CLOUDINARY_API_SECRET = "JG0TcrTAN4f-uCCwW8CkFEDlb0o"

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        uploadStatus = null
        deleteStatus = null
    }

    // Load banners from Firestore on launch
    LaunchedEffect(Unit) {
        delay(500) // Simulate loading
        val db = Firebase.firestore
        db.collection("banners")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                banners.clear()
                for (doc in documents) {
                    val url = doc.getString("imageUrl")
                    val publicId = doc.getString("publicId")
                    val timestamp = doc.getLong("timestamp") ?: 0

                    if (url != null && publicId != null) {
                        banners.add(BannerData(url, publicId, doc.id, timestamp))
                    }
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                uploadStatus = "Failed to load banners: ${exception.message}"
                isLoading = false
            }
    }

    fun uploadImage(uri: Uri) {
        isUploading = true
        uploadStatus = "Uploading..."
        deleteStatus = null

        MediaManager.get().upload(uri)
            .unsigned("gecb_unsigned_upload")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    uploadStatus = "Starting upload..."
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    val progress = (bytes * 100 / totalBytes).toInt()
                    uploadStatus = "Uploading... $progress%"
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    isUploading = false
                    val url = resultData?.get("secure_url") as? String
                    val publicId = resultData?.get("public_id") as? String

                    if (url != null && publicId != null) {
                        uploadStatus = "Upload successful! Saving to database..."

                        val db = Firebase.firestore
                        val banner = hashMapOf(
                            "imageUrl" to url,
                            "publicId" to publicId,
                            "timestamp" to System.currentTimeMillis()
                        )

                        db.collection("banners").add(banner)
                            .addOnSuccessListener { documentReference ->
                                banners.add(
                                    BannerData(
                                        imageUrl = url,
                                        publicId = publicId,
                                        documentId = documentReference.id,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                                imageUri = null
                                uploadStatus = "Banner added successfully!"

                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(3000)
                                    uploadStatus = null
                                }
                            }
                            .addOnFailureListener { exception ->
                                uploadStatus = "Failed to save to database: ${exception.message}"
                                isUploading = false
                            }
                    } else {
                        uploadStatus = "Upload failed: Missing URL or Public ID"
                        isUploading = false
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    isUploading = false
                    uploadStatus = "Upload failed: ${error?.description ?: "Unknown error"}"
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    isUploading = false
                    uploadStatus = "Upload rescheduled: ${error?.description ?: "Unknown"}"
                }
            })
            .dispatch()
    }

    // Function to generate signature for Cloudinary API
    fun generateSignature(params: Map<String, String>, apiSecret: String): String {
        val sortedParams = params.toSortedMap()
        val stringToSign = sortedParams.map { "${it.key}=${it.value}" }.joinToString("&")
        val fullString = "$stringToSign$apiSecret"

        val md = MessageDigest.getInstance("SHA-1")
        val digest = md.digest(fullString.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    // Function to delete image from Cloudinary using REST API
    fun deleteFromCloudinary(publicId: String, onResult: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val timestamp = (System.currentTimeMillis() / 1000).toString()

                val params = mapOf(
                    "public_id" to publicId,
                    "timestamp" to timestamp
                )

                val signature = generateSignature(params, CLOUDINARY_API_SECRET)

                val formBody = FormBody.Builder()
                    .add("public_id", publicId)
                    .add("timestamp", timestamp)
                    .add("api_key", CLOUDINARY_API_KEY)
                    .add("signature", signature)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/destroy")
                    .post(formBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        CoroutineScope(Dispatchers.Main).launch {
                            onResult(false, "Network error: ${e.message}")
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (response.isSuccessful) {
                                onResult(true, "Successfully deleted from Cloudinary")
                            } else {
                                onResult(false, "Failed to delete from Cloudinary: ${response.message}")
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    onResult(false, "Error: ${e.message}")
                }
            }
        }
    }

    fun removeBanner(bannerData: BannerData) {
        isDeleting = true
        deleteStatus = "Deleting from Cloudinary..."
        uploadStatus = null

        deleteFromCloudinary(bannerData.publicId) { success, message ->
            if (success) {
                deleteStatus = "Deleted from Cloudinary. Removing from database..."

                val db = Firebase.firestore
                db.collection("banners").document(bannerData.documentId)
                    .delete()
                    .addOnSuccessListener {
                        banners.removeAll { it.documentId == bannerData.documentId }
                        deleteStatus = "Banner deleted successfully!"
                        isDeleting = false

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(3000)
                            deleteStatus = null
                        }
                    }
                    .addOnFailureListener { exception ->
                        deleteStatus = "Failed to remove from database: ${exception.message}"
                        isDeleting = false
                    }
            } else {
                deleteStatus = "Cloudinary deletion failed. Removing from database anyway..."

                val db = Firebase.firestore
                db.collection("banners").document(bannerData.documentId)
                    .delete()
                    .addOnSuccessListener {
                        banners.removeAll { it.documentId == bannerData.documentId }
                        deleteStatus = "Removed from database (Cloudinary deletion failed: $message)"
                        isDeleting = false
                    }
                    .addOnFailureListener { exception ->
                        deleteStatus = "Failed to delete: $message AND ${exception.message}"
                        isDeleting = false
                    }
            }
        }
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Back Button and Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { navController.navigateUp() },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrowback),
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Banner Management",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Upload and manage banners",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Banner Icon
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Photo,
                                contentDescription = "Banner",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF667eea)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickStatItem(
                            icon = Icons.Default.Photo,
                            label = "Total Banners",
                            value = banners.size.toString()
                        )

                        Divider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = Color.White.copy(alpha = 0.3f)
                        )

                        QuickStatItem(
                            icon = if (isUploading || isDeleting) Icons.Default.Sync else Icons.Default.CheckCircle,
                            label = "Status",
                            value = when {
                                isUploading -> "Uploading"
                                isDeleting -> "Deleting"
                                else -> "Ready"
                            }
                        )
                    }
                }
            }

            // Main Content
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Upload Section
                    item {
                        UploadSection(
                            imageUri = imageUri,
                            isUploading = isUploading,
                            isDeleting = isDeleting,
                            uploadStatus = uploadStatus,
                            deleteStatus = deleteStatus,
                            onSelectImage = { launcher.launch("image/*") },
                            onUpload = { uri -> uploadImage(uri) },
                            onCancel = {
                                imageUri = null
                                uploadStatus = null
                                deleteStatus = null
                            }
                        )
                    }

                    // Banner List Section
                    if (isLoading) {
                        item {
                            LoadingSection()
                        }
                    } else if (banners.isEmpty()) {
                        item {
                            EmptyBannersSection()
                        }
                    } else {
                        item {
                            BannerListHeader(count = banners.size)
                        }

                        items(banners) { banner ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically() + fadeIn(),
                                exit = slideOutVertically() + fadeOut()
                            ) {
                                BannerCard(
                                    banner = banner,
                                    isDeleting = isDeleting,
                                    onDelete = { removeBanner(banner) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { launcher.launch("image/*") },
                containerColor = Color(0xFF667EEA),
                elevation = FloatingActionButtonDefaults.elevation(12.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Banner",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun QuickStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun UploadSection(
    imageUri: Uri?,
    isUploading: Boolean,
    isDeleting: Boolean,
    uploadStatus: String?,
    deleteStatus: String?,
    onSelectImage: () -> Unit,
    onUpload: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = "Upload",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Upload New Banner",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            imageUri?.let { uri ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "Selected banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onUpload(uri) },
                        enabled = !isUploading && !isDeleting,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            if (isUploading) "Uploading..." else "Upload Banner",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = onCancel,
                        enabled = !isUploading && !isDeleting,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }
                }
            } ?: run {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { onSelectImage() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF3F4F6)
                    ),
                    border = BorderStroke(2.dp, Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = "Select Image",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF9CA3AF)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Tap to select banner image",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )

                        Text(
                            text = "JPG, PNG up to 10MB",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            // Status Messages
            uploadStatus?.let { status ->
                Spacer(modifier = Modifier.height(16.dp))
                StatusMessage(status)
            }

            deleteStatus?.let { status ->
                Spacer(modifier = Modifier.height(16.dp))
                StatusMessage(status)
            }
        }
    }
}

@Composable
fun StatusMessage(status: String) {
    val color = when {
        "success" in status.lowercase() -> Color(0xFF10B981)
        "failed" in status.lowercase() || "error" in status.lowercase() -> Color(0xFFEF4444)
        else -> Color(0xFF3B82F6)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    "success" in status.lowercase() -> Icons.Default.CheckCircle
                    "failed" in status.lowercase() || "error" in status.lowercase() -> Icons.Default.Error
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = status,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun LoadingSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp,
                color = Color(0xFF667eea)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading banners...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun EmptyBannersSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.PhotoLibrary,
                contentDescription = "No banners",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No banners yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Text(
                text = "Upload your first banner to get started",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BannerListHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Uploaded Banners",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "$count banner${if (count != 1) "s" else ""} uploaded",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = "$count",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun BannerCard(
    banner: BannerData,
    isDeleting: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = banner.imageUrl),
                    contentDescription = "Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Banner ID",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9CA3AF)
                    )
                    Text(
                        text = banner.publicId,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Uploaded ${
                            SimpleDateFormat(
                                "MMM dd, yyyy",
                                Locale.getDefault()
                            ).format(Date(banner.timestamp))
                        }",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Button(
                    onClick = onDelete,
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Delete",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}