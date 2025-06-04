package com.example.gecbadminapp.screens.GECB_Communities

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

// Responsive design system
object ResponsiveDesign {
    const val COMPACT_WIDTH = 480
    const val MEDIUM_WIDTH = 840
    const val EXPANDED_WIDTH = 1200
}

enum class ScreenSize { COMPACT, MEDIUM, EXPANDED }
enum class ScreenOrientation { PORTRAIT, LANDSCAPE }

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
        ScreenSize.MEDIUM -> 24.dp
        ScreenSize.EXPANDED -> 28.dp
    }
    val elevationS: Dp get() = 2.dp
    val elevationM: Dp get() = 4.dp
    val elevationL: Dp get() = 8.dp
}

@Composable
fun responsiveTextSize(
    baseSize: Float,
    config: ResponsiveConfig,
    minSize: Float = baseSize * 0.8f,
    maxSize: Float = baseSize * 1.2f
): TextUnit {
    val scaleFactor = when (config.screenSize) {
        ScreenSize.COMPACT -> 0.9f
        ScreenSize.MEDIUM -> 1.0f
        ScreenSize.EXPANDED -> 1.1f
    }
    val adjustedSize = baseSize * scaleFactor
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

    return ResponsiveConfig(
        screenSize = screenSize,
        orientation = orientation,
        screenWidth = screenWidthDp,
        screenHeight = screenHeightDp,
        density = density,
        isTablet = screenSize != ScreenSize.COMPACT,
        isLandscape = orientation == ScreenOrientation.LANDSCAPE
    )
}

data class Community(
    val id: String,
    val name: String,
    val description: String,
    val detailedDescription: String,
    val icon: ImageVector,
    val color: Color,
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
    var selectedCategory by remember { mutableStateOf("All") }
    var showStats by remember { mutableStateOf(true) }

    val communities = listOf(
        Community(
            id = "nss",
            name = "NSS",
            description = "National Service Scheme - Community service and social responsibility",
            detailedDescription = "National Service Scheme is a Central Sector Scheme of Government of India, Ministry of Youth Affairs & Sports.",
            icon = Icons.Default.VolunteerActivism,
            color = Color(0xFF10B981),
            memberCount = "150+",
            upcomingEvents = 3,
            category = "Service",
            isActive = true,
            rating = 4.8f,
            achievements = listOf("Best NSS Unit 2023", "Community Impact Award", "Environmental Excellence"),
            meetingTime = "Saturdays 10:00 AM",
            location = "Community Hall"
        ),
        Community(
            id = "sports",
            name = "Sports Club",
            description = "Promoting fitness, athletics and sportsmanship excellence",
            detailedDescription = "Our Sports Club promotes physical fitness, sportsmanship, and athletic excellence among students.",
            icon = Icons.Default.SportsBaseball,
            color = Color(0xFF3B82F6),
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
            description = "Celebrating arts, creativity and cultural expression",
            detailedDescription = "The Cultural Club is the heart of artistic expression in our college.",
            icon = Icons.Default.MusicVideo,
            color = Color(0xFFEF4444),
            memberCount = "180+",
            upcomingEvents = 4,
            category = "Arts",
            isActive = true,
            rating = 4.9f,
            achievements = listOf("Best Cultural Event 2023", "Creative Excellence Award", "Art Festival Winners"),
            meetingTime = "Fridays 4:00 PM",
            location = "Auditorium"
        ),
        Community(
            id = "coding",
            name = "Coding Club",
            description = "Programming, development and technical innovation hub",
            detailedDescription = "The Coding Club is a community of passionate programmers and tech enthusiasts.",
            icon = Icons.Default.Code,
            color = Color(0xFF8B5CF6),
            memberCount = "120+",
            upcomingEvents = 2,
            category = "Technology",
            isActive = true,
            rating = 4.6f,
            achievements = listOf("National Hackathon Winners", "Best Tech Innovation", "Coding Excellence"),
            meetingTime = "Wednesdays 6:00 PM",
            location = "Computer Lab"
        ),
        Community(
            id = "tpo",
            name = "TPO Cell",
            description = "Career guidance and placement assistance services",
            detailedDescription = "Training and Placement Office works as a bridge between students and industry.",
            icon = Icons.Default.Work,
            color = Color(0xFFF59E0B),
            memberCount = "50+",
            upcomingEvents = 6,
            category = "Career",
            isActive = true,
            rating = 4.5f,
            achievements = listOf("100% Placement Record", "Industry Partnership Excellence", "Career Success Award"),
            meetingTime = "Mondays 2:00 PM",
            location = "Placement Office"
        ),
        Community(
            id = "gdsc",
            name = "GDSC",
            description = "Google Developer Student Club for tech enthusiasts",
            detailedDescription = "Google Developer Student Club for college students interested in Google developer technologies.",
            icon = Icons.Default.DeveloperMode,
            color = Color(0xFF06B6D4),
            memberCount = "80+",
            upcomingEvents = 3,
            category = "Technology",
            isActive = true,
            rating = 4.7f,
            achievements = listOf("Google Developer Challenge", "Tech Innovation Award", "Community Impact"),
            meetingTime = "Thursdays 5:00 PM",
            location = "Innovation Lab"
        )
    )

    val categories = listOf("All", "Service", "Sports", "Arts", "Technology", "Career")
    val filteredCommunities = if (selectedCategory == "All") communities else communities.filter { it.category == selectedCategory }

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
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Section
            CommunityHeader(config = config)

            // Main Content
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
                    verticalArrangement = Arrangement.spacedBy(config.spacingL)
                ) {
                    // Page Title
                    item {
                        PageTitle(
                            config = config,
                            showStats = showStats,
                            onToggleStats = { showStats = !showStats }
                        )
                    }

                    // Stats Section
                    if (showStats) {
                        item {
                            StatsSection(stats = communityStats, config = config)
                        }
                    }

                    // Category Filter
                    item {
                        CategoryFilter(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it },
                            config = config
                        )
                    }

                    // Featured Communities


                    // All Communities Grid
                    item {
                        Text(
                            text = "All Communities",
                            fontSize = responsiveTextSize(18f, config),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(bottom = config.spacingS)
                        )
                    }

                    val communityRows = filteredCommunities.chunked(2)
                    itemsIndexed(communityRows) { _, communityPair ->
                        CommunityRow(
                            communityPair = communityPair,
                            config = config
                        )
                    }

                    // Join CTA
                    item {
                        JoinCommunityCard(config = config)
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityHeader(config: ResponsiveConfig) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(config.spacingL)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GECB",
                    fontSize = responsiveTextSize(16f, config),
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Communities",
                    fontSize = responsiveTextSize(28f, config),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(config.elevationM)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = "Communities",
                        tint = Color(0xFF667eea),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(config.spacingM))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(config.cornerRadiusM),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            )
        ) {
            Text(
                text = "Connect, learn, and grow with our vibrant student communities",
                fontSize = responsiveTextSize(14f, config),
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(config.spacingM)
            )
        }
    }
}

