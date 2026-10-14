package com.example.dontforget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dontforget.core.navigation.Route
import com.example.dontforget.data.local.DontForgetDatabase
import com.example.dontforget.data.repository.ReminderRepositoryImpl
import com.example.dontforget.ui.theme.DontForgetTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Auto-purge inactive reminders older than 24h on launch
        val db = DontForgetDatabase.getDatabase(applicationContext)
        val reminderRepo = ReminderRepositoryImpl(db.reminderDao(), db.checklistItemDao())

        setContent {
            DontForgetTheme {
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        val cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
                        reminderRepo.deleteInactiveRemindersOlderThan(cutoffTime)
                    }
                }

                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Route.Home.route) {
                            PlaceholderScreen(title = "Home Screen (Chunk 1)")
                        }
                        composable(Route.CreateReminder.route) {
                            PlaceholderScreen(title = "Create Reminder Screen (Chunk 2)")
                        }
                        composable(
                            route = Route.EditReminder.ROUTE,
                            arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("reminderId") ?: 0L
                            PlaceholderScreen(title = "Edit Reminder Screen #$id (Chunk 2)")
                        }
                        composable(Route.TemplateManagement.route) {
                            PlaceholderScreen(title = "Template Management Screen (Chunk 3)")
                        }
                        composable(
                            route = Route.TemplateDetail.ROUTE,
                            arguments = listOf(navArgument("templateId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("templateId") ?: 0L
                            PlaceholderScreen(title = "Template Detail Screen #$id (Chunk 3)")
                        }
                        composable(
                            route = Route.TemplateEdit.ROUTE,
                            arguments = listOf(navArgument("templateId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("templateId") ?: 0L
                            PlaceholderScreen(title = "Template Edit Screen #$id (Chunk 3)")
                        }
                        composable(
                            route = Route.ReminderDetail.ROUTE,
                            arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("reminderId") ?: 0L
                            PlaceholderScreen(title = "Reminder Detail Screen #$id (Chunk 1)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}
