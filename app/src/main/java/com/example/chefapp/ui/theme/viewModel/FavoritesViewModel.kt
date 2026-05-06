package com.example.chefapp.ui.theme.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefapp.data.model.Meal
import com.example.chefapp.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val favoritesRepository = FavoritesRepository(application)

    val favoriteMeals: StateFlow<List<Meal>> = favoritesRepository.favoriteMeals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
