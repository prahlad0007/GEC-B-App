package com.example.gecbadminapp.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gecbadminapp.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val configuration = LocalConfiguration.current

    // Responsive sizing based on screen width
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isCompact = screenWidth < 600.dp
    val isSmallHeight = screenHeight < 700.dp

    // Dynamic sizing values
    val horizontalPadding = if (isCompact) 16.dp else 32.dp
    val cardPadding = if (isCompact) 20.dp else 32.dp
    val logoSize = if (isCompact) 60.dp else 80.dp
    val titleFontSize = if (isCompact) 18.sp else 22.sp
    val subtitleFontSize = if (isCompact) 14.sp else 16.sp
    val headerFontSize = if (isCompact) 22.sp else 26.sp
    val verticalSpacing = if (isSmallHeight) 8.dp else 16.dp
    val fieldSpacing = if (isSmallHeight) 12.dp else 16.dp

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var graduationYear by remember { mutableStateOf("") }
    var passoutYear by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

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
                .padding(horizontal = horizontalPadding)
                .padding(vertical = if (isSmallHeight) 16.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSmallHeight) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Responsive Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = if (isSmallHeight) 16.dp else 24.dp)
            ) {
                // College Logo
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(if (isCompact) 12.dp else 16.dp),
                    modifier = Modifier.shadow(if (isCompact) 16.dp else 24.dp, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "GECB Logo",
                        modifier = Modifier
                            .size(logoSize)
                            .padding(if (isCompact) 8.dp else 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 20.dp))

                // College Title - Responsive text sizing
                Text(
                    text = "Government Engineering College",
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = if (isCompact) 20.sp else 24.sp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                Text(
                    text = "Bilaspur (C.G.)",
                    fontSize = subtitleFontSize,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(verticalSpacing))

                // Sanskrit Slogan
                Card(
                    shape = RoundedCornerShape(if (isCompact) 16.dp else 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "योग कर्मसु कौशलम",
                        fontSize = if (isCompact) 12.sp else 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            horizontal = if (isCompact) 16.dp else 20.dp,
                            vertical = if (isCompact) 8.dp else 10.dp
                        )
                    )
                }
            }

            // Registration Card with responsive sizing
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(if (isCompact) 16.dp else 20.dp, RoundedCornerShape(if (isCompact) 24.dp else 30.dp)),
                shape = RoundedCornerShape(if (isCompact) 24.dp else 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(if (isCompact) 12.dp else 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(cardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Welcome Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = if (isSmallHeight) 12.dp else 20.dp)
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = "Register",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(if (isCompact) 24.dp else 28.dp)
                        )
                        Spacer(modifier = Modifier.width(if (isCompact) 8.dp else 12.dp))
                        Text(
                            text = "Create Account",
                            fontSize = headerFontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    Text(
                        text = "Join the GECB community today",
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = if (isSmallHeight) 16.dp else 24.dp)
                    )

                    // Name Fields Row - Responsive layout
                    if (isCompact) {
                        // Stack vertically on small screens
                        EnhancedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = "First Name",
                            icon = Icons.Default.Person,
                            iconTint = Color(0xFF667eea),
                            isCompact = isCompact,
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )

                        EnhancedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = "Last Name",
                            icon = Icons.Default.Person,
                            iconTint = Color(0xFF667eea),
                            isCompact = isCompact,
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                    } else {
                        // Side by side on larger screens
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = fieldSpacing),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EnhancedTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = "First Name",
                                icon = Icons.Default.Person,
                                iconTint = Color(0xFF667eea),
                                isCompact = isCompact,
                                modifier = Modifier.weight(1f)
                            )

                            EnhancedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = "Last Name",
                                icon = Icons.Default.Person,
                                iconTint = Color(0xFF667eea),
                                isCompact = isCompact,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Year Fields Row - Responsive layout
                    if (isCompact) {
                        // Stack vertically on small screens
                        EnhancedTextField(
                            value = graduationYear,
                            onValueChange = { graduationYear = it },
                            label = "Graduation Year",
                            icon = Icons.Default.School,
                            iconTint = Color(0xFF10B981),
                            keyboardType = KeyboardType.Number,
                            isCompact = isCompact,
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )

                        EnhancedTextField(
                            value = passoutYear,
                            onValueChange = { passoutYear = it },
                            label = "Passout Year",
                            icon = Icons.Default.CalendarToday,
                            iconTint = Color(0xFF10B981),
                            keyboardType = KeyboardType.Number,
                            isCompact = isCompact,
                            modifier = Modifier.padding(bottom = fieldSpacing)
                        )
                    } else {
                        // Side by side on larger screens
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = fieldSpacing),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EnhancedTextField(
                                value = graduationYear,
                                onValueChange = { graduationYear = it },
                                label = "Graduation Year",
                                icon = Icons.Default.School,
                                iconTint = Color(0xFF10B981),
                                keyboardType = KeyboardType.Number,
                                isCompact = isCompact,
                                modifier = Modifier.weight(1f)
                            )

                            EnhancedTextField(
                                value = passoutYear,
                                onValueChange = { passoutYear = it },
                                label = "Passout Year",
                                icon = Icons.Default.CalendarToday,
                                iconTint = Color(0xFF10B981),
                                keyboardType = KeyboardType.Number,
                                isCompact = isCompact,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Email Field
                    EnhancedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        icon = Icons.Default.Email,
                        iconTint = Color(0xFF667eea),
                        keyboardType = KeyboardType.Email,
                        isCompact = isCompact,
                        modifier = Modifier.padding(bottom = fieldSpacing)
                    )

                    // Password Field
                    EnhancedPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isVisible = passwordVisible,
                        onVisibilityToggle = { passwordVisible = !passwordVisible },
                        isCompact = isCompact,
                        modifier = Modifier.padding(bottom = fieldSpacing)
                    )

                    // Confirm Password Field
                    EnhancedPasswordField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        isVisible = confirmVisible,
                        onVisibilityToggle = { confirmVisible = !confirmVisible },
                        isCompact = isCompact,
                        modifier = Modifier.padding(bottom = if (isSmallHeight) 16.dp else 24.dp)
                    )

                    // Register Button
                    EnhancedButton(
                        onClick = {
                            if (!isLoading) {
                                when {
                                    firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
                                            graduationYear.isBlank() || passoutYear.isBlank() || password.length < 6 -> {
                                        Toast.makeText(context, "Please fill all fields properly", Toast.LENGTH_SHORT).show()
                                    }

                                    password != confirmPassword -> {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    }

                                    else -> {
                                        isLoading = true
                                        auth.createUserWithEmailAndPassword(email.trim(), password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(context, "Registration successful! Welcome to GECB", Toast.LENGTH_SHORT).show()
                                                    navController.navigate(Routes.Login.route) {
                                                        popUpTo(Routes.Register.route) { inclusive = true }
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        task.exception?.message ?: "Registration failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                isLoading = false
                                            }
                                    }
                                }
                            }
                        },
                        isLoading = isLoading,
                        text = "Create Account",
                        icon = Icons.Default.PersonAdd,
                        isCompact = isCompact
                    )

                    Spacer(modifier = Modifier.height(if (isSmallHeight) 16.dp else 24.dp))

                    // Divider
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = if (isSmallHeight) 8.dp else 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFFE5E7EB))
                        )
                        Text(
                            text = "or",
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontSize = if (isCompact) 12.sp else 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFFE5E7EB))
                        )
                    }

                    // Login Section
                    Card(
                        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8FAFC)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Routes.Login.route)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(if (isCompact) 12.dp else 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Login,
                                contentDescription = "Login",
                                tint = Color(0xFF667eea),
                                modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Already have an account? ",
                                color = Color(0xFF6B7280),
                                fontSize = if (isCompact) 12.sp else 14.sp
                            )
                            Text(
                                "Sign In",
                                color = Color(0xFF667eea),
                                fontSize = if (isCompact) 12.sp else 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (!isSmallHeight) {
                Spacer(modifier = Modifier.height(24.dp))

                // Footer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Secure Registration Portal",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = if (isCompact) 10.sp else 12.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = "Secure",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(if (isCompact) 10.dp else 12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Protected by Firebase Authentication",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = if (isCompact) 8.sp else 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isCompact: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    label,
                    color = Color(0xFF6B7280),
                    fontSize = if (isCompact) 12.sp else 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = iconTint,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color(0xFF1F2937),
                unfocusedTextColor = Color(0xFF1F2937)
            ),
            shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = if (isCompact) 14.sp else 16.sp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    label,
                    color = Color(0xFF6B7280),
                    fontSize = if (isCompact) 12.sp else 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = label,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                )
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Toggle Password",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFEF4444),
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color(0xFF1F2937),
                unfocusedTextColor = Color(0xFF1F2937)
            ),
            shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = if (isCompact) 14.sp else 16.sp)
        )
    }
}

@Composable
fun EnhancedButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isCompact: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCompact) 48.dp else 56.dp)
            .clickable(enabled = !isLoading) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF10B981),
                            Color(0xFF059669)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(if (isCompact) 20.dp else 24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        icon,
                        contentDescription = text,
                        tint = Color.White,
                        modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text,
                        color = Color.White,
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}