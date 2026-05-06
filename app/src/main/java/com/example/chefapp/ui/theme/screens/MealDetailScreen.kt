package com.example.chefapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.chefapp.data.model.UIState
import com.example.chefapp.ui.theme.theme.*
import com.example.chefapp.ui.theme.viewModel.MealDetail
import com.example.chefapp.ui.theme.viewModel.MealDetailViewModel

@Composable
fun MealDetailScreen(
    mealId: String,
    viewModel: MealDetailViewModel = viewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(mealId) {
        viewModel.loadMealDetail(mealId)
    }

    val detailState by viewModel.detailState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    MealDetailContent(
        detailState = detailState,
        isFavorite = isFavorite,
        onBack = onBack,
        onFavoriteToggle = { 
            val current = (detailState as? UIState.Success)?.data
            if (current != null) {
                viewModel.toggleFavorite(current)
            }
        },
        onRetry = { viewModel.loadMealDetail(mealId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailContent(
    detailState: UIState<MealDetail>,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(ChefBackground)) {
        when (val state = detailState) {
            is UIState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ChefPrimary)
            }

            is UIState.Success -> {
                val meal = state.data
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        // Header Image with buttons
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                            AsyncImage(
                                model = meal.strMealThumb,
                                contentDescription = meal.strMeal,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Custom top actions
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                                }
                                IconButton(
                                    onClick = onFavoriteToggle,
                                    modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) Color.Red else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Content Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-30).dp)
                                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                                .background(ChefSurface)
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = meal.strMeal,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ChefDarkGreen
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    color = ChefTertiary,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = ChefPrimary, modifier = Modifier.size(16.dp))
                                        @Suppress("SetTextI18n")
                                        Text(" 4.9", style = MaterialTheme.typography.labelSmall, color = ChefSecondary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Info badges
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoBadge(Icons.Default.Timer, "30 min")
                                InfoBadge(Icons.Default.Restaurant, "2 Servings")
                                InfoBadge(Icons.Default.Bolt, "420 kcal")
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                "Description",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            @Suppress("SetTextI18n")
                            Text(
                                "A light, flavorful dinner that pairs flaky salmon with a zesty herb crust. Perfect for a quick weeknight meal.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ChefGrey,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Ingredients",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text("${meal.ingredients.size} items", color = ChefSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    items(meal.ingredients) { (ingredient, measure) ->
                        IngredientItem(ingredient, measure)
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                            Text(
                                "Instructions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val steps = meal.strInstructions?.split("\r\n", "\n")?.filter { it.isNotBlank() } ?: emptyList()
                            steps.forEachIndexed { index, step ->
                                InstructionStep(index + 1, step)
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                // Bottom Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                ) {
                    Button(
                        onClick = { /* Start Cooking */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ChefDarkGreen),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Text(" Start Cooking", fontWeight = FontWeight.Bold)
                    }
                }
            }

            is UIState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = onRetry) { Text("Reintentar") }
                }
            }
        }
    }
}

@Composable
fun InfoBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        color = ChefLightGrey,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = ChefSecondary)
            Text(" $text", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
        }
    }
}

@Composable
fun IngredientItem(ingredient: String, measure: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ChefBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ChefSurface)
                .padding(2.dp)
        ) {
            // Checkbox placeholder
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(ingredient, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(measure, style = MaterialTheme.typography.labelSmall, color = ChefGrey)
        }
    }
}

@Composable
fun InstructionStep(number: Int, text: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Surface(
            modifier = Modifier.size(24.dp),
            color = ChefPrimary,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(number.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    }
}

@Preview(showBackground = true)
@Composable
fun MealDetailPreview() {
    ChefAppTheme {
        MealDetailContent(
            detailState = UIState.Success(
                MealDetail(
                    idMeal = "1",
                    strMeal = "Herb-Crusted Salmon",
                    strMealThumb = "https://www.themealdb.com/images/media/meals/uvqtpv1468239893.jpg",
                    strInstructions = "Preheat your oven to 200C.\r\nIn a small bowl combine panko...",
                    ingredients = listOf("Salmon" to "2 pieces", "Asparagus" to "1 bunch")
                )
            ),
            isFavorite = true,
            onBack = {},
            onFavoriteToggle = {},
            onRetry = {}
        )
    }
}
