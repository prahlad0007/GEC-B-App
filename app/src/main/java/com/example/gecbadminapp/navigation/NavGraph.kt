package com.example.gecbadminapp.navigation

import Faculty
import Routes
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gecbadminapp.admin.Screens.*
import com.example.gecbadminapp.admin.Screens.Club.ManageCulturalClub
import com.example.gecbadminapp.admin.Screens.Club.ManageGDSC
import com.example.gecbadminapp.admin.Screens.Club.ManageINT64_T
import com.example.gecbadminapp.admin.Screens.Club.ManageNSS
import com.example.gecbadminapp.admin.Screens.Club.ManageSportClub
import com.example.gecbadminapp.admin.Screens.Club.ManageTpoCell
import com.example.gecbadminapp.screens.*
import com.example.gecbadminapp.screens.GECB_Communities.CommunityScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        // Splash Screen
        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }

        // Authentication
        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController)
        }

        // User-side navigation
        composable(Routes.BottomNav.route) {
            BottomNav(navController)
        }
        composable(Routes.Home.route) { Home() }
        composable(Routes.Faculty.route) { Faculty() }
        composable(Routes.Community.route) { CommunityScreen() }
        composable(Routes.AboutUs.route) { AboutUs() }

        // Admin-side navigation
        composable(Routes.AdminDashBoard.route) {
            AdminDashBoard(navController)
        }
        composable(Routes.ManageTimetable.route) {
            ManageTimetable()
        }
        composable(Routes.ManageBanner.route) {
            ManageBanner(navController)
        }
        composable(Routes.manageNotice.route) {
            ManageNotice()
        }
        composable(Routes.ManageFaculty.route) {
            ManageFaculty()
        }

        // Fixed: Call the actual ManageEvents composable function
        composable(Routes.ManageEvents.route) {
            ManageEvents(navController)
        }
        // Individual community management screens
        composable("ManageNSS") {
            ManageNSS()
        }

        composable("ManageINT64_T") {
            ManageINT64_T()
        }

        composable("ManageCulturalClub") {
            ManageCulturalClub()
        }

        composable("ManageSportClub") {
            ManageSportClub()
        }

        composable("ManageTpoCell") {
            ManageTpoCell()
        }

        composable("ManageGDSC") {
            ManageGDSC()
        }

        composable(Routes.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Routes.Website.route) { WebsiteScreen() }
        composable(Routes.Notice.route) { NoticeScreen() }
        composable(Routes.Timetable.route) { TimetableScreen() }
        composable(Routes.Contact.route) { ContactScreen() }
    }
}