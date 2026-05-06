package com.example.chefapp.ui.theme.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.chefapp.data.model.Category
import com.example.chefapp.data.model.UIState
import com.example.chefapp.ui.theme.theme.*
import com.example.chefapp.ui.theme.viewModel.CategoriesViewModel

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = viewModel(),
    onCategoryClick: (String) -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            println("Permiso de notificaciones denegado")
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    val categoriesState by viewModel.categoriesState.collectAsState()
    CategoriesScreenContent(state = categoriesState, onCategoryClick = onCategoryClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreenContent(
    state: UIState<List<Category>>,
    onCategoryClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = ChefSecondary)
                        Text(
                            "ChefConnect",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ChefSecondary
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ChefLightGrey)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.fillMaxSize())
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        "What are we cooking today?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ChefDarkGreen,
                            fontSize = 28.sp
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(ChefTertiary),
                        placeholder = { Text("Search recipes, ingredients...", color = ChefGrey) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ChefGrey) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            }

            item {
                if (state is UIState.Success) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.data) { category ->
                            FilterChip(
                                selected = false,
                                onClick = { onCategoryClick(category.strCategory) },
                                label = { Text(category.strCategory) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = ChefTertiary,
                                    labelColor = ChefSecondary
                                ),
                                border = null,
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }

            when (state) {
                is UIState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ChefPrimary)
                        }
                    }
                }
                is UIState.Success -> {
                    gridItems(state.data) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { onCategoryClick(category.strCategory) }
                        )
                    }
                }
                is UIState.Error -> {
                    item {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ChefSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = category.strCategoryThumb,
                    contentDescription = category.strCategory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.padding(12.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Featured",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = ChefSecondary
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = category.strCategory,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ChefDarkGreen
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ChefTertiary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Saludable",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ChefSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "• 20 min",
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
fun CategoriesScreenPreview() {
    ChefAppTheme {
        CategoriesScreenContent(
            state = UIState.Success(
                listOf(
                    Category("1", "Beef", "https://www.themealdb.com/images/category/beef.png", ""),
                    Category("2", "Chicken", "", ""),
                    Category("3", "Dessert", "", "")
                )
            ),
            onCategoryClick = {}
        )
    }
}
