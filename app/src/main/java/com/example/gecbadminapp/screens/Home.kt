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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gecbadminapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

// Enhanced responsive design system
object ResponsiveDesign {
    // Screen size breakpoints
    const val COMPACT_WIDTH = 480
    const val MEDIUM_WIDTH = 840
    const val EXPANDED_WIDTH = 1200

    // Density breakpoints
    const val LOW_DENSITY = 1.0f
    const val MEDIUM_DENSITY = 1.5f
    const val HIGH_DENSITY = 2.0f
    const val EXTRA_HIGH_DENSITY = 3.0f
}

// Screen size class for better responsive design
enum class ScreenSize {
    COMPACT,
    MEDIUM,
    EXPANDED
}

enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE
}

// Enhanced responsive configuration
data class ResponsiveConfig(
    val screenSize: ScreenSize,
    val orientation: ScreenOrientation,
    val screenWidth: Dp,
    val screenHeight: Dp,
    val density: Float,
    val isTablet: Boolean,
    val isLandscape: Boolean
) {
    // Dynamic spacing system
    val spacingXS: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 4.dp
        ScreenSize.MEDIUM -> 6.dp
        ScreenSize.EXPANDED -> 8.dp
    }

    val spacingS: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 8.dp
        ScreenSize.MEDIUM -> 12.dp
        ScreenSize.EXPANDED -> 16.dp
    }

    val spacingM: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 16.dp
        ScreenSize.MEDIUM -> 20.dp
        ScreenSize.EXPANDED -> 24.dp
    }

    val spacingL: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 24.dp
        ScreenSize.MEDIUM -> 32.dp
        ScreenSize.EXPANDED -> 40.dp
    }

    val spacingXL: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 32.dp
        ScreenSize.MEDIUM -> 48.dp
        ScreenSize.EXPANDED -> 64.dp
    }

    // Dynamic corner radius
    val cornerRadiusS: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 8.dp
        ScreenSize.MEDIUM -> 12.dp
        ScreenSize.EXPANDED -> 16.dp
    }

    val cornerRadiusM: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 16.dp
        ScreenSize.MEDIUM -> 20.dp
        ScreenSize.EXPANDED -> 24.dp
    }

    val cornerRadiusL: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 20.dp
        ScreenSize.MEDIUM -> 28.dp
        ScreenSize.EXPANDED -> 32.dp
    }

    // Dynamic elevation
    val elevationS: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 2.dp
        ScreenSize.MEDIUM -> 4.dp
        ScreenSize.EXPANDED -> 6.dp
    }

    val elevationM: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 4.dp
        ScreenSize.MEDIUM -> 6.dp
        ScreenSize.EXPANDED -> 8.dp
    }

    val elevationL: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> 8.dp
        ScreenSize.MEDIUM -> 12.dp
        ScreenSize.EXPANDED -> 16.dp
    }

    // Grid columns
    val gridColumns: Int get() = when {
        screenSize == ScreenSize.EXPANDED && isLandscape -> 4
        screenSize == ScreenSize.EXPANDED -> 3
        screenSize == ScreenSize.MEDIUM && isLandscape -> 3
        screenSize == ScreenSize.MEDIUM -> 2
        isLandscape -> 3
        else -> 2
    }

    // Banner height
    val bannerHeight: Dp get() = when {
        screenSize == ScreenSize.EXPANDED -> (screenHeight * 0.25f).coerceAtMost(280.dp)
        screenSize == ScreenSize.MEDIUM -> (screenHeight * 0.3f).coerceAtMost(240.dp)
        isLandscape -> (screenHeight * 0.4f).coerceAtMost(180.dp)
        else -> (screenWidth * 0.45f).coerceAtMost(200.dp)
    }

    // Logo size
    val logoSize: Dp get() = when (screenSize) {
        ScreenSize.COMPACT -> if (isLandscape) 48.dp else 60.dp
        ScreenSize.MEDIUM -> 72.dp
        ScreenSize.EXPANDED -> 80.dp
    }
}

