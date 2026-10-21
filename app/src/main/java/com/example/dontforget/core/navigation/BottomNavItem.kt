package com.example.dontforget.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.dontforget.R

sealed class BottomNavItem(
    val route: String,
    @param:StringRes val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Reminders : BottomNavItem(
        route = Route.Home.route,
        titleResId = R.string.common_nav_reminders,
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )

    object Templates : BottomNavItem(
        route = Route.TemplateManagement.route,
        titleResId = R.string.common_nav_templates,
        selectedIcon = Icons.Filled.GridView,
        unselectedIcon = Icons.Outlined.GridView
    )

    companion object {
        val items = listOf(Reminders, Templates)
    }
}
