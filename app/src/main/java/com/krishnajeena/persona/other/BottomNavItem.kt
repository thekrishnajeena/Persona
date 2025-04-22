package com.krishnajeena.persona.other

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Explore : BottomNavItem("explore", Icons.Default.Explore, "Explore")
    object ReelStack : BottomNavItem("reelstack", Icons.Default.Movie, "Reels")
    object Clicks : BottomNavItem("clicks", Icons.Default.CameraAlt, "Clicks")
    object Study : BottomNavItem("study", Icons.AutoMirrored.Filled.MenuBook, "Study")
    object Tools : BottomNavItem("tools", Icons.Default.Build, "Tools")
    object BlogsOfCategory : BottomNavItem("blogsofcategory", Icons.Default.MenuBook, "Blogs")
    object Music : BottomNavItem("music", Icons.Default.MusicNote, "Music")
}
