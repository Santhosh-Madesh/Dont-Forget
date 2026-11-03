package com.example.dontforget.core.ui.components

import androidx.annotation.StringRes
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
import com.example.dontforget.R

data class ContextIconItem(
    val name: String,
    @param:StringRes val labelResId: Int,
    val icon: ImageVector
)

object ContextIcons {
    val AVAILABLE_ICONS = listOf(
        ContextIconItem("school", R.string.context_icon_college, Icons.Filled.School),
        ContextIconItem("work", R.string.context_icon_work, Icons.Filled.Work),
        ContextIconItem("office", R.string.context_icon_office, Icons.Filled.Business),
        ContextIconItem("temple", R.string.context_icon_temple, Icons.Filled.Place),
        ContextIconItem("home", R.string.context_icon_home, Icons.Filled.Home),
        ContextIconItem("gym", R.string.context_icon_gym, Icons.Filled.FitnessCenter),
        ContextIconItem("shopping", R.string.context_icon_shopping, Icons.Filled.ShoppingCart),
        ContextIconItem("travel", R.string.context_icon_travel, Icons.Filled.Flight),
        ContextIconItem("sports", R.string.context_icon_sports, Icons.Filled.SportsSoccer),
        ContextIconItem("event", R.string.context_icon_event, Icons.Filled.Event)
    )

    fun getIconByName(name: String): ImageVector {
        return AVAILABLE_ICONS.find { it.name.equals(name, ignoreCase = true) }?.icon
            ?: Icons.Filled.Event
    }
}
