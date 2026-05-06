package com.example.chefapp.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefapp.data.model.Meal
import com.example.chefapp.data.model.UIState
import com.example.chefapp.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealsViewModel : ViewModel() {
    private val repository = RecipeRepository()

    private val _mealsState = MutableStateFlow<UIState<List<Meal>>>(UIState.Loading)
    val mealsState: StateFlow<UIState<List<Meal>>> = _mealsState.asStateFlow()

    private var currentCategory: String = ""

    fun loadMealsByCategory(category: String) {
        if (currentCategory == category && _mealsState.value is UIState.Success) {
            return // Evita recargar la misma categoría
        }

        currentCategory = category
        viewModelScope.launch {
            repository.getMealsByCategory(category).collect { state ->
                _mealsState.value = state
            }
        }
    }

    fun retryLoad() {
        if (currentCategory.isNotEmpty()) {
            loadMealsByCategory(currentCategory)
        }
    }
}