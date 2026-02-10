package com.example.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
            GalleryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "catalog") {
                        composable("catalog") {
                            CatalogScreen(onComponentClick = { componentId ->
                                navController.navigate("preview/$componentId")
                            })
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
