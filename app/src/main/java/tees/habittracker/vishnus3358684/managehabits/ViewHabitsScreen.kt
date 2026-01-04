package tees.habittracker.vishnus3358684.managehabits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import tees.habittracker.vishnus3358684.Habit
import tees.habittracker.vishnus3358684.database.HabitViewModel
import tees.habittracker.vishnus3358684.ui.theme.PrimaryC1


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewHabitsScreen(viewModel: HabitViewModel, navController: NavController) {
    val habits by viewModel.allHabits.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    var selectedTime by remember { mutableStateOf("All") }

    val categories = listOf("All", "Health", "Study", "Fitness", "Self-Care", "Mindfulness")
    val times = listOf("All", "Morning", "Mid-day", "Afternoon", "Night")

    val filtered = habits.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
                (selectedTime == "All" || it.timeOfDay == selectedTime)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Habits", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryC1,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(brush = Brush.verticalGradient(listOf(Color(0xFFEDEBFF), Color.White)))
                .padding(16.dp)
        ) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PremiumFilterDropdown(
                    label = "Category",
                    selected = selectedCategory,
                    items = categories,
                    onSelect = { selectedCategory = it }
                )

                PremiumFilterDropdown(
                    label = "Time",
                    selected = selectedTime,
                    items = times,
                    onSelect = { selectedTime = it }
                )
            }


            Spacer(Modifier.height(16.dp))

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No habits found", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(filtered) { habit ->

                        val streak by produceState(0) {
                            value = viewModel.getHabitStreak(habit)
                        }

                        PremiumHabitCard(
                            habit = habit,
                            streak = streak,
                            onClick = { navController.navigate("habitDetails/${habit.id}") }
                        )
                    }
                }
            }


        }
    }
}



@Composable
fun PremiumHabitCard(
    habit: Habit,
    streak: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = habit.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E2E2E)
                    )

                    if (habit.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = habit.description,
                            fontSize = 14.sp,
                            color = Color(0xFF7A7A7A),
                            maxLines = 2
                        )
                    }
                }

                if (streak > 0) {
                    HabitStreakBadge(streak)
                }
            }

            Divider(
                color = Color(0xFFE6E6F0),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                PremiumHabitChip(
                    icon = Icons.Default.Category,
                    text = habit.category
                )

                PremiumHabitChip(
                    icon = Icons.Default.AccessTime,
                    text = habit.reminderTime
                )

                PremiumHabitChip(
                    icon = Icons.Default.Repeat,
                    text = habit.frequency
                )
            }
        }
    }
}


@Composable
fun PremiumHabitChip(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F2FF))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PrimaryC1,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3B3B3B)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumFilterDropdown(
    label: String,
    selected: String,
    items: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(160.dp)
    ) {

        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontWeight = FontWeight.SemiBold) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryC1,
                unfocusedBorderColor = PrimaryC1,
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


