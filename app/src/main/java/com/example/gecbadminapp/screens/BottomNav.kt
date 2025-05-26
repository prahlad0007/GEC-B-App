package com.example.gecbadminapp.screens

import Faculty
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.gecbadminapp.ui.theme.GradientEnd
import com.example.gecbadminapp.ui.theme.GradientStart
import com.example.gecbadminapp.ui.theme.TransparentCard
import com.example.gecbadminapp.ui.theme.WhiteText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

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
        NavItem("Notes", R.drawable.notes),
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
                        .width(320.dp), // Increased width slightly
                    drawerContainerColor = Color.White,
                    drawerContentColor = Color(0xFF1F2937)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
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
                                            Toast.makeText(context, "${item.title} clicked", Toast.LENGTH_SHORT).show()
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

                            Spacer(modifier = Modifier.height(24.dp))

                            // Divider
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = Color(0xFFE5E7EB)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Settings Section
                            Text(
                                text = "Settings",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )

                            // Theme Toggle
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.theme),
                                            contentDescription = "Theme Toggle",
                                            modifier = Modifier.size(20.dp),
                                            tint = Color(0xFFF59E0B)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            "Dark Theme",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF374151),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            "Switch app appearance",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF6B7280),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Switch(
                                        checked = isDarkTheme,
                                        onCheckedChange = { isDarkTheme = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color(0xFF667eea),
                                            checkedTrackColor = Color(0xFF667eea).copy(alpha = 0.5f)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

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

                            // Footer
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
                        Surface(
                            shadowElevation = 4.dp,
                            color = TransparentCard
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF667eea),
                                                Color(0xFF764ba2)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.menu),
                                            contentDescription = "Menu",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "GEC Bilaspur",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Added Settings Icon to the right side
                                    IconButton(
                                        onClick = {
                                            navController1.navigate(Routes.Settings.route) {
                                                popUpTo(navController1.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.settings),
                                            contentDescription = "Settings",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
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
                        composable(Routes.Gallery.route) { Gallery() }
                        composable(Routes.AboutUs.route) { AboutUs() }
                        composable(Routes.Settings.route) {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun MyBottomNav(navController: NavHostController) {
    val navStackEntry = navController.currentBackStackEntryAsState()
    val items = listOf(
        BottomNavItem("Home", R.drawable.home, Routes.Home.route),
        BottomNavItem("Faculty", R.drawable.graduated, Routes.Faculty.route),
        BottomNavItem("Gallery", R.drawable.gallery, Routes.Gallery.route),
        BottomNavItem("About", R.drawable.info, Routes.AboutUs.route)
        // Removed Settings from bottom nav since it's now in top bar
    )

    // Enhanced bottom navigation with increased height
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp), // Increased height from 90dp to 110dp
        shadowElevation = 16.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.98f),
                            Color(0xFFF8FAFC).copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 12.dp) // Increased padding
        ) {
            // Enhanced background card with better contrast
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(32.dp), // Increased corner radius
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFE2E8F0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 12.dp), // Increased vertical padding
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { item ->
                        val selected = item.route == navStackEntry.value?.destination?.route

                        // Enhanced nav item with better visibility
                        EnhancedNavItem(
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
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Animation values
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (selected) 8.dp else 2.dp,
        animationSpec = tween(300)
    )

    Column(
        modifier = Modifier
            .width(75.dp) // Increased width for better spacing with 4 items
            .height(80.dp) // Increased height
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .scale(animatedScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon container with enhanced visibility
        Card(
            modifier = Modifier
                .size(if (selected) 48.dp else 44.dp), // Increased size
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
                    modifier = Modifier.size(if (selected) 26.dp else 24.dp), // Increased icon size
                    tint = if (selected) {
                        Color.White
                    } else {
                        Color(0xFF475569)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp)) // Increased spacing

        // Enhanced text label with proper sizing
        Text(
            text = item.title,
            fontSize = if (selected) 12.sp else 11.sp, // Increased text size
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) {
                Color(0xFF667eea)
            } else {
                Color(0xFF64748B)
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // Selection indicator
        if (selected) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .width(8.dp) // Increased indicator width
                    .height(3.dp) // Increased indicator height
                    .background(
                        Color(0xFF667eea),
                        RoundedCornerShape(1.5.dp)
                    )
            )
        }
    }
}