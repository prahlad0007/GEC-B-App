package com.example.gecbadminapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.navigation.compose.rememberNavController
import com.example.gecbadminapp.R.style.Theme_GECBAdminApp
import com.example.gecbadminapp.navigation.NavGraph
import com.example.gecbadminapp.ui.theme.GECBAdminAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Switch from Splash theme to real app theme BEFORE Compose
        setTheme(Theme_GECBAdminApp)

        super.onCreate(savedInstanceState)

        // ✅ Initialize Firebase
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )


        // ✅ Optional: Immersive edge-to-edge layout
        enableEdgeToEdge()

        setContent {
            GECBAdminAppTheme {
                val systemUiController = rememberSystemUiController()
                val background = MaterialTheme.colorScheme.background

                // ✅ Set system bar colors to match theme
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = background,
                        darkIcons = true
                    )
                }

                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
