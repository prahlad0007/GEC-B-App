package com.example.gecbadminapp.admin.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gecbadminapp.ui.theme.GradientEnd
import com.example.gecbadminapp.ui.theme.GradientStart
import com.example.gecbadminapp.ui.theme.TransparentCard
import com.example.gecbadminapp.ui.theme.WhiteText

@Composable
fun ManageCollegeInfo() {
    var collegeName by remember { mutableStateOf("Government Engineering College, Bhopal") }
    var contactEmail by remember { mutableStateOf("gecbhopal@college.in") }
    var phoneNumber by remember { mutableStateOf("+91 1234567890") }
    var aboutCollege by remember { mutableStateOf("A prestigious college affiliated with RGPV, known for quality technical education.") }

    Scaffold(
        topBar = { CollegeInfoTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFe0f7fa), Color.White)))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "College Icon",
                tint = GradientStart,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Edit College Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GradientStart
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = collegeName,
                onValueChange = { collegeName = it },
                label = { Text("College Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it },
                label = { Text("Contact Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = aboutCollege,
                onValueChange = { aboutCollege = it },
                label = { Text("About College") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    // TODO: Save updated info to backend or Firebase
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
            ) {
                Text("Save Changes", color = WhiteText)
            }
        }
    }
}

@Composable
fun CollegeInfoTopBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        color = TransparentCard,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd))),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "College Info Manager",
                fontWeight = FontWeight.Bold,
                color = WhiteText,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 20.dp)
            )
        }
    }
}
