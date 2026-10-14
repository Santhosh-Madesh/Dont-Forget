package com.example.dontforget.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

data class ContextIconItem(
    val name: String,
    val label: String,
    val icon: ImageVector
)

object ContextIcons {
    val AVAILABLE_ICONS = listOf(
        ContextIconItem("school", "College / School", Icons.Filled.School),
        ContextIconItem("work", "Work", Icons.Filled.Work),
        ContextIconItem("office", "Office", Icons.Filled.Business),
        ContextIconItem("temple", "Temple / Worship", Icons.Filled.Place),
        ContextIconItem("home", "Home", Icons.Filled.Home),
        ContextIconItem("gym", "Gym / Fitness", Icons.Filled.FitnessCenter),
        ContextIconItem("shopping", "Shopping", Icons.Filled.ShoppingCart),
        ContextIconItem("travel", "Travel / Flight", Icons.Filled.Flight),
        ContextIconItem("sports", "Sports", Icons.Filled.SportsSoccer),
        ContextIconItem("event", "Event", Icons.Filled.Event)
    )

    fun getIconByName(name: String): ImageVector {
        return AVAILABLE_ICONS.find { it.name.equals(name, ignoreCase = true) }?.icon
            ?: Icons.Filled.Event
    }
}
