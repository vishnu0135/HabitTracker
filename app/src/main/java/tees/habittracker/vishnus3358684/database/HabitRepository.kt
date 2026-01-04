package tees.habittracker.vishnus3358684.database

import kotlinx.coroutines.flow.Flow
import tees.habittracker.vishnus3358684.Habit


class HabitRepository(private val dao: HabitDao) {

    val allHabits: Flow<List<Habit>> = dao.getAllHabits()

    suspend fun insert(habit: Habit): Long {
        return dao.insert(habit)
    }

    suspend fun update(habit: Habit) {
        dao.update(habit)
    }

    suspend fun delete(habit: Habit) {
        dao.delete(habit)
    }

    suspend fun getHabitById(habitId: Int): Habit? {
        return dao.getHabitById(habitId)
    }

    suspend fun getAllHabitsOnce(): List<Habit> {
        return dao.getAllHabitsOnce()
    }


}

