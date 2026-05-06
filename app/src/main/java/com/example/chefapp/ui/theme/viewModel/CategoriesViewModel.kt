package com.example.chefapp.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefapp.data.model.Category
import com.example.chefapp.data.model.UIState
import com.example.chefapp.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel(){
    private val repository = RecipeRepository()

    private val _categoriesState = MutableStateFlow<UIState<List<Category>>>(UIState.Loading)
    val categoriesState: StateFlow<UIState<List<Category>>> = _categoriesState.asStateFlow()

    init{
        loadCategories()
    }

    private fun loadCategories(){
        viewModelScope.launch {
            repository.getCategories().collect { state ->
                _categoriesState.value = state
            }
        }
    }
}