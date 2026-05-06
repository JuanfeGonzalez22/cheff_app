package com.example.chefapp.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefapp.data.model.UIState
import com.example.chefapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MealDetail(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val strInstructions: String?,
    val ingredients: List<Pair<String, String>> // Ingrediente y medida
)

class MealDetailViewModel : ViewModel() {
    private val api = RetrofitInstance.api

    private val _detailState = MutableStateFlow<UIState<MealDetail>>(UIState.Loading)
    val detailState: StateFlow<UIState<MealDetail>> = _detailState.asStateFlow()

    fun loadMealDetail(mealId: String) {
        viewModelScope.launch {
            _detailState.value = UIState.Loading
            try {
                val response = api.getMealDetail(mealId)
                val meal = response?.get("meals")?.firstOrNull()

                if (meal != null) {
                    // Procesar ingredientes y medidas
                    val ingredients = mutableListOf<Pair<String, String>>()
                    for (i in 1..20) {
                        val ingredient = meal["strIngredient$i"] as? String
                        val measure = meal["strMeasure$i"] as? String
                        if (!ingredient.isNullOrBlank()) {
                            ingredients.add(ingredient to (measure ?: ""))
                        }
                    }

                    val detail = MealDetail(
                        idMeal = meal["idMeal"] as? String ?: "",
                        strMeal = meal["strMeal"] as? String ?: "",
                        strMealThumb = meal["strMealThumb"] as? String ?: "",
                        strInstructions = meal["strInstructions"] as? String,
                        ingredients = ingredients
                    )
                    _detailState.value = UIState.Success(detail)
                } else {
                    _detailState.value = UIState.Error("No se encontró la receta")
                }
            } catch (e: Exception) {
                _detailState.value = UIState.Error("Error al cargar detalle: ${e.message}")
            }
        }
    }
}
