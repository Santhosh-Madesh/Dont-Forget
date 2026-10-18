package com.example.dontforget

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dontforget.core.navigation.Route
import com.example.dontforget.data.local.DontForgetDatabase
import com.example.dontforget.data.repository.ReminderRepositoryImpl
import com.example.dontforget.data.repository.TemplateRepositoryImpl
import com.example.dontforget.feature.createreminder.CreateReminderScreen
import com.example.dontforget.feature.createreminder.CreateReminderViewModel
import com.example.dontforget.feature.editreminder.EditReminderScreen
import com.example.dontforget.feature.editreminder.EditReminderViewModel
import com.example.dontforget.feature.home.HomeScreen
import com.example.dontforget.feature.home.HomeUiEvent
import com.example.dontforget.feature.home.HomeViewModel
import com.example.dontforget.feature.reminderdetail.ReminderDetailScreen
import com.example.dontforget.feature.reminderdetail.ReminderDetailViewModel
import com.example.dontforget.notification.NotificationConstants
import com.example.dontforget.notification.ReminderNotificationManager
import com.example.dontforget.ui.theme.DontForgetTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val pendingReminderIdState = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ReminderNotificationManager.createChannel(applicationContext)
        handleIntent(intent)

        enableEdgeToEdge()

        val db = DontForgetDatabase.getDatabase(applicationContext)
        val reminderRepo = ReminderRepositoryImpl(db.reminderDao(), db.checklistItemDao(), applicationContext)
        val templateRepo = TemplateRepositoryImpl(db.contextDao(), db.templateDao(), db.checklistItemDao())

        setContent {
            DontForgetTheme {
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        val cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
                        reminderRepo.deleteInactiveRemindersOlderThan(cutoffTime)
                    }
                }

                val navController = rememberNavController()

                val pendingReminderId = pendingReminderIdState.value
                LaunchedEffect(pendingReminderId) {
                    if (pendingReminderId != null && pendingReminderId != NotificationConstants.DEFAULT_REMINDER_ID) {
                        navController.navigate(Route.ReminderDetail.createRoute(pendingReminderId))
                        pendingReminderIdState.value = null
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home.route,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        composable(Route.Home.route) {
                            val homeViewModel = remember { HomeViewModel(reminderRepo) }
                            val context = LocalContext.current

                            fun checkPermission(): Boolean {
                                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                } else {
                                    true
                                }
                            }

                            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                            androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
                                val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                                    if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                                        homeViewModel.onEvent(
                                            HomeUiEvent.OnNotificationPermissionStatusChanged(checkPermission())
                                        )
                                    }
                                }
                                lifecycleOwner.lifecycle.addObserver(observer)
                                onDispose {
                                    lifecycleOwner.lifecycle.removeObserver(observer)
                                }
                            }

                            val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                                androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
                            ) { isGranted ->
                                homeViewModel.onEvent(HomeUiEvent.OnNotificationPermissionStatusChanged(isGranted))
                            }

                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToDetail = { reminderId ->
                                    navController.navigate(Route.ReminderDetail.createRoute(reminderId))
                                },
                                onNavigateToCreateReminder = {
                                    navController.navigate(Route.CreateReminder.route)
                                },
                                onOpenNotificationSettings = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !checkPermission()) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", packageName, null)
                                        }
                                        startActivity(intent)
                                    }
                                }
                            )
                        }
                        composable(Route.CreateReminder.route) {
                            val createViewModel = remember { CreateReminderViewModel(reminderRepo, templateRepo) }
                            CreateReminderScreen(
                                viewModel = createViewModel,
                                onNavigateHome = {
                                    navController.popBackStack(Route.Home.route, false)
                                },
                                onNavigateToReminderDetail = { reminderId ->
                                    navController.navigate(Route.ReminderDetail.createRoute(reminderId)) {
                                        popUpTo(Route.Home.route)
                                    }
                                }
                            )
                        }
                        composable(
                            route = Route.EditReminder.ROUTE,
                            arguments = listOf(navArgument("reminderId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("reminderId") ?: 0L
                            val editViewModel = remember(id) { EditReminderViewModel(reminderRepo) }
                            EditReminderScreen(
                                reminderId = id,
                                viewModel = editViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
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
                            val detailViewModel = remember(id) { ReminderDetailViewModel(id, reminderRepo) }
                            ReminderDetailScreen(
                                viewModel = detailViewModel,
                                onNavigateToEdit = { reminderId ->
                                    navController.navigate(Route.EditReminder.createRoute(reminderId))
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val reminderId = intent?.getLongExtra(
            NotificationConstants.EXTRA_REMINDER_ID,
            NotificationConstants.DEFAULT_REMINDER_ID
        )
        if (reminderId != null && reminderId != NotificationConstants.DEFAULT_REMINDER_ID) {
            pendingReminderIdState.value = reminderId
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
