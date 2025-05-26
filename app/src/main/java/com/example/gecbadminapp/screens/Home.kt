package com.example.gecbadminapp.screens
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.gecbadminapp.R
import com.example.gecbadminapp.ui.theme.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

// Data class for banners
data class BannerItem(
    val imageUrl: String,
    val publicId: String
)

// Data class for college stats
data class CollegeStats(
    val icon: ImageVector,
    val count: String,
    val title: String,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() {
    val context = LocalContext.current
    var banners by remember { mutableStateOf<List<BannerItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    // Enhanced college stats with Faculty color scheme
    val collegeStats = listOf(
        CollegeStats(Icons.Default.School, "100+", "Faculty Members", Color(0xFF10B981)),
        CollegeStats(Icons.Default.Group, "2000+", "Students", Color(0xFF3B82F6)),
        CollegeStats(Icons.Default.Business, "8", "Departments", Color(0xFFF59E0B)),
        CollegeStats(Icons.Default.EmojiEvents, "75+", "Years Legacy", Color(0xFF8B5CF6))
    )

    // Load banners from Firestore
    LaunchedEffect(Unit) {
        val db = Firebase.firestore
        db.collection("banners")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                val bannerList = mutableListOf<BannerItem>()
                for (doc in documents) {
                    val url = doc.getString("imageUrl")
                    val publicId = doc.getString("publicId")
                    if (url != null && publicId != null) {
                        bannerList.add(BannerItem(url, publicId))
                    }
                }
                banners = bannerList
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Enhanced Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header with greeting and logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome to",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "GECB Portal",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // College Logo with enhanced styling
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "College Logo",
                            modifier = Modifier
                                .size(60.dp)
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Enhanced College Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Government Engineering College",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Bilaspur (C.G.)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Enhanced Sanskrit Slogan
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = "योग कर्मसु कौशलम",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            // Main Content Card
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
                    // Enhanced Banner Section
                    if (isLoading) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF667eea).copy(alpha = 0.1f),
                                                Color(0xFF764ba2).copy(alpha = 0.1f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF667eea),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Loading banners...",
                                        color = Color(0xFF6B7280),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else if (banners.isNotEmpty()) {
                        val pagerState = rememberPagerState(pageCount = { banners.size })

                        LaunchedEffect(pagerState) {
                            while (true) {
                                delay(2000)
                                val nextPage = (pagerState.currentPage + 1) % banners.size
                                pagerState.animateScrollToPage(nextPage)
                            }
                        }

                        Column {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) { page ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    AsyncImage(
                                        model = banners[page].imageUrl,
                                        contentDescription = "College Banner",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            // Enhanced Banner Indicators
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(banners.size) { index ->
                                    val isSelected = pagerState.currentPage == index
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .width(if (isSelected) 24.dp else 8.dp)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (isSelected) Color(0xFF667eea) else Color.Gray.copy(alpha = 0.4f)
                                            )
                                    )
                                }
                            }
                        }
                    } else {
                        // Enhanced Default Banner
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF667eea).copy(alpha = 0.1f),
                                                Color(0xFF764ba2).copy(alpha = 0.05f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Photo,
                                        contentDescription = "No banners",
                                        modifier = Modifier.size(48.dp),
                                        tint = Color(0xFF667eea)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Welcome to GECB",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = "Engineering Excellence",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Enhanced College Statistics Section
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
                                    Icons.Default.Analytics,
                                    contentDescription = "Stats",
                                    tint = Color(0xFF667eea),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "College Overview",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                            }

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.height(200.dp)
                            ) {
                                items(collegeStats.size) { index ->
                                    val stat = collegeStats[index]
                                    EnhancedStatsCard(stat = stat)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Enhanced About Section
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
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "About",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "About GECB",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                            }

                            Text(
                                text = "Government Engineering College Bilaspur is a premier technical institution in Chhattisgarh, dedicated to providing quality engineering education. With state-of-the-art infrastructure and experienced faculty, we nurture future engineers and innovators who contribute to society's technological advancement.",
                                fontSize = 15.sp,
                                color = Color(0xFF6B7280),
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Enhanced Quick Links Section
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
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Link,
                                    contentDescription = "Quick Links",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Quick Access",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                            }

                            val quickLinks = listOf(
                                Triple(Icons.Default.School, "Academics", Color(0xFF10B981)),
                                Triple(Icons.Default.People, "Admissions", Color(0xFF3B82F6)),
                                Triple(Icons.Default.MenuBook, "Library", Color(0xFFF59E0B)),
                                Triple(Icons.Default.Computer, "Facilities", Color(0xFF8B5CF6))
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.height(120.dp)
                            ) {
                                items(quickLinks.size) { index ->
                                    val link = quickLinks[index]
                                    EnhancedQuickLinkCard(
                                        icon = link.first,
                                        title = link.second,
                                        color = link.third
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun EnhancedStatsCard(stat: CollegeStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, stat.color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(stat.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = stat.title,
                    tint = stat.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stat.count,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = stat.color
            )

            Text(
                text = stat.title,
                fontSize = 11.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EnhancedQuickLinkCard(
    icon: ImageVector,
    title: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle navigation */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )
        }
    }
}