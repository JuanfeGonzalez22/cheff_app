package com.example.chefapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.chefapp.data.model.Meal
import com.example.chefapp.data.model.UIState
import com.example.chefapp.ui.theme.theme.*
import com.example.chefapp.ui.theme.viewModel.MealsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsGridScreen(
    category: String,
    viewModel: MealsViewModel = viewModel(),
    onMealClick: (String) -> Unit
) {
    LaunchedEffect(category) {
        viewModel.loadMealsByCategory(category)
    }

    val mealsState by viewModel.mealsState.collectAsState()

    MealsGridScreenContent(
        category = category,
        mealsState = mealsState,
        onRetry = { viewModel.retryLoad() },
        onMealClick = onMealClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsGridScreenContent(
    category: String,
    mealsState: UIState<List<Meal>>,
    onRetry: () -> Unit,
    onMealClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ChefDarkGreen
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Back handled by NavHost */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ChefSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ChefBackground
                )
            )
        },
        containerColor = ChefBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = mealsState) {
                is UIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ChefPrimary
                    )
                }

                is UIState.Success -> {
                    if (state.data.isEmpty()) {
                        Text(
                            text = "No hay recetas en esta categoría",
                            modifier = Modifier.align(Alignment.Center),
                            color = ChefGrey
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.data, key = { it.idMeal }) { meal ->
                                MealCard(
                                    meal = meal,
                                    onClick = { onMealClick(meal.idMeal) }
                                )
                            }
                        }
                    }
                }

                is UIState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = ChefPrimary)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealCard(
    meal: Meal,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ChefSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = meal.strMealThumb,
                    contentDescription = meal.strMeal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = ChefPrimary,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = meal.strMeal,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ChefDarkGreen
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ChefTertiary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Nuevo",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ChefSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "• 4.8",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChefGrey
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MealsGridScreenPreview() {
    ChefAppTheme {
        MealsGridScreenContent(
            category = "Beef",
            mealsState = UIState.Success(
                listOf(
                    Meal("1", "Beef and Mustard Pie", ""),
                    Meal("2", "Beef and Oyster Pie", ""),
                    Meal("3", "Beef Wellington", "")
                )
            ),
            onRetry = {},
            onMealClick = {}
        )
    }
}
