package com.example.chefapp.data.network

import com.example.chefapp.data.model.CategoryResponse
import com.example.chefapp.data.model.MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    @GET("filter.php")
    suspend fun getMealsByCategory(
        @Query("c") category: String
    ): MealsResponse

    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") query: String
    ): MealsResponse

    @GET("lookup.php")
    suspend fun getMealDetail(
        @Query("i") id: String
    ): Map<String, List<Map<String, Any>>>?
}
