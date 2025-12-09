package tees.habittracker.vishnus3358684

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.launch
import tees.habittracker.vishnus3358684.database.HabitViewModel
import tees.habittracker.vishnus3358684.utils.HabitNotificationScheduler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ---------- AddHabitScreen ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    viewModel: HabitViewModel,
    navController: NavController
) {
    // sample lists
    val categories = listOf("Health", "Study", "Fitness", "Self-Care", "Mindfulness")
    val priorities = listOf("Low", "Normal", "High")
    val frequencyList = listOf("Daily", "Weekly", "Custom")

    // keep dialog states at top-level of screen
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Notification permission denied. Reminders may not show.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val exactAlarmPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (HabitNotificationScheduler.canScheduleExactAlarms(context)) {
            Toast.makeText(context, "Alarms & reminders permission granted!", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                context,
                "Alarms & reminders permission denied. Exact reminders may not work.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Habit", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Habit Title") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Description") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            Spacer(Modifier.height(16.dp))

            // Category (Exposed dropdown)
            Text("Category", fontWeight = FontWeight.Bold)
            SimpleExposedDropdown(
                selected = viewModel.category,
                items = categories,
                placeholder = "Select category",
                onSelected = { viewModel.category = it }
            )
            Spacer(Modifier.height(16.dp))

            // Priority
            Text("Priority", fontWeight = FontWeight.Bold)
            SimpleExposedDropdown(
                selected = viewModel.priority,
                items = priorities,
                placeholder = "Select priority",
                onSelected = { viewModel.priority = it }
            )
            Spacer(Modifier.height(16.dp))

            // Time of day chips
            Text("Time of Day", fontWeight = FontWeight.Bold)
            TimeOfDayChips(viewModel)
            Spacer(Modifier.height(16.dp))

            // Start Date (opens DatePicker dialog)
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

            Spacer(Modifier.height(16.dp))

            // Reminder time (opens TimePicker)
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

            Spacer(Modifier.height(16.dp))

            // Frequency
            Text("Frequency", fontWeight = FontWeight.Bold)
            SimpleExposedDropdown(
                selected = viewModel.frequency,
                items = frequencyList,
                placeholder = "Select frequency",
                onSelected = { viewModel.frequency = it }
            )

            Spacer(Modifier.height(24.dp))

            // Save
            Button(
                onClick = {
                    if (viewModel.title.isNotBlank() &&
                        viewModel.reminderTime.isNotBlank() &&
                        viewModel.frequency.isNotBlank()
                    ) {

                        val newHabit = Habit(
                            title = viewModel.title,
                            description = viewModel.description,
                            category = viewModel.category,
                            priority = viewModel.priority,
                            timeOfDay = viewModel.timeOfDay,
                            startDate = viewModel.startDate,
                            reminderTime = viewModel.reminderTime,
                            frequency = viewModel.frequency
                        )

                        // ANDROID 13+ handling notification + alarm permissions
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                            val hasNotif = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED

                            if (!hasNotif) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                Toast.makeText(
                                    context,
                                    "Enable notification permission!",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }

                            if (!HabitNotificationScheduler.canScheduleExactAlarms(context)) {
                                val intent =
                                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                exactAlarmPermissionLauncher.launch(intent)
                                Toast.makeText(
                                    context,
                                    "Enable 'Alarms & reminders' permission!",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }

                            // All permission OK → Save Habit & schedule alarms
                            scope.launch {
                                val habitId = viewModel.saveHabit()
                                HabitNotificationScheduler.scheduleHabitNotification(
                                    context = context,
                                    habitId = habitId.toInt(),
                                    title = newHabit.title,
                                    description = newHabit.description,
                                    reminderTime = newHabit.reminderTime,
                                    frequency = newHabit.frequency
                                )
                                navController.popBackStack()
                            }
                        }

                        // ANDROID 12 (API 31–32)
                        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                            if (!HabitNotificationScheduler.canScheduleExactAlarms(context)) {
                                val intent =
                                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                exactAlarmPermissionLauncher.launch(intent)
                                Toast.makeText(
                                    context,
                                    "Please allow exact alarms in settings.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }

                            scope.launch {
                                val habitId = viewModel.saveHabit()
                                HabitNotificationScheduler.scheduleHabitNotification(
                                    context = context,
                                    habitId = habitId.toInt(),
                                    title = newHabit.title,
                                    description = newHabit.description,
                                    reminderTime = newHabit.reminderTime,
                                    frequency = newHabit.frequency
                                )
                                navController.popBackStack()
                            }
                        }

                        // ANDROID 11 and below
                        else {
                            scope.launch {
                                val habitId = viewModel.saveHabit()
                                HabitNotificationScheduler.scheduleHabitNotification(
                                    context = context,
                                    habitId = habitId.toInt(),
                                    title = newHabit.title,
                                    description = newHabit.description,
                                    reminderTime = newHabit.reminderTime,
                                    frequency = newHabit.frequency
                                )
                                navController.popBackStack()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all required fields!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text("Save Habit")
            }


            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogUI(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    // IMPORTANT: State must be outside dialog content block
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = SimpleDateFormat(
                            "dd MMM yyyy",
                            Locale.getDefault()
                        ).format(Date(millis))
                        onDateSelected(formatted)
                    }
                    onDismiss()
                }
            ) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        // THE FIX → always use named param for content
        content = {
            DatePicker(state = datePickerState)
        }
    )
}


@Composable
fun ShowTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    // keep slider state stable across recompositions
    var hour by remember { mutableStateOf(12f) }   // 0..23
    var minute by remember { mutableStateOf(0f) }  // 0..59

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val h = hour.toInt()
                val m = minute.toInt()
                val formatted = String.format(
                    "%02d:%02d %s",
                    if (h % 12 == 0) 12 else h % 12,
                    m,
                    if (h < 12) "AM" else "PM"
                )
                onTimeSelected(formatted)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        // <-- use named 'text' slot so the compiler knows this lambda is the dialog body
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select time", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                Text("Hour: ${hour.toInt()}")
                Slider(
                    value = hour,
                    onValueChange = { hour = it },
                    valueRange = 0f..23f,
                    steps = 23
                )

                Spacer(Modifier.height(8.dp))

                Text("Minute: ${minute.toInt()}")
                Slider(
                    value = minute,
                    onValueChange = { minute = it },
                    valueRange = 0f..59f,
                    steps = 59
                )
            }
        }
        // no trailing lambda here
    )
}


