package com.example.gecbadminapp.navigation


import Faculty
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gecbadminapp.admin.Screens.*
import com.example.gecbadminapp.screens.*

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
        composable(Routes.Gallery.route) { Gallery() }
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
        composable(Routes.ManageEvents.route) {
            ManageEvents()
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