// Enhanced responsive text scaling
@Composable
fun responsiveTextSize(
    baseSize: Float,
    config: ResponsiveConfig,
    minSize: Float = baseSize * 0.7f,
    maxSize: Float = baseSize * 1.3f
): TextUnit {
    val scaleFactor = when (config.screenSize) {
        ScreenSize.COMPACT -> 0.9f
        ScreenSize.MEDIUM -> 1.0f
        ScreenSize.EXPANDED -> 1.1f
    }

    val densityAdjustment = when {
        config.density <= ResponsiveDesign.LOW_DENSITY -> 0.9f
        config.density <= ResponsiveDesign.MEDIUM_DENSITY -> 1.0f
        config.density <= ResponsiveDesign.HIGH_DENSITY -> 1.05f
        else -> 1.1f
    }

    val adjustedSize = baseSize * scaleFactor * densityAdjustment
    return max(minSize, min(maxSize, adjustedSize)).sp
}

@Composable
fun rememberResponsiveConfig(): ResponsiveConfig {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density

    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val screenWidthPx = configuration.screenWidthDp

    val screenSize = when {
        screenWidthPx < ResponsiveDesign.COMPACT_WIDTH -> ScreenSize.COMPACT
        screenWidthPx < ResponsiveDesign.MEDIUM_WIDTH -> ScreenSize.MEDIUM
        else -> ScreenSize.EXPANDED
    }

    val orientation = if (screenWidthDp > screenHeightDp) {
        ScreenOrientation.LANDSCAPE
    } else {
        ScreenOrientation.PORTRAIT
    }

    val isTablet = screenSize != ScreenSize.COMPACT
    val isLandscape = orientation == ScreenOrientation.LANDSCAPE

    return ResponsiveConfig(
        screenSize = screenSize,
        orientation = orientation,
        screenWidth = screenWidthDp,
        screenHeight = screenHeightDp,
        density = density,
        isTablet = isTablet,
        isLandscape = isLandscape
    )
}

// Data classes remain the same
data class BannerItem(
    val imageUrl: String,
    val publicId: String
)

data class CollegeStats(
    val icon: ImageVector,
    val count: String,
    val title: String,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home() {
    val config = rememberResponsiveConfig()
    val context = LocalContext.current

    var banners by remember { mutableStateOf<List<BannerItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    // Enhanced college stats with responsive colors
    val collegeStats = listOf(
        CollegeStats(Icons.Default.School, "100+", "Faculty Members", Color(0xFF10B981)),
        CollegeStats(Icons.Default.Group, "2000+", "Students", Color(0xFF3B82F6)),
        CollegeStats(Icons.Default.Business, "8", "Departments", Color(0xFFF59E0B)),
        CollegeStats(Icons.Default.EmojiEvents, "75+", "Years Legacy", Color(0xFF8B5CF6))
    )

    // Load banners from Firestore (same as before)
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
            ResponsiveHeader(config = config)

            // Main Content Card
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = config.spacingM),
                shape = RoundedCornerShape(
                    topStart = config.cornerRadiusL,
                    topEnd = config.cornerRadiusL
                ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(config.spacingL)
                ) {
                    // Banner Section
                    ResponsiveBannerSection(
                        banners = banners,
                        isLoading = isLoading,
                        config = config
                    )

                    Spacer(modifier = Modifier.height(config.spacingL))

                    // College Statistics Section
                    ResponsiveStatsSection(
                        stats = collegeStats,
                        config = config
                    )

                    Spacer(modifier = Modifier.height(config.spacingM))

                    // About Section
                    ResponsiveAboutSection(config = config)

                    Spacer(modifier = Modifier.height(config.spacingM))

                    // Quick Links Section
                    ResponsiveQuickLinksSection(config = config)

                    Spacer(modifier = Modifier.height(config.spacingXL))
                }
            }
        }
    }
}

