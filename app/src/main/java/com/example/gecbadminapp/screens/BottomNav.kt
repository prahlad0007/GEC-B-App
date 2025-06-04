package com.example.gecbadminapp.screens
import Faculty
import android.content.Intent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gecbadminapp.R
import com.example.gecbadminapp.model.BottomNavItem
import com.example.gecbadminapp.model.NavItem
import com.example.gecbadminapp.screens.GECB_Communities.CommunityScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.TextUnit
import androidx.core.net.toUri

@Composable
fun ResponsiveTopAppBar(
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val windowInsets = WindowInsets.systemBars
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val topInset = with(density) { windowInsets.getTop(density).toDp() }
    val appBarHeight = when {
        isLandscape && isCompact -> 60.dp // Landscape on phones
        isCompact -> 70.dp // Portrait on phones
        else -> 80.dp // Tablets
    }
    val iconSize = when {
        isLandscape && isCompact -> 22.dp
        isCompact -> 24.dp
        else -> 28.dp
    }

    val iconButtonSize = when {
        isLandscape && isCompact -> 42.dp
        isCompact -> 46.dp
        else -> 52.dp
    }

    val titleSize = when {
        isLandscape && isCompact -> 16.sp
        isCompact -> 18.sp
        else -> 22.sp
    }

    val horizontalPadding = when {
        isLandscape && isCompact -> 12.dp
        isCompact -> 16.dp
        else -> 20.dp
    }

    // Animation for app bar
    val animatedElevation by animateFloatAsState(
        targetValue = 8f,
        animationSpec = tween(300),
        label = "elevation_animation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(appBarHeight + topInset)
            .statusBarsPadding()
            .graphicsLayer {
                shadowElevation = animatedElevation
            },
        color = Color.Transparent
    ) {
        Column {
            // Status bar space (handled by statusBarsPadding)

            // Main app bar content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(appBarHeight)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2)
                            )
                        )
                    )
                    .animateContentSize()
            ) {
                // Glass morphism overlay for modern look
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.White.copy(alpha = 0.1f)
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left side - Menu button
                    ResponsiveIconButton(
                        onClick = onMenuClick,
                        iconRes = R.drawable.menu,
                        contentDescription = "Menu",
                        size = iconButtonSize,
                        iconSize = iconSize,
                        isCompact = isCompact
                    )

                    // Center - Title with responsive layout
                    ResponsiveTitleSection(
                        title = "GEC Bilaspur",
                        subtitle = if (isLandscape && isCompact) null else "Student Portal",
                        titleSize = titleSize,
                        isCompact = isCompact,
                        isLandscape = isLandscape,
                        modifier = Modifier.weight(1f)
                    )

                    // Right side - Settings button
                    ResponsiveIconButton(
                        onClick = onSettingsClick,
                        iconRes = R.drawable.settings,
                        contentDescription = "Settings",
                        size = iconButtonSize,
                        iconSize = iconSize,
                        isCompact = isCompact
                    )
                }

                // Bottom border with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun ResponsiveIconButton(
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String,
    size: Dp,
    iconSize: Dp,
    isCompact: Boolean
) {
    // Animation states
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )

    Card(
        modifier = Modifier
            .size(size)
            .scale(animatedScale)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(
            if (isCompact) 4.dp else 6.dp
        ),
        border = BorderStroke(
            0.5.dp,
            Color.White.copy(alpha = 0.2f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = Color.White
            )
        }
    }
}

