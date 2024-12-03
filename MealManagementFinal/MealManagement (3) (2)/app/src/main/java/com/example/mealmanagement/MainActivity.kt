package com.example.mealmanagement

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mealmanagement.ui.theme.MainScreen
import com.example.mealmanagement.ui.theme.Meal
import com.example.mealmanagement.ui.theme.MealAnalysisScreen
import com.example.mealmanagement.ui.theme.MealDetailScreen
import com.example.mealmanagement.ui.theme.MealInputScreen
import com.example.mealmanagement.ui.theme.MealListScreen
import com.example.mealmanagement.ui.theme.MealManagementTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealManagementTheme {
                // NavController 초기화
                val navController = rememberNavController()

                // 식사 데이터를 저장할 리스트
                val mealList = remember { mutableStateListOf<Meal>() }

                // NavHost를 사용하여 화면 간 네비게이션 구현
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainScreen(navController) }
                    composable("mealInput") {
                        MealInputScreen(
                            onSaveMeal = { meal ->
                                mealList.add(meal)
                                navController.navigate("mealList")
                            },
                            onNavigateHome = { navController.navigate("main") }
                        )
                    }
                    composable("mealList") {
                        MealListScreen(
                            mealList = mealList,
                            onAddNewMeal = { navController.navigate("mealInput") },
                            onMealClick = { meal -> navController.navigate("mealDetail/${mealList.indexOf(meal)}") },
                            onNavigateHome = { navController.navigate("main") }
                        )
                    }
                    composable("mealAnalysis") {
                        MealAnalysisScreen(
                            mealList = mealList,
                            onNavigateHome = { navController.navigate("main") },
                            onAddMeal = { navController.navigate("mealInput") }
                        )
                    }
                    composable("mealDetail/{mealId}") { backStackEntry ->
                        val mealId = backStackEntry.arguments?.getString("mealId")?.toIntOrNull()
                        mealId?.let {
                            MealDetailScreen(
                                meal = mealList[it],
                                onNavigateBack = { navController.popBackStack() } // 뒤로 가기
                            )
                        }
                    }
                }

            }
        }
    }
}
