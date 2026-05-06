package com.example.chefapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chefapp.data.model.Meal
import com.example.chefapp.ui.theme.theme.*
import com.example.chefapp.ui.theme.viewModel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel(),
    onMealClick: (String) -> Unit
) {
    val favorites by viewModel.favoriteMeals.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Saved Recipes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ChefDarkGreen
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChefBackground)
            )
        },
        containerColor = ChefBackground
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No saved recipes yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ChefGrey
                    )
                    Text(
                        "Tap the heart icon on any recipe to save it!",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChefGrey
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(favorites, key = { it.idMeal }) { meal ->
                    MealCard(
                        meal = meal,
                        onClick = { onMealClick(meal.idMeal) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    ChefAppTheme {
        FavoritesScreen(onMealClick = {})
    }
}
