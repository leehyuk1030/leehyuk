package com.example.mealmanagement.ui.theme

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
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

            }
        }
    }
}

@Composable
fun MealListScreen(mealList: List<Meal>, onAddNewMeal: () -> Unit, onMealClick: (Meal) -> Unit, onNavigateHome: () -> Unit) {
    // 날짜별로 식사를 그룹화
    val groupedMeals = mealList.groupBy { it.date }

    Column {
        Text(
            "식사 리스트",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(Color(0xFF3EB489))
                .padding(start = 151.dp, end = 151.dp, top = 16.dp, bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            // 날짜별로 식사 리스트 표시
            items(groupedMeals.toList()) { (date, meals) ->
                // 날짜 제목
                Text(
                    text = "날짜: $date",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                )

                // 각 날짜별 식사 항목 표시
                meals.forEach { meal ->
                    Box(modifier = Modifier.fillMaxWidth().background(Color.Black).height(1.dp)) {}
                    MealItem(meal = meal, onMealClick = { onMealClick(meal) })
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { onNavigateHome() }, modifier = Modifier.weight(1f), colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF3EB489))) {
                Text("초기화면으로")
            }
            Button(onClick = { onAddNewMeal() }, modifier = Modifier.weight(1f), colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF3EB489))) {
                Text("새로운 식사 추가")
            }
        }
    }
}

@Composable
fun MealItem(meal: Meal, onMealClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        meal.imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = null,
                modifier = Modifier.padding(vertical = 8.dp).weight(1f),
                contentScale = ContentScale.Crop
            )
        }

        // 식사 유형 텍스트 표시
        val mealTypeText = when (meal.type) {
            MealType.Breakfast -> "조식"
            MealType.Lunch -> "중식"
            MealType.Dinner -> "석식"
            MealType.Snack -> "음료/간식"
            else -> "알 수 없음"
        }
        Text(mealTypeText, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), modifier = Modifier.padding(start = 16.dp, end = 230.dp))

        // 상세보기 버튼 추가 (오른쪽 정렬)
        Button(
            onClick = { onMealClick() },
            modifier = Modifier.padding(start = 16.dp).width(100.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_search),
                contentDescription = null
            )
        }
    }
}

@Composable
fun MealDetailScreen(meal: Meal, onNavigateBack: () -> Unit) {
    val mealTypeText = when (meal.type) {
        MealType.Breakfast -> "조식"
        MealType.Lunch -> "중식"
        MealType.Dinner -> "석식"
        MealType.Snack -> "음료/간식"
        else -> "알 수 없음"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("식사 유형: $mealTypeText", style = MaterialTheme.typography.bodyMedium)
            Text("날짜: ${meal.date}", style = MaterialTheme.typography.bodyMedium)
            Text("음식 이름: ${meal.name}", style = MaterialTheme.typography.bodySmall)
            Text("반찬: ${meal.sideDish}", style = MaterialTheme.typography.bodySmall)
            Text("비용: ${meal.cost}원", style = MaterialTheme.typography.bodySmall)
            Text("소감평: ${meal.review}", style = MaterialTheme.typography.bodySmall)
            meal.imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(model = it),
                    contentDescription = null,
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 이전 식사 리스트 화면으로 이동하는 버튼을 오른쪽 하단에 고정
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("뒤로")
        }
    }
}

/*@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
fun PreviewMealListScreen() {
    MealListScreen(mealList = listOf(
        Meal(LocalDate.now(), "치킨", "소시지", 1200, "3000", MealType.Lunch, null)
    ))
}*/
