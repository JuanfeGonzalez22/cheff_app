package com.example.chefapp.data.repository

import com.example.chefapp.data.model.Category
import com.example.chefapp.data.model.Meal
import com.example.chefapp.data.model.UIState
import com.example.chefapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class RecipeRepository {
    private val api = RetrofitInstance.api

    fun getCategories(): Flow<UIState<List<Category>>> = flow{
        emit(UIState.Loading)
        try{
            val response = api.getCategories()
            emit(UIState.Success(response.categories))
        }catch (e: IOException){
            emit(UIState.Error("Error de red: ${e.message}"))
        }catch (e: Exception){
            emit(UIState.Error("Error inesperado: ${e.message}"))
        }
    }

    fun getMealsByCategory(category: String): Flow<UIState<List<Meal>>> = flow {
        emit(UIState.Loading)
        try {
            val response = api.getMealsByCategory(category)
            emit(UIState.Success(response.meals ?: emptyList()))
        } catch (e: Exception) {
            emit(UIState.Error("Error al cargar recetas: ${e.message}"))
        }
    }

    fun searchMeals(query: String): Flow<UIState<List<Meal>>> = flow {
        if (query.isEmpty()) {
            emit(UIState.Success(emptyList()))
            return@flow
        }

        emit(UIState.Loading)
        try {
            val response = api.searchMeals(query)
            emit(UIState.Success(response.meals ?: emptyList()))
        } catch (e: Exception) {
            emit(UIState.Error("Error en la búsqueda: ${e.message}"))
        }
    }
}