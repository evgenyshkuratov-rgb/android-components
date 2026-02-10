package com.example.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gallery.catalog.CatalogScreen
import com.example.gallery.preview.ChipsViewPreviewScreen
import com.example.gallery.theme.GalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            LaunchedEffect(isDarkTheme) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
                insetsController.isAppearanceLightNavigationBars = !isDarkTheme
            }

            GalleryTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "catalog") {
                        composable("catalog") {
                            CatalogScreen(
                                isDarkTheme = isDarkTheme,
                                onThemeChanged = { isDarkTheme = it },
                                onComponentClick = { componentId ->
                                    navController.navigate("preview/$componentId")
                                }
                            )
                        }
                        composable("preview/{componentId}") { backStackEntry ->
                            val componentId = backStackEntry.arguments?.getString("componentId") ?: ""
                            ChipsViewPreviewScreen(
                                componentId = componentId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
