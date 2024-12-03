package com.example.mealmanagement.ui.theme

import android.net.Uri
import java.time.LocalDate

data class Meal(
    val date: LocalDate,
    val name: String,
    val cost: String,
    val type: MealType,
    val imageUri: Uri?,
    val calories: Int,
    val sideDish: String,

    val review: String,

    )

enum class MealType {
    Breakfast, Lunch, Dinner, Snack
}