@Composable
fun PageTitle(
    config: ResponsiveConfig,
    showStats: Boolean,
    onToggleStats: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationS)
    ) {
        Row(
            modifier = Modifier.padding(config.spacingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Explore,
                    contentDescription = "Explore",
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(config.spacingS))
                Column {
                    Text(
                        text = "Explore Communities",
                        fontSize = responsiveTextSize(18f, config),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Find your perfect community match",
                        fontSize = responsiveTextSize(12f, config),
                        color = Color(0xFF6B7280)
                    )
                }
            }

            IconButton(onClick = onToggleStats) {
                Icon(
                    if (showStats) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Stats",
                    tint = Color(0xFF667eea)
                )
            }
        }
    }
}

@Composable
fun StatsSection(stats: CommunityStats, config: ResponsiveConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationS)
    ) {
        Column(modifier = Modifier.padding(config.spacingM)) {
            Text(
                text = "Community Statistics",
                fontSize = responsiveTextSize(16f, config),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = config.spacingM)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(config.spacingS)
            ) {
                StatCard("${stats.totalMembers}+", "Members", Icons.Default.People, Color(0xFF10B981), config, Modifier.weight(1f))
                StatCard("${stats.activeCommunities}", "Active", Icons.Default.Groups, Color(0xFF3B82F6), config, Modifier.weight(1f))
                StatCard("${stats.upcomingEvents}", "Events", Icons.Default.Event, Color(0xFFEF4444), config, Modifier.weight(1f))
                StatCard("${stats.totalAchievements}", "Awards", Icons.Default.EmojiEvents, Color(0xFFF59E0B), config, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    config: ResponsiveConfig,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(config.cornerRadiusS),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(config.spacingS),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(config.spacingXS))
            Text(
                text = value,
                fontSize = responsiveTextSize(16f, config),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Text(
                text = label,
                fontSize = responsiveTextSize(10f, config),
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun CategoryFilter(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    config: ResponsiveConfig
) {
    Column {
        Text(
            text = "Categories",
            fontSize = responsiveTextSize(16f, config),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = config.spacingS)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(config.spacingS)
        ) {
            items(categories) { category ->
                FilterChip(
                    onClick = { onCategorySelected(category) },
                    label = { Text(category, fontSize = responsiveTextSize(12f, config)) },
                    selected = category == selectedCategory,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF667eea),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color(0xFF6B7280)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = category == selectedCategory,
                        borderColor = if (category == selectedCategory) Color.Transparent else Color(0xFFE5E7EB)
                    )
                )
            }
        }
    }
}


@Composable
fun FeaturedCommunityCard(
    community: Community,
    config: ResponsiveConfig
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp)
            .clickable { /* Handle click */ },
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationS)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                community.color.copy(alpha = 0.1f),
                                community.color.copy(alpha = 0.05f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(config.spacingM)
            ) {
                // Icon section
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = community.color.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            community.icon,
                            contentDescription = community.name,
                            tint = community.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(config.spacingM))

                // Content section
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = community.name,
                            fontSize = responsiveTextSize(16f, config),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = community.description,
                            fontSize = responsiveTextSize(12f, config),
                            color = Color(0xFF6B7280),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(config.spacingM),
                        verticalAlignment = Alignment.CenterVertically
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
                                fontSize = responsiveTextSize(11f, config),
                                color = Color(0xFF6B7280)
                            )
                        }

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
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            // Featured badge
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(config.spacingS),
                shape = RoundedCornerShape(config.cornerRadiusS),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B))
            ) {
                Text(
                    text = "Featured",
                    fontSize = responsiveTextSize(8f, config),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CommunityRow(
    communityPair: List<Community>,
    config: ResponsiveConfig
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(config.spacingM)
    ) {
        CommunityCard(
            community = communityPair[0],
            config = config,
            modifier = Modifier.weight(1f)
        )

        if (communityPair.size > 1) {
            CommunityCard(
                community = communityPair[1],
                config = config,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun CommunityCard(
    community: Community,
    config: ResponsiveConfig,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(220.dp)
            .clickable { /* Handle click */ },
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(config.elevationS)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with icon and title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                community.color.copy(alpha = 0.8f),
                                community.color.copy(alpha = 0.6f)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(config.spacingM),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                community.icon,
                                contentDescription = community.name,
                                tint = community.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(config.spacingS))

                    Text(
                        text = community.name,
                        fontSize = responsiveTextSize(16f, config),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Status indicator - moved inside the header box
                if (community.isActive) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(config.spacingS),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981))
                    ) {
                        Box(
                            modifier = Modifier.size(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Active status indicator dot
                        }
                    }
                }
            }

            // Content section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(config.spacingM),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = community.description,
                        fontSize = responsiveTextSize(12f, config),
                        color = Color(0xFF6B7280),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(config.spacingS))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(config.spacingS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category tag
                        Card(
                            shape = RoundedCornerShape(config.cornerRadiusS),
                            colors = CardDefaults.cardColors(
                                containerColor = community.color.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = community.category,
                                fontSize = responsiveTextSize(9f, config),
                                fontWeight = FontWeight.Medium,
                                color = community.color,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = community.rating.toString(),
                                fontSize = responsiveTextSize(10f, config),
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                // Bottom info
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Members count
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
                                fontSize = responsiveTextSize(11f, config),
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F2937)
                            )
                        }

                        // Upcoming events
                        if (community.upcomingEvents > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Event,
                                    contentDescription = "Events",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${community.upcomingEvents} events",
                                    fontSize = responsiveTextSize(10f, config),
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(config.spacingXS))

                    // Meeting info
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                }
            }
        }
    }
}


@Composable
fun JoinCommunityCard(config: ResponsiveConfig) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle join community */ },
        shape = RoundedCornerShape(config.cornerRadiusM),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF667eea)
        ),
        elevation = CardDefaults.cardElevation(config.elevationM)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(config.spacingL),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start Your Own Community",
                        fontSize = responsiveTextSize(18f, config),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(config.spacingXS))
                    Text(
                        text = "Have an idea for a new community? Let's make it happen together!",
                        fontSize = responsiveTextSize(12f, config),
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = responsiveTextSize(16f, config)
                    )
                    Spacer(modifier = Modifier.height(config.spacingM))

                    Card(
                        shape = RoundedCornerShape(config.cornerRadiusS),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = config.spacingM,
                                vertical = config.spacingS
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Create",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(config.spacingXS))
                            Text(
                                text = "Create Community",
                                fontSize = responsiveTextSize(12f, config),
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Illustration
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.GroupAdd,
                            contentDescription = "Join Community",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
    }
}