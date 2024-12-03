package com.example.mealmanagement.ui.theme

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Calendar
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealInputScreen(onSaveMeal: (Meal) -> Unit, onNavigateHome: () -> Unit) {
    var mealPlace by remember { mutableStateOf("상록원 1층") }
    var mealName by remember { mutableStateOf("") }
    var sideDishName by remember { mutableStateOf("") }
    var mealDate by remember { mutableStateOf(LocalDate.now()) }

    var mealType by remember { mutableStateOf(MealType.Breakfast) }
    var mealCost by remember { mutableStateOf(0) }
    var foodImageUri by remember { mutableStateOf<Uri?>(null) }
    var mealReview by remember { mutableStateOf("") }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        foodImageUri = uri
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(0.dp)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFF6B6B))
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "식사 입력",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    )
                }

            }
            item {
                Button(
                    onClick = { showDatePickerDialog(context) { date -> mealDate = date } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth() // 가로로 화면 전체를 차지하도록 변경
                        .border(2.dp, Color.Black, RectangleShape)
                ){
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(end = 2.dp))
                        Text(text = mealDate.toString(), color = Color.Black)
                    }
                }
            }
            item {

                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    shape = RectangleShape
                ) {
                    if (foodImageUri == null) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(model = foodImageUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally)) {
                    DropdownMenuComponentForMealType(
                        selectedItem = when (mealType) {
                            MealType.Breakfast -> "조식"
                            MealType.Lunch -> "중식"
                            MealType.Dinner -> "석식"
                            MealType.Snack -> "간식/음료"
                        },
                        onItemSelected = { selected ->
                            mealType = when (selected) {
                                "조식" -> MealType.Breakfast
                                "중식" -> MealType.Lunch
                                "석식" -> MealType.Dinner
                                "간식/음료" -> MealType.Snack
                                else -> MealType.Breakfast
                            }
                        }
                    )
                    DropdownMenuComponentForMealPlace(
                        selectedItem = mealPlace,
                        onItemSelected = { mealPlace = it }
                    )
                }
            }


            item {
                Divider(color = Color.Black, thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))
                Spacer(modifier = Modifier.height(24.dp))
                TextField(
                    value = mealName,
                    onValueChange = { mealName = it },
                    label = { Text("음식") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Black, RectangleShape)
                        .background(Color.White).background(Color.White)
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = sideDishName,
                    onValueChange = { sideDishName = it },
                    label = { Text("반찬/음료") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Black, RectangleShape)
                        .background(Color.White).background(Color.White)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = mealCost.toString(),
                    onValueChange = {
                        mealCost = it.toIntOrNull() ?: 0  // 빈 값 처리
                    },
                    label = { Text("비용") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Black, RectangleShape)
                        .background(Color.White).background(Color.White)
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = mealReview,
                    onValueChange = { mealReview = it },
                    label = { Text("소감평") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(2.dp, Color.Black, RectangleShape)
                        .background(Color.White).background(Color.White)
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // 초기화면으로 버튼을 먼저 추가
                    Button(
                        onClick = { onNavigateHome() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("초기화면으로")
                    }
                    Button(
                        onClick = {
                            val meal = Meal(
                                date = mealDate,
                                name = mealName,
                                sideDish = sideDishName,
                                cost = mealCost.toString(),
                                type = mealType,
                                imageUri = foodImageUri,
                                review = mealReview,
                                calories = 0
                            )
                            onSaveMeal(meal)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("저장")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun showDatePickerDialog(context: Context, onDateSelected: (LocalDate) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
        onDateSelected(selectedDate)
    }, year, month, day).show()
}

@Composable
fun DropdownMenuComponentForMealPlace(selectedItem: String, onItemSelected: (String) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val items = listOf("편의점", "카페", "상록원 1층", "상록원 2층", "상록원 3층", "기숙사식당", "가든쿡")

    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
            .background(Color.White).background(Color.White)
            .padding(2.dp)
    ) {
        TextButton(onClick = { expanded.value = true }) {
            Text(text = selectedItem, fontSize = 20.sp, color = Color.Black,fontWeight = FontWeight.Bold)
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onItemSelected(item)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuComponentForMealType(selectedItem: String, onItemSelected: (String) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val items = listOf("조식", "중식", "석식", "간식/음료")

    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
            .background(Color.White).background(Color.White)
            .padding(2.dp)
    ) {
        TextButton(onClick = { expanded.value = true }) {
            Text(text = selectedItem, fontSize = 20.sp, color = Color.Black,fontWeight = FontWeight.Bold)
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onItemSelected(item)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

/*@Composable
@Preview(showBackground = true)
fun PreviewMealInputScreen() {
    MealInputScreen {}
}*/
