package tees.habittracker.vishnus3358684.database

class HabitCompletionRepository(
    private val dao: HabitCompletionDao
) {

    suspend fun markCompletion(
        habitId: Int,
        date: String,
        isCompleted: Boolean
    ) {
        dao.insert(
            HabitCompletion(
                habitId = habitId,
                date = date,
                isCompleted = isCompleted
            )
        )
    }

    suspend fun getTodayStatus(
        habitId: Int,
        date: String
    ): HabitCompletion? {
        return dao.getCompletionForDate(habitId, date)
    }

    suspend fun getCompletionsDesc(habitId: Int): List<HabitCompletion> {
        return dao.getCompletionsDesc(habitId)
    }

    suspend fun getCompletionsForMonth(
        habitId: Int,
        month: String
    ): List<HabitCompletion> {
        return dao.getCompletionsForMonth(habitId, month)
    }

    suspend fun getCompletionsForHabitOnDate(
        habitId: Int,
        date: String
    ): List<HabitCompletion> {
        return dao.getCompletionsForHabitOnDate(habitId, date)
    }

    suspend fun insertCompletion(completion: HabitCompletion) {
        dao.insertCompletion(completion)
    }

    suspend fun getCompletionForHabitOnDate(
        habitId: Int,
        date: String
    ): HabitCompletion? {
        return dao.getCompletionForHabitOnDate(habitId, date)
    }
}
