package com.example.chefapp.data.model

data class MealDetailResponse(
    val meals: List<Map<String, Any?>>? // Respuesta dinámica de la API
)