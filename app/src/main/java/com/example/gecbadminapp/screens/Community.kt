package com.example.gecbadminapp.screens.GECB_Communities

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

// Using the same responsive design system from Home screen
object ResponsiveDesign {
    const val COMPACT_WIDTH = 480
    const val MEDIUM_WIDTH = 840
    const val EXPANDED_WIDTH = 1200
    const val LOW_DENSITY = 1.0f
    const val MEDIUM_DENSITY = 1.5f
    const val HIGH_DENSITY = 2.0f
    const val EXTRA_HIGH_DENSITY = 3.0f
}

enum class ScreenSize {
    COMPACT,
    MEDIUM,
    EXPANDED
}

enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE
}

data class ResponsiveConfig(
    val screenSize: ScreenSize,
    val orientation: ScreenOrientation,
    val screenWidth: Dp,
    val screenHeight: Dp,
    val density: Float,
    val isTablet: Boolean,
    val isLandscape: Boolean
) {
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
}

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

// Enhanced Community data class with more features
data class Community(
    val id: String,
    val name: String,
    val description: String,
    val detailedDescription: String,
    val icon: ImageVector,
    val color: Color,
    val imageUrl: String,
    val memberCount: String,
    val upcomingEvents: Int,
    val category: String,
    val isActive: Boolean,
    val rating: Float,
    val achievements: List<String>,
    val meetingTime: String,
    val location: String
)

data class CommunityStats(
    val totalMembers: Int,
    val activeCommunities: Int,
    val upcomingEvents: Int,
    val totalAchievements: Int
)

