package com.example.gecbadminapp.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri

// Data classes
data class ContactInfo(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val subtitle: String = "",
    val color: Color
)

data class DepartmentContact(
    val name: String,
    val hod: String,
    val phone: String,
    val email: String,
    val color: Color
)

@Composable
fun ContactScreen() {
    val context = LocalContext.current

    // Main contact information
    val mainContacts = listOf(
        ContactInfo(
            icon = Icons.Default.Phone,
            title = "Main Office",
            value = "+91 7752-260066",
            subtitle = "Mon-Fri: 9:00 AM - 5:00 PM",
            color = Color(0xFF10B981)
        ),
        ContactInfo(
            icon = Icons.Default.Email,
            title = "Official Email",
            value = "info@gecbilaspur.ac.in",
            subtitle = "Primary communication",
            color = Color(0xFF3B82F6)
        ),
        ContactInfo(
            icon = Icons.Default.LocationOn,
            title = "College Address",
            value = "Seepat Road, Bilaspur",
            subtitle = "Chhattisgarh - 495009",
            color = Color(0xFFF59E0B)
        ),
        ContactInfo(
            icon = Icons.Default.Language,
            title = "Official Website",
            value = "www.gecbilaspur.ac.in",
            subtitle = "Latest updates & notices",
            color = Color(0xFF8B5CF6)
        )
    )

    // Department contacts
    val departments = listOf(
        DepartmentContact(
            name = "Computer Science",
            hod = "Dr. Rajesh Kumar",
            phone = "+91 7752-260067",
            email = "cse@gecbilaspur.ac.in",
            color = Color(0xFF10B981)
        ),
        DepartmentContact(
            name = "Mechanical",
            hod = "Dr. Priya Sharma",
            phone = "+91 7752-260068",
            email = "mech@gecbilaspur.ac.in",
            color = Color(0xFF3B82F6)
        ),
        DepartmentContact(
            name = "Electrical",
            hod = "Dr. Amit Verma",
            phone = "+91 7752-260069",
            email = "ee@gecbilaspur.ac.in",
            color = Color(0xFFF59E0B)
        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Get in Touch",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Contact Us",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // College Logo
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "College Logo",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier
                                .size(60.dp)
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Header Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ContactMail,
                            contentDescription = "Contact",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "We're Here to Help",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Reach out to us for any queries or assistance",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Contact Information Section
                    ContactSectionCard(
                        title = "Contact Information",
                        icon = Icons.Default.ContactPhone,
                        iconColor = Color(0xFF10B981)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            mainContacts.forEach { contact ->
                                ContactInfoCard(contact = contact, context = context)
                            }
                        }
                    }

                    // Quick Actions Section
                    ContactSectionCard(
                        title = "Quick Actions",
                        icon = Icons.Default.Speed,
                        iconColor = Color(0xFF3B82F6)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionButton(
                                icon = Icons.Default.Call,
                                text = "Call Now",
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:+917752260066")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Handle error silently
                                    }
                                }
                            )
                            QuickActionButton(
                                icon = Icons.Default.Email,
                                text = "Send Email",
                                color = Color(0xFF3B82F6),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:info@gecbilaspur.ac.in")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Handle error silently
                                    }
                                }
                            )
                        }
                    }

                    // Office Hours Section
                    ContactSectionCard(
                        title = "Office Hours",
                        icon = Icons.Default.Schedule,
                        iconColor = Color(0xFFF59E0B)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OfficeHourRow("Monday - Friday", "9:00 AM - 5:00 PM", true)
                            OfficeHourRow("Saturday", "9:00 AM - 1:00 PM", true)
                            OfficeHourRow("Sunday", "Closed", false)
                        }
                    }

                    // Department Contacts Section
                    ContactSectionCard(
                        title = "Department Contacts",
                        icon = Icons.Default.School,
                        iconColor = Color(0xFF8B5CF6)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            departments.forEach { dept ->
                                DepartmentContactCard(department = dept, context = context)
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
fun ContactSectionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
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
fun ContactInfoCard(
    contact: ContactInfo,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                try {
                    when (contact.title) {
                        "Main Office" -> {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${contact.value}")
                            }
                            context.startActivity(intent)
                        }
                        "Official Email" -> {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${contact.value}")
                            }
                            context.startActivity(intent)
                        }
                        "College Address" -> {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("geo:0,0?q=${contact.value}")
                            }
                            context.startActivity(intent)
                        }
                        "Official Website" -> {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://${contact.value}")
                            }
                            context.startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    // Handle error silently
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, contact.color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(contact.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = contact.icon,
                    contentDescription = contact.title,
                    tint = contact.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = contact.value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                if (contact.subtitle.isNotEmpty()) {
                    Text(
                        text = contact.subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OfficeHourRow(day: String, time: String, isOpen: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (isOpen) Color(0xFF10B981) else Color(0xFFEF4444),
                        CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = time,
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DepartmentContactCard(
    department: DepartmentContact,
    context: android.content.Context
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, department.color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(department.color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = department.name,
                        tint = department.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = department.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "HOD: ${department.hod}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${department.phone}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Handle error silently
                            }
                        }
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = department.color,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = department.phone,
                        fontSize = 10.sp,
                        color = Color(0xFF1F2937)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${department.email}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Handle error silently
                            }
                        }
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = department.color,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = department.email,
                        fontSize = 8.sp,
                        color = Color(0xFF1F2937)
                    )
                }
            }
        }
    }
}