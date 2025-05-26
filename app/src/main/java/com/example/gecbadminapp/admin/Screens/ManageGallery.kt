package com.example.gecbadminapp.admin.Screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.gecbadminapp.ui.theme.GradientStart
import com.example.gecbadminapp.ui.theme.GradientEnd
import com.example.gecbadminapp.ui.theme.WhiteText
import com.example.gecbadminapp.ui.theme.TransparentCard
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

@Composable
fun ManageGallery() {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val storage = Firebase.storage

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("gallery/$fileName")

            storageRef.putFile(it)
                .addOnSuccessListener {
                    Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Scaffold(topBar = { GalleryTopBar() }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFe0f7fa), Color.White)
                    )
                )
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Gallery Icon",
                tint = GradientStart,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Manage Gallery",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = GradientStart
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Here you can upload, delete or update photos in your college gallery.",
                fontSize = 16.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = {
                launcher.launch("image/*")
            }) {
                Text("Add New Image")
            }

            Spacer(modifier = Modifier.height(20.dp))

            selectedImageUri?.let {
                Text("Selected Image: ${it.lastPathSegment}", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun GalleryTopBar() {
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
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Gallery Manager",
                fontWeight = FontWeight.Bold,
                color = WhiteText,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 20.dp)
            )
        }
    }
}
