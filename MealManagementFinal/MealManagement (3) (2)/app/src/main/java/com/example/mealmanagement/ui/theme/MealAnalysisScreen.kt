package com.example.mealmanagement.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import com.example.mealmanagement.R
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun MealAnalysisScreen(mealList: List<Meal>, onNavigateHome: () -> Unit, onAddMeal: () -> Unit) {
    // 현재 날짜를 구합니다.
    val currentDate = LocalDate.now()

    // 최근 1달 동안의 식사만 필터링
    val recentMeals = mealList.filter { meal ->
        // 식사의 날짜가 null이 아닌지 체크하고, 날짜와 현재 날짜의 차이를 계산
        meal.date?.let { date ->
            date.isAfter(currentDate.minus(30, ChronoUnit.DAYS)) || date.isEqual(currentDate)
        } ?: false // 날짜가 null일 경우 제외
    }

    // 현재 컨텍스트를 가져옵니다.
    val context = LocalContext.current

    // 최근 1달 동안의 칼로리 합산
    val calorieSum = recentMeals.sumOf { meal ->
        getCalorieForMeal(meal, context) + getCaloriesForSideDishes(meal, context)
    }

    // 식사 유형별 비용 합산
    val mealCostByType = MealType.values().associateWith { type ->
        recentMeals.filter { it.type == type }.sumOf {
            it.cost.toDoubleOrNull() ?: 0.0 // 빈 문자열을 0.0으로 처리
        }
    }

    // 총 비용 계산
    val totalCost = mealCostByType.values.sum()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(0.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2196F3))
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "식사 분석",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.align(Alignment.Center) // Box 내부 중앙에 배치
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${currentDate.minus(30, ChronoUnit.DAYS)} ~ $currentDate",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "칼로리 총량: $calorieSum kcal",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "총 비용: ${totalCost}원",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            mealCostByType.forEach { (type, totalCost) ->
                val typeName = when (type) {
                    MealType.Breakfast -> "조식"
                    MealType.Lunch -> "중식"
                    MealType.Dinner -> "석식"
                    MealType.Snack -> "간식/음료"
                }
                Text(
                    text = "${typeName} 비용: ${totalCost}원",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // 버튼을 오른쪽 하단에 고정, 공백 없이 크기 조정
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 0.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = { onNavigateHome() },
                modifier = Modifier.weight(1f)
            ) {
                Text("초기화면으로")
            }
            Button(
                onClick = { onAddMeal() },
                modifier = Modifier.weight(1f)
            ) {
                Text("새로운 식사 추가")
            }
        }
    }
}

fun getCalorieForMeal(meal: Meal, context: Context): Long {
    val resources = context.resources
    val foodCaloriesArray = resources.getStringArray(R.array.foods)
    var calorieValue: Long? = null

    for (item in foodCaloriesArray) {
        val (name, calories) = item.split(",")
        if (name.trim() == meal.name.trim()) {
            calorieValue = calories.trim().toLongOrNull()
            break
        }
    }

    return calorieValue?.also { println("Calorie used for: ${meal.name} = $calorieValue") } ?: getDefaultCalories(meal.type).also { println("Default calorie used for: ${meal.name}") }
}

fun getCaloriesForSideDishes(meal: Meal, context: Context): Long {
    val resources = context.resources
    val foodCaloriesArray = resources.getStringArray(R.array.foods)
    var totalSideDishCalories: Long = 0

    meal.sideDish?.split(",")?.forEach { sideDish ->
        for (item in foodCaloriesArray) {
            val (name, calories) = item.split(",")
            if (name.trim() == sideDish.trim()) {
                totalSideDishCalories += calories.trim().toLongOrNull() ?: 0L
                break
            }
        }
    }

    return totalSideDishCalories
}

fun getDefaultCalories(type: MealType): Long {
    return when (type) {
        MealType.Breakfast -> 500L // 조식
        MealType.Lunch -> 800L // 중식
        MealType.Dinner -> 1200L // 석식
        MealType.Snack -> 200L // 간식
    }
}
