package com.example.gecbadminapp.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gecbadminapp.R
import com.example.gecbadminapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sin

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    // Reduced core animations - only essential ones
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    // Single infinite transition for performance
    val infiniteTransition = rememberInfiniteTransition()

    // Simplified rotation animation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    // Simplified glow effect
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "glow"
    )

    // Background navigation logic moved to background thread
    LaunchedEffect(true) {
        // Simple sequential animations
        launch {
            logoAlpha.animateTo(1f, tween(800, easing = EaseOut))
        }

        launch {
            logoScale.animateTo(1.1f, tween(600, easing = EaseOutBack))
            logoScale.animateTo(1f, tween(300, easing = EaseInOut))
        }

        delay(800)
        textAlpha.animateTo(1f, tween(600, easing = EaseOut))

        // Move heavy operations to background
        withContext(Dispatchers.IO) {
            delay(Constants.SPLASH_DURATION - 1400)

            // Background thread operations
            val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            Constants.isAdmin = sharedPref.getBoolean("isAdmin", false)
            val currentUser = FirebaseAuth.getInstance().currentUser

            withContext(Dispatchers.Main) {
                // Navigate on main thread
                if (currentUser != null) {
                    navController.navigate(
                        if (Constants.isAdmin) Routes.AdminDashBoard.route
                        else Routes.BottomNav.route
                    ) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
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
            ),
        contentAlignment = Alignment.Center
    ) {
        // Reduced background elements - only 3 instead of 13
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size((120 + index * 60).dp)
                    .graphicsLayer {
                        alpha = 0.05f
                        rotationZ = rotation * 0.5f * (index + 1)
                    }
                    .background(
                        color = Color.White.copy(alpha = 0.03f),
                        shape = CircleShape
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = (sin(rotation * 0.017f) * 4f).dp) // Simplified float
        ) {
            // Simplified logo section
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                    }
            ) {
                // Single animated border instead of multiple
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .rotate(rotation * 0.5f)
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFFD700).copy(alpha = glow),
                                    Color.White.copy(alpha = glow * 0.8f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Optimized image loading with Coil
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(R.drawable.logo)
                        .crossfade(true)
                        .build(),
                    contentDescription = "College Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                        .shadow(8.dp, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Simplified text animations
            Text(
                text = "Government Engineering College",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer { alpha = textAlpha.value }
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bilaspur (C.G.)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer { alpha = textAlpha.value }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Simplified loading indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.graphicsLayer { alpha = textAlpha.value }
            ) {
                repeat(3) { index ->
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ), label = "dot$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(dotScale)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

// Additional Performance Optimizations

@Composable
fun PerformantImageLoader(
    imageRes: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageRes)
            .memoryCacheKey("logo_cache_key")
            .diskCacheKey("logo_disk_key")
            .crossfade(200)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
    )
}

// Background Processing Helper
class SplashViewModel {
    suspend fun performBackgroundSetup(context: Context): NavigationDestination {
        return withContext(Dispatchers.IO) {
            // Heavy operations on background thread
            val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            Constants.isAdmin = sharedPref.getBoolean("isAdmin", false)

            // Any other heavy initialization
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                if (Constants.isAdmin) NavigationDestination.AdminDashboard
                else NavigationDestination.BottomNav
            } else {
                NavigationDestination.Login
            }
        }
    }
}

enum class NavigationDestination {
    AdminDashboard,
    BottomNav,
    Login
}