@Composable
fun ResponsiveTitleSection(
    title: String,
    subtitle: String?,
    titleSize: TextUnit,
    isCompact: Boolean,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main title with responsive styling
        Text(
            text = title,
            color = Color.White,
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge.copy(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                    blurRadius = 3f
                )
            )
        )

        // Subtitle (hidden in landscape compact mode)
        subtitle?.let { sub ->
            if (!isLandscape || !isCompact) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sub,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = when {
                        isCompact -> 12.sp
                        else -> 14.sp
                    },
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.2f),
                            offset = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                            blurRadius = 2f
                        )
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNav(navController: NavHostController) {
    val navController1 = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val selectedItemIndex = rememberSaveable { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }

    val items = listOf(
        NavItem("Website", R.drawable.website),
        NavItem("Notice", R.drawable.notice),
        NavItem("Timetable", R.drawable.timetable),
        NavItem("Contact", R.drawable.contact)
    )

    MaterialTheme(colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                // Enhanced Drawer Content with proper background and styling
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(320.dp),
                    drawerContainerColor = Color.White,
                    drawerContentColor = Color(0xFF1F2937)
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        // Enhanced Header Section with gradient background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF667eea),
                                            Color(0xFF764ba2)
                                        )
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // College Logo
                                Card(
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = "College Logo",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "GECB Portal",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "User Dashboard",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Main Menu Items Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Menu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )

                            items.forEachIndexed { index, item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (index == selectedItemIndex.value)
                                            Color(0xFF667eea).copy(alpha = 0.1f)
                                        else Color.Transparent
                                    ),
                                    elevation = CardDefaults.cardElevation(0.dp)
                                ) {
                                    NavigationDrawerItem(
                                        label = {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (index == selectedItemIndex.value) FontWeight.SemiBold else FontWeight.Normal,
                                                color = if (index == selectedItemIndex.value) Color(0xFF667eea) else Color(0xFF374151),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        selected = index == selectedItemIndex.value,
                                        onClick = {
                                            selectedItemIndex.value = index
                                            scope.launch { drawerState.close() }

                                            when (item.title) {
                                                "Website" -> {
                                                    val intent = Intent(Intent.ACTION_VIEW,
                                                        "https://gecbsp.ac.in/".toUri())
                                                    context.startActivity(intent)
                                                }
                                                "Notice" -> {
                                                    navController1.navigate(Routes.Notice.route) {
                                                        popUpTo(navController1.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                                "Timetable" -> {
                                                    navController1.navigate(Routes.Timetable.route) {
                                                        popUpTo(navController1.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                                "Contact" -> {
                                                    navController1.navigate(Routes.Contact.route) {
                                                        popUpTo(navController1.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            }
                                        },
                                        icon = {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(
                                                        if (index == selectedItemIndex.value)
                                                            Color(0xFF667eea).copy(alpha = 0.2f)
                                                        else Color(0xFFF3F4F6),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = item.icon),
                                                    contentDescription = item.title,
                                                    modifier = Modifier.size(20.dp),
                                                    tint = if (index == selectedItemIndex.value) Color(0xFF667eea) else Color(0xFF6B7280)
                                                )
                                            }
                                        },
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = Color.Transparent,
                                            unselectedContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = Color(0xFFE5E7EB)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Settings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sign Out Button
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                NavigationDrawerItem(
                                    label = {
                                        Text(
                                            "Sign Out",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFDC2626),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    selected = false,
                                    onClick = {
                                        auth.signOut()
                                        navController.navigate(Routes.Login.route) {
                                            popUpTo(0)
                                        }
                                    },
                                    icon = {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFFDC2626).copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.logout),
                                                contentDescription = "Sign Out",
                                                modifier = Modifier.size(20.dp),
                                                tint = Color(0xFFDC2626)
                                            )
                                        }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = Color.Transparent,
                                        unselectedContainerColor = Color.Transparent
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "GEC Bilaspur v1.0",
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            content = {
                Scaffold(
                    topBar = {
                        // Use the new responsive top app bar
                        ResponsiveTopAppBar(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onSettingsClick = {
                                navController1.navigate(Routes.Settings.route) {
                                    popUpTo(navController1.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    },
                    bottomBar = {
                        MyBottomNav(navController = navController1)
                    },
                    containerColor = Color.White
                ) { padding ->
                    NavHost(
                        navController = navController1,
                        startDestination = Routes.Home.route,
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        composable(Routes.Home.route) { Home() }
                        composable(Routes.Faculty.route) { Faculty() }
                        composable(Routes.Community.route) { CommunityScreen() }
                        composable(Routes.AboutUs.route) { AboutUs() }
                        composable(Routes.Settings.route) {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(Routes.Notice.route) { NoticeScreen() }
                        composable(Routes.Timetable.route) { TimetableScreen() }
                        composable(Routes.Contact.route) { ContactScreen() }
                    }
                }
            }
        )
    }
}

@Composable
fun MyBottomNav(navController: NavHostController) {
    val navStackEntry = navController.currentBackStackEntryAsState()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val windowInsets = WindowInsets.systemBars

    // Calculate responsive dimensions
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Get bottom inset for navigation bar
    val bottomInset = with(density) { windowInsets.getBottom(density).toDp() }

    // Responsive sizing
    val navHeight = when {
        isLandscape && isCompact -> 70.dp // Landscape on phones
        isCompact -> 85.dp // Portrait on phones
        else -> 95.dp // Tablets
    }

    val cardPadding = when {
        isLandscape && isCompact -> 6.dp
        isCompact -> 8.dp
        else -> 12.dp
    }

    val iconSize = when {
        isLandscape && isCompact -> 22.dp
        isCompact -> 24.dp
        else -> 26.dp
    }

    val containerSize = when {
        isLandscape && isCompact -> 42.dp
        isCompact -> 46.dp
        else -> 50.dp
    }

    val items = listOf(
        BottomNavItem("Home", R.drawable.home, Routes.Home.route),
        BottomNavItem("Faculty", R.drawable.faculty, Routes.Faculty.route),
        BottomNavItem("Community", R.drawable.group, Routes.Community.route),
        BottomNavItem("Developer", R.drawable.coding, Routes.AboutUs.route)
    )

    // Adaptive surface with proper insets
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(navHeight + bottomInset)
            .navigationBarsPadding(), // Handle system navigation bar
        shadowElevation = if (isCompact) 12.dp else 16.dp,
        color = Color.Transparent
    ) {
        Column {
            // Main navigation content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.98f),
                                Color(0xFFF8FAFC).copy(alpha = 0.95f)
                            )
                        )
                    )
                    .padding(horizontal = cardPadding, vertical = cardPadding)
            ) {
                // Responsive navigation card
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(
                        when {
                            isLandscape && isCompact -> 20.dp
                            isCompact -> 24.dp
                            else -> 28.dp
                        }
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        if (isCompact) 8.dp else 12.dp
                    ),
                    border = BorderStroke(
                        0.5.dp,
                        Color(0xFFE2E8F0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = if (isCompact) 4.dp else 8.dp,
                                vertical = if (isLandscape && isCompact) 6.dp else 8.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items.forEach { item ->
                            val selected = item.route == navStackEntry.value?.destination?.route

                            ResponsiveNavItem(
                                item = item,
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                iconSize = iconSize,
                                containerSize = containerSize,
                                isCompact = isCompact,
                                isLandscape = isLandscape
                            )
                        }
                    }
                }
            }

            // Bottom safe area (for devices with navigation bar)
            if (bottomInset > 0.dp) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomInset)
                        .background(Color.White)
                )
            }
        }
    }
}

@Composable
fun ResponsiveNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    iconSize: Dp,
    containerSize: Dp,
    isCompact: Boolean,
    isLandscape: Boolean
) {
    // Responsive animations
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale_animation"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (selected) {
            if (isCompact) 6.dp else 8.dp
        } else {
            if (isCompact) 2.dp else 3.dp
        },
        animationSpec = tween(200),
        label = "elevation_animation"
    )

    // Container width calculation for proper spacing
    val itemWidth = when {
        isLandscape && isCompact -> 65.dp
        isCompact -> 70.dp
        else -> 80.dp
    }

    Column(
        modifier = Modifier
            .width(itemWidth)
            .fillMaxHeight()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .scale(animatedScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Adaptive icon container
        Card(
            modifier = Modifier.size(
                if (selected) containerSize + 2.dp else containerSize
            ),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (selected) {
                    Color(0xFF667eea)
                } else {
                    Color(0xFFF1F5F9)
                }
            ),
            elevation = CardDefaults.cardElevation(animatedElevation)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.title,
                    modifier = Modifier.size(
                        if (selected) iconSize + 2.dp else iconSize
                    ),
                    tint = if (selected) {
                        Color.White
                    } else {
                        Color(0xFF475569)
                    }
                )
            }
        }

        // Adaptive selection indicator
        if (selected) {
            Spacer(modifier = Modifier.height(
                if (isLandscape && isCompact) 3.dp else 4.dp
            ))
            Box(
                modifier = Modifier
                    .size(
                        if (isLandscape && isCompact) 4.dp else 5.dp
                    )
                    .background(
                        Color(0xFF667eea),
                        CircleShape
                    )
            )
        }
    }
}

