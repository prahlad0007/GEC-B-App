package com.example.gecbadminapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
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
import com.example.gecbadminapp.utils.Constants.isAdmin
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val configuration = LocalConfiguration.current

    // Responsive sizing based on screen dimensions
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isCompact = screenWidth < 600.dp
    val isSmallHeight = screenHeight < 700.dp

    // Dynamic sizing values
    val horizontalPadding = if (isCompact) 16.dp else 32.dp
    val cardPadding = if (isCompact) 20.dp else 32.dp
    val logoSize = if (isCompact) 70.dp else 100.dp
    val titleFontSize = if (isCompact) 18.sp else 24.sp
    val subtitleFontSize = if (isCompact) 14.sp else 18.sp
    val headerFontSize = if (isCompact) 20.sp else 26.sp
    val verticalSpacing = if (isSmallHeight) 8.dp else 16.dp
    val fieldSpacing = if (isSmallHeight) 12.dp else 20.dp

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Enhanced animations
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_animation"
    )

    val scrollState = rememberScrollState()

    if (showResetDialog) {
        EnhancedForgotPasswordDialog(
            onDismiss = { showResetDialog = false },
            onSendClick = { enteredEmail ->
                if (enteredEmail.isNotBlank()) {
                    auth.sendPasswordResetEmail(enteredEmail.trim())
                        .addOnSuccessListener {
                            Toast.makeText(context, "Reset link sent to $enteredEmail", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    showResetDialog = false
                } else {
                    Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            },
            isCompact = isCompact
        )
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
                modifier = Modifier.padding(bottom = if (isSmallHeight) 24.dp else 40.dp)
            ) {
                // Enhanced College Logo with responsive sizing
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
                            .padding(if (isCompact) 10.dp else 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))

                // Enhanced College Title with responsive text
                Text(
                    text = "Government Engineering College",
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = if (isCompact) 20.sp else 28.sp,
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

                // Enhanced Sanskrit Slogan
                Card(
                    shape = RoundedCornerShape(if (isCompact) 16.dp else 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "योग कर्मसु कौशलम",
                        fontSize = if (isCompact) 12.sp else 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            horizontal = if (isCompact) 16.dp else 24.dp,
                            vertical = if (isCompact) 8.dp else 12.dp
                        )
                    )
                }
            }

            // Enhanced Login Card with responsive sizing
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
                        modifier = Modifier.padding(bottom = if (isSmallHeight) 16.dp else 24.dp)
                    ) {
                        Icon(
                            Icons.Default.Login,
                            contentDescription = "Login",
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(if (isCompact) 24.dp else 28.dp)
                        )
                        Spacer(modifier = Modifier.width(if (isCompact) 8.dp else 12.dp))
                        Text(
                            text = "Welcome Back",
                            fontSize = headerFontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    }

                    Text(
                        text = "Sign in to access your GECB portal",
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = if (isSmallHeight) 20.dp else 32.dp)
                    )

                    // Enhanced Email Field
                    EnhancedLoginTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        icon = Icons.Default.Email,
                        iconTint = Color(0xFF667eea),
                        keyboardType = KeyboardType.Email,
                        isCompact = isCompact,
                        modifier = Modifier.padding(bottom = fieldSpacing)
                    )

                    // Enhanced Password Field
                    EnhancedLoginPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isVisible = passwordVisible,
                        onVisibilityToggle = { passwordVisible = !passwordVisible },
                        isCompact = isCompact,
                        modifier = Modifier.padding(bottom = if (isSmallHeight) 8.dp else 12.dp)
                    )

                    // Forgot Password Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (isSmallHeight) 16.dp else 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showResetDialog = true }
                        ) {
                            Text(
                                "Forgot Password?",
                                color = Color(0xFF667eea),
                                fontSize = if (isCompact) 12.sp else 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Enhanced Sign In Button
                    EnhancedLoginButton(
                        onClick = {
                            if (!isLoading) {
                                isLoading = true
                                val trimmedEmail = email.trim()
                                val trimmedPassword = password.trim()

                                if (trimmedEmail == "prahlady444@gmail.com" && trimmedPassword == "sonal1234") {
                                    isAdmin = true
                                    sharedPref.edit { putBoolean("isAdmin", true) }
                                    Toast.makeText(context, "Admin login successful", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Routes.AdminDashBoard.route) {
                                        popUpTo(Routes.Login.route) { inclusive = true }
                                    }
                                    isLoading = false
                                } else {
                                    auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                isAdmin = false
                                                sharedPref.edit { putBoolean("isAdmin", false) }
                                                Toast.makeText(context, "User login successful", Toast.LENGTH_SHORT).show()
                                                navController.navigate(Routes.BottomNav.route) {
                                                    popUpTo(Routes.Login.route) { inclusive = true }
                                                }
                                            } else {
                                                Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                                            }
                                            isLoading = false
                                        }
                                }
                            }
                        },
                        isLoading = isLoading,
                        text = "Sign In",
                        icon = Icons.Default.Login,
                        isCompact = isCompact
                    )

                    Spacer(modifier = Modifier.height(if (isSmallHeight) 16.dp else 24.dp))

                    // Divider with text
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

                    // Enhanced Register Section
                    Card(
                        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8FAFC)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Routes.Register.route)
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
                                Icons.Default.PersonAdd,
                                contentDescription = "Register",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "New to GECB? ",
                                color = Color(0xFF6B7280),
                                fontSize = if (isCompact) 12.sp else 14.sp
                            )
                            Text(
                                "Create Account",
                                color = Color(0xFF10B981),
                                fontSize = if (isCompact) 12.sp else 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (!isSmallHeight) {
                Spacer(modifier = Modifier.height(24.dp))

                // Enhanced Footer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Secure Login Portal",
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
fun EnhancedLoginTextField(
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
fun EnhancedLoginPasswordField(
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
                    tint = Color(0xFF667eea),
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
                focusedBorderColor = Color(0xFF667eea),
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
fun EnhancedLoginButton(
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
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSendClick: (String) -> Unit,
    isCompact: Boolean = false
) {
    var emailInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Card(
                shape = RoundedCornerShape(if (isCompact) 8.dp else 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF667eea)
                ),
                modifier = Modifier.clickable { onSendClick(emailInput) }
            ) {
                Text(
                    "Send Reset Link",
                    color = Color.White,
                    modifier = Modifier.padding(
                        horizontal = if (isCompact) 12.dp else 16.dp,
                        vertical = if (isCompact) 6.dp else 8.dp
                    ),
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isCompact) 12.sp else 14.sp
                )
            }
        },
        dismissButton = {
            Card(
                shape = RoundedCornerShape(if (isCompact) 8.dp else 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F4F6)
                ),
                modifier = Modifier.clickable { onDismiss() }
            ) {
                Text(
                    "Cancel",
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(
                        horizontal = if (isCompact) 12.dp else 16.dp,
                        vertical = if (isCompact) 6.dp else 8.dp
                    ),
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isCompact) 12.sp else 14.sp
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Reset Password",
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                )
                Spacer(modifier = Modifier.width(if (isCompact) 8.dp else 12.dp))
                Text(
                    "Reset Password",
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isCompact) 16.sp else 18.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    "Enter your email address and we'll send you a link to reset your password.",
                    color = Color(0xFF6B7280),
                    fontSize = if (isCompact) 12.sp else 14.sp,
                    modifier = Modifier.padding(bottom = if (isCompact) 12.dp else 16.dp)
                )

                Card(
                    shape = RoundedCornerShape(if (isCompact) 8.dp else 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = {
                            Text(
                                "Email Address",
                                fontSize = if (isCompact) 12.sp else 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color(0xFF667eea),
                                modifier = Modifier.size(if (isCompact) 16.dp else 20.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667eea),
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1F2937),
                            unfocusedTextColor = Color(0xFF1F2937)
                        ),
                        shape = RoundedCornerShape(if (isCompact) 8.dp else 12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = if (isCompact) 14.sp else 16.sp)
                    )
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(if (isCompact) 16.dp else 20.dp)
    )
}

