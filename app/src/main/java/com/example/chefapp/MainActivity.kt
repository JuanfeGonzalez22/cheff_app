package com.example.chefapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.chefapp.ui.theme.screens.*
import com.example.chefapp.ui.theme.theme.ChefAppTheme
import com.example.chefapp.ui.theme.theme.ChefPrimary
import com.example.chefapp.ui.theme.theme.ChefSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChefAppTheme {
                ChefConnectApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("categories", "Home", Icons.Default.Home)
    object Search : Screen("search_tab", "Explore", Icons.Default.Search)
    object Saved : Screen("favorites", "Saved", Icons.Default.Favorite)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun ChefConnectApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Saved,
        Screen.Profile
    )

    val showBottomBar = currentDestination?.route in items.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ChefPrimary,
                                selectedTextColor = ChefPrimary,
                                unselectedIconColor = ChefSecondary.copy(alpha = 0.6f),
                                unselectedTextColor = ChefSecondary.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(onNavigateToHome = {
                    navController.navigate("categories") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }

            composable("categories") {
                CategoriesScreen(
                    onCategoryClick = { category ->
                        navController.navigate("meals/$category")
                    },
                    onMealClick = { mealId ->
                        navController.navigate("mealDetail/$mealId")
                    }
                )
            }

            composable("search_tab") {
                CategoriesScreen(
                    onCategoryClick = { category ->
                        navController.navigate("meals/$category")
                    },
                    onMealClick = { mealId ->
                        navController.navigate("mealDetail/$mealId")
                    }
                )
            }

            composable("favorites") {
                FavoritesScreen(
                    onMealClick = { mealId ->
                        navController.navigate("mealDetail/$mealId")
                    }
                )
            }

            composable("profile") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Profile Screen (Coming Soon)")
                }
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
}
