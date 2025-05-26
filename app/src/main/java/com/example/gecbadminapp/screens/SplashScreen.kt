package com.example.gecbadminapp.screens

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gecbadminapp.R
import com.example.gecbadminapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val logoScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(40f) }

    // Background shimmer-like wave effect (optional)
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing =
                LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(true) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = EaseOutBack)
        )
        delay(300)
        textAlpha.animateTo(1f, tween(600))
        textOffsetY.animateTo(0f, tween(600))
        delay(Constants.SPLASH_DURATION)

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
                    listOf(Color(0xFF667eea), Color(0xFF764ba2), Color(0xFFF093FB))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "College Logo",
                modifier = Modifier
                    .size(130.dp)
                    .scale(logoScale.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Government Engineering College\nBilaspur (C.G.)",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = shimmerAlpha),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = textOffsetY.value.dp)
            )
        }
    }
}
