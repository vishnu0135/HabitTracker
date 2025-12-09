package tees.habittracker.vishnus3358684

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
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
import tees.habittracker.vishnus3358684.database.HabitViewModel

// -------------------------------------------------------------------------
//                🔥 PREMIUM VIEW HABITS SCREEN
// -------------------------------------------------------------------------

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
                    containerColor = Color(0xFF6A5AE0),
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

            // ---------------------- FILTER ROW ----------------------
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
                        PremiumHabitCard(
                            habit = habit,
                            onClick = { navController.navigate("habitDetails/${habit.id}") }
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
//                🔥 PREMIUM GLASSMORPHIC CARD FOR HABIT ITEM
// -------------------------------------------------------------------------

@Composable
fun PremiumHabitCard(habit: Habit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Column(Modifier.padding(18.dp)) {

            // Title row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF6A5AE0),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = habit.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B3B3B)
                )
            }

            Spacer(Modifier.height(8.dp))

            if (habit.description.isNotEmpty()) {
                Text(
                    text = habit.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(10.dp))
            }

            // Details row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HabitTag(Icons.Default.Category, habit.category)
                HabitTag(Icons.Default.AccessTime, habit.reminderTime)
                HabitTag(Icons.Default.Repeat, habit.frequency)
            }
        }
    }
}

// -------------------------------------------------------------------------
//                    🔥 PREMIUM TAG CHIP
// -------------------------------------------------------------------------

@Composable
fun HabitTag(icon: ImageVector, text: String) {
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFF2F0FF))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF6A5AE0), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 12.sp, color = Color(0xFF4A45B0))
    }
}

// -------------------------------------------------------------------------
//              🔥 PREMIUM FILTER DROPDOWN
// -------------------------------------------------------------------------

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
                .menuAnchor()   // REQUIRED FOR DROPDOWN TO WORK
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6A5AE0),
                unfocusedBorderColor = Color(0xFF6A5AE0),
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


// -------------------------------------------------------------------------
//                🔥 PREMIUM HABIT DETAILS SCREEN
// -------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailsScreen(
    habitId: Int,
    viewModel: HabitViewModel,
    navController: NavController,
    onEdit: () -> Unit = {},
    onDelete: (Habit) -> Unit = {}
) {
    val habit = viewModel.allHabits.collectAsState().value.find { it.id == habitId }

    if (habit == null) {
        Text("Habit not found", modifier = Modifier.padding(20.dp))
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(habit.title, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6A5AE0)
                )
            )
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(20.dp)
        ) {

            PremiumDetailRow("Category", habit.category, Icons.Default.Category)
            PremiumDetailRow("Priority", habit.priority, Icons.Default.Flag)
            PremiumDetailRow("Time of Day", habit.timeOfDay, Icons.Default.WbSunny)
            PremiumDetailRow("Reminder Time", habit.reminderTime, Icons.Default.AccessTime)
            PremiumDetailRow("Frequency", habit.frequency, Icons.Default.Repeat)
            PremiumDetailRow("Start Date", habit.startDate, Icons.Default.CalendarMonth)

            Spacer(Modifier.height(30.dp))

            // Action Buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5AE0))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit")
                }

                Button(
                    onClick = { onDelete(habit) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun PremiumDetailRow(label: String, value: String, icon: ImageVector) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7FF)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6A5AE0))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(value, fontSize = 13.sp, color = Color.DarkGray)
            }
        }
    }
}