// ---------- SimpleExposedDropdown using Material3's exposed dropdown ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleExposedDropdown(
    selected: String,
    items: List<String>,
    placeholder: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    // ExposedDropdownMenuBox anchors menu automatically
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor() // important to anchor the menu
                .fillMaxWidth(),
            placeholder = { Text(placeholder) },
            shape = RoundedCornerShape(14.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimeOfDayChips(viewModel: HabitViewModel) {
    val times = listOf("Morning", "Afternoon", "Evening")
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        times.forEach { time ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .background(if (viewModel.timeOfDay == time) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(25.dp))
                    .clickable { viewModel.timeOfDay = time }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(time)
            }
        }
    }
}

// ---------- DatePickerField (opens dialog via onOpenRequest) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onOpenRequest: () -> Unit
) {
    Box(modifier = Modifier.clickable { onOpenRequest() }) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            shape = RoundedCornerShape(14.dp),
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

// ---------- TimePickerField (opens dialog via onOpenRequest, simple display) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    label: String,
    selectedTime: String,
    onOpenRequest: () -> Unit
) {
    Box(modifier = Modifier.clickable { onOpenRequest() }) {
        OutlinedTextField(
            value = selectedTime,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            shape = RoundedCornerShape(14.dp),
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}




@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val timeOfDay: String,
    val startDate: String,
    val reminderTime: String,
    val frequency: String
)

