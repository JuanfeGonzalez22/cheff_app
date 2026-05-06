package com.example.chefapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.chefapp.ui.theme.screens.CategoriesScreen
import com.example.chefapp.ui.theme.screens.MealDetailScreen
import com.example.chefapp.ui.theme.screens.MealsGridScreen
import com.example.chefapp.ui.theme.theme.ChefAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChefAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ChefConnectApp()
                    }
                }
            }
        }
    }
}

@Composable
fun ChefConnectApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "categories"
    ) {
        composable("categories") {
            CategoriesScreen(
                onCategoryClick = { category ->
                    navController.navigate("meals/$category")
                }
            )
        }

        composable(
            route = "meals/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: return@composable
            MealsGridScreen(
                category = category,
                onBack = { navController.popBackStack() },
                onMealClick = { mealId ->
                    navController.navigate("mealDetail/$mealId")
                }
            )
        }

        composable(
            route = "mealDetail/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "chefapp://mealDetail/{mealId}" })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: return@composable
            MealDetailScreen(
                mealId = mealId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
