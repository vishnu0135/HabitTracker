package tees.habittracker.vishnus3358684.database

import kotlinx.coroutines.flow.Flow
import tees.habittracker.vishnus3358684.Habit

class HabitRepository(private val habitDao: HabitDao) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun insert(habit: Habit) {
        habitDao.insertHabit(habit)
    }
}