@Composable
fun ResponsiveHeader(config: ResponsiveConfig) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(config.spacingL)
    ) {
        // Header layout adapts to orientation and screen size
        if (config.isLandscape && config.screenSize == ScreenSize.COMPACT) {
            // Compact landscape layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GECB Portal",
                        fontSize = responsiveTextSize(20f, config, 16f, 24f),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                ResponsiveLogo(config = config, compact = true)
            }
        } else {
            // Normal layout for other configurations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome to",
                        fontSize = responsiveTextSize(14f, config, 12f, 16f),
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "GECB Portal",
                        fontSize = responsiveTextSize(28f, config, 20f, 36f),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                ResponsiveLogo(config = config)
            }
        }

        Spacer(modifier = Modifier.height(config.spacingM))

        // College Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(config.cornerRadiusM),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(config.spacingL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Government Engineering College",
                    fontSize = responsiveTextSize(18f, config, 14f, 24f),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Bilaspur (C.G.)",
                    fontSize = responsiveTextSize(14f, config, 12f, 18f),
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(config.spacingS))

                // Sanskrit Slogan
                Card(
                    shape = RoundedCornerShape(config.cornerRadiusM),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "योग कर्मसु कौशलम",
                        fontSize = responsiveTextSize(14f, config, 12f, 18f),
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            horizontal = config.spacingM,
                            vertical = config.spacingS
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveLogo(config: ResponsiveConfig, compact: Boolean = false) {
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationM)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "College Logo",
            modifier = Modifier
                .size(if (compact) config.logoSize * 0.7f else config.logoSize)
                .padding(config.spacingS)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResponsiveBannerSection(
    banners: List<BannerItem>,
    isLoading: Boolean,
    config: ResponsiveConfig
) {
    when {
        isLoading -> {
            ResponsiveLoadingBanner(config = config)
        }
        banners.isNotEmpty() -> {
            ResponsiveBannerPager(banners = banners, config = config)
        }
        else -> {
            ResponsiveDefaultBanner(config = config)
        }
    }
}

@Composable
fun ResponsiveLoadingBanner(config: ResponsiveConfig) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.bannerHeight),
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationM)
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
//                horizontalAlignment = Alignment.CenterVertically
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                CircularProgressIndicator(
                    color = Color(0xFF667eea),
                    modifier = Modifier.size(
                        when (config.screenSize) {
                            ScreenSize.COMPACT -> 24.dp
                            ScreenSize.MEDIUM -> 32.dp
                            ScreenSize.EXPANDED -> 40.dp
                        }
                    )
                )
                Spacer(modifier = Modifier.height(config.spacingS))
                Text(
                    text = "Loading banners...",
                    color = Color(0xFF6B7280),
                    fontSize = responsiveTextSize(12f, config, 10f, 16f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResponsiveBannerPager(
    banners: List<BannerItem>,
    config: ResponsiveConfig
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(config.bannerHeight)
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(config.elevationL, RoundedCornerShape(config.cornerRadiusM)),
                shape = RoundedCornerShape(config.cornerRadiusM)
            ) {
                AsyncImage(
                    model = banners[page].imageUrl,
                    contentDescription = "College Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Banner Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = config.spacingM),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                val isSelected = pagerState.currentPage == index
                val indicatorSize = when (config.screenSize) {
                    ScreenSize.COMPACT -> 6.dp
                    ScreenSize.MEDIUM -> 8.dp
                    ScreenSize.EXPANDED -> 10.dp
                }
                val selectedWidth = indicatorSize * 3

                Box(
                    modifier = Modifier
                        .padding(horizontal = config.spacingXS)
                        .width(if (isSelected) selectedWidth else indicatorSize)
                        .height(indicatorSize)
                        .clip(RoundedCornerShape(indicatorSize / 2))
                        .background(
                            if (isSelected) Color(0xFF667eea) else Color.Gray.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
fun ResponsiveDefaultBanner(config: ResponsiveConfig) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.bannerHeight),
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationM)
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
                    modifier = Modifier.size(
                        when (config.screenSize) {
                            ScreenSize.COMPACT -> 32.dp
                            ScreenSize.MEDIUM -> 48.dp
                            ScreenSize.EXPANDED -> 64.dp
                        }
                    ),
                    tint = Color(0xFF667eea)
                )
                Spacer(modifier = Modifier.height(config.spacingS))
                Text(
                    text = "Welcome to GECB",
                    fontSize = responsiveTextSize(18f, config, 14f, 24f),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "Engineering Excellence",
                    fontSize = responsiveTextSize(12f, config, 10f, 16f),
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun ResponsiveStatsSection(
    stats: List<CollegeStats>,
    config: ResponsiveConfig
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(config.cornerRadiusL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationM)
    ) {
        Column(
            modifier = Modifier.padding(config.spacingL)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = config.spacingM)
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = "Stats",
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(
                        when (config.screenSize) {
                            ScreenSize.COMPACT -> 20.dp
                            ScreenSize.MEDIUM -> 24.dp
                            ScreenSize.EXPANDED -> 28.dp
                        }
                    )
                )
                Spacer(modifier = Modifier.width(config.spacingS))
                Text(
                    text = "College Overview",
                    fontSize = responsiveTextSize(18f, config, 16f, 26f),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            // Responsive grid for stats
            val rows = (stats.size + config.gridColumns - 1) / config.gridColumns
            val statsGridHeight = when (config.screenSize) {
                ScreenSize.COMPACT -> if (config.isLandscape) 100.dp else 160.dp
                ScreenSize.MEDIUM -> if (config.isLandscape) 120.dp else 180.dp
                ScreenSize.EXPANDED -> if (config.isLandscape) 140.dp else 200.dp
            } * rows

            LazyVerticalGrid(
                columns = GridCells.Fixed(config.gridColumns),
                horizontalArrangement = Arrangement.spacedBy(config.spacingS),
                verticalArrangement = Arrangement.spacedBy(config.spacingS),
                modifier = Modifier.height(statsGridHeight)
            ) {
                items(stats.size) { index ->
                    ResponsiveStatsCard(
                        stat = stats[index],
                        config = config
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveStatsCard(
    stat: CollegeStats,
    config: ResponsiveConfig
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationS),
        border = BorderStroke(1.dp, stat.color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(config.spacingS),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val iconSize = when (config.screenSize) {
                ScreenSize.COMPACT -> 32.dp
                ScreenSize.MEDIUM -> 40.dp
                ScreenSize.EXPANDED -> 48.dp
            }

            Box(
                modifier = Modifier
                    .size(iconSize + config.spacingS)
                    .background(stat.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = stat.title,
                    tint = stat.color,
                    modifier = Modifier.size(iconSize * 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(config.spacingXS))

            Text(
                text = stat.count,
                fontSize = responsiveTextSize(16f, config, 12f, 20f),
                fontWeight = FontWeight.Bold,
                color = stat.color
            )

            Text(
                text = stat.title,
                fontSize = responsiveTextSize(10f, config, 8f, 12f),
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ResponsiveAboutSection(config: ResponsiveConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(config.cornerRadiusL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationM)
    ) {
        Column(
            modifier = Modifier.padding(config.spacingL)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = config.spacingS)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "About",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(
                        when (config.screenSize) {
                            ScreenSize.COMPACT -> 20.dp
                            ScreenSize.MEDIUM -> 24.dp
                            ScreenSize.EXPANDED -> 28.dp
                        }
                    )
                )
                Spacer(modifier = Modifier.width(config.spacingS))
                Text(
                    text = "About GECB",
                    fontSize = responsiveTextSize(18f, config, 16f, 26f),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            Text(
                text = "Government Engineering College Bilaspur is a premier technical institution in Chhattisgarh, dedicated to providing quality engineering education. With state-of-the-art infrastructure and experienced faculty, we nurture future engineers and innovators who contribute to society's technological advancement.",
                fontSize = responsiveTextSize(13f, config, 11f, 16f),
                color = Color(0xFF6B7280),
                lineHeight = responsiveTextSize(18f, config, 16f, 22f),
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun ResponsiveQuickLinksSection(config: ResponsiveConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(config.cornerRadiusL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationM)
    ) {
        Column(
            modifier = Modifier.padding(config.spacingL)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = config.spacingS)
            ) {
                Icon(
                    Icons.Default.Link,
                    contentDescription = "Quick Links",
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(
                        when (config.screenSize) {
                            ScreenSize.COMPACT -> 20.dp
                            ScreenSize.MEDIUM -> 24.dp
                            ScreenSize.EXPANDED -> 28.dp
                        }
                    )
                )
                Spacer(modifier = Modifier.width(config.spacingS))
                Text(
                    text = "Quick Access",
                    fontSize = responsiveTextSize(18f, config, 16f, 26f),
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

            // Responsive quick links grid height
            val rows = (quickLinks.size + config.gridColumns - 1) / config.gridColumns
            val quickLinksHeight = when (config.screenSize) {
                ScreenSize.COMPACT -> if (config.isLandscape) 60.dp else 80.dp
                ScreenSize.MEDIUM -> if (config.isLandscape) 70.dp else 100.dp
                ScreenSize.EXPANDED -> if (config.isLandscape) 80.dp else 120.dp
            } * rows

            LazyVerticalGrid(
                columns = GridCells.Fixed(config.gridColumns),
                horizontalArrangement = Arrangement.spacedBy(config.spacingS),
                verticalArrangement = Arrangement.spacedBy(config.spacingS),
                modifier = Modifier.height(quickLinksHeight)
            ) {
                items(quickLinks.size) { index ->
                    val link = quickLinks[index]
                    ResponsiveQuickLinkCard(
                        icon = link.first,
                        title = link.second,
                        color = link.third,
                        config = config
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveQuickLinkCard(
    icon: ImageVector,
    title: String,
    color: Color,
    config: ResponsiveConfig
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle navigation */ },
        shape = RoundedCornerShape(config.cornerRadiusS),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationS),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        when (config.screenSize) {
            ScreenSize.EXPANDED -> {
                // For expanded screens (tablets), use column layout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(config.spacingM),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val iconContainerSize = 48.dp
                    Box(
                        modifier = Modifier
                            .size(iconContainerSize)
                            .background(color.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(config.spacingXS))

                    Text(
                        text = title,
                        fontSize = responsiveTextSize(12f, config, 10f, 16f),
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937),
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }

            ScreenSize.MEDIUM -> {
                // For medium screens, adaptive layout based on orientation
                if (config.isLandscape) {
                    // Row layout for landscape tablets
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(config.spacingS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconContainerSize = 32.dp
                        Box(
                            modifier = Modifier
                                .size(iconContainerSize)
                                .background(color.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(config.spacingXS))

                        Text(
                            text = title,
                            fontSize = responsiveTextSize(11f, config, 9f, 14f),
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937),
                            maxLines = 1
                        )
                    }
                } else {
                    // Column layout for portrait tablets
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(config.spacingS),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val iconContainerSize = 36.dp
                        Box(
                            modifier = Modifier
                                .size(iconContainerSize)
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

                        Spacer(modifier = Modifier.height(config.spacingXS))

                        Text(
                            text = title,
                            fontSize = responsiveTextSize(11f, config, 9f, 14f),
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }

            ScreenSize.COMPACT -> {
                // For compact screens (phones)
                if (config.isLandscape) {
                    // Compact row layout for landscape phones
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(config.spacingXS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconContainerSize = 24.dp
                        Box(
                            modifier = Modifier
                                .size(iconContainerSize)
                                .background(color.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = color,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(config.spacingXS))

                        Text(
                            text = title,
                            fontSize = responsiveTextSize(10f, config, 8f, 12f),
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937),
                            maxLines = 1
                        )
                    }
                } else {
                    // Column layout for portrait phones
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(config.spacingS),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val iconContainerSize = 28.dp
                        Box(
                            modifier = Modifier
                                .size(iconContainerSize)
                                .background(color.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = color,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(config.spacingXS))

                        Text(
                            text = title,
                            fontSize = responsiveTextSize(10f, config, 8f, 12f),
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}