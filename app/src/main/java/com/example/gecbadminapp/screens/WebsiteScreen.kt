package com.example.gecbadminapp.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun WebsiteScreen() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gecbsp.ac.in/"))
        context.startActivity(intent)
    }

    // Optional: Blank screen or message while redirecting
    Text("Redirecting to website...")
}
