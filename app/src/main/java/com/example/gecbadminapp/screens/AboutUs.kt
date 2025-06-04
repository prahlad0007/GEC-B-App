package com.example.gecbadminapp.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gecbadminapp.R

// Data classes
data class SocialLink(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color,
    val action: () -> Unit
)

data class Skill(
    val name: String,
    val level: Float,
    val color: Color
)

@Composable
fun AboutUs() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 400
    val scrollState = rememberScrollState()

    // Clean gradient colors matching your theme
    val gradientColors = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2),
        Color(0xFFF093FB)
    )

    // Simple floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    // Skills data
    val skills = listOf(
        Skill("Competitive Programming", 0.95f, Color(0xFF10B981)),
        Skill("Android Development", 0.90f, Color(0xFF3B82F6)),
        Skill("Cross Platform Dev", 0.85f, Color(0xFFF59E0B)),
        Skill("Backend Development", 0.80f, Color(0xFF8B5CF6)),

    )

    // Social links
    val socialLinks = listOf(
        SocialLink(
            Icons.Default.Phone,
            "Call Me",
            "+91 89485 72025",
            Color(0xFF10B981)
        ) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+918948572025"))
            context.startActivity(intent)
        },
        SocialLink(
            Icons.Default.Email,
            "Email Me",
            "prahlady444@gmail.com",
            Color(0xFF3B82F6)
        ) {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:prahlady444@gmail.com"))
            context.startActivity(intent)
        },
        SocialLink(
            Icons.Default.Link,
            "LinkedIn",
            "Connect professionally",
            Color(0xFF0077B5)
        ) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/in/prahlad-yadav-478040257/")
            )
            context.startActivity(intent)
        },
        SocialLink(
            Icons.Default.Code,
            "GitHub",
            "View my projects",
            Color(0xFF1F2937)
        ) {
            // Add GitHub link when available
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/Prahlad-07")
            )
            context.startActivity(intent)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = gradientColors)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isSmallScreen) 20.dp else 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Title
                Text(
                    text = "About Me",
                    fontSize = if (isSmallScreen) 28.sp else 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp),
                    modifier = Modifier.offset(y = floatingOffset.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isSmallScreen) 140.dp else 160.dp)
                            .padding(6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.prahlad),
                            contentDescription = "Prahlad Yadav",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        // Status indicator
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFF10B981), CircleShape)
                                .border(3.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Name Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
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
                            text = "Prahlad Yadav",
                            fontSize = if (isSmallScreen) 24.sp else 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Software Engineer (MIDAS)",
                            fontSize = if (isSmallScreen) 14.sp else 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = "Education",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Information Technology | 2022 – 2026",
                                fontSize = if (isSmallScreen) 12.sp else 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Main Content
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // About Section
                    SectionCard(
                        title = "About Me",
                        icon = Icons.Default.Person,
                        iconColor = Color(0xFF667eea)
                    ) {
                        Text(
                            text = "Hey! I'm an Android and backend-focused Software Engineer who loves building clean, intuitive apps with scalable backends that actually solve real problems. I'm currently studying IT at Government Engineering College Bilaspur, and when I'm not coding, you'll probably find me deep into competitive programming — ranked in the top 1% across all major platforms."
                            , fontSize = 15.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Justify
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Skills Section
                    SectionCard(
                        title = "Skills",
                        icon = Icons.Default.Stars,
                        iconColor = Color(0xFF10B981)
                    ) {
                        skills.forEach { skill ->
                            SkillItem(skill = skill, isSmallScreen = isSmallScreen)
                            if (skill != skills.last()) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))



                    // Contact Section
                    SectionCard(
                        title = "Get In Touch",
                        icon = Icons.Default.ContactMail,
                        iconColor = Color(0xFF3B82F6)
                    ) {
                        socialLinks.forEach { link ->
                            ContactItem(link = link, isSmallScreen = isSmallScreen)
                            if (link != socialLinks.last()) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    // Achievements Section
                    SectionCard(
                        title = "Achievements",
                        icon = Icons.Default.EmojiEvents,
                        iconColor = Color(0xFFF59E0B)
                    ) {
                        val achievements = listOf(
                            "🏆 Developed Complete GECB Admin App",
                            "🔥 Top 1% Competitive Programmer",
                            "📱 Mobile App Development Specialist"
                        )


                        achievements.forEach { achievement ->
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = achievement,
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }
            content()
        }
    }
}

@Composable
fun SkillItem(skill: Skill, isSmallScreen: Boolean) {
    val animatedProgress by animateFloatAsState(
        targetValue = skill.level,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "skill_progress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = skill.name,
                fontSize = if (isSmallScreen) 14.sp else 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "${(skill.level * 100).toInt()}%",
                fontSize = if (isSmallScreen) 12.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                color = skill.color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(skill.color, RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
fun ContactItem(link: SocialLink, isSmallScreen: Boolean) {
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                link.action()
                isPressed = true
            }
            .scale(if (isPressed) 0.98f else 1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    .background(link.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = link.icon,
                    contentDescription = link.title,
                    tint = link.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = link.title,
                    fontSize = if (isSmallScreen) 14.sp else 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = link.subtitle,
                    fontSize = if (isSmallScreen) 12.sp else 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Open",
                tint = link.color,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}