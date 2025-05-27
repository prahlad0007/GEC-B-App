package com.example.gecbadminapp.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.unit.Dp


@Composable
fun ResponsiveScreen(content: @Composable () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize() // Keep outer layer full
        // Don’t apply padding globally
    ) {
        val width = this@BoxWithConstraints.maxWidth
        val fontSize = if (width < 360.dp) 13.sp else 16.sp
        val spacing = if (width < 360.dp) 8.dp else 16.dp

        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.onBackground
            )
        ) {
            // Just provide font & spacing context
            content()
        }
    }
}
