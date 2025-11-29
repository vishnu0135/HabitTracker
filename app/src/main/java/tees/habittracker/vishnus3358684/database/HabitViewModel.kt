package tees.habittracker.vishnus3358684.database

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tees.habittracker.vishnus3358684.Habit

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var category by mutableStateOf("")
    var priority by mutableStateOf("")
    var timeOfDay by mutableStateOf("")
    var startDate by mutableStateOf("")
    var reminderTime by mutableStateOf("")
    var frequency by mutableStateOf("")

    val allHabits: StateFlow<List<Habit>> = repository.allHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveHabit() {
        viewModelScope.launch {
            val habit = Habit(
                title = title,
                description = description,
                category = category,
                priority = priority,
                timeOfDay = timeOfDay,
                startDate = startDate,
                reminderTime = reminderTime,
                frequency = frequency
            )
            repository.insert(habit)
            clearForm()
        }
    }

    private fun clearForm() {
        title = ""
        description = ""
        category = ""
        priority = ""
        timeOfDay = ""
        startDate = ""
        reminderTime = ""
        frequency = ""
    }
}

class HabitViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
