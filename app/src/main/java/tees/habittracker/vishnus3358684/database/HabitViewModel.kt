package tees.habittracker.vishnus3358684.database

import android.util.Log
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
import tees.habittracker.vishnus3358684.managehabits.MonthlyHabitStats
import tees.habittracker.vishnus3358684.managehabits.WeeklyDayStat
import tees.habittracker.vishnus3358684.managehabits.isHabitActiveToday
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.jvm.java


class HabitViewModel(private val repository: HabitRepository,private val completionRepository: HabitCompletionRepository) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var category by mutableStateOf("")
    var priority by mutableStateOf("")
    var timeOfDay by mutableStateOf("")
    var startDate by mutableStateOf("")
    var reminderTime by mutableStateOf("")
    var frequency by mutableStateOf("Daily")

    val allHabits: StateFlow<List<Habit>> = repository.allHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun saveHabit(): Long {
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

        val id = repository.insert(habit)
        clearForm()
        return id
    }

    suspend fun loadHabitForEdit(habitId: Int) {
        val habit = repository.getHabitById(habitId)
        habit?.let {
            title = it.title
            description = it.description
            category = it.category
            priority = it.priority
            timeOfDay = it.timeOfDay
            startDate = it.startDate
            reminderTime = it.reminderTime
            frequency = it.frequency
        }
    }

    suspend fun updateHabit(habit: Habit) {
        repository.update(habit)
    }



    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.delete(habit)
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

    var todayCompletion by mutableStateOf<Boolean?>(null)
        private set

    fun loadTodayCompletion(habitId: Int) {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            todayCompletion =
                completionRepository
                    .getTodayStatus(habitId, today)
                    ?.isCompleted
        }
    }

    fun loadTodayHabits() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val habits = repository.getAllHabitsOnce()
            val result = mutableListOf<TodayHabitUI>()

            habits.forEach { habit ->

                if (!isHabitActiveToday(habit.startDate, habit.reminderTime)) return@forEach

                val completion =
                    completionRepository.getTodayStatus(habit.id, today)

                val status = when {
                    completion != null && completion.isCompleted ->
                        HabitStatus.COMPLETED

                    completion != null && !completion.isCompleted ->
                        HabitStatus.MISSED

                    !isAfterHabitTime(habit.reminderTime) ->
                        HabitStatus.LOCKED

                    isDayOver() ->
                        HabitStatus.MISSED

                    else ->
                        HabitStatus.PENDING
                }


                val streak = getHabitStreak(habit)

                result.add(
                    TodayHabitUI(
                        habit = habit,
                        status = status,
                        streak = streak
                    )
                )
            }

            todayHabits = result
        }
    }



    fun markToday(
        habitId: Int,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            completionRepository.markCompletion(
                habitId = habitId,
                date = today,
                isCompleted = isCompleted
            )
            todayCompletion = isCompleted
        }
    }



    fun markHabitForToday(
        habitId: Int,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            val today = LocalDate.now().toString()

            completionRepository.markCompletion(
                habitId = habitId,
                date = today,
                isCompleted = isCompleted
            )

            loadTodayHabits()
        }
    }



    var todayHabits by mutableStateOf<List<TodayHabitUI>>(emptyList())
        private set


    suspend fun getHabitStreak(habit: Habit): Int {
        val completions =
            completionRepository.getCompletionsDesc(habit.id)

        return calculateDailyStreak(
            completions = completions,
            habitStartDate = habit.startDate
        )
    }

    var monthlyHabitStats by mutableStateOf<MonthlyHabitStats?>(null)
        private set

    fun loadMonthlyAnalyticsForHabit(
        habitId: Int,
        month: YearMonth
    ) {
        viewModelScope.launch {

            val monthKey = month.toString()

            val habit = repository.getHabitById(habitId)
            if (habit == null) {
                monthlyHabitStats = null
                return@launch
            }

            val completions =
                completionRepository.getCompletionsForMonth(
                    habitId = habitId,
                    month = monthKey
                )

            val completedCount = completions.count { it.isCompleted }
            val missedCount = completions.count { !it.isCompleted }

            monthlyHabitStats = MonthlyHabitStats(
                habitId = habit.id,
                habitName = habit.title,
                completedDays = completedCount,
                missedDays = missedCount
            )
        }
    }


    var weeklyStats by mutableStateOf<List<WeeklyDayStat>>(emptyList())
    var monthlyStats by mutableStateOf<List<MonthlyHabitStats>>(emptyList())

    fun loadWeeklyAnalyticsForHabit(
        habitId: Int,
        weekStart: LocalDate
    ) {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("EEE")
            val result = mutableListOf<WeeklyDayStat>()

            for (i in 0..6) {
                val date = weekStart.plusDays(i.toLong())
                val dateKey = date.toString()

                val completions =
                    completionRepository.getCompletionsForHabitOnDate(
                        habitId = habitId,
                        date = dateKey
                    )

                result.add(
                    WeeklyDayStat(
                        date = date.format(formatter),
                        completed = completions.count { it.isCompleted },
                        missed = completions.count { !it.isCompleted }
                    )
                )
            }

            weeklyStats = result
        }
    }


    fun seedTestDataFromNovember() {
        viewModelScope.launch {

            val TAG = "SeedTestData"

            val habits = repository.getAllHabitsOnce()
            Log.d(TAG, "Found ${habits.size} habits")

            if (habits.isEmpty()) {
                Log.d(TAG, "No habits found. Exiting seeding.")
                return@launch
            }

            val startDate = LocalDate.of(LocalDate.now().year, 11, 1)
            val today = LocalDate.now()

            val habitDateFormatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

            habits.forEach { habit ->

                Log.d(TAG, "Seeding habit: ${habit.title} (ID=${habit.id})")

                var date = startDate

                while (!date.isAfter(today)) {

                    if (habit.startDate.isNotBlank()) {
                        try {
                            val habitStart =
                                LocalDate.parse(habit.startDate, habitDateFormatter)

                            if (date.isBefore(habitStart)) {
                                date = date.plusDays(1)
                                continue
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Date parse failed for habit '${habit.title}': ${habit.startDate}")
                        }
                    }

                    val dateKey = date.toString()

                    val existing =
                        completionRepository.getCompletionForHabitOnDate(
                            habit.id,
                            dateKey
                        )

                    if (existing == null) {
                        val completed = listOf(true, false).random()

                        completionRepository.insertCompletion(
                            HabitCompletion(
                                habitId = habit.id,
                                date = dateKey,
                                isCompleted = completed
                            )
                        )

                        Log.d(
                            TAG,
                            "Inserted: habit=${habit.title}, date=$dateKey, completed=$completed"
                        )
                    } else {
                        Log.d(
                            TAG,
                            "Skipped (already exists): habit=${habit.title}, date=$dateKey"
                        )
                    }

                    date = date.plusDays(1)
                }
            }

            Log.d(TAG, "✅ Seeding completed successfully")
        }
    }







}

