package com.example.chefapp.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefapp.data.model.Meal
import com.example.chefapp.data.model.UIState
import com.example.chefapp.data.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repository = RecipeRepository()

    private val _searchState = MutableStateFlow<UIState<List<Meal>>>(UIState.Success(emptyList()))
    val searchState: StateFlow<UIState<List<Meal>>> = _searchState.asStateFlow()

    private var searchJob: Job? = null

    fun searchMeals(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchState.value = UIState.Success(emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce para no saturar la API mientras el usuario escribe
            repository.searchMeals(query).collect { state ->
                _searchState.value = state
            }
        }
    }
}