@Composable
fun CommunityScreen() {
    val config = rememberResponsiveConfig()
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var showStats by remember { mutableStateOf(true) }

    // Enhanced community data with more details
    val communities = listOf(
        Community(
            id = "nss",
            name = "NSS",
            description = "National Service Scheme - Dedicated to community service",
            detailedDescription = "National Service Scheme is a Central Sector Scheme of Government of India, Ministry of Youth Affairs & Sports. It provides opportunity to the student youth of 11th & 12th Class of schools at +2 Board level and student youth of Technical Institution, Graduate & Post Graduate at colleges and University level of India to take part in various government led community service activities & programmes.",
            icon = Icons.Default.VolunteerActivism,
            color = Color(0xFF10B981),
            imageUrl = "nss_community_banner.jpg",
            memberCount = "150+",
            upcomingEvents = 3,
            category = "Service",
            isActive = true,
            rating = 4.8f,
            achievements = listOf("Best NSS Unit 2023", "Community Impact Award", "Environmental Excellence"),
            meetingTime = "Every Saturday 10:00 AM",
            location = "Community Hall"
        ),
        Community(
            id = "sports",
            name = "Sports Club",
            description = "Promoting fitness and athletic excellence",
            detailedDescription = "Our Sports Club is dedicated to promoting physical fitness, sportsmanship, and athletic excellence among students. We organize various tournaments, training sessions, and fitness programs throughout the year.",
            icon = Icons.Default.SportsBaseball,
            color = Color(0xFF3B82F6),
            imageUrl = "sports_club_banner.jpg",
            memberCount = "200+",
            upcomingEvents = 5,
            category = "Sports",
            isActive = true,
            rating = 4.7f,
            achievements = listOf("Inter-College Champions", "Best Sports Facility", "Fitness Excellence Award"),
            meetingTime = "Daily 6:00 AM & 5:00 PM",
            location = "Sports Complex"
        ),
        Community(
            id = "cultural",
            name = "Cultural Club",
            description = "Celebrating arts and creative expression",
            detailedDescription = "The Cultural Club is the heart of artistic expression in our college. We organize cultural festivals, art exhibitions, music concerts, dance performances, and various creative workshops.",
            icon = Icons.Default.MusicVideo,
            color = Color(0xFFEF4444),
            imageUrl = "cultural_club_banner.jpg",
            memberCount = "180+",
            upcomingEvents = 4,
            category = "Arts",
            isActive = true,
            rating = 4.9f,
            achievements = listOf("Best Cultural Event 2023", "Creative Excellence Award", "Art Festival Winners"),
            meetingTime = "Every Friday 4:00 PM",
            location = "Auditorium"
        ),
        Community(
            id = "coding",
            name = "Coding Club",
            description = "Fostering programming and technical innovation",
            detailedDescription = "The Coding Club is a community of passionate programmers and tech enthusiasts. We organize hackathons, coding competitions, tech talks, and workshops on latest technologies.",
            icon = Icons.Default.Code,
            color = Color(0xFF8B5CF6),
            imageUrl = "coding_club_banner.jpg",
            memberCount = "120+",
            upcomingEvents = 2,
            category = "Technology",
            isActive = true,
            rating = 4.6f,
            achievements = listOf("National Hackathon Winners", "Best Tech Innovation", "Coding Excellence"),
            meetingTime = "Every Wednesday 6:00 PM",
            location = "Computer Lab"
        ),
        Community(
            id = "tpo",
            name = "TPO/Placement Cell",
            description = "Bridging students with career opportunities",
            detailedDescription = "Training and Placement Office works as a bridge between students and industry. We provide career guidance, skill development programs, placement assistance, and industry interaction opportunities.",
            icon = Icons.Default.Work,
            color = Color(0xFFF59E0B),
            imageUrl = "tpo_banner.jpg",
            memberCount = "50+",
            upcomingEvents = 6,
            category = "Career",
            isActive = true,
            rating = 4.5f,
            achievements = listOf("100% Placement Record", "Industry Partnership Excellence", "Career Success Award"),
            meetingTime = "Every Monday 2:00 PM",
            location = "Placement Office"
        ),
        Community(
            id = "gdsc",
            name = "GDSC",
            description = "Google Developer Student Club",
            detailedDescription = "Google Developer Student Club is a community group for college students interested in Google developer technologies. Students from all undergraduate or graduate programs with an interest in growing as a developer are welcome.",
            icon = Icons.Default.DeveloperMode,
            color = Color(0xFF06B6D4),
            imageUrl = "gdsc_banner.jpg",
            memberCount = "80+",
            upcomingEvents = 3,
            category = "Technology",
            isActive = true,
            rating = 4.7f,
            achievements = listOf("Google Developer Challenge", "Tech Innovation Award", "Community Impact"),
            meetingTime = "Every Thursday 5:00 PM",
            location = "Innovation Lab"
        )
    )

    val categories = listOf("All", "Service", "Sports", "Arts", "Technology", "Career")

    val filteredCommunities = if (selectedCategory == "All") {
        communities
    } else {
        communities.filter { it.category == selectedCategory }
    }

    val communityStats = CommunityStats(
        totalMembers = communities.sumOf { it.memberCount.replace("+", "").toIntOrNull() ?: 0 },
        activeCommunities = communities.count { it.isActive },
        upcomingEvents = communities.sumOf { it.upcomingEvents },
        totalAchievements = communities.sumOf { it.achievements.size }
    )

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
        // Animated Background Elements
        AnimatedBackgroundElements(config = config)

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced Header Section with Animation
            EnhancedCommunitiesHeader(config = config)

            // Main Content Card with Enhanced Design
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = config.spacingM),
                shape = RoundedCornerShape(
                    topStart = config.cornerRadiusL,
                    topEnd = config.cornerRadiusL
                ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                elevation = CardDefaults.cardElevation(config.elevationL)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(config.spacingL),
                    verticalArrangement = Arrangement.spacedBy(config.spacingM)
                ) {
                    // Enhanced Page Title with Stats Toggle
                    item {
                        EnhancedPageTitle(
                            config = config,
                            showStats = showStats,
                            onToggleStats = { showStats = !showStats }
                        )
                    }

                    // Community Stats Cards (Animated)
                    if (showStats) {
                        item {
                            AnimatedStatsSection(
                                stats = communityStats,
                                config = config
                            )
                        }
                    }

                    // Category Filter Chips
                    item {
                        CategoryFilterSection(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it },
                            config = config
                        )
                    }

                    // Featured Community Carousel (Top 3)
                    item {
                        FeaturedCommunitiesCarousel(
                            communities = communities.take(3),
                            config = config,
                            onCommunityClick = { community ->
                                println("${community.name} - Detailed view coming soon!")
                            }
                        )
                    }

                    // Section Header for All Communities
                    item {
                        SectionHeader(
                            title = "All Communities",
                            subtitle = "${filteredCommunities.size} communities available",
                            config = config
                        )
                    }

                    // Enhanced Community Grid
                    val communityRows = filteredCommunities.chunked(2)
                    itemsIndexed(communityRows) { index, communityPair ->
                        AnimatedCommunityRow(
                            communityPair = communityPair,
                            config = config,
                            animationDelay = index * 100,
                            onCommunityClick = { community ->
                                println("${community.name} - Detailed view coming soon!")
                            }
                        )
                    }

                    // Join Community CTA Section
                    item {
                        JoinCommunitySection(config = config)
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(config.spacingXL))
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedBackgroundElements(config: ResponsiveConfig) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    // Floating circles animation
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ),
        label = "offset1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing)
        ),
        label = "offset2"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Floating elements
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size((50 + index * 20).dp)
                    .offset(
                        x = (config.screenWidth * (0.1f + index * 0.3f)) + (offset1 * 0.1f).dp,
                        y = (config.screenHeight * (0.2f + index * 0.2f)) + (offset2 * 0.05f).dp
                    )
                    .background(
                        Color.White.copy(alpha = 0.05f),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
fun EnhancedCommunitiesHeader(config: ResponsiveConfig) {
    val scale by rememberInfiniteTransition(label = "header").animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = config.spacingM,
                vertical = config.spacingL
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Animated text reveal
                var isVisible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(300)
                    isVisible = true
                }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInHorizontally() + fadeIn()
                ) {
                    Column {
                        Text(
                            text = "GECB",
                            fontSize = responsiveTextSize(
                                baseSize = when {
                                    config.screenWidth < 350.dp -> 14f
                                    config.screenWidth < 400.dp -> 16f
                                    else -> 18f
                                },
                                config = config,
                                minSize = 12f,
                                maxSize = 20f
                            ),
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Communities",
                            fontSize = responsiveTextSize(
                                baseSize = when {
                                    config.screenWidth < 350.dp -> 24f
                                    config.screenWidth < 400.dp -> 28f
                                    else -> 32f
                                },
                                config = config,
                                minSize = 22f,
                                maxSize = 36f
                            ),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Enhanced animated icon
            val iconContainerSize = (config.screenWidth * 0.15f).coerceIn(48.dp, 72.dp)

            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(config.elevationM),
                modifier = Modifier.scale(scale)
            ) {
                Box(
                    modifier = Modifier
                        .size(iconContainerSize)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF667eea).copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = "Communities",
                        modifier = Modifier.size(iconContainerSize * 0.5f),
                        tint = Color(0xFF667eea)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(config.spacingM))

        // Enhanced subtitle with gradient background
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(config.cornerRadiusM),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        )
                    )
                    .padding(config.spacingM)
            ) {
                Text(
                    text = "🚀 Join our vibrant communities and be part of something amazing! Connect, learn, and grow together.",
                    fontSize = responsiveTextSize(
                        baseSize = when {
                            config.screenWidth < 350.dp -> 12f
                            config.screenWidth < 400.dp -> 13f
                            else -> 14f
                        },
                        config = config,
                        minSize = 11f,
                        maxSize = 16f
                    ),
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EnhancedPageTitle(
    config: ResponsiveConfig,
    showStats: Boolean,
    onToggleStats: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(config.cornerRadiusL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationM)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(config.spacingM)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val iconSize = (config.screenWidth * 0.08f).coerceIn(20.dp, 32.dp)

                Box(
                    modifier = Modifier
                        .size(iconSize + 8.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF667eea).copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Explore,
                        contentDescription = "Explore",
                        tint = Color(0xFF667eea),
                        modifier = Modifier.size(iconSize)
                    )
                }

                Spacer(modifier = Modifier.width(config.spacingS))

                Column {
                    Text(
                        text = "Explore Communities",
                        fontSize = responsiveTextSize(
                            baseSize = when {
                                config.screenWidth < 350.dp -> 16f
                                config.screenWidth < 400.dp -> 18f
                                else -> 20f
                            },
                            config = config,
                            minSize = 16f,
                            maxSize = 24f
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Discover your passion and connect with peers",
                        fontSize = responsiveTextSize(
                            baseSize = when {
                                config.screenWidth < 350.dp -> 10f
                                config.screenWidth < 400.dp -> 11f
                                else -> 12f
                            },
                            config = config,
                            minSize = 10f,
                            maxSize = 14f
                        ),
                        color = Color(0xFF6B7280),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Toggle Stats Button
            IconButton(
                onClick = onToggleStats,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (showStats) Color(0xFF667eea).copy(alpha = 0.1f) else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    if (showStats) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Stats",
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AnimatedStatsSection(
    stats: CommunityStats,
    config: ResponsiveConfig
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 }
        ) + fadeIn()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(config.spacingS)
        ) {
            val statCards = listOf(
                Triple("Members", "${stats.totalMembers}+", Icons.Default.People),
                Triple("Communities", "${stats.activeCommunities}", Icons.Default.Groups),
                Triple("Events", "${stats.upcomingEvents}", Icons.Default.Event),
                Triple("Awards", "${stats.totalAchievements}", Icons.Default.EmojiEvents)
            )

            statCards.forEachIndexed { index, (title, value, icon) ->
                AnimatedStatCard(
                    title = title,
                    value = value,
                    icon = icon,
                    color = when (index) {
                        0 -> Color(0xFF10B981)
                        1 -> Color(0xFF3B82F6)
                        2 -> Color(0xFFEF4444)
                        else -> Color(0xFFF59E0B)
                    },
                    config = config,
                    modifier = Modifier.weight(1f),
                    animationDelay = index * 200
                )
            }
        }
    }
}

@Composable
fun AnimatedStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    config: ResponsiveConfig,
    modifier: Modifier = Modifier,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationS)
    ) {
        Column(
            modifier = Modifier.padding(config.spacingS),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(
                    when {
                        config.screenWidth < 350.dp -> 20.dp
                        config.screenWidth < 400.dp -> 24.dp
                        else -> 28.dp
                    }
                )
            )
            Spacer(modifier = Modifier.height(config.spacingXS))
            Text(
                text = value,
                fontSize = responsiveTextSize(
                    baseSize = when {
                        config.screenWidth < 350.dp -> 16f
                        config.screenWidth < 400.dp -> 18f
                        else -> 20f
                    },
                    config = config,
                    minSize = 14f,
                    maxSize = 22f
                ),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )
            Text(
                text = title,
                fontSize = responsiveTextSize(
                    baseSize = when {
                        config.screenWidth < 350.dp -> 10f
                        config.screenWidth < 400.dp -> 11f
                        else -> 12f
                    },
                    config = config,
                    minSize = 9f,
                    maxSize = 13f
                ),
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryFilterSection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    config: ResponsiveConfig
) {
    Column {
        Text(
            text = "Categories",
            fontSize = responsiveTextSize(
                baseSize = 16f,
                config = config,
                minSize = 14f,
                maxSize = 18f
            ),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = config.spacingS)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(config.spacingS),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                    config = config
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    config: ResponsiveConfig
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF667eea) else Color.White,
        animationSpec = tween(300),
        label = "backgroundColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF6B7280),
        animationSpec = tween(300),
        label = "textColor"
    )

    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(config.cornerRadiusL),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            if (isSelected) config.elevationM else config.elevationS
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = config.spacingM,
                vertical = config.spacingS
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            val categoryIcon = when (category) {
                "All" -> Icons.Default.Apps
                "Service" -> Icons.Default.VolunteerActivism
                "Sports" -> Icons.Default.SportsBaseball
                "Arts" -> Icons.Default.MusicNote
                "Technology" -> Icons.Default.Code
                "Career" -> Icons.Default.Work
                else -> Icons.Default.Category
            }

            Icon(
                categoryIcon,
                contentDescription = category,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(config.spacingXS))
            Text(
                text = category,
                fontSize = responsiveTextSize(
                    baseSize = 12f,
                    config = config,
                    minSize = 10f,
                    maxSize = 14f
                ),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun FeaturedCommunitiesCarousel(
    communities: List<Community>,
    config: ResponsiveConfig,
    onCommunityClick: (Community) -> Unit
) {
    Column {
        SectionHeader(
            title = "Featured Communities",
            subtitle = "Top performing communities this month",
            config = config
        )

        Spacer(modifier = Modifier.height(config.spacingS))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(config.spacingM),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(communities) { index, community ->
                FeaturedCommunityCard(
                    community = community,
                    config = config,
                    onClick = { onCommunityClick(community) },
                    animationDelay = index * 150
                )
            }
        }
    }
}

@Composable
fun FeaturedCommunityCard(
    community: Community,
    config: ResponsiveConfig,
    onClick: () -> Unit,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val cardWidth = (config.screenWidth * 0.7f).coerceIn(250.dp, 320.dp)
    val cardHeight = cardWidth * 1.2f

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(config.cornerRadiusL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationL)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                community.color.copy(alpha = 0.1f),
                                community.color.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with community info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight * 0.4f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    community.color.copy(alpha = 0.8f),
                                    community.color.copy(alpha = 0.6f)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(config.spacingM),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Community icon with enhanced styling
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color.White.copy(alpha = 0.9f),
                                    CircleShape
                                )
                                .shadow(4.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = community.icon,
                                contentDescription = community.name,
                                tint = community.color,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(config.spacingS))

                        Text(
                            text = community.name,
                            fontSize = responsiveTextSize(18f, config),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Featured badge
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(config.spacingS),
                        shape = RoundedCornerShape(config.cornerRadiusS),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = config.spacingXS,
                                vertical = 2.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Featured",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Featured",
                                fontSize = responsiveTextSize(8f, config),
                                fontWeight = FontWeight.Bold,
                                color = community.color
                            )
                        }
                    }
                }

                // Content section
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(config.spacingM)
                ) {
                    // Rating section
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            Icon(
                                if (index < community.rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Rating",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(config.spacingXS))
                        Text(
                            text = community.rating.toString(),
                            fontSize = responsiveTextSize(12f, config),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    Spacer(modifier = Modifier.height(config.spacingS))

                    // Description
                    Text(
                        text = community.description,
                        fontSize = responsiveTextSize(11f, config),
                        color = Color(0xFF6B7280),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.height(config.spacingS))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Members
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.People,
                                contentDescription = "Members",
                                tint = community.color,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = community.memberCount,
                                fontSize = responsiveTextSize(10f, config),
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Events
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = "Events",
                                tint = community.color,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${community.upcomingEvents} events",
                                fontSize = responsiveTextSize(10f, config),
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    config: ResponsiveConfig
) {
    Column {
        Text(
            text = title,
            fontSize = responsiveTextSize(18f, config),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        Text(
            text = subtitle,
            fontSize = responsiveTextSize(12f, config),
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AnimatedCommunityRow(
    communityPair: List<Community>,
    config: ResponsiveConfig,
    animationDelay: Int = 0,
    onCommunityClick: (Community) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    val horizontalSpacing = (config.screenWidth * 0.03f).coerceIn(8.dp, 16.dp)

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 }
        ) + fadeIn()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
        ) {
            EnhancedCommunityCard(
                community = communityPair[0],
                config = config,
                onClick = { onCommunityClick(communityPair[0]) },
                modifier = Modifier.weight(1f)
            )

            if (communityPair.size > 1) {
                EnhancedCommunityCard(
                    community = communityPair[1],
                    config = config,
                    onClick = { onCommunityClick(communityPair[1]) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun EnhancedCommunityCard(
    community: Community,
    config: ResponsiveConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )

    val cardHeight = (config.screenWidth * 0.75f).coerceIn(260.dp, 320.dp)
    val headerHeight = cardHeight * 0.45f

    Card(
        modifier = modifier
            .height(cardHeight)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            }
            .shadow(config.elevationM, RoundedCornerShape(config.cornerRadiusL)),
        shape = RoundedCornerShape(config.cornerRadiusL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
            ) {
                // Gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    community.color.copy(alpha = 0.9f),
                                    community.color.copy(alpha = 0.7f),
                                    community.color.copy(alpha = 0.5f)
                                ),
                                radius = 300f
                            )
                        )
                )

                // Pattern overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.05f)
                                ),
                                radius = 200f
                            )
                        )
                )

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(config.spacingM),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val iconContainerSize = (headerHeight * 0.35f).coerceIn(36.dp, 60.dp)

                    // Enhanced icon container
                    Box(
                        modifier = Modifier
                            .size(iconContainerSize)
                            .background(
                                Color.White.copy(alpha = 0.95f),
                                CircleShape
                            )
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner glow effect
                        Box(
                            modifier = Modifier
                                .size(iconContainerSize * 0.8f)
                                .background(
                                    community.color.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = community.icon,
                                contentDescription = community.name,
                                tint = community.color,
                                modifier = Modifier.size(iconContainerSize * 0.45f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(config.spacingS))

                    Text(
                        text = community.name,
                        fontSize = responsiveTextSize(
                            baseSize = when {
                                config.screenWidth < 350.dp -> 14f
                                config.screenWidth < 400.dp -> 16f
                                else -> 18f
                            },
                            config = config
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Enhanced member badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(config.spacingS),
                    shape = RoundedCornerShape(config.cornerRadiusM),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = config.spacingS,
                            vertical = config.spacingXS
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = "Members",
                            tint = community.color,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = community.memberCount,
                            fontSize = responsiveTextSize(10f, config),
                            fontWeight = FontWeight.Bold,
                            color = community.color
                        )
                    }
                }

                // Activity indicator (if active)
                if (community.isActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(config.spacingS)
                            .size(12.dp)
                            .background(Color(0xFF10B981), CircleShape)
                            .shadow(2.dp, CircleShape)
                    )
                }
            }

            // Enhanced Content Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(config.spacingM)
            ) {
                // Rating and category row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = community.rating.toString(),
                            fontSize = responsiveTextSize(11f, config),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    // Category chip
                    Card(
                        shape = RoundedCornerShape(config.cornerRadiusS),
                        colors = CardDefaults.cardColors(
                            containerColor = community.color.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = community.category,
                            fontSize = responsiveTextSize(8f, config),
                            fontWeight = FontWeight.Bold,
                            color = community.color,
                            modifier = Modifier.padding(
                                horizontal = config.spacingXS,
                                vertical = 2.dp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(config.spacingS))

                // Description
                Text(
                    text = community.description,
                    fontSize = responsiveTextSize(11f, config),
                    color = Color(0xFF6B7280),
                    lineHeight = responsiveTextSize(15f, config),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(config.spacingS))

                // Meeting info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Schedule",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = community.meetingTime,
                        fontSize = responsiveTextSize(9f, config),
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(config.spacingS))

                // Bottom action row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Events info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = "Events",
                            tint = community.color,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${community.upcomingEvents} events",
                            fontSize = responsiveTextSize(10f, config),
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Enhanced action button
                    Card(
                        shape = RoundedCornerShape(config.cornerRadiusM),
                        colors = CardDefaults.cardColors(
                            containerColor = community.color
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = config.spacingS,
                                vertical = config.spacingXS
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Join Now",
                                fontSize = responsiveTextSize(9f, config),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Join",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
fun JoinCommunitySection(config: ResponsiveConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(config.cornerRadiusL),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF667eea).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(config.elevationS)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(config.spacingL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.GroupAdd,
                contentDescription = "Join Community",
                tint = Color(0xFF667eea),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(config.spacingM))

            Text(
                text = "Ready to Join a Community?",
                fontSize = responsiveTextSize(20f, config),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(config.spacingS))

            Text(
                text = "Connect with like-minded peers, participate in exciting events, and make lasting friendships. Your journey starts here!",
                fontSize = responsiveTextSize(14f, config),
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = responsiveTextSize(20f, config)
            )

            Spacer(modifier = Modifier.height(config.spacingL))

            Button(
                onClick = {
                    // Handle join community action
                    println("Join Community - Contact admin for more details!")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(config.cornerRadiusM),
                elevation = ButtonDefaults.buttonElevation(config.elevationS)
            ) {
                Icon(
                    Icons.Default.ContactSupport,
                    contentDescription = "Contact",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(config.spacingS))
                Text(
                    text = "Contact Admin",
                    fontSize = responsiveTextSize(14f, config),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}