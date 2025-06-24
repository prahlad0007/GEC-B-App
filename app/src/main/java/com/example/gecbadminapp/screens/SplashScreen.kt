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
import androidx.compose.ui.draw.blur
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
import com.example.gecbadminapp.R
import com.example.gecbadminapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.math.cos

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    // Enhanced logo animations
    val logoScale = remember { Animatable(0f) }
    val logoRotation = remember { Animatable(-360f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoBounce = remember { Animatable(0f) }
    val logoGlow = remember { Animatable(0f) }

    // Enhanced text animations
    val titleAlpha = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0.5f) }
    val titleOffsetY = remember { Animatable(100f) }
    val titleRotation = remember { Animatable(-10f) }

    val subtitleAlpha = remember { Animatable(0f) }
    val subtitleOffsetY = remember { Animatable(50f) }
    val subtitleScale = remember { Animatable(0.8f) }

    // Enhanced background animations
    val infiniteTransition = rememberInfiniteTransition()

    // Dynamic gradient animation
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Multi-layer glow effects
    val primaryGlow by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    val secondaryGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )



    // Floating animation with sine wave
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f, // 2π
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val floatOffset = sin(time) * 8f

    // Multiple rotating borders
    val outerBorderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )




    val innerBorderRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Enhanced text effects
    val textShimmer by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Particle-like background elements
    val particleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(true) {
        // Enhanced sequential animation timeline

        // Phase 1: Logo dramatic entrance (0-1500ms)
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = EaseOutExpo)
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(1000, easing = EaseOutBack)
            )
            delay(200)
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(400, easing = EaseInOutBounce)
            )
        }
        launch {
            logoRotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(1200, easing = EaseOutElastic)
            )
        }
        launch {
            delay(400)
            logoGlow.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = EaseOut)
            )
        }

        delay(1000)

        // Phase 2: Enhanced bounce sequence (1000-1600ms)
        launch {
            repeat(3) { i ->
                val bounceHeight = if (i == 1) -15f else -8f
                logoBounce.animateTo(
                    targetValue = bounceHeight,
                    animationSpec = tween(120, easing = EaseOutQuart)
                )
                logoBounce.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(120, easing = EaseInQuart)
                )
                delay(80)
            }
        }

        delay(600)

        // Phase 3: Title entrance with rotation (1600-2400ms)
        launch {
            titleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = EaseOutExpo)
            )
        }
        launch {
            titleScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = EaseOutBack)
            )
        }
        launch {
            titleOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(800, easing = EaseOutQuart)
            )
        }
        launch {
            titleRotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(800, easing = EaseOutElastic)
            )
        }

        delay(400)

        // Phase 4: Subtitle elegant entrance (2000-2800ms)
        launch {
            subtitleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = EaseOutExpo)
            )
        }
        launch {
            subtitleOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(800, easing = EaseOutQuart)
            )
        }
        launch {
            subtitleScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = EaseOutBack)
            )
        }

        delay(Constants.SPLASH_DURATION - 2800)

        // Navigation logic
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        Constants.isAdmin = sharedPref.getBoolean("isAdmin", false)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            navController.navigate(if (Constants.isAdmin) Routes.AdminDashBoard.route else Routes.BottomNav.route) {
                popUpTo(Routes.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.Login.route) {
                popUpTo(Routes.Splash.route) { inclusive = true }
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
        // Animated background particles
        repeat(8) { index ->
            Box(
                modifier = Modifier
                    .size((80 + index * 40).dp)
                    .offset(
                        x = (cos(particleRotation * 0.017f + index) * (50 + index * 20)).dp,
                        y = (sin(particleRotation * 0.017f + index) * (30 + index * 15)).dp
                    )
                    .graphicsLayer {
                        alpha = 0.03f + primaryGlow * 0.02f
                        rotationZ = particleRotation * (index % 2 * 2 - 1)
                    }
                    .background(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = if (index % 2 == 0) CircleShape else RoundedCornerShape(12.dp)
                    )
            )
        }

        // Enhanced decorative circles with multiple layers
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size((150 + index * 80).dp)
                    .graphicsLayer {
                        alpha = (0.08f + primaryGlow * 0.03f) / (index + 1)
                        rotationZ = outerBorderRotation * (index + 1) * 0.2f
                    }
                    .border(
                        width = (2 - index * 0.3f).dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF7209b7).copy(alpha = 0.3f),
                                Color.Transparent,
                                Color(0xFF533483).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = floatOffset.dp)
        ) {
            // Enhanced logo section with multiple effects
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer {
                        alpha = logoAlpha.value
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        rotationZ = logoRotation.value
                        translationY = logoBounce.value
                    }
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .rotate(outerBorderRotation)
                        .blur(8.dp)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF7209b7).copy(alpha = primaryGlow * 0.6f),
                                    Color(0xFFa663cc).copy(alpha = primaryGlow * 0.8f),
                                    Color(0xFF533483).copy(alpha = primaryGlow * 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Primary animated border
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .rotate(outerBorderRotation)
                        .border(
                            width = 4.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFFD700).copy(alpha = primaryGlow),
                                    Color.White.copy(alpha = primaryGlow * 0.9f),
                                    Color(0xFF7209b7).copy(alpha = primaryGlow * 0.7f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Secondary counter-rotating border
                Box(
                    modifier = Modifier
                        .size(165.dp)
                        .rotate(innerBorderRotation)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF533483).copy(alpha = secondaryGlow * 0.8f),
                                    Color.Transparent,
                                    Color(0xFFa663cc).copy(alpha = secondaryGlow * 0.6f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Enhanced shadow effect
                Box(
                    modifier = Modifier
                        .size(155.dp)
                        .shadow(
                            elevation = 25.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF7209b7).copy(alpha = 0.4f),
                            spotColor = Color.White.copy(alpha = 0.3f)
                        )
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Logo image with enhanced styling
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "College Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.9f),
                                    Color(0xFFFFD700).copy(alpha = 0.7f),
                                    Color.White.copy(alpha = 0.9f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .graphicsLayer {
                            shadowElevation = 15f
                        }
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Enhanced main title with shimmer effect
            Text(
                text = "Government Engineering College",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = titleAlpha.value
                        scaleX = titleScale.value
                        scaleY = titleScale.value
                        translationY = titleOffsetY.value
                        rotationZ = titleRotation.value
                        shadowElevation = 12f
                    }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.1f * primaryGlow),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(
                                textShimmer * 1000f - 500f, 0f
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                textShimmer * 1000f, 0f
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Enhanced subtitle with gradient text effect
            Text(
                text = "Bilaspur (C.G.)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = subtitleAlpha.value
                        translationY = subtitleOffsetY.value
                        scaleX = subtitleScale.value
                        scaleY = subtitleScale.value
                    }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF7209b7).copy(alpha = 0.1f),
                                Color(0xFFa663cc).copy(alpha = 0.05f),
                                Color(0xFF7209b7).copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Enhanced loading indicator with wave effect
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha.value
                }
            ) {
                repeat(4) { index ->
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, delayMillis = index * 150),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, delayMillis = index * 150),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(dotScale)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = dotAlpha),
                                        Color(0xFFFFD700).copy(alpha = dotAlpha * 0.8f),
                                        Color(0xFF7209b7).copy(alpha = dotAlpha * 0.6f)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                ambientColor = Color.White.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }

        // Enhanced corner accents
        repeat(4) { corner ->
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(
                        x = if (corner % 2 == 0) (-200).dp else 200.dp,
                        y = if (corner < 2) (-300).dp else 300.dp
                    )
                    .rotate(particleRotation * 0.5f)
                    .graphicsLayer {
                        alpha = 0.1f + primaryGlow * 0.05f
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF7209b7).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}