class HabitViewModelFactory(private val repository: HabitRepository,private val completionRepository: HabitCompletionRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository,completionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class TodayHabitUI(
    val habit: Habit,
    val status: HabitStatus,
    val streak: Int
)


enum class HabitStatus {
    LOCKED,
    PENDING,
    COMPLETED,
    MISSED
}

fun isAfterHabitTime(reminderTime: String): Boolean {
    if (reminderTime.isBlank()) return true

    return try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val habitTime = sdf.parse(reminderTime) ?: return false

        val now = Calendar.getInstance()
        val habitCal = Calendar.getInstance().apply {
            time = habitTime
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            set(Calendar.MONTH, now.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
        }

        now.after(habitCal)
    } catch (e: Exception) {
        false
    }
}

fun isDayOver(): Boolean {
    val now = Calendar.getInstance()
    return now.get(Calendar.HOUR_OF_DAY) >= 23
}


fun calculateDailyStreak(
    completions: List<HabitCompletion>,
    habitStartDate: String
): Int {
    if (completions.isEmpty()) return 0

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    val startDate = try {
        LocalDate.parse(habitStartDate, startFormatter)
    } catch (e: Exception) {
        null
    }

    var streak = 0
    var expectedDate = LocalDate.now()

    completions.forEach { completion ->

        val completionDate = LocalDate.parse(completion.date, formatter)

        if (startDate != null && completionDate.isBefore(startDate)) {
            return streak
        }

        if (completionDate != expectedDate) {
            return streak
        }

        if (completion.isCompleted) {
            streak++
            expectedDate = expectedDate.minusDays(1)
        } else {
            return streak
        }
    }

    return streak
}
