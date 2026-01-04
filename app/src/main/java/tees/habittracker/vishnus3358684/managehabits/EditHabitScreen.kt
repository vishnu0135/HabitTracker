package tees.habittracker.vishnus3358684.managehabits

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import tees.habittracker.vishnus3358684.DatePickerDialogUI
import tees.habittracker.vishnus3358684.DatePickerField
import tees.habittracker.vishnus3358684.Habit
import tees.habittracker.vishnus3358684.ShowTimePickerDialog
import tees.habittracker.vishnus3358684.TimeOfDayChips
import tees.habittracker.vishnus3358684.TimePickerField
import tees.habittracker.vishnus3358684.database.HabitViewModel
import tees.habittracker.vishnus3358684.utils.HabitNotificationScheduler
import tees.habittracker.vishnus3358684.utils.HabitReminderReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habitId: Int,
    viewModel: HabitViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }


    LaunchedEffect(habitId) {
        viewModel.loadHabitForEdit(habitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Habit",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Habit Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            HabitDropdownField(
                label = "Category",
                selected = viewModel.category,
                options = listOf("Health", "Study", "Fitness", "Self-Care", "Mindfulness"),
                onSelected = { viewModel.category = it }
            )

            HabitDropdownField(
                label = "Priority",
                selected = viewModel.priority,
                options = listOf("Low", "Normal", "High"),
                onSelected = { viewModel.priority = it }
            )

            TimeOfDayChips(viewModel)



            DatePickerField(
                label = "Start Date",
                selectedDate = viewModel.startDate,
                onOpenRequest = { showDatePicker = true }
            )

            if (showDatePicker) {
                DatePickerDialogUI(
                    onDismiss = { showDatePicker = false },
                    onDateSelected = {
                        viewModel.startDate = it
                        showDatePicker = false
                    }
                )
            }

            TimePickerField(
                label = "Reminder Time",
                selectedTime = viewModel.reminderTime,
                onOpenRequest = { showTimePicker = true }
            )

            if (showTimePicker) {
                ShowTimePickerDialog(
                    onDismiss = { showTimePicker = false },
                    onTimeSelected = { formatted ->
                        viewModel.reminderTime = formatted
                        showTimePicker = false
                    }
                )
            }

            HabitDropdownField(
                label = "Frequency",
                selected = viewModel.frequency,
                options = listOf("Daily", "Weekly", "Custom"),
                onSelected = { viewModel.frequency = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (viewModel.title.isBlank()) {
                        Toast.makeText(
                            context,
                            "Habit title cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    scope.launch {
                        val updatedHabit = Habit(
                            id = habitId,
                            title = viewModel.title,
                            description = viewModel.description,
                            category = viewModel.category,
                            priority = viewModel.priority,
                            timeOfDay = viewModel.timeOfDay,
                            startDate = viewModel.startDate,
                            reminderTime = viewModel.reminderTime,
                            frequency = viewModel.frequency
                        )

                        viewModel.updateHabit(updatedHabit)

                        HabitNotificationScheduler.cancelHabitReminder(
                            context = context,
                            habitId = habitId
                        )

                        if (updatedHabit.reminderTime.isNotBlank()) {
                            HabitNotificationScheduler.scheduleHabitNotification(
                                context = context,
                                habitId = habitId,
                                title = updatedHabit.title,
                                description = updatedHabit.description,
                                reminderTime = updatedHabit.reminderTime,
                                frequency = updatedHabit.frequency
                            )
                        }

                        Toast.makeText(
                            context,
                            "Habit updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Update Habit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}





