package com.example.chefapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.chefapp.data.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorites")

class FavoritesRepository(private val context: Context) {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_meals_data")

    val favoriteMeals: Flow<List<Meal>> = context.dataStore.data
        .map { preferences ->
            val set = preferences[FAVORITES_KEY] ?: emptySet()
            set.map {
                val parts = it.split("|")
                Meal(
                    idMeal = parts.getOrNull(0) ?: "",
                    strMeal = parts.getOrNull(1) ?: "",
                    strMealThumb = parts.getOrNull(2) ?: ""
                )
            }.filter { it.idMeal.isNotEmpty() }
        }

    suspend fun toggleFavorite(meal: Meal) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[FAVORITES_KEY] ?: emptySet()
            val mealString = "${meal.idMeal}|${meal.strMeal}|${meal.strMealThumb}"
            
            val existing = currentSet.find { it.startsWith("${meal.idMeal}|") }
            
            if (existing != null) {
                preferences[FAVORITES_KEY] = currentSet - existing
            } else {
                preferences[FAVORITES_KEY] = currentSet + mealString
            }
        }
    }

    suspend fun isFavorite(mealId: String): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            val set = preferences[FAVORITES_KEY] ?: emptySet()
            set.any { it.startsWith("$mealId|") }
        }
    }